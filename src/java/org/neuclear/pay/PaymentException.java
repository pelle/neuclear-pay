package org.neuclear.pay;

import org.neuclear.ledger.LedgerException;
import org.neuclear.ledger.Ledger;

/**
 * 
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:39:02 AM
 */
public abstract class PaymentException extends Exception {
    public PaymentException(PaymentProcessor proc) {
        this.proc=proc;
    }

    public PaymentProcessor getProc() {
        return proc;
    }
    public String getMessage() {
        return "NeuClear Payment Exception: "+proc.toString()+"\n"+getSubMessage();
    }

    abstract public String getSubMessage();

    private PaymentProcessor proc;

}
