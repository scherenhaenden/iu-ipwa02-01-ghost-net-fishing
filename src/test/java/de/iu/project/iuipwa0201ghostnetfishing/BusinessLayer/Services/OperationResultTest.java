package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperationResultTest {

    @Test
    void enum_containsExpectedValues() {
        OperationResult[] vals = OperationResult.values();
        assertNotNull(vals);
        assertTrue(vals.length >= 4);
        assertEquals(OperationResult.OK, OperationResult.valueOf("OK"));
        assertEquals(OperationResult.NOT_FOUND, OperationResult.valueOf("NOT_FOUND"));
        assertEquals(OperationResult.CONFLICT, OperationResult.valueOf("CONFLICT"));
        assertEquals(OperationResult.BAD_REQUEST, OperationResult.valueOf("BAD_REQUEST"));
    }
}

