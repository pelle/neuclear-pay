package org.neuclear.pay;

import org.neuclear.commons.configuration.Configuration;
import org.neuclear.commons.configuration.ConfigurationException;
import org.neuclear.ledger.*;
import org.picocontainer.PicoContainer;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:11:49 PM
 * To change this template use Options | File Templates.
 */
public class PaymentProcessor {
//    public PaymentProcessor(String ledgername,String title,String reserve) throws LedgerCreationException, LowlevelLedgerException, BookExistsException {
//        this(LedgerFactory.getInstance().getLedger(ledgername),title,reserve);
//    }
    public PaymentProcessor(Ledger ledger, String title, String reserve) throws LowlevelLedgerException, BookExistsException {
        this.ledger = ledger;
        this.title = title;
        Book issuerBook = null;
        try {
            issuerBook = ledger.getBook(reserve);
        } catch (UnknownBookException e) {
            createAccount(reserve, title);
        }
        issuerAccount = new Issuer(this, issuerBook);
    }

    public PaymentReceipt processPayment(PaymentRequest req) throws UnknownBookException, LowlevelLedgerException, UnBalancedTransactionException, InvalidTransactionException {
        Book from = req.getFrom().getBook();

        Book to = req.getTo().getBook();
        PostedTransaction posted = from.transfer(to, req.getAmount(), req.getComment(), req.getValuedate());


        return new PaymentReceipt(this, req, posted.getXid());
    }


    public HeldPaymentReceipt processHeldPayment(HeldPaymentRequest req) throws UnknownBookException, LowlevelLedgerException, InvalidTransactionException {
        Book from = req.getFrom().getBook();

        Book to = req.getTo().getBook();
        try {
            PostedTransaction posted = from.hold(to, req.getAmount(), req.getComment(), req.getValuedate(), req.getHeldUntil());

            return new HeldPaymentReceipt(this, req, posted.getXid());
        } catch (UnBalancedTransactionException e) {
            // This should never happen so I will rethrow this as a lowlevel
            throw new LowlevelLedgerException(ledger, e);
        }
    }

    public PaymentReceipt processCompleteHold(HeldPaymentReceipt hold, Date valuedate, double amount, String comment) throws LowlevelLedgerException, NegativePaymentException, PaymentLargerThanHeldException, PaymentNotStartedException, ExpiredHeldPaymentException {
        try {
            if (amount > hold.getAmount())
                throw new PaymentLargerThanHeldException(this, hold, amount);
            if (amount < 0)
                throw new NegativePaymentException(this, amount);
            if (hold.getHeldUntil().before(valuedate) || hold.getValuedate().after(valuedate))
                throw new ExpiredHeldPaymentException(this, hold, valuedate);
            PostedHeldTransaction heldTran = ledger.findHeldTransaction(hold.getId());
            PostedTransaction tran = heldTran.complete(amount, valuedate, comment);
            return new PaymentReceipt(this, new PaymentRequest(hold.getFrom(), hold.getTo(), amount, valuedate, comment), tran.getXid());
        } catch (UnknownTransactionException e) {
            throw new LowlevelLedgerException(ledger, e);
        } catch (TransactionExpiredException e) {
            throw new ExpiredHeldPaymentException(this, hold, valuedate);
        } catch (InvalidTransactionException e) {
            throw new LowlevelLedgerException(ledger, e);
        }
    }

    public void processCancelHold(HeldPaymentReceipt hold) throws LowlevelLedgerException {
        try {
            PostedHeldTransaction heldTran = ledger.findHeldTransaction(hold.getId());
            heldTran.cancel();
        } catch (UnknownTransactionException e) {
            throw new LowlevelLedgerException(ledger, e);
        }
    }

    public Account getAccount(String id) throws UnknownBookException, LowlevelLedgerException {
        Book book = ledger.getBook(id);
        return new Account(this, book);
    }

    public Account createAccount(String id, String title) throws BookExistsException, LowlevelLedgerException {
        return new Account(this, ledger.createNewBook(id, title));
    }

    public Issuer getIssuerAccount() {
        return issuerAccount;
    }

    public String getTitle() {
        return title;
    }

    public static PaymentProcessor getInstance() throws LowlevelLedgerException, LedgerCreationException, ConfigurationException {
        PicoContainer pico = Configuration.getContainer();
        return (PaymentProcessor) pico.getComponent(PaymentProcessor.class);
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
    private final Issuer issuerAccount;
    private final String title;
}
