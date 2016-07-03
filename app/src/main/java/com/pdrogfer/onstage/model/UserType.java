package com.pdrogfer.onstage.model;

/**
 * Created by pedrogonzalezferrandez on 03/07/16.
 */
public enum UserType {
    FAN ("fan"),
    MUSICIAN ("musician"),
    VENUE ("VENUE");

    private String userType;

    UserType(String userType) {
        this.userType = userType;
    }
}
