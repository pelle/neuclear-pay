package org.neuclear.asset.controllers.currency;

import org.neuclear.asset.*;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.IssueOrder;
import org.neuclear.asset.orders.IssueReceipt;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.asset.orders.TransferReceipt;
import org.neuclear.asset.orders.builders.IssueReceiptBuilder;
import org.neuclear.asset.orders.builders.TransferReceiptBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.exchange.orders.*;
import org.neuclear.exchange.orders.builders.CancelExchangeReceiptBuilder;
import org.neuclear.exchange.orders.builders.ExchangeCompletedReceiptBuilder;
import org.neuclear.exchange.orders.builders.ExchangeOrderReceiptBuilder;
import org.neuclear.id.Identity;
import org.neuclear.id.Service;
import org.neuclear.id.Signatory;
import org.neuclear.id.SignedNamedObject;
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

    public SignedNamedObject process(Identity identity) throws LowLevelPaymentException {
        try {
            ledger.registerBook(identity.getSignatory().getName(), identity.getSignatory().getName().substring(0, 10), "identity", "", identity.getEncoded());
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        }
        return null;
    }

    public boolean canProcess(final Service asset) {
        return this.asset.getName().equals(asset.getName());
    }

    public final TransferReceipt process(final TransferOrder req) throws InvalidTransferException, LowLevelPaymentException, TransferDeniedException, NeuClearException {

        try {
            if (req.getSignatory().getName().equals(issuerBook))
                throw new InvalidTransferException("Issuer is not allowed to transfer");

            final PostedTransaction posted = ledger.verifiedTransfer(req.getDigest(), req.getSignatory().getName(), req.getRecipient(), req.getAmount().getAmount(), req.getComment());
            final TransferReceipt receipt = (TransferReceipt) new TransferReceiptBuilder(req, posted.getTransactionTime()).convert(alias, signer);
            ledger.setReceiptId(req.getDigest(), receipt.getDigest());
            return receipt;
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        } catch (InvalidTransactionException e) {
            throw new InvalidTransferException(e);
        } catch (NegativeTransferException e) {
            throw new InvalidTransferException("postive amount");
        } catch (UnknownTransactionException e) {
            throw new LowLevelPaymentException(e);
        }
    }

    /**
     * Issues an asset. Thus moving assets into circulation
     *
     * @param req IssueOrder
     * @return Unsigned Receipt
     * @throws org.neuclear.asset.LowLevelPaymentException
     *
     * @throws org.neuclear.asset.TransferDeniedException
     *
     * @throws org.neuclear.asset.InvalidTransferException
     *
     */
    public IssueReceipt process(IssueOrder req) throws LowLevelPaymentException, TransferDeniedException, InvalidTransferException, NeuClearException {
        try {
            if (!req.getSignatory().getName().equals(issuerBook))
                throw new InvalidTransferException("Only Issuer is allowed to issue");
            final PostedTransaction posted = ledger.transfer(req.getDigest(), req.getSignatory().getName(), req.getRecipient(), req.getAmount().getAmount(), req.getComment());
            final IssueReceipt receipt = (IssueReceipt) new IssueReceiptBuilder(req, posted.getTransactionTime()).convert(alias, signer);
            ledger.setReceiptId(req.getDigest(), receipt.getDigest());
            return receipt;
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        } catch (InvalidTransactionException e) {
            throw new InvalidTransferException(e);
        } catch (NegativeTransferException e) {
            throw new InvalidTransferException("postive amount");
        } catch (UnknownTransactionException e) {
            throw new LowLevelPaymentException(e);
        }
    }


    public final ExchangeOrderReceipt process(final ExchangeOrder req) throws InvalidTransferException, LowLevelPaymentException, TransferDeniedException, NeuClearException {
        try {
            if (req.getSignatory().getName().equals(issuerBook))
                throw new InvalidTransferException("Issuer is not allowed to Exchange");

            final PostedHeldTransaction posted = ledger.hold(req.getDigest(), req.getSignatory().getName(), req.getAgent().getSignatory().getName(), req.getExpiry(), req.getAmount().getAmount(), req.getComment());
            final ExchangeOrderReceipt receipt = (ExchangeOrderReceipt) new ExchangeOrderReceiptBuilder(req, posted.getTransactionTime()).convert(alias, signer);
            ledger.setHeldReceiptId(req.getDigest(), receipt.getDigest());
            return receipt;
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        } catch (InvalidTransactionException e) {
            throw new InvalidTransferException(e);
        } catch (NegativeTransferException e) {
            throw new InvalidTransferException("postive amount");
        } catch (UnknownTransactionException e) {
            throw new LowLevelPaymentException(e);
        }
    }

    public final ExchangeCompletedReceipt process(final ExchangeCompletionOrder complete) throws LowLevelPaymentException, InvalidTransferException, TransferDeniedException, NeuClearException {
        try {
            if (!complete.getSignatory().getPublicKey().equals(complete.getReceipt().getOrder().getAgent().getServiceKey()))
                throw new InvalidTransferException("Only Agent is allowed to Sign Completion Order");
            PostedTransaction tran = ledger.complete(complete.getReceipt().getOrder().getDigest(), complete.getAmount().getAmount(), complete.getComment());
            ExchangeCompletedReceipt receipt = (ExchangeCompletedReceipt) new ExchangeCompletedReceiptBuilder(complete, tran.getTransactionTime()).convert(alias, signer);
            ledger.setReceiptId(complete.getReceipt().getOrder().getDigest(), receipt.getDigest());
            return receipt;
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        } catch (UnknownTransactionException e) {
            throw new InvalidTransferException(e);
        } catch (TransactionExpiredException e) {
            throw new InvalidTransferException(e);
        } catch (InvalidTransactionException e) {
            throw new InvalidTransferException(e);
        }
    }

    public final CancelExchangeReceipt process(final CancelExchangeOrder cancel) throws InvalidTransferException, LowLevelPaymentException, TransferDeniedException, NeuClearException {
        if (!(cancel.getSignatory().getName().equals(cancel.getReceipt().getOrder().getSignatory().getName())
                || cancel.getSignatory().getPublicKey().equals(cancel.getReceipt().getOrder().getAgent().getServiceKey())))
            throw new InvalidTransferException("Only Agent is allowed to Sign Completion Order");
        try {
            final Date time = ledger.cancel(cancel.getReceipt().getOrder().getDigest());
            return (CancelExchangeReceipt) new CancelExchangeReceiptBuilder(cancel, time).convert(alias, signer);
        } catch (LowlevelLedgerException e) {
            throw new LowLevelPaymentException(e);
        } catch (UnknownTransactionException e) {
            throw new InvalidTransferException(e);
        }
    }

    public Asset getAsset() {
        return asset;
    }


    private final Ledger ledger;
    private final Asset asset;
    private final String issuerBook;
    private final Signer signer;
    private final String alias;
}
