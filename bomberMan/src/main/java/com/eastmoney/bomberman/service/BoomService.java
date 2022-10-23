package com.eastmoney.bomberman.service;

import com.eastmoney.bomberman.model.GameMapData;
import com.eastmoney.bomberman.model.RequestParam;
import org.apache.catalina.LifecycleState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 判断是否放炸弹实现类
 */
public class BoomService {

    //策略开关
    //九步没放直接放
    private String strategy00 = "1";
    //摧毁障碍物
    private String strategy01 = "1";
    //靠近敌人放炸弹
    private String strategy02 = "1";

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
        int x = slefLocationX/64 - 1;
        int y = slefLocationY/64 - 1;
        //获取移动后位置
        switch (move) {
            case "RIGHT": {
                x++;
                break;
            }
            case "LEFT": {
                x--;
                break;
            }
            case "TOP": {
                y--;
                break;
            }
            case "DOWN": {
                y++;
                break;
            }
            case "STOP": {
                //doNothing
                break;
            }
        }
        //获取上下左右没有越界的坐标集合
        Map<Integer,Integer> map = new HashMap<>();
        if (!isOver(requestParam,x-1,y)) {
            map.put(x-1,y);
        }
        if (!isOver(requestParam,x+1,y)) {
            map.put(x+1,y);
        }
        if (!isOver(requestParam,x,y+1)) {
            map.put(x,y+1);
        }
        if (!isOver(requestParam,x,y-1)) {
            map.put(x,y-1);
        }
        //如果已经有九步没有放炸弹，直接放炸弹
        if (strategy00.equals("1")) {
            return true;
        }
        //如果靠近了可摧毁障碍物，放炸弹
        if (strategy01.equals("1")) {
            //遍历map判断可摧毁障碍物
            for (Integer key : map.keySet()) {
                if (getValue(requestParam,key,map.get(key)) == 2){
                    return true;
                }
            }
        }
        //如果靠近了敌人，放炸弹
        if (strategy01.equals("2")) {
            //遍历map判断敌人
            for (Integer key : map.keySet()) {
                if (getValue(requestParam,key,map.get(key)) == 8){
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
        GameMapData gameMapData = requestParam.getGameMapData();
        int mapcols = gameMapData.getMapCols(); //行
        int mapRows = gameMapData.getMapRows(); //列
        if (x < 0 || x>= mapcols) {
            return true;
        }
        if (y < 0 || y>=mapRows) {
            return true;
        }
        return false;
    }

    /**
     * 获取具体坐标的数值
     */
    private int getValue(RequestParam requestParam,int x,int y) {
        List<List<String>> lists = requestParam.getGameMapData().getMapList();
        List<String> list = lists.get(y);
        String s = list.get(x);
        return Integer.valueOf(s)/10;
    }


}
