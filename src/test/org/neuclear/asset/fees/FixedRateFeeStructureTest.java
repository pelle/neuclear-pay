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
 * User: pelleb
 * Date: Sep 6, 2004
 * Time: 2:37:50 PM
 */
public class FixedRateFeeStructureTest extends AbstractFeeStructureTest {
    public static final double RATE = 0.01;

    public FixedRateFeeStructureTest(String s) {
        super(s);
    }

    protected FeeStructure createFeeStructure() {
        return new FixedRateFeeStructure(RATE);
    }

    public void testFeeStructure() {
        for (double i = 0; i < 100; i += 0.01) {
            assertFee(i, i * RATE);
        }
    }
}
