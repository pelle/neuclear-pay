package org.neuclear.asset.contracts;

import org.neuclear.id.Identity;

import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 11:57:39 AM
 */
public interface Exchange {
    public Date getValidTo();
    public Identity getAgent();
}
