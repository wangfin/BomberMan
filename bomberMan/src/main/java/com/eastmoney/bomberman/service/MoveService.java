package com.eastmoney.bomberman.service;

import com.eastmoney.bomberman.model.RequestParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MoveService {
    /**
     * 不能走的方向
     * 1. 躲避炸弹（先躲自己的，躲别人）
     * 2. 障碍物判断
     * @return 可以前往的方向
     */
    public List<String> dontMove(RequestParam params) {

        return new ArrayList<>();
    }

    /**
     * 从可选的方向里面选出最优前进方向
     * @param params 入参
     * @param moves 可选方向
     * @return
     */
    public String bestMove(RequestParam params, List<String> moves) {

        return null;
    }

    /**
     * 运行主函数，返回最终的前进方向
     * @param params 入参
     * @return 最终前进的方向
     */
    public String run(RequestParam params){
        List<String> moves = dontMove(params);
        return bestMove(params, moves);
    }
}
