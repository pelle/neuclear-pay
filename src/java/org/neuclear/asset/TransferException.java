package org.neuclear.asset;



/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:39:02 AM
 */
public abstract class TransferException extends Exception {
    public TransferException(AssetController proc) {
        this.proc = proc;
    }

    public AssetController getProc() {
        return proc;
    }

    public String getMessage() {
        return "NeuClear Transfer Exception: " + proc.toString() + "\n" + getSubMessage();
    }

    abstract public String getSubMessage();

    private AssetController proc;

}
