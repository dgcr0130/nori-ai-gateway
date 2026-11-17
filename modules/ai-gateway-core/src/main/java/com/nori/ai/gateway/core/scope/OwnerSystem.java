package com.nori.ai.gateway.core.scope;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** 등록 여부와 별개로 영속 자료의 시스템 식별자를 보존합니다. */
public record OwnerSystem(@JsonValue String name) {
    public static final OwnerSystem TC = new OwnerSystem("TC");
    public static final OwnerSystem WM = new OwnerSystem("WM");
    public static final OwnerSystem HRM = new OwnerSystem("HRM");
    public OwnerSystem {
        if (name == null || !name.matches("[A-Za-z0-9][A-Za-z0-9_-]{0,63}"))
            throw new IllegalArgumentException("시스템 ID 형식이 잘못되었습니다");
    }
    public static OwnerSystem valueOf(String value) { return new OwnerSystem(value); }
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static OwnerSystem fromJson(tools.jackson.databind.JsonNode value) {
        if (value == null || !value.isString()) throw new IllegalArgumentException("시스템 ID는 문자열이어야 합니다");
        return valueOf(value.asString());
    }
    @Override public String toString() { return name; }
}
