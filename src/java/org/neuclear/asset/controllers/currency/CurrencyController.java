package org.neuclear.asset.controllers.currency;

import org.neuclear.asset.*;
import org.neuclear.asset.contracts.*;
import org.neuclear.asset.contracts.builders.TransferReceiptBuilder;
import org.neuclear.asset.contracts.builders.HeldTransferReceiptBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.configuration.Configuration;
import org.neuclear.commons.configuration.ConfigurationException;
import org.neuclear.id.Identity;
import org.neuclear.id.resolver.NSResolver;
import org.neuclear.ledger.*;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:11:49 PM
 * To change this template use Options | File Templates.
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
        return false;
    }

    public final TransferReceiptBuilder processTransfer(TransferRequest req) throws InvalidTransferException {
        try {
            Book from = getBook(req.getFrom());
            Book to = getBook(req.getTo());

            PostedTransaction posted = from.transfer(to, req.getAmount(), req.getComment(), req.getValueTime());


            return new TransferReceiptBuilder(req, createTransactionId(req, posted));
        } catch (UnknownBookException e) { //TODO Implement something like this eg. AccountNotValidException
            throw new InvalidTransferException(e.getSubMessage());
        } catch (LowlevelLedgerException e) { //TODO Really need to move this out of ledger
            e.printStackTrace();
        } catch (InvalidTransactionException e) {
            throw new InvalidTransferException(e.getSubMessage());
        } catch (UnBalancedTransactionException e) {
            throw new InvalidTransferException("unbalanced");
        } catch (NegativeTransferException e) {
            throw new InvalidTransferException("postive amount");
        }
        return null;//TODO No no no
    }

    private String createTransactionId(TransferRequest req, PostedTransaction posted) {
        return req.getAsset().getName() + "/" + posted.getXid();
    }

    private Book getBook(Identity id) throws UnknownBookException, LowlevelLedgerException {
        return ledger.getBook(id.getName());
    }


    public final HeldTransferReceiptBuilder processHeldTransfer(HeldTransferRequest req) throws InvalidTransferException {
       try {
            Book from = getBook(req.getFrom());
            Book to = getBook(req.getTo());

            PostedTransaction posted = from.transfer(to, req.getAmount(), req.getComment(), req.getValueTime());


            return new HeldTransferReceiptBuilder(req, createTransactionId(req, posted));
        } catch (UnknownBookException e) { //TODO Implement something like this eg. AccountNotValidException
            throw new InvalidTransferException(e.getSubMessage());
        } catch (LowlevelLedgerException e) { //TODO Really need to move this out of ledger
            e.printStackTrace();
        } catch (InvalidTransactionException e) {
            throw new InvalidTransferException(e.getSubMessage());
        } catch (UnBalancedTransactionException e) {
            throw new InvalidTransferException("unbalanced");
        } catch (NegativeTransferException e) {
            throw new InvalidTransferException("postive amount");
        } catch (InvalidTransferException e) {
           e.printStackTrace();  //To change body of catch statement use Options | File Templates.
       }
        return null;//TODO No no no
    }

    public final TransferReceipt processCompleteHold(HeldTransferReceipt hold, Date valuedate, double amount, String comment) throws LowlevelLedgerException, NegativeTransferException, TransferLargerThanHeldException, TransferNotStartedException, ExpiredHeldTransferException, InvalidTransferException {
        try {
            if (amount > hold.getAmount())
                throw new TransferLargerThanHeldException(this, hold, amount);
            if (amount < 0)
                throw new NegativeTransferException(this, amount);
            if (hold.getHeldUntil().before(valuedate) || hold.getValueTime().after(valuedate))
                throw new ExpiredHeldTransferException(this, hold, valuedate);
            PostedHeldTransaction heldTran = ledger.findHeldTransaction(hold.getId());
            PostedTransaction tran = heldTran.complete(amount, valuedate, comment);
            return createTransferReceipt(new TransferRequest(getIssuer().getID(), hold.getFrom(), hold.getTo(), amount, valuedate, comment), tran.getXid());
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

    public static CurrencyController getInstance() throws LowlevelLedgerException, LedgerCreationException, ConfigurationException {
        return (CurrencyController) Configuration.getComponent(CurrencyController.class, "neuclear-pay");
    }

    public Issuer getIssuer() {
        return issuerAccount;
    }

/*    public static PicoContainer getContainer() throws LedgerCreationException, LowlevelLedgerException {
       CompositePicoContainer pico=new CompositePicoContainer.WithContainerArray(new PicoContainer[]{LedgerFactory.getInstance().getContainer("neu://superbux/reserve")});
        try {
            pico.
            pico.registerComponentByClass( CurrencyController.class);
            pico.addParameterToComponent(CurrencyController.class,String.class,"SuperBux");
            pico.addParameterToComponent(CurrencyController.class,String.class,"neu://superbux/reserve");
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
    private final Asset asset;
    private final Book issuerBook;
}
