package io.platform.exception;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -1348111296355997096L;

    public BusinessException(String s) {
        super(s);
    }
}
