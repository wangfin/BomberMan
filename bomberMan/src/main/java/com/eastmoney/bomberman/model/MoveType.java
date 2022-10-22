package com.eastmoney.bomberman.model;

/**
 * @author lifei
 * @date 2022/10/22
 */
public enum MoveType {

    /**
     * 移动类型枚举
     */
    LEFT(1),
    TOP(2),
    RIGHT(3),
    DOWN(4),
    STOP(5),

    ;

    final Integer code;

    MoveType(Integer code) {
        this.code = code;
    }

}
