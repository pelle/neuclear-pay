package org.neuclear.asset.controllers.currency;

import org.neuclear.asset.*;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.asset.orders.builders.TransferReceiptBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.exchange.orders.CancelExchangeOrder;
import org.neuclear.exchange.orders.ExchangeCompletionOrder;
import org.neuclear.exchange.orders.ExchangeOrder;
import org.neuclear.exchange.orders.builders.CancelExchangeReceiptBuilder;
import org.neuclear.exchange.orders.builders.ExchangeReceiptBuilder;
import org.neuclear.id.Identity;
import org.neuclear.id.resolver.NSResolver;
import org.neuclear.ledger.*;

import java.sql.Timestamp;
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
    public CurrencyController(final Ledger ledger, final String assetname) throws LowlevelLedgerException, BookExistsException, NeuClearException {
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

    public boolean canProcess(final Asset asset) {
        return this.asset.getName().equals(asset.getName());
    }

    public final TransferReceiptBuilder process(final TransferOrder req) throws InvalidTransferException, LowLevelPaymentException, TransferDeniedException, NeuClearException {

        try {
            final Book from = getBook(req.getSignatory());
            final Book to = getBook(req.getRecipient());

            final Timestamp valuetime =TimeTools.now();
            final PostedTransaction posted = from.transfer(to, req.getAmount(), req.getComment(), valuetime);
            final String transaction = createTransactionId(req, posted);
            return new TransferReceiptBuilder(req, transaction);
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

    private String createTransactionId(final TransferOrder req, final PostedTransaction posted) {
        return req.getDigest();
    }

    private Book getBook(final Identity id) throws UnknownBookException, LowlevelLedgerException {
        return ledger.getBook(id.getName());
    }

    /**
     * Returns balance for a given Identity.
     * This is a temporary method and will be replaced by a full BalanceRequestContract etc.
     * 
     * @param id 
     * @return 
     * @throws LowLevelPaymentException 
     */
    public double getBalance(final Identity id, final Date time) throws LowLevelPaymentException {
        try {
            return getBook(id).getBalance(time);
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        } catch (UnknownBookException e) {
            return 0.0;// If an account isnt listed its balance is always 0
        }
    }

    public final ExchangeReceiptBuilder process(final ExchangeOrder req) throws InvalidTransferException, LowLevelPaymentException, TransferDeniedException, NeuClearException {
        try {
            if (!req.getSignatory().equals(req.getFrom()))
                throw new TransferDeniedException(req);
            final Book from = getBook(req.getFrom());
            final Book to = getBook(req.getTo());

            final PostedHeldTransaction posted = from.hold(to, req.getAmount(), req.getComment(), req.getValueTime(), req.getValidTo());


            return new ExchangeReceiptBuilder(req, createTransactionId(req, posted));
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

    public final TransferReceiptBuilder process(final ExchangeCompletionOrder complete) throws LowLevelPaymentException, InvalidTransferException, TransferDeniedException, NeuClearException {
        try {
            if (!complete.getSignatory().equals(complete.getTo()))
                throw new TransferDeniedException(complete);

            final PostedHeldTransaction heldTran = ledger.findHeldTransaction(complete.getName());
            if (heldTran == null)
                throw new InvalidTransferException("holdid");
            final double amount = getTransactionAmount(heldTran);
            if (amount > complete.getAmount())
                throw new TransferLargerThanHeldException(complete, amount);
            if (complete.getAmount() < 0)
                throw new NegativeTransferException(complete.getAmount());
            if (heldTran.getExpiryTime().before(complete.getValueTime()) || heldTran.getTransactionTime().after(complete.getValueTime()))
                throw new ExpiredHeldTransferException(complete);

            final PostedTransaction tran = heldTran.complete(complete.getAmount(), complete.getValueTime(), complete.getComment());
            return new TransferReceiptBuilder(complete, tran.getXid());
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

    public final CancelExchangeReceiptBuilder process(final CancelExchangeOrder cancel) throws InvalidTransferException, LowLevelPaymentException, TransferDeniedException, NeuClearException {
        try {
            final PostedHeldTransaction heldTran = ledger.findHeldTransaction(cancel.getHoldId());
            if (!isRecipient(cancel.getSignatory(), heldTran))
                throw new TransferDeniedException(cancel);
            heldTran.cancel();
            return new CancelExchangeReceiptBuilder(cancel);
        } catch (UnknownTransactionException e) {
            throw new NonExistantHoldException(cancel.getHoldId());
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        }
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
            if (item.getAmount() >= 0 && item.getBook().getBookID().equals(id.getName()))
                return true;
        }
        return false;
    }

    private final Ledger ledger;
    private final Asset asset;
    private final Book issuerBook;
}
