package com.study.billingservice.exception;

public class BalanceNotEnoughException extends RuntimeException {

    public BalanceNotEnoughException() {
        super();
    }

    public BalanceNotEnoughException(String message) {
        super(message);
    }

    public BalanceNotEnoughException(String message, Throwable cause) {
        super(message, cause);
    }

    public BalanceNotEnoughException(Throwable cause) {
        super(cause);
    }
}
