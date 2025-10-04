package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IGhostNetBusinessLayerServiceTest {

    @Test
    void interface_declares_expected_methods() {
        Class<?> iface = IGhostNetBusinessLayerService.class;
        List<String> names = Arrays.stream(iface.getMethods()).map(Method::getName).toList();
        assertTrue(names.contains("findAll"));
        assertTrue(names.contains("save"));
        assertTrue(names.contains("findById"));
        assertTrue(names.contains("findByIdOrThrow"));
        assertTrue(names.contains("reserve"));
        assertTrue(names.contains("recover"));
        assertTrue(names.contains("markMissing"));
    }
}

