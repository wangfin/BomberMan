package com.eastmoney.bomberman.strategy;

/**
 * @author lifei
 * @date 2022/10/22
 */
public interface Strategy {

    /**
     * 下一步走法
     *
     * @return 返回一个 int，参考 MoveType 枚举值
     */
    Integer getMoveType();

    /**
     * 是否放置炸弹
     *
     * @return 返回一个 int，参考 ReleaseBoom 枚举值
     */
    Integer getReleaseBoom();

}
