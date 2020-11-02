package io.platform.client.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;

public class ApiErrors {

    private List<String> errors;

    public ApiErrors(BindingResult br) {
        this.errors = br.getAllErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
    }

    public List<String> getErrors() {
        return errors;
    }
}
