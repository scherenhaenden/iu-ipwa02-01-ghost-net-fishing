package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class GhostNetViewModelTest {

    @Test
    void constructorAndGettersSetters_workAsExpected() {
        Instant now = Instant.now();
        GhostNetViewModel vm = new GhostNetViewModel(1L, "Loc", 2.5, NetStatusBusinessLayerEnum.REPORTED, now, "John");

        assertEquals(1L, vm.getId());
        assertEquals("Loc", vm.getLocation());
        assertEquals(2.5, vm.getSize());
        assertEquals(NetStatusBusinessLayerEnum.REPORTED, vm.getStatus());
        assertEquals(now, vm.getCreatedAt());
        assertEquals("John", vm.getRecoveringPersonName());

        // setters
        vm.setId(2L);
        vm.setLocation("L2");
        vm.setSize(null);
        vm.setStatus(null);
        vm.setCreatedAt(null);
        vm.setRecoveringPersonName(null);

        assertEquals(2L, vm.getId());
        assertEquals("L2", vm.getLocation());
        assertNull(vm.getSize());
        assertNull(vm.getStatus());
        assertNull(vm.getCreatedAt());
        assertNull(vm.getRecoveringPersonName());
    }
}

