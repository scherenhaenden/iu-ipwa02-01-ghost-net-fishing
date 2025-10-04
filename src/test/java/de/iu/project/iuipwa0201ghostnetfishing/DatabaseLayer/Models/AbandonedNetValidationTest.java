package de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AbandonedNetValidationTest {

    private static ValidatorFactory vf;
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        vf = Validation.buildDefaultValidatorFactory();
        validator = vf.getValidator();
    }

    @AfterAll
    static void close() {
        vf.close();
    }

    @Test
    void invalidWhenLocationBlank() {
        AbandonedNetDataLayerModel net = new AbandonedNetDataLayerModel();
        net.setLocation("");
        net.setSize(1.0);
        net.setStatus(NetStatusDataLayerEnum.REPORTED);

        Set<ConstraintViolation<AbandonedNetDataLayerModel>> violations = validator.validate(net);
        assertFalse(violations.isEmpty(), "Expected violations for blank location");
        boolean hasLocation = violations.stream().anyMatch(v -> "location".equals(v.getPropertyPath().toString()));
        assertTrue(hasLocation, "location should be reported as invalid");
    }

    @Test
    void invalidWhenSizeNegative() {
        AbandonedNetDataLayerModel net = new AbandonedNetDataLayerModel();
        net.setLocation("51.0,9.0");
        net.setSize(-5.0);
        net.setStatus(NetStatusDataLayerEnum.REPORTED);

        Set<ConstraintViolation<AbandonedNetDataLayerModel>> violations = validator.validate(net);
        assertFalse(violations.isEmpty(), "Expected violations for negative size");
        boolean hasSize = violations.stream().anyMatch(v -> "size".equals(v.getPropertyPath().toString()));
        assertTrue(hasSize, "size should be reported as invalid");
    }

    @Test
    void invalidWhenCreatedAtFuture() {
        AbandonedNetDataLayerModel net = new AbandonedNetDataLayerModel();
        net.setLocation("51.0,9.0");
        net.setSize(2.0);
        net.setStatus(NetStatusDataLayerEnum.REPORTED);
        Date future = Date.from(Instant.now().plus(2, ChronoUnit.DAYS));
        net.setCreatedAt(future);

        Set<ConstraintViolation<AbandonedNetDataLayerModel>> violations = validator.validate(net);
        assertFalse(violations.isEmpty(), "Expected violations for future createdAt");
        boolean hasCreatedAt = violations.stream().anyMatch(v -> "createdAt".equals(v.getPropertyPath().toString()));
        assertTrue(hasCreatedAt, "createdAt should be reported as invalid");
    }

    @Test
    void validAbandonedNet() {
        AbandonedNetDataLayerModel net = new AbandonedNetDataLayerModel();
        net.setLocation("51.0,9.0");
        net.setSize(0.0);
        net.setStatus(NetStatusDataLayerEnum.REPORTED);
        net.setCreatedAt(new Date());

        Set<ConstraintViolation<AbandonedNetDataLayerModel>> violations = validator.validate(net);
        assertTrue(violations.isEmpty(), () -> "Expected no violations but got: " + violations);
    }

    @Test
    void ghostNetInvalidNegativeSize() {
        GhostNetDataLayerModel g = new GhostNetDataLayerModel();
        g.setLocation("51.0,9.0");
        g.setSize(-1.0);
        g.setStatus(NetStatusDataLayerEnum.REPORTED);

        Set<ConstraintViolation<GhostNetDataLayerModel>> violations = validator.validate(g);
        assertFalse(violations.isEmpty(), "Expected violations for negative size in GhostNet");
        boolean hasSize = violations.stream().anyMatch(v -> "size".equals(v.getPropertyPath().toString()));
        assertTrue(hasSize, "size should be reported as invalid for GhostNet");
    }
}

