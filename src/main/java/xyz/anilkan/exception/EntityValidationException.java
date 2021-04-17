package xyz.anilkan.exception;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class EntityValidationException extends RuntimeException {
    private final Set<ConstraintViolation<Object>> violations;

    public EntityValidationException(Set<ConstraintViolation<Object>> violations) {
        this.violations = violations;
    }

    public Set<ConstraintViolation<Object>> getViolations() {
        return violations;
    }
}
