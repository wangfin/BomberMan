package com.eastmoney.bomberman.service;

import com.eastmoney.bomberman.model.MoveType;
import com.eastmoney.bomberman.model.ReleaseBoom;
import com.eastmoney.bomberman.model.RequestParam;
import com.eastmoney.bomberman.model.ResponseData;
import com.eastmoney.bomberman.strategy.Strategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lifei
 * @date 2022/10/22
 */
@Slf4j
@Service
public class PlayerService {

    private Strategy randomStrategy;

    @Autowired
    public void setRandomStrategy(Strategy randomStrategy) {
        this.randomStrategy = randomStrategy;
    }

    public ResponseData doAction(RequestParam params) {
        ResponseData respData = new ResponseData();
        respData.setMoveType(MoveType.values()[randomStrategy.getMoveType(params)].getValue());
        respData.setReleaseBoom(ReleaseBoom.values()[randomStrategy.getReleaseBoom(params)].getValue());
        log.info("respData = {}", respData);
        return respData;
    }

}
