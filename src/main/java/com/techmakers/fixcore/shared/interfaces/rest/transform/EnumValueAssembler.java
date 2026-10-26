package com.techmakers.fixcore.shared.interfaces.rest.transform;

import java.util.Arrays;

public final class EnumValueAssembler {

    private EnumValueAssembler() {
    }

    public static <E extends Enum<E>> E toEnum(Class<E> type, String value) {
        if (value == null) throw new IllegalArgumentException("%s is required".formatted(type.getSimpleName()));
        return Arrays.stream(type.getEnumConstants())
                .filter(constant -> toValue(constant).equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "%s %s is not supported".formatted(type.getSimpleName(), value)));
    }

    public static String toValue(Enum<?> constant) {
        return constant.name().toLowerCase().replace('_', '-');
    }
}
