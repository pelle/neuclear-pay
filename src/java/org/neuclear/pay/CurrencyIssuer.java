package org.neuclear.pay;

import org.neuclear.asset.*;
import org.neuclear.ledger.*;

import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 22, 2003
 * Time: 6:14:35 PM
 */
public class CurrencyIssuer extends CurrencyAccount implements Issuer {
    CurrencyIssuer(PaymentProcessor proc, Book book) {
        super(proc, book);
    }

    final public TransferReceipt issue(Account recipient, double amount, Date valueDate) throws UnknownBookException, UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException, NegativeTransferException, AssetMismatchException {
        return getController().processTransfer(new TransferRequest(this, recipient, amount, valueDate, "Funding"));
    }

    final public double getCirculationBalance(Date date) throws LowlevelLedgerException {
        return -getBook().getBalance(date);
    }

    final public double getCirculationBalance() throws LowlevelLedgerException {
        return getCirculationBalance(new Date());
    }


}
