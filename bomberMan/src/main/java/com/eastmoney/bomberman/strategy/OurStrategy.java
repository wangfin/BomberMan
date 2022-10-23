package com.eastmoney.bomberman.strategy;

import com.eastmoney.bomberman.aspect.Constant;
import com.eastmoney.bomberman.model.RequestParam;
import com.eastmoney.bomberman.service.BoomService;
import com.eastmoney.bomberman.service.MoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lifei
 * @date 2022/10/23
 */
@Component
public class OurStrategy implements Strategy {

    MoveService moveService;

    BoomService boomService;

    @Autowired
    public void setMoveService(MoveService moveService) {
        this.moveService = moveService;
    }

    @Autowired
    public void setBoomService(BoomService boomService) {
        this.boomService = boomService;
    }

    @Override
    public String getMoveType(RequestParam params) {
        Constant.curWantMove = moveService.run(params);
        return Constant.curWantMove;
    }

    @Override
    public Boolean getReleaseBoom(RequestParam params) {
        return boomService.doService(Constant.curWantMove, params);
    }

}
