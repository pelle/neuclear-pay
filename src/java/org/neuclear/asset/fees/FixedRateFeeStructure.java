package org.neuclear.asset.fees;

/*
 *  The NeuClear Project and it's libraries are
 *  (c) 2002-2004 Antilles Software Ventures SA
 *  For more information see: http://neuclear.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * A fixed percentage rate fee structure with no minimum or maximum
 */
public class FixedRateFeeStructure extends FeeStructure {

    public FixedRateFeeStructure(double rate) {
        this.rate = rate;
    }

    private final double rate;

    /**
     * Calculates the rate based on a transaction amount
     *
     * @param amount
     * @return the calculated rate
     */

    public double calculateFee(double amount) {
        return rate * amount;
    }
}
