package com.eastmoney.bomberman.strategy;

import com.eastmoney.bomberman.model.RequestParam;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author lifei
 * @date 2022/10/22
 */
@Component
public class RandomStrategy implements Strategy {

    private final Random random = new Random();

    @Override
    public Integer getMoveType(RequestParam params) {
        return random.nextInt(5);
    }

    @Override
    public Integer getReleaseBoom(RequestParam params) {
        return random.nextInt(2);
    }

}
