package de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NetStatusDataLayerEnumTest {

    @Test
    void enum_hasExpectedValues_andValueOfWorks() {
        NetStatusDataLayerEnum[] vals = NetStatusDataLayerEnum.values();
        assertNotNull(vals);
        assertTrue(vals.length >= 4);

        // check expected names
        assertEquals(NetStatusDataLayerEnum.REPORTED, NetStatusDataLayerEnum.valueOf("REPORTED"));
        assertEquals(NetStatusDataLayerEnum.RECOVERY_PENDING, NetStatusDataLayerEnum.valueOf("RECOVERY_PENDING"));
        assertEquals(NetStatusDataLayerEnum.RECOVERED, NetStatusDataLayerEnum.valueOf("RECOVERED"));
        assertEquals(NetStatusDataLayerEnum.MISSING, NetStatusDataLayerEnum.valueOf("MISSING"));
    }

    @Test
    void toString_returnsName() {
        assertEquals("REPORTED", NetStatusDataLayerEnum.REPORTED.toString());
    }
}

