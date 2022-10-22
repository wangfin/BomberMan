package com.eastmoney.bomberman.model;

/**
 * @author lifei
 * @date 2022/10/22
 */
public enum ReleaseBoom {

    /**
     * 是否放置炸弹枚举
     */
    TRUE(0, true),
    FALSE(1, false),

    ;

    final Integer code;
    final Boolean value;

    ReleaseBoom(Integer code, Boolean value) {
        this.code = code;
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

}
