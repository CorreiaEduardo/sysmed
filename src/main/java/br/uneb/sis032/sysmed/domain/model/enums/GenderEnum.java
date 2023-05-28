package br.uneb.sis032.sysmed.domain.model.enums;

import java.util.Arrays;

public enum GenderEnum {
    MALE("H"), FEMALE("M");

    private final String code;

    GenderEnum(String code) {
        this.code = code;
    }

    public static GenderEnum of(String code) {
        return Arrays.stream(values()).filter(it -> it.code.equals(code.toUpperCase())).findFirst().orElse(null);
    }
}
