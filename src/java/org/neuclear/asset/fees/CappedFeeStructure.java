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
 * A simple capped fee structure with a minimum, a maximum and a percentage for inbetween.
 */
public class CappedFeeStructure extends FeeStructure {

    public CappedFeeStructure(double min, double max, double rate) {
        this.min = min;
        this.max = max;
        this.rate = rate;
    }

    private final double min;
    private final double max;
    private final double rate;

    /**
     * Calculates the fee based on a transaction amount
     *
     * @param amount
     * @return the calculated fee
     */

    public double calculateFee(final double amount) {
        final double fee = amount * rate;
        if (fee < min)
            return min;
        if (fee > max)
            return max;
        return fee;
    }
}
