package com.example.demo.enums;

public enum LogPermissionEnum {

    MANAGER("MANAGER"),
    ADMIN("ADMIN"),
    USER("USER"),
    GUEST("GUEST");

    private String value;

    private LogPermissionEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String[] stringValues() {
        return new String[]{MANAGER.getValue(), ADMIN.getValue(), USER.getValue(), GUEST.getValue()};
    }

}
