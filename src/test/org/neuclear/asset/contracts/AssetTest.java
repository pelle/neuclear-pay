package org.neuclear.asset.contracts;

import org.neuclear.asset.contracts.builders.AssetBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.tests.AbstractSigningTest;

import java.security.GeneralSecurityException;

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
 * Date: Sep 8, 2004
 * Time: 4:15:43 PM
 */
public class AssetTest extends AbstractSigningTest {
    public AssetTest(final String string) throws NeuClearException, GeneralSecurityException {
        super(string);
        AssetGlobals.registerReaders();
    }

    public void testRoundNoMinimum() throws Exception {
        Asset asset = (Asset) new AssetBuilder("test", "http://localhost:8080/rules.html", "http://localhost:8080/Asset", signer.getPublicKey("eve"), signer.getPublicKey("carol"), 2, 0, "t").convert("bux", signer);
        assertEquals(100, asset.getMultiplier());
        assertEquals(2, asset.getDecimal());
        assertEquals(0.0, asset.getMinimumTransaction(), 0);
        assertEquals(0, asset.round(0), 0);
        assertEquals(0.11, asset.round(0.111), 0);
        assertEquals(0.00, asset.round(0.001), 0);
        assertEquals(0.12, asset.round(0.115), 0);
        assertEquals(0.01, asset.round(0.005), 0);
    }

    public void testRoundWithMinimum() throws Exception {
        Asset asset = (Asset) new AssetBuilder("test", "http://localhost:8080/rules.html", "http://localhost:8080/Asset", signer.getPublicKey("eve"), signer.getPublicKey("carol"), 2, 0.1, "t").convert("bux", signer);
        assertEquals(100, asset.getMultiplier());
        assertEquals(2, asset.getDecimal());
        assertEquals(0.1, asset.getMinimumTransaction(), 0);
        assertEquals(0.1, asset.round(0), 0);
        assertEquals(0.11, asset.round(0.111), 0);
        assertEquals(0.1, asset.round(0.001), 0);
        assertEquals(0.12, asset.round(0.115), 0);
        assertEquals(0.1, asset.round(0.005), 0);
    }
}
