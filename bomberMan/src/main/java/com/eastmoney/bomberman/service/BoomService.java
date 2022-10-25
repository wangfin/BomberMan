package com.eastmoney.bomberman.service;

import com.eastmoney.bomberman.aspect.Constant;
import com.eastmoney.bomberman.model.GameMap;
import com.eastmoney.bomberman.model.RequestParam;
import com.eastmoney.bomberman.model.gamemap.BoomShortInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 判断是否放炸弹实现类
 */
@Slf4j
@Service
public class BoomService {

    //策略开关
    //九步没放直接放
    private String strategy00 = "1";
    //摧毁障碍物
    private String strategy01 = "1";
    //一步内炸敌人
    private String strategy02 = "0";
    //如果附近有一个炸弹了，不放
    private String strategy03 = "1";
    //两步内炸敌人
    private String strategy04 = "1";

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
        List<List<Integer>> lists0 = getLists0(requestParam,1,x,y);
        //获取一步上下左右没有越界的坐标集合
        List<List<Integer>> lists = getLists(requestParam,1,x,y);
        List<List<Integer>> lists4 = getLists(requestParam,2,x,y);
        //获取两步上下左右没有越界的坐标集合
        List<List<Integer>> lists2 = getLists2(requestParam,2,x,y);
        //如果已经有九步没有放炸弹，直接放炸弹
        if (strategy00.equals("1")) {
            if (Constant.noBoomTimes == 9) {
                log.info("执行了十步必须放策略");
                return true;
            }
        }
        BoomShortInfo boomShortInfo = Constant.myBoomHistory.get(Constant.curIndex - 1);
        for (List<Integer> list : lists0) {
            if (boomShortInfo.getCol() == list.get(0) && boomShortInfo.getRow() == list.get(1)){
                log.info("执行了附近1格有炸弹不放策略");
                return true;
            }
        }
        //如果附件已经有一个炸弹，不放
        if (strategy03.equals("1")) {
            for (List<Integer> list : lists2) {
                if (getValue(requestParam,list.get(0),list.get(1)) == '9'){
                    log.info("执行了附近2格有炸弹不放策略");
                    return false;
                }
            }
        }
        //如果靠近了可摧毁障碍物，放炸弹
        if (strategy01.equals("1")) {
            //遍历map判断可摧毁障碍物
            for (List<Integer> list : lists0) {
                if (getValue(requestParam,list.get(0),list.get(1)) == '2'){
                    log.info("执行了炸可摧毁障碍物策略");
                    return true;
                }
            }
        }
        //如果靠近了敌人，放炸弹 1 步
        if (strategy02.equals("1")) {
            //遍历map判断敌人
            for (List<Integer> list : lists) {
                if (getValue(requestParam,list.get(0),list.get(1)) == '8'){
                    log.info("执行了靠近敌人放炸弹策略");
                    return true;
                }
            }
        }
        //如果靠近了敌人，放炸弹 2 步
        if (strategy04.equals("1")) {
            //遍历map判断敌人
            for (List<Integer> list : lists2) {
                if (getValue(requestParam,list.get(0),list.get(1)) == '8'){
                    log.info("执行了2步靠近敌人放炸弹策略");
                    return true;
                }
            }
        }
        return false;
    }

    private List<List<Integer>> getLists0(RequestParam requestParam , int step ,int x,int y) {
        List<List<Integer>> lists = new ArrayList<>(4);
        int s = 0;
        if (!isOver(requestParam,x-step,y)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x-step);
            lists.get(s).add(y);
            s++;
        }
        if (!isOver(requestParam,x+step,y)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x+step);
            lists.get(s).add(y);
            s++;
        }

        if (!isOver(requestParam,x,y+step)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x);
            lists.get(s).add(y+step);
            s++;
        }

        if (!isOver(requestParam,x,y-step)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x);
            lists.get(s).add(y-step);
            s++;
        }
        return lists;
    }

    /**
     * 获取N步之内的上下左右坐标集合 用于周身炸弹
     */
    private List<List<Integer>> getLists(RequestParam requestParam , int step ,int x,int y) {
        List<List<Integer>> lists = new ArrayList<>(8);
        int s = 0;
        if (!isOver(requestParam,x-step,y)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x-step);
            lists.get(s).add(y);
            s++;
        }
        if (!isOver(requestParam,x-step,y-step)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x-step);
            lists.get(s).add(y-step);
            s++;
        }
        if (!isOver(requestParam,x+step,y)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x+step);
            lists.get(s).add(y);
            s++;
        }
        if (!isOver(requestParam,x+step,y+step)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x+step);
            lists.get(s).add(y+step);
            s++;
        }
        if (!isOver(requestParam,x,y+step)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x);
            lists.get(s).add(y+step);
            s++;
        }
        if (!isOver(requestParam,x-step,y+step)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x-step);
            lists.get(s).add(y+step);
            s++;
        }
        if (!isOver(requestParam,x,y-step)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x);
            lists.get(s).add(y-step);
            s++;
        }
        if (!isOver(requestParam,x+step,y-step)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x+step);
            lists.get(s).add(y-step);
            s++;
        }
        return lists;
    }

    /**
     * 获取N步之内的上下左右坐标集合 用于2步内敌人
     */
    private List<List<Integer>> getLists2(RequestParam requestParam ,int step ,int x,int y) {
        List<List<Integer>> lists = new ArrayList<>(8);
        int s = 0;
        if (!isOver(requestParam,x-step,y)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x-step);
            lists.get(s).add(y);
            s++;
        }
        if (!isOver(requestParam,x-step+1,y-step+1)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x-step+1);
            lists.get(s).add(y-step+1);
            s++;
        }
        if (!isOver(requestParam,x+step,y)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x+step);
            lists.get(s).add(y);
            s++;
        }
        if (!isOver(requestParam,x+step-1,y+step-1)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x+step-1);
            lists.get(s).add(y+step-1);
            s++;
        }
        if (!isOver(requestParam,x,y+step)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x);
            lists.get(s).add(y+step);
            s++;
        }
        if (!isOver(requestParam,x-step+1,y+step-1)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x-step+1);
            lists.get(s).add(y+step-1);
            s++;
        }
        if (!isOver(requestParam,x,y-step)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x);
            lists.get(s).add(y-step);
            s++;
        }
        if (!isOver(requestParam,x+step-1,y-step+1)) {
            lists.add(new ArrayList<Integer>());
            lists.get(s).add(x+step-1);
            lists.get(s).add(y-step+1);
            s++;
        }
        return lists;
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
    private char getValue(RequestParam requestParam,int x,int y) {
        List<List<String>> lists = requestParam.getGameMap().getMapList();
        List<String> list = lists.get(y);
        String s = list.get(x);
        return s.charAt(0);
    }


}
