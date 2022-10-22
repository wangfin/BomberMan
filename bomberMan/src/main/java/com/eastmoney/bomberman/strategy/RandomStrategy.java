package com.eastmoney.bomberman.strategy;

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
    public Integer getMoveType() {
        return random.nextInt(5);
    }

    @Override
    public Integer getReleaseBoom() {
        return random.nextInt(2);
    }

}
