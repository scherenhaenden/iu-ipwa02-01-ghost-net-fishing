package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import de.iu.project.iuipwa0201ghostnetfishing.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(ResourceNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "NOT_FOUND");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> badRequest(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "VALIDATION_ERROR");
        response.put("details", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private Map<String, String> mapFieldError(FieldError error) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("field", error.getField());
        errorMap.put("msg", error.getDefaultMessage());
        return errorMap;
    }
}
