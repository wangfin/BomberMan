package com.eastmoney.bomberman.controller;

import com.eastmoney.bomberman.model.MoveType;
import com.eastmoney.bomberman.model.RequestParam;
import com.eastmoney.bomberman.model.ResponseData;
import com.eastmoney.bomberman.service.BoomService;
import com.eastmoney.bomberman.service.MoveService;
import com.eastmoney.bomberman.strategy.RandomStrategy;
import org.graalvm.compiler.debug.PathUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Random;

@RequestMapping("player")
public class PlayController2 {

    @Autowired
    MoveService moveService;

    @Autowired
    BoomService boomService;

    private final HashMap<Integer,String> moveMap = new HashMap<Integer,String>(){{
        put(0,"LEFT");
        put(1,"RIGHT");
        put(2,"UP");
        put(3,"DOWN");
        put(4,"STOP");
    }};

    @RequestMapping("/action")
    public Object main(@RequestBody RequestParam requestParam) {

        ResponseData responseData = null;
        try {
            String move = moveService.run(requestParam);
            Boolean boom = boomService.doService(move,requestParam);
            responseData = new ResponseData();
            responseData.setMoveType(move);
            responseData.setReleaseBoom(boom);
        } catch (Exception e) {
            e.printStackTrace();
            RandomStrategy randomStrategy = new RandomStrategy();
            Random random = new Random();
            responseData.setMoveType(moveMap.get(random.nextInt(5)));
            responseData.setReleaseBoom(false);
            return responseData;
        }

        return responseData;
    }
}
