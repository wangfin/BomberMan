package com.eastmoney.bomberman.aspect;

import com.eastmoney.bomberman.model.MoveType;
import com.eastmoney.bomberman.model.ReleaseBoom;
import com.eastmoney.bomberman.model.RequestParam;
import com.eastmoney.bomberman.model.ResponseData;
import com.eastmoney.bomberman.model.gamemap.BoomShortInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author lifei
 * @date 2022/10/23
 */
@Slf4j
@Aspect
@Order(1)
@Component
public class PlayerAspect {

    @SneakyThrows
    @Around(value = "execution(public * com.eastmoney.bomberman.controller.PlayController2.main(*)) && args(params)")
    public Object aroundAction(ProceedingJoinPoint joinPoint, RequestParam params) {
        Constant.curIndex++;
        log.info("-------------------- 第 {} 次请求开始 --------------------", Constant.curIndex);

        Constant.curRow = params.getSlefLocationY() / Constant.BLOCK_SIZE;
        Constant.curCol = params.getSlefLocationX() / Constant.BLOCK_SIZE;
        Constant.reqHistory.add(Constant.curIndex, params);
        log.info("请求数据 params = {}", params);

        Object result = joinPoint.proceed(joinPoint.getArgs());

        if (result instanceof ResponseData) {
            ResponseData respData = (ResponseData) result;
            Constant.respHistory.add(Constant.curIndex, respData);
            log.info("响应数据 respData = {}", respData);

            // 维护常量
            if (MoveType.STOP.getValue().equals(respData.getMoveType())) {
                Constant.stopTimes++;
            } else {
                Constant.stopTimes = 0;
            }
            // log.info("更新变量：多少次没走 stopTimes = {}", Constant.stopTimes);

            if (ReleaseBoom.TRUE.getValue().equals(respData.getReleaseBoom())) {
                Constant.noBoomTimes = 0;
                Constant.myBoomHistory.add(new BoomShortInfo(Constant.curRow, Constant.curCol));
            } else {
                Constant.noBoomTimes++;
                Constant.myBoomHistory.add(null);
            }
            // log.info("更新变量：多少次没放炸弹 noBoomTimes = {}", Constant.noBoomTimes);
            // log.info("更新变量：我投放的炸弹位置 myBoomHistory = {}", Constant.myBoomHistory);
        }

        // log.info("-------------------- 第 {} 次请求结束 --------------------", Constant.curIndex);
        return result;
    }

}
