package org.neuclear.asset.fees;

import org.dom4j.Element;
import org.neuclear.xml.AbstractElementProxy;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
 * Time: 3:05:36 PM
 */
public class FeeStructureBuilder extends AbstractElementProxy {
    public FeeStructureBuilder() {
        super("div");
        getElement().addAttribute("id", "fees");
        addElement("h3").setText("Fee Structure");
        table = addElement("table");
        Element tr = table.addElement("tr");
        tr.addElement("th").setText("Size");
        tr.addElement("th").setText("Fee");
        tr.addElement("th").setText("Explanation");
    }

    public FeeStructureBuilder(String units, double flat) {
        this();
        addFlat(units, flat);
    }

    public FeeStructureBuilder(String units, double min, double rate) {
        this();
        addMinimum(units, min);
        addRate(rate);
    }

    public FeeStructureBuilder(String units, double min, double rate, double max) {
        this();
        addMinimum(units, min);
        addRate(rate);
        addMaximum(units, max);
    }

    public FeeStructureBuilder(double fixed) {
        this();
        addRate(fixed);
    }

    public FeeStructureBuilder(boolean zero) {
        this();
        if (zero)
            addZeroRate();
    }

    public Element addRange(String id, String size, String fee, String explanation) {
        Element tr = table.addElement("tr");
        tr.addElement("td").setText(size);
        Element feeElem = tr.addElement("td");
        feeElem.setText(fee);
        feeElem.addAttribute("id", id);
        tr.addElement("td").setText(explanation);

        return tr;
    }

    public Element addMinimum(String units, double fee) {

        return addRange("fee_min", "Minimum", getFormatter(units).format(fee), "Minimum transaction fee");
    }

    public Element addFlat(String units, double fee) {
        return addRange("fee_min", "Flat fee", getFormatter(units).format(fee), "Flat transaction fee");
    }

    public Element addMaximum(String units, double fee) {
        return addRange("fee_max", "Maximum", getFormatter(units).format(fee), "Maximum transaction fee");
    }

    public Element addRate(double rate) {
        return addRange("fee_rate", "Any", percentFormat.format(rate), "Transaction fee rate");
    }

    public Element addZeroRate() {
        return addRange("fee_zero", "Any", "Zero", "There is no transaction fee");
    }

    public static NumberFormat getFormatter(String unit) {
        NumberFormat format = NumberFormat.getCurrencyInstance();

        if (format instanceof DecimalFormat) {
            DecimalFormat dec = (DecimalFormat) format;
            String pattern = dec.toLocalizedPattern();
            int loc = pattern.indexOf('\u00A4');
            if (loc >= 0) {
                final String p2 = pattern.substring(0, loc) + unit + pattern.substring(loc + 1, pattern.length() - 1);
                dec.applyLocalizedPattern(p2);
//                System.out.println("New format: "+p2);
            }
        }
        return format;
    }

    private Element table;
    private NumberFormat percentFormat = NumberFormat.getPercentInstance();

}
