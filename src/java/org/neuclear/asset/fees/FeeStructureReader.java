package org.neuclear.asset.fees;

import org.dom4j.Element;
import org.neuclear.commons.Utility;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.xml.XMLTools;

import java.text.NumberFormat;
import java.text.ParseException;

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
 * Time: 3:34:33 PM
 */
public final class FeeStructureReader {
    private FeeStructureReader() {
    };

    public static FeeStructure readFeeStructure(final String units, final Element elem) throws InvalidNamedObjectException {
        Element minElem = XMLTools.getByID(elem, "fee_min");
        Element maxElem = XMLTools.getByID(elem, "fee_max");
        Element rateElem = XMLTools.getByID(elem, "fee_rate");
        NumberFormat curFormat = FeeStructureBuilder.getFormatter(units);
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        double max = readAmount(curFormat, maxElem);
        double min = readAmount(curFormat, minElem);
        double rate = readAmount(percentFormat, rateElem);
        if (maxElem == null && minElem == null && rateElem == null)
            return new ZeroFeeStructure();
        if (maxElem != null)
            return new CappedFeeStructure(min, max, rate);
        if (minElem == null && rateElem != null)
            return new FixedRateFeeStructure(rate);
        if (minElem != null && rateElem != null)
            return new MinimumFeeStructure(min, rate);
        if (minElem != null)
            return new FlatFeeStructure(min);
        throw new InvalidNamedObjectException("Unknown Fee Structure");
    }

    private static double readAmount(NumberFormat format, Element elem) throws InvalidNamedObjectException {
        if (elem == null)
            return 0;
        String text = elem.getTextTrim();
        if (Utility.isEmpty(text))
            throw new InvalidNamedObjectException("Fee Parse Error", "Empty tag");
        try {
            return format.parse(text).doubleValue();
        } catch (ParseException e) {
            throw new InvalidNamedObjectException("Fee Parse Error", e);
        }
    }

}
