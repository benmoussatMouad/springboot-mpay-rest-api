package com.springboot.mpaybackend.entity;

public enum TransactionStatus {
    WAITING,
    ACCEPTED,
    FORM_FILLED,
    ID_REQUESTED,
    OTP_REQUESTED,
    AUTHENTICATED,
    REDIRECTION_CONFIRMED,
    CONFIRMED,
    CANCELED,
    REFUND,
    FAILED,
    CANCELED_BY_CLIENT,
    ABANDONED
}
