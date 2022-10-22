package com.eastmoney.bomberman.service;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lifei
 * @date 2022/10/22
 */
@Service
public class PlayerService {

    List<String> moveTypeList = new LinkedList<>();
    List<Boolean> releaseBoomList = new LinkedList<>();

    static Random random = new Random();

    public PlayerService() {
        moveTypeList.add("LEFT");
        moveTypeList.add("RIGHT");
        moveTypeList.add("TOP");
        moveTypeList.add("DOWN");
        moveTypeList.add("STOP");
        releaseBoomList.add(true);
        releaseBoomList.add(false);
    }

    public Map<String, Object> doAction() {
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("moveType", moveTypeList.get(random.nextInt(5)));
        retMap.put("releaseBoom", releaseBoomList.get(random.nextInt(2)));
        return retMap;
    }

}
