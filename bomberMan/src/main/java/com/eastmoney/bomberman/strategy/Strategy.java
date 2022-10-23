package com.eastmoney.bomberman.strategy;

import com.eastmoney.bomberman.model.RequestParam;

/**
 * @author lifei
 * @date 2022/10/22
 */
public interface Strategy {

    /**
     * 下一步走法
     *
     * @param params 本次请求参数
     * @return 参考 MoveType
     */
    String getMoveType(RequestParam params);

    /**
     * 是否放置炸弹
     *
     * @param params 本次请求参数
     * @return 参考 ReleaseBoom
     */
    Boolean getReleaseBoom(RequestParam params);

}
