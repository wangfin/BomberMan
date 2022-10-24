package com.eastmoney.bomberman.strategy;

import com.eastmoney.bomberman.model.MoveType;
import com.eastmoney.bomberman.model.ReleaseBoom;
import com.eastmoney.bomberman.model.RequestParam;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author lifei
 * @date 2022/10/22
 */
@Component
public class RandomStrategy implements Strategy {

//    private final Random random = new Random();
//
//    @Override
//    public String getMoveType(RequestParam params) {
//        return MoveType.values()[random.nextInt(5)].getValue();
//    }
//
//    @Override
//    public Boolean getReleaseBoom(RequestParam params) {
//        return ReleaseBoom.values()[random.nextInt(2)].getValue();
//    }

}
