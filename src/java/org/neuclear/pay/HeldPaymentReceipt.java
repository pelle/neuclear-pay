package org.neuclear.pay;

import org.neuclear.ledger.UnBalancedTransactionException;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownBookException;

import java.util.Date;

/**
 * 
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 11:59:36 AM
 */
public class HeldPaymentReceipt extends PaymentReceipt implements Held {
    public Date getHeldUntil() {
        return req.getHeldUntil();
    }

    public HeldPaymentReceipt(PaymentProcessor proc, HeldPaymentRequest preq, String id) {
        super(proc, preq, id);
        req=preq;
    }

    public void cancel() throws  LowlevelLedgerException, ExpiredHeldPaymentException {
        getProc().processCancelHold(this);
    }
    public PaymentReceipt complete(Date valueDate,double amount, String comment) throws  LowlevelLedgerException, PaymentNotStartedException, ExpiredHeldPaymentException, PaymentLargerThanHeldException, NegativePaymentException {

        return getProc().processCompleteHold(this,valueDate,amount,comment);

    }
    private HeldPaymentRequest req;
}
