package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class IAbandonedNetBusinessLayerServiceTest {

    @Test
    void interface_declares_expected_methods() {
        Class<?> iface = IAbandonedNetBusinessLayerService.class;
        String[] expected = new String[]{"getAllNetsNewestFirst", "findByStatus", "save", "findById", "deleteById"};
        var names = Arrays.stream(iface.getMethods()).map(Method::getName).toList();
        for (String e : expected) {
            assertTrue(names.contains(e), "Expected method missing: " + e);
        }
    }
}

