package org.neuclear.asset.controllers.currency;

import org.neuclear.asset.*;
import org.neuclear.asset.contracts.*;
import org.neuclear.asset.contracts.builders.TransferReceiptBuilder;
import org.neuclear.asset.contracts.builders.HeldTransferReceiptBuilder;
import org.neuclear.asset.contracts.builders.CancelHeldTransferReceiptBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.configuration.Configuration;
import org.neuclear.commons.configuration.ConfigurationException;
import org.neuclear.id.Identity;
import org.neuclear.id.resolver.NSResolver;
import org.neuclear.ledger.*;

import java.util.Date;
import java.util.Iterator;

/**
 * The CurrencyController is an AssetController implementation that manages an Electronic currency based
 * on book entries.<p>
 * The book entries are managed by <a href="http://neuclear.org/ledger/">NeuClear Ledger</a> which by default
 * uses a SQL database to store the entries.
 */
public final class CurrencyController extends AssetController {
//    public CurrencyController(String ledgername,String title,String reserve) throws LedgerCreationException, LowlevelLedgerException, BookExistsException {
//        this(LedgerFactory.getInstance().getLedger(ledgername),title,reserve);
//    }
    public CurrencyController(Ledger ledger, String assetname) throws LowlevelLedgerException, BookExistsException, NeuClearException {
        super();
        this.ledger = ledger;
        asset = (Asset) NSResolver.resolveIdentity(assetname);

        Book tmpIssuer = null;
        try {
            tmpIssuer = ledger.getBook(asset.getName());
        } catch (UnknownBookException e) {
            tmpIssuer = ledger.createNewBook(asset.getName());
        }
        issuerBook = tmpIssuer;
    }

    public boolean canProcess(Asset asset) {
        return this.asset.getName().equals(asset.getName());
    }

    public final TransferReceiptBuilder processTransfer(TransferRequest req) throws InvalidTransferException, LowLevelPaymentException, TransferDeniedException {
        try {
            if (!req.getSignatory().equals(req.getFrom()))
                throw new TransferDeniedException(req);
            Book from = getBook(req.getFrom());
            Book to = getBook(req.getTo());

            PostedTransaction posted = from.transfer(to, req.getAmount(), req.getComment(), req.getValueTime());
            return new TransferReceiptBuilder(req, createTransactionId(req, posted));
        } catch (UnknownBookException e) {
            throw new InvalidTransferException(e.getSubMessage());
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        } catch (InvalidTransactionException e) {
            throw new InvalidTransferException(e.getSubMessage());
        } catch (UnBalancedTransactionException e) {
            throw new InvalidTransferException("unbalanced");
        } catch (NegativeTransferException e) {
            throw new InvalidTransferException("postive amount");
        }
    }

    private String createTransactionId(TransferRequest req, PostedTransaction posted) {
        return req.getAsset().getName() + "/" + posted.getXid();
    }

    private Book getBook(Identity id) throws UnknownBookException, LowlevelLedgerException {
        return ledger.getBook(id.getName());
    }


    public final HeldTransferReceiptBuilder processHeldTransfer(HeldTransferRequest req) throws InvalidTransferException, LowLevelPaymentException, TransferDeniedException {
       try {
           if (!req.getSignatory().equals(req.getFrom()))
               throw new TransferDeniedException(req);
            Book from = getBook(req.getFrom());
            Book to = getBook(req.getTo());

            PostedHeldTransaction posted = from.hold(to, req.getAmount(), req.getComment(), req.getValueTime(),req.getHeldUntil());


            return new HeldTransferReceiptBuilder(req, createTransactionId(req, posted));
        } catch (UnknownBookException e) { //TODO Implement something like this eg. AccountNotValidException
            throw new InvalidTransferException(e.getSubMessage());
        } catch (LowlevelLedgerException e) {
           throw new LowLevelPaymentException(e);
        } catch (InvalidTransactionException e) {
            throw new InvalidTransferException(e.getSubMessage());
        } catch (UnBalancedTransactionException e) {
            throw new InvalidTransferException("unbalanced");
        } catch (NegativeTransferException e) {
            throw new InvalidTransferException("postive amount");
       }
    }

    public final TransferReceiptBuilder processCompleteHold(CompleteHeldTransferRequest complete) throws LowLevelPaymentException,InvalidTransferException,TransferDeniedException {
        try {
            if (!complete.getSignatory().equals(complete.getTo()))
                throw new TransferDeniedException(complete);

            PostedHeldTransaction heldTran = ledger.findHeldTransaction(complete.getName());
            if (heldTran==null)
                throw new InvalidTransferException("holdid");
            double amount = getTransactionAmount(heldTran);
            if (amount > complete.getAmount())
                throw new TransferLargerThanHeldException(complete,amount);
            if (complete.getAmount() < 0)
                throw new NegativeTransferException(complete.getAmount());
            if (heldTran.getExpiryTime().before(complete.getValueTime()) || heldTran.getTransactionTime().after(complete.getValueTime()))
                throw new ExpiredHeldTransferException(complete);

            PostedTransaction tran = heldTran.complete(complete.getAmount(),complete.getValueTime(),complete.getComment());
            return new TransferReceiptBuilder(complete,tran.getXid());
        } catch (UnknownTransactionException e) {
            throw new NonExistantHoldException(complete.getHoldId());
        } catch (TransactionExpiredException e) {
            throw new ExpiredHeldTransferException(complete);
        } catch (InvalidTransactionException e) {
            throw new InvalidTransferException(e.getLocalizedMessage());
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        }
    }

    public final CancelHeldTransferReceiptBuilder processCancelHold(CancelHeldTransferRequest cancel) throws InvalidTransferException,LowLevelPaymentException,TransferDeniedException {
        try {
            PostedHeldTransaction heldTran = ledger.findHeldTransaction(cancel.getHoldId());
            if (!isRecipient(cancel.getSignatory(),heldTran))
                throw new TransferDeniedException(cancel);
            heldTran.cancel();
            return new CancelHeldTransferReceiptBuilder(cancel);
        } catch (UnknownTransactionException e) {
            throw new NonExistantHoldException(cancel.getHoldId());
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException( e);
        }
    }

    /**
     * Little tool to return the amount of a ledger transaction.
     * It returns the sum of all the positive values.
     * @param tran
     * @return
     */
    private static double getTransactionAmount(PostedTransaction tran){
        Iterator iter=tran.getItems();
        double amount=0;
        while (iter.hasNext()) {
            TransactionItem item = (TransactionItem) iter.next();
            if (item.getAmount()>0)
                amount+=item.getAmount();
        }
        return amount;
    }
    /**
     * Utility to verify if the identity is a recepient of funds in a given transaction
     * @param id
     * @param tran
     * @return
     */
    private static boolean isRecipient(Identity id,PostedTransaction tran){
        Iterator iter=tran.getItems();
        while (iter.hasNext()) {
            TransactionItem item = (TransactionItem) iter.next();
            if (item.getAmount()>=0&&item.getBook().getBookID().equals(id.getName()))
                return true;
        }
        return false;
    }

    private final Ledger ledger;
    private final Asset asset;
    private final Book issuerBook;
}
