package org.neuclear.pay;

import org.neuclear.asset.*;
import org.neuclear.commons.configuration.Configuration;
import org.neuclear.commons.configuration.ConfigurationException;
import org.neuclear.ledger.*;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:11:49 PM
 * To change this template use Options | File Templates.
 */
public final class PaymentProcessor extends AssetController {
//    public PaymentProcessor(String ledgername,String title,String reserve) throws LedgerCreationException, LowlevelLedgerException, BookExistsException {
//        this(LedgerFactory.getInstance().getLedger(ledgername),title,reserve);
//    }
    public PaymentProcessor(Ledger ledger, String title, String reserve) throws LowlevelLedgerException, BookExistsException {
        super(title);
        this.ledger = ledger;

        Book issuerBook = null;
        try {
            issuerBook = ledger.getBook(reserve);
        } catch (UnknownBookException e) {
            createAccount(reserve, title);
        }
        issuerAccount = new CurrencyIssuer(this, issuerBook);
    }

    public final TransferReceipt processTransfer(TransferRequest req) throws UnknownBookException, LowlevelLedgerException, UnBalancedTransactionException, InvalidTransactionException {
        Book from = ((CurrencyAccount) req.getFrom()).getBook();

        Book to = ((CurrencyAccount) req.getTo()).getBook();
        PostedTransaction posted = from.transfer(to, req.getAmount(), req.getComment(), req.getValueTime());


        return createTransferReceipt(req, posted.getXid());
    }


    public final HeldTransferReceipt processHeldTransfer(HeldTransferRequest req) throws UnknownBookException, LowlevelLedgerException, InvalidTransactionException {
        Book from = ((CurrencyAccount) req.getFrom()).getBook();

        Book to = ((CurrencyAccount) req.getTo()).getBook();
        try {
            PostedTransaction posted = from.hold(to, req.getAmount(), req.getComment(), req.getValueTime(), req.getHeldUntil());

            return createHeldTransferReceipt(req, posted.getXid());
        } catch (UnBalancedTransactionException e) {
            // This should never happen so I will rethrow this as a lowlevel
            throw new LowlevelLedgerException(ledger, e);
        }
    }

    public final TransferReceipt processCompleteHold(HeldTransferReceipt hold, Date valuedate, double amount, String comment) throws LowlevelLedgerException, NegativeTransferException, TransferLargerThanHeldException, TransferNotStartedException, ExpiredHeldTransferException {
        try {
            if (amount > hold.getAmount())
                throw new TransferLargerThanHeldException(this, hold, amount);
            if (amount < 0)
                throw new NegativeTransferException(this, amount);
            if (hold.getHeldUntil().before(valuedate) || hold.getValueTime().after(valuedate))
                throw new ExpiredHeldTransferException(this, hold, valuedate);
            PostedHeldTransaction heldTran = ledger.findHeldTransaction(hold.getId());
            PostedTransaction tran = heldTran.complete(amount, valuedate, comment);
            return createTransferReceipt(new TransferRequest(hold.getFrom(), hold.getTo(), amount, valuedate, comment), tran.getXid());
        } catch (UnknownTransactionException e) {
            throw new LowlevelLedgerException(ledger, e);
        } catch (TransactionExpiredException e) {
            throw new ExpiredHeldTransferException(this, hold, valuedate);
        } catch (InvalidTransactionException e) {
            throw new LowlevelLedgerException(ledger, e);
        } catch (AssetMismatchException e) {
            throw new LowlevelLedgerException(ledger, e);// At this stage this should never reall happen. Best be safe.
        }
    }

    public final void processCancelHold(HeldTransferReceipt hold) throws LowlevelLedgerException {
        try {
            PostedHeldTransaction heldTran = ledger.findHeldTransaction(hold.getId());
            heldTran.cancel();
        } catch (UnknownTransactionException e) {
            throw new LowlevelLedgerException(ledger, e);
        }
    }

    public final Account getAccount(String id) throws UnknownBookException, LowlevelLedgerException {
        Book book = ledger.getBook(id);
        return new CurrencyAccount(this, book);
    }

    public final Account createAccount(String id, String title) throws BookExistsException, LowlevelLedgerException {
        return new CurrencyAccount(this, ledger.createNewBook(id, title));
    }

    public static PaymentProcessor getInstance() throws LowlevelLedgerException, LedgerCreationException, ConfigurationException {
        return (PaymentProcessor) Configuration.getComponent(PaymentProcessor.class, "neuclear-pay");
    }

    public Issuer getIssuer() {
        return issuerAccount;
    }

/*    public static PicoContainer getContainer() throws LedgerCreationException, LowlevelLedgerException {
       CompositePicoContainer pico=new CompositePicoContainer.WithContainerArray(new PicoContainer[]{LedgerFactory.getInstance().getContainer("neu://superbux/reserve")});
        try {
            pico.
            pico.registerComponentByClass( PaymentProcessor.class);
            pico.addParameterToComponent(PaymentProcessor.class,String.class,"SuperBux");
            pico.addParameterToComponent(PaymentProcessor.class,String.class,"neu://superbux/reserve");
            pico.instantiateComponents();
        } catch (DuplicateComponentKeyRegistrationException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (AssignabilityRegistrationException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (NotConcreteRegistrationException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (PicoIntrospectionException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (PicoInvocationTargetInitializationException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (PicoInitializationException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        return pico;
    }
  */
    private final Ledger ledger;
    protected final Issuer issuerAccount;
}
