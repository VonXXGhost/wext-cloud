package com.wext.wextservice.domain;

public enum PRState {

    WAITING("waiting"),
    PROCESSING("processing"),
    SUCCESS("success"),
    REJECT("reject");

    public final String value;

    PRState(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static boolean contains(String test) {

        for (PRState c : PRState.values()) {
            if (c.value().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
