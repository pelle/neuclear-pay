package org.neuclear.asset.controllers.currency;

import org.neuclear.asset.*;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.asset.orders.TransferReceipt;
import org.neuclear.asset.orders.builders.TransferReceiptBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.exchange.orders.*;
import org.neuclear.exchange.orders.builders.CancelExchangeReceiptBuilder;
import org.neuclear.exchange.orders.builders.ExchangeCompletedReceiptBuilder;
import org.neuclear.exchange.orders.builders.ExchangeOrderReceiptBuilder;
import org.neuclear.id.Service;
import org.neuclear.id.Signatory;
import org.neuclear.ledger.*;

import java.util.Date;

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
    public CurrencyController(final Ledger ledger, final Asset asset, final Signer signer, final String alias) throws LowlevelLedgerException, NeuClearException {
        super();
        this.ledger = ledger;
        this.signer = signer;
        this.asset = asset;
        this.alias = alias;
        issuerBook = new Signatory(asset.getIssuerKey()).getName();
    }

    public boolean canProcess(final Service asset) {
        return this.asset.getName().equals(asset.getName());
    }

    public final TransferReceipt process(final TransferOrder req) throws InvalidTransferException, LowLevelPaymentException, TransferDeniedException, NeuClearException {

        try {

            final PostedTransaction posted = ledger.verifiedTransfer(req.getDigest(), req.getSignatory().getName(), req.getRecipient(), req.getAmount().getAmount(), req.getComment());
            final TransferReceipt receipt = (TransferReceipt) new TransferReceiptBuilder(req, posted.getTransactionTime()).convert(alias, signer);
            ledger.setReceiptId(req.getDigest(), receipt.getDigest());
            return receipt;
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        } catch (InvalidTransactionException e) {
            throw new InvalidTransferException(e.getSubMessage());
        } catch (NegativeTransferException e) {
            throw new InvalidTransferException("postive amount");
        } catch (UnknownTransactionException e) {
            throw new LowLevelPaymentException(e);
        }
    }


    public final ExchangeOrderReceipt process(final ExchangeOrder req) throws InvalidTransferException, LowLevelPaymentException, TransferDeniedException, NeuClearException {
        try {

            final PostedHeldTransaction posted = ledger.hold(req.getDigest(), req.getSignatory().getName(), req.getAgent().getSignatory().getName(), req.getExpiry(), req.getAmount().getAmount(), req.getComment());
            final ExchangeOrderReceipt receipt = (ExchangeOrderReceipt) new ExchangeOrderReceiptBuilder(req, posted.getTransactionTime()).convert(alias, signer);
            ledger.setHeldReceiptId(req.getDigest(), receipt.getDigest());
            return receipt;
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        } catch (InvalidTransactionException e) {
            throw new InvalidTransferException(e.getSubMessage());
        } catch (NegativeTransferException e) {
            throw new InvalidTransferException("postive amount");
        } catch (UnknownTransactionException e) {
            throw new LowLevelPaymentException(e);
        }
    }

    public final ExchangeCompletedReceipt process(final ExchangeCompletionOrder complete) throws LowLevelPaymentException, InvalidTransferException, TransferDeniedException, NeuClearException {
        try {
            PostedTransaction tran = ledger.complete(complete.getReceipt().getOrder().getDigest(), complete.getAmount().getAmount(), complete.getComment());
            ExchangeCompletedReceipt receipt = (ExchangeCompletedReceipt) new ExchangeCompletedReceiptBuilder(complete, tran.getTransactionTime()).convert(alias, signer);
            ledger.setReceiptId(complete.getReceipt().getOrder().getDigest(), receipt.getDigest());
            return receipt;
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        } catch (UnknownTransactionException e) {
            throw new InvalidTransferException(e.getLocalizedMessage());
        } catch (TransactionExpiredException e) {
            throw new InvalidTransferException(e.getLocalizedMessage());
        } catch (InvalidTransactionException e) {
            throw new InvalidTransferException(e.getLocalizedMessage());
        }
    }

    public final CancelExchangeReceipt process(final CancelExchangeOrder cancel) throws InvalidTransferException, LowLevelPaymentException, TransferDeniedException, NeuClearException {
        try {
            ledger.cancel(cancel.getReceipt().getOrder().getDigest());
            return (CancelExchangeReceipt) new CancelExchangeReceiptBuilder(cancel, new Date()).convert(alias, signer);
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        } catch (UnknownTransactionException e) {
            throw new InvalidTransferException(e.getLocalizedMessage());
        }
    }


    private final Ledger ledger;
    private final Service asset;
    private final String issuerBook;
    private final Signer signer;
    private final String alias;
}
