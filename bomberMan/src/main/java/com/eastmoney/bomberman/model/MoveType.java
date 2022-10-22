package com.eastmoney.bomberman.model;

/**
 * @author lifei
 * @date 2022/10/22
 */
public enum MoveType {

    /**
     * 移动类型枚举
     */
    LEFT(0, "LEFT"),
    TOP(1, "TOP"),
    RIGHT(2, "RIGHT"),
    DOWN(3, "DOWN"),
    STOP(4, "STOP"),

    ;

    final Integer code;
    final String value;

    MoveType(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
