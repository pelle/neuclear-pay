package org.neuclear.asset;

/*
NeuClear Distributed Transaction Clearing Platform
(C) 2003 Pelle Braendgaard

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

$Id: AssetStatistics.java,v 1.1 2004/04/22 23:59:06 pelle Exp $
$Log: AssetStatistics.java,v $
Revision 1.1  2004/04/22 23:59:06  pelle
Added various statistics to Ledger as well as AssetController
Improved look and feel in the web app.

*/

/**
 * User: pelleb
 * Date: Apr 22, 2004
 * Time: 10:54:09 PM
 */
public interface AssetStatistics {
    public double getCirculation();

    public long getAmountOfAccounts();

    public long getTransactionCount();
}
