package org.neuclear.asset.controllers.currency;

import org.neuclear.asset.*;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.asset.orders.TransferReceipt;
import org.neuclear.asset.orders.builders.TransferReceiptBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.exchange.orders.*;
import org.neuclear.id.Identity;
import org.neuclear.id.Service;
import org.neuclear.id.resolver.Resolver;
import org.neuclear.ledger.*;

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
    public CurrencyController(final Ledger ledger, final Signer signer, final String assetname) throws LowlevelLedgerException, NeuClearException {
        super();
        this.ledger = ledger;
        this.signer = signer;
        asset = (Service) Resolver.resolveIdentity(assetname);
        issuerBook = asset.getName();
    }

    public boolean canProcess(final Service asset) {
        return this.asset.getName().equals(asset.getName());
    }

    public final TransferReceipt process(final TransferOrder req) throws InvalidTransferException, LowLevelPaymentException, TransferDeniedException, NeuClearException {

        try {

            final PostedTransaction posted = ledger.verifiedTransfer("id", req.getSignatory().getName(), req.getRecipient(), req.getAmount().getAmount(), req.getComment());
            return (TransferReceipt) new TransferReceiptBuilder(req, posted.getTransactionTime()).convert(asset.getName(), signer);
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        } catch (InvalidTransactionException e) {
            throw new InvalidTransferException(e.getSubMessage());
        } catch (NegativeTransferException e) {
            throw new InvalidTransferException("postive amount");
        }
    }

    private String createTransactionId(final TransferOrder req, final PostedTransaction posted) {
        return req.getDigest();
    }

    /**
     * Returns balance for a given Identity.
     * This is a temporary method and will be replaced by a full BalanceRequestContract etc.
     * 
     * @param id 
     * @return 
     * @throws LowLevelPaymentException 
     */
    public double getBalance(final String id) throws LowLevelPaymentException {
        try {
            return ledger.getBalance(id);
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        }
    }


    public final ExchangeOrderReceipt process(final ExchangeOrder req) throws InvalidTransferException, LowLevelPaymentException, TransferDeniedException, NeuClearException {
/*
        try {
            final PostedHeldTransaction posted = ledger.hold(to, req.getAmount().getAmount(), req.getComment(), valuetime,req.getExpiry());

            return (ExchangeOrderReceipt) new ExchangeOrderReceiptBuilder(req, valuetime).convert(asset.getName(),signer);
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
*/
        return null;
    }

    public final ExchangeCompletedReceipt process(final ExchangeCompletionOrder complete) throws LowLevelPaymentException, InvalidTransferException, TransferDeniedException, NeuClearException {
/*
        try {
            //
            final PostedHeldTransaction heldTran = ledger.findHeldTransaction(complete.getReceipt().getOrder().getDigest());
            if (heldTran == null)
                throw new InvalidTransferException("holdid");
            final double amount = getTransactionAmount(heldTran);
            if (amount > complete.getAmount().getAmount())
                throw new TransferLargerThanHeldException(complete, amount);
            if (complete.getAmount().getAmount() < 0)
                throw new NegativeTransferException(complete.getAmount());
            if (heldTran.getExpiryTime().before(complete.getExchangeTime()) || heldTran.getTransactionTime().after(complete.getExchangeTime()))
                throw new ExpiredHeldTransferException(complete);

            final PostedTransaction tran = heldTran.complete(complete.getAmount().getAmount(), complete.getExchangeTime(), complete.getComment());
            return (ExchangeCompletedReceipt) new ExchangeCompletedReceiptBuilder(complete,TimeTools.now()).convert(asset.getName(),signer);
        } catch (UnknownTransactionException e) {
            throw new NonExistantHoldException(complete.getReceipt().getOrder().getDigest());
        } catch (TransactionExpiredException e) {
            throw new ExpiredHeldTransferException(complete);
        } catch (InvalidTransactionException e) {
            throw new InvalidTransferException(e.getLocalizedMessage());
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        }
*/           return null;
    }

    public final CancelExchangeReceipt process(final CancelExchangeOrder cancel) throws InvalidTransferException, LowLevelPaymentException, TransferDeniedException, NeuClearException {
/*
        try {
            final PostedHeldTransaction heldTran = ledger.findHeldTransaction(cancel.getReceipt().getOrder().getDigest());
            if (!isRecipient(cancel.getSignatory(), heldTran))
                throw new TransferDeniedException(cancel);
            heldTran.cancel();
            return (CancelExchangeReceipt) new CancelExchangeReceiptBuilder(cancel,TimeTools.now()).convert(asset.getName(),signer);
        } catch (UnknownTransactionException e) {
            throw new NonExistantHoldException(cancel.getReceipt().getOrder().getDigest());
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        }
*/    return null;
    }


    /**
     * Little tool to return the amount of a ledger transaction.
     * It returns the sum of all the positive values.
     * 
     * @param tran 
     * @return 
     */
    private static double getTransactionAmount(final PostedTransaction tran) {
        final Iterator iter = tran.getItems();
        double amount = 0;
        while (iter.hasNext()) {
            final TransactionItem item = (TransactionItem) iter.next();
            if (item.getAmount() > 0)
                amount += item.getAmount();
        }
        return amount;
    }

    /**
     * Utility to verify if the identity is a recepient of funds in a given transaction
     * 
     * @param id   
     * @param tran 
     * @return 
     */
    private static boolean isRecipient(final Identity id, final PostedTransaction tran) {
        final Iterator iter = tran.getItems();
        while (iter.hasNext()) {
            final TransactionItem item = (TransactionItem) iter.next();
            if (item.getAmount() >= 0 && item.getBook().equals(id.getName()))
                return true;
        }
        return false;
    }

    private final Ledger ledger;
    private final Service asset;
    private final String issuerBook;
    private final Signer signer;
}
