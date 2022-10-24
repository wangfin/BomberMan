package com.eastmoney.bomberman.service;

import com.eastmoney.bomberman.aspect.Constant;
import com.eastmoney.bomberman.model.GameMap;
import com.eastmoney.bomberman.model.RequestParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 判断是否放炸弹实现类
 */
@Service
public class BoomService {

    //策略开关
    //九步没放直接放
    private String strategy00 = "1";
    //摧毁障碍物
    private String strategy01 = "1";
    //靠近敌人放炸弹
    private String strategy02 = "1";
    //如果附近有一个炸弹了，不放
    private String strategy03 = "1";

    /**
     * 0：    不可炸毁障碍物
     * 1：    地板（可通行）
     * 2：    可炸毁障碍物
     * 3：    道具（可拣取）
     * 8：    玩家
     * 9：    炸弹
     * @param move
     * @param requestParam
     * @return 是否放炸弹
     */

    public Boolean doService(String move, RequestParam requestParam) {
        int slefLocationX = requestParam.getSlefLocationX();
        int slefLocationY = requestParam.getSlefLocationY();
        int x = slefLocationX/64;
        int y = slefLocationY/64;
        //获取上下左右没有越界的坐标集合
        List<List<Integer>> lists = new ArrayList<>(4);
        int s = 0;
        if (!isOver(requestParam,x-1,y)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x-1);
            lists.get(s).add(y);
            s++;
        }
        if (!isOver(requestParam,x+1,y)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x+1);
            lists.get(s).add(y);
            s++;
        }
        if (!isOver(requestParam,x,y+1)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x);
            lists.get(s).add(y+1);
            s++;
        }
        if (!isOver(requestParam,x,y-1)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x);
            lists.get(s).add(y-1);
        }
        //如果已经有九步没有放炸弹，直接放炸弹
        if (strategy00.equals("1")) {
            if (Constant.noBoomTimes.equals("9")) {
                return true;
            }
        }
        //如果附件已经有一个炸弹，不放
        if (strategy03.equals("1")) {
            for (List<Integer> list : lists) {
                if (getValue(requestParam,list.get(0),list.get(1)) == 9){
                    int a = getValue(requestParam,list.get(0),list.get(1));
                    return false;
                }
            }
        }
        //如果靠近了可摧毁障碍物，放炸弹
        if (strategy01.equals("1")) {
            //遍历map判断可摧毁障碍物
            for (List<Integer> list : lists) {
                if (getValue(requestParam,list.get(0),list.get(1)) == 2){
                    return true;
                }
            }
        }
        //如果靠近了敌人，放炸弹
        if (strategy02.equals("1")) {
            //遍历map判断敌人
            for (List<Integer> list : lists) {
                if (getValue(requestParam,list.get(0),list.get(1)) == 8){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否超过数组范围 true 是 false 否
     */
    private Boolean isOver(RequestParam requestParam,int x,int y) {
        GameMap gameMap = requestParam.getGameMap();
        int mapcols = gameMap.getMapCols(); //行
        int mapRows = gameMap.getMapRows(); //列
        if (x < 0 || x >= mapcols) {
            return true;
        }
        if (y < 0 || y >= mapRows) {
            return true;
        }
        return false;
    }

    /**
     * 获取具体坐标的数值
     */
    private int getValue(RequestParam requestParam,int x,int y) {
        List<List<String>> lists = requestParam.getGameMap().getMapList();
        List<String> list = lists.get(y);
        String s = list.get(x);
        return Integer.valueOf(s)/10;
    }


}
