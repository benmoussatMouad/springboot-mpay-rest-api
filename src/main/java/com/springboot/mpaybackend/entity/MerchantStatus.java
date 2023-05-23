package com.springboot.mpaybackend.entity;

public enum MerchantStatus {
    NON_VERIFIED,
    FILLED_INFO,
    IN_PROGRESS,
    REVIEW,
    ACCEPTED_CONTRACT_SIGNED,
    ACCEPTED,
    SATIM_ACCEPTED,
    VERIFIED,
    SATIM_REVIEW,
    SATIM_REJECTED,
    REJECTED
}
