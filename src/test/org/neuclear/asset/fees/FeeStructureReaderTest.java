package org.neuclear.asset.fees;

import junit.framework.TestCase;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.neuclear.id.InvalidNamedObjectException;

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
 * Time: 3:51:27 PM
 */
public class FeeStructureReaderTest extends TestCase {
    public FeeStructureReaderTest(String s) {
        super(s);
    }

    public FeeStructure assertFeeReader(String html, Class structureClass) throws DocumentException, InvalidNamedObjectException {
        Document doc = DocumentHelper.parseText(html);

        FeeStructure fees = FeeStructureReader.readFeeStructure("$", doc.getRootElement());
        assertEquals(structureClass, fees.getClass());
        return fees;
    }

    public void assertFee(final FeeStructure fees, final double amount, final double fee) {
        assertEquals(fee, fees.calculateFee(amount), 0);
    }

    public void testCapped() throws DocumentException, InvalidNamedObjectException {
        String html = "<html><body><td id=\"fee_min\">$0.10</td>" +
                "<td id=\"fee_rate\">1%</td><td id=\"fee_max\">$1</td></body></html>";
        FeeStructure fees = assertFeeReader(html, CappedFeeStructure.class);
        assertFee(fees, 0, 0.1);
        assertFee(fees, 5, 0.1);
        assertFee(fees, 10, 0.1);
        assertFee(fees, 20, 0.2);
        assertFee(fees, -10, 0.1);
        assertFee(fees, 999, 1);

    }

    public void testMinimum() throws DocumentException, InvalidNamedObjectException {
        String html = "<html><body><td id=\"fee_min\">$0.10</td>" +
                "<td id=\"fee_rate\">1%</td></body></html>";
        FeeStructure fees = assertFeeReader(html, MinimumFeeStructure.class);
        assertFee(fees, 0, 0.1);
        assertFee(fees, 5, 0.1);
        assertFee(fees, 10, 0.1);
        assertFee(fees, 20, 0.2);
        assertFee(fees, -10, 0.1);
        assertFee(fees, 999, 9.99);
    }

    public void testFlat() throws DocumentException, InvalidNamedObjectException {
        String html = "<html><body><td id=\"fee_min\">$0.10</td>" +
                "</body></html>";
        FeeStructure fees = assertFeeReader(html, FlatFeeStructure.class);
        assertFee(fees, 0, 0.1);
        assertFee(fees, 10, 0.1);
        assertFee(fees, -10, 0.1);
        assertFee(fees, 999, 0.1);
    }

    public void testFixed() throws DocumentException, InvalidNamedObjectException {
        String html = "<html><body>" +
                "<td id=\"fee_rate\">1%</td></body></html>";
        FeeStructure fees = assertFeeReader(html, FixedRateFeeStructure.class);
        assertFee(fees, 0, 0);
        assertFee(fees, 5, .05);
        assertFee(fees, 10, 0.1);
        assertFee(fees, 999, 9.99);
    }

    public void testZero() throws DocumentException, InvalidNamedObjectException {
        String html = "<html><body>" +
                "</body></html>";
        FeeStructure fees = assertFeeReader(html, ZeroFeeStructure.class);
        assertFee(fees, 0, 0);
        assertFee(fees, 10, 0);
        assertFee(fees, -10, 0);
        assertFee(fees, 999, 0);

    }


}
