package com.pdrogfer.onstage.model;

public enum UserType {
    FAN ("fan"),
    MUSICIAN ("musician"),
    VENUE ("VENUE");

    private String userType;

    UserType(String userType) {
        this.userType = userType;
    }
}
