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
public class MinimumFeeStructureTest extends AbstractFeeStructureTest {
    public static final double RATE = 0.01;
    public static final double MIN = 0.10;

    public MinimumFeeStructureTest(String s) {
        super(s);
    }

    protected FeeStructure createFeeStructure() {
        return new MinimumFeeStructure(MIN, RATE);
    }

    public void testFeeStructure() {
        for (double i = 0; i < 1000; i += 0.01) {
            if (i < 10)
                assertFee(i, MIN);
            else
                assertFee(i, i * RATE);
        }
    }
}
