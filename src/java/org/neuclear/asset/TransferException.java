package org.neuclear.asset;

import org.neuclear.commons.Utility;


/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:39:02 AM
 */
public abstract class TransferException extends Exception {
    public TransferException(final AssetController proc) {
        this.proc = proc;
    }

    public TransferException() {
        ;
    }

    protected TransferException(final Throwable cause) {
        super(cause);
    }

    public final AssetController getProc() {
        return proc;
    }

    public final String getMessage() {
        return "NeuClear Transfer Exception: " + Utility.denullString(proc.toString()) + "\n" + getSubMessage();
    }

    abstract public String getSubMessage();

    private AssetController proc;

}
