package com.security.scanner.entity.enums;

public enum Severity {
    CRITICAL(10),
    HIGH(8),
    MEDIUM(5),
    LOW(2),
    INFO(0);

    private final int weight;

    Severity(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
