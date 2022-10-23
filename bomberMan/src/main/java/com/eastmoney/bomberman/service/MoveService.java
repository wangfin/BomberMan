package com.eastmoney.bomberman.service;

import com.eastmoney.bomberman.model.GameMap;
import com.eastmoney.bomberman.model.MoveType;
import com.eastmoney.bomberman.model.RequestParam;
import com.eastmoney.bomberman.model.gamemap.BoomShortInfo;
import com.eastmoney.bomberman.model.gamemap.ExplodeShortInfo;
import com.eastmoney.bomberman.model.gamemap.MagicBoxShortInfo;
import com.eastmoney.bomberman.model.gamemap.NpcShortInfo;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MoveService {
    /**
     * 不能走的方向
     * 1. 躲避炸弹（先躲自己的，躲别人）
     * 2. 障碍物判断
     *
     * @return 可以前往的方向
     */
    public List<String> dontMove(RequestParam params) {
        // 输出结果
        Map<String, String> canMovesMap = new HashMap<>();
        canMovesMap.put(MoveType.LEFT.getValue(), MoveType.LEFT.getValue());
        canMovesMap.put(MoveType.TOP.getValue(), MoveType.TOP.getValue());
        canMovesMap.put(MoveType.RIGHT.getValue(), MoveType.RIGHT.getValue());
        canMovesMap.put(MoveType.DOWN.getValue(), MoveType.DOWN.getValue());
        canMovesMap.put(MoveType.STOP.getValue(), MoveType.STOP.getValue());

        // 自己所在位置的行列
        // row行数 col列数
        // x列数 y行数
        int selfLocationX = params.getSlefLocationX() / 64;
        int selfLocationY = params.getSlefLocationY() / 64;

        // 地图信息
        GameMap gameMap = params.getGameMap();

        // 1. 越界
        // 判断上下左右四个方向是否越界
        // 正上，行数-1，列数=
        if (isOver(params, selfLocationX, selfLocationY - 1)) {
            canMovesMap.remove(MoveType.TOP.getValue());
        }
        // 正下，行数+1，列数=
        if (isOver(params, selfLocationX, selfLocationY + 1)) {
            canMovesMap.remove(MoveType.DOWN.getValue());
        }
        // 正左，列数-1，行数=
        if (isOver(params, selfLocationX - 1, selfLocationY)) {
            canMovesMap.remove(MoveType.LEFT.getValue());
        }
        // 正右，列数+1，行数=
        if (isOver(params, selfLocationX + 1, selfLocationY)) {
            canMovesMap.remove(MoveType.RIGHT.getValue());
        }

        // 2. 躲避自己的炸弹

        // 3. 躲避其他人的炸弹
        // 当从输入中获取炸弹信息的时候，也就是敌人放下炸弹的第二回合，那么该炸弹会在第三回合，也就是下次爆炸
        // 所以见到炸弹直接逃跑即可

        // 循环遍历目前角色所在位置周边的炸弹信息
        // 正上、正下、正左、正右
        // 左上、右上、左下、右下
        List<BoomShortInfo> boomShortInfoList = gameMap.getActiveBooms();
        for (BoomShortInfo boomShortInfo : boomShortInfoList) {
            // 正上，行数-1，列数=
            if (Objects.equals(boomShortInfo.getRow(), selfLocationY - 1) &&
                    Objects.equals(boomShortInfo.getCol(), selfLocationX)) {
                canMovesMap.remove(MoveType.TOP.getValue());
                canMovesMap.remove(MoveType.STOP.getValue());
            }
            // 正下，行数+1，列数=
            if (Objects.equals(boomShortInfo.getRow(), selfLocationY + 1) &&
                    (Objects.equals(boomShortInfo.getCol(), selfLocationX))) {
                canMovesMap.remove(MoveType.DOWN.getValue());
                canMovesMap.remove(MoveType.STOP.getValue());
            }
            // 正左，列数-1，行数=
            if (Objects.equals(boomShortInfo.getCol(), selfLocationX - 1) &&
                    Objects.equals(boomShortInfo.getRow(), selfLocationY)) {
                canMovesMap.remove(MoveType.LEFT.getValue());
                canMovesMap.remove(MoveType.STOP.getValue());
            }
            // 正右，列数+1，行数=
            if (Objects.equals(boomShortInfo.getCol(), selfLocationX + 1) &&
                    Objects.equals(boomShortInfo.getRow(), selfLocationY)) {
                canMovesMap.remove(MoveType.RIGHT.getValue());
                canMovesMap.remove(MoveType.STOP.getValue());
            }

            // 左上，行数-1，列数-1；右上，行数-1，列数+1
            if (Objects.equals(boomShortInfo.getRow(), selfLocationY - 1) &&
                    (Objects.equals(boomShortInfo.getCol(), selfLocationX - 1) ||
                            Objects.equals(boomShortInfo.getCol(), selfLocationX + 1))) {
                canMovesMap.remove(MoveType.TOP.getValue());
            }
            // 左下，行数-1，列数-1；右下，行数+1，列数+1
            if (Objects.equals(boomShortInfo.getRow(), selfLocationY + 1) &&
                    (Objects.equals(boomShortInfo.getCol(), selfLocationX - 1) ||
                            Objects.equals(boomShortInfo.getCol(), selfLocationX + 1))) {
                canMovesMap.remove(MoveType.DOWN.getValue());
            }
        }

        // 4. 爆炸波判断
        // TODO 爆炸波判断
        List<ExplodeShortInfo> explodeShortInfoList = gameMap.getActiveExplodes();
        for (ExplodeShortInfo explodeShortInfo : explodeShortInfoList) {
            // 正上，行数-1，列数=
            if (Objects.equals(explodeShortInfo.getRow(), selfLocationY - 1) &&
                    Objects.equals(explodeShortInfo.getCol(), selfLocationX)) {
                canMovesMap.remove(MoveType.TOP.getValue());
                canMovesMap.remove(MoveType.STOP.getValue());
            }
            // 正下，行数+1，列数=
            if (Objects.equals(explodeShortInfo.getRow(), selfLocationY + 1) &&
                    (Objects.equals(explodeShortInfo.getCol(), selfLocationX))) {
                canMovesMap.remove(MoveType.DOWN.getValue());
                canMovesMap.remove(MoveType.STOP.getValue());
            }
            // 正左，列数-1，行数=
            if (Objects.equals(explodeShortInfo.getCol(), selfLocationX - 1) &&
                    Objects.equals(explodeShortInfo.getRow(), selfLocationY)) {
                canMovesMap.remove(MoveType.LEFT.getValue());
                canMovesMap.remove(MoveType.STOP.getValue());
            }
            // 正右，列数+1，行数=
            if (Objects.equals(explodeShortInfo.getCol(), selfLocationX + 1) &&
                    Objects.equals(explodeShortInfo.getRow(), selfLocationY)) {
                canMovesMap.remove(MoveType.RIGHT.getValue());
                canMovesMap.remove(MoveType.STOP.getValue());
            }

            // 左上，行数-1，列数-1；右上，行数-1，列数+1
            if (Objects.equals(explodeShortInfo.getRow(), selfLocationY - 1) &&
                    (Objects.equals(explodeShortInfo.getCol(), selfLocationX - 1) ||
                            Objects.equals(explodeShortInfo.getCol(), selfLocationX + 1))) {
                canMovesMap.remove(MoveType.TOP.getValue());
            }
            // 左下，行数-1，列数-1；右下，行数+1，列数+1
            if (Objects.equals(explodeShortInfo.getRow(), selfLocationY + 1) &&
                    (Objects.equals(explodeShortInfo.getCol(), selfLocationX - 1) ||
                            Objects.equals(explodeShortInfo.getCol(), selfLocationX + 1))) {
                canMovesMap.remove(MoveType.DOWN.getValue());
            }
        }

        // 4. 躲避障碍物
        List<List<String>> mapList = gameMap.getMapList();
        // 不可破坏的障碍物，0开头
        // 防止Map数组越界
        if (!isOver(params, selfLocationX, selfLocationY - 1)) {
            // 正上，行数-1，列数=
            if (mapList.get(selfLocationY - 1).get(selfLocationX).charAt(0) == '0' ||
                    mapList.get(selfLocationY - 1).get(selfLocationX).charAt(0) == '2') {
                canMovesMap.remove(MoveType.TOP.getValue());
            }
        }
        if (!isOver(params, selfLocationX, selfLocationY + 1)) {
            // 正下，行数+1，列数=
            if (mapList.get(selfLocationY + 1).get(selfLocationX).charAt(0) == '0' ||
                    mapList.get(selfLocationY + 1).get(selfLocationX).charAt(0) == '2') {
                canMovesMap.remove(MoveType.DOWN.getValue());
            }
        }
        if (!isOver(params, selfLocationX - 1, selfLocationY)) {
            // 正左，列数-1，行数=
            if (mapList.get(selfLocationY).get(selfLocationX - 1).charAt(0) == '0' ||
                    mapList.get(selfLocationY).get(selfLocationX - 1).charAt(0) == '2') {
                canMovesMap.remove(MoveType.LEFT.getValue());
            }
        }
        if (!isOver(params, selfLocationX + 1, selfLocationY)) {
            // 正右，列数+1，行数=
            if (mapList.get(selfLocationY).get(selfLocationX + 1).charAt(0) == '0' ||
                    mapList.get(selfLocationY).get(selfLocationX + 1).charAt(0) == '2') {
                canMovesMap.remove(MoveType.RIGHT.getValue());
            }
        }

        return new ArrayList<>(canMovesMap.values());
    }

    /**
     * 从可选的方向里面选出最优前进方向
     *
     * @param params 入参
     * @param moves  可选方向
     * @return
     */
    public String bestMove(RequestParam params, List<String> moves) {
        //首先确定可以走的方向：List<String>
        String bestMove = moves.get(0);
        double score = 0.0;
        GameMap gameMap = params.getGameMap();
        //将地图进行可走和不可走进行区分
        List<List<String>> mapList = gameMap.getMapList();
        List<int[]> canBrokenWall = new ArrayList<>();
        int[][] map = new int[gameMap.getMapRows()][gameMap.getMapCols()];
        int[][] map1 = new int[gameMap.getMapRows()][gameMap.getMapCols()];
        for (int i = 0; i < gameMap.getMapRows(); i++) {
            for (int j = 0; j < gameMap.getMapCols(); j++) {
                int temp = Integer.valueOf(mapList.get(i).get(j));
                int temp1;
                if (temp < 10) {
                    temp = 1;
                    temp1 = 1;
                } else if (temp < 20) {
                    temp = 0;
                    temp1 = 0;
                } else if (temp < 30) {
                    temp = 1;
                    temp1 = 0;
                    int[] loc = new int[2];
                    loc[0] = i;
                    loc[1] = j;
                    canBrokenWall.add(loc);
                } else {
                    temp = 0;
                    temp1 = 0;
                }
                map[i][j] = temp;
                map1[i][j] = temp1;
            }
        }
        for (String move : moves) {
            double scoreMove = getMoveScore(map, map1, params, move, canBrokenWall);
            if (scoreMove > score) {
                bestMove = move;
                score = scoreMove;
            }
        }
        return bestMove;
    }

    private double getMoveScore(int[][] map, int[][] map1, RequestParam params, String move, List<int[]> canBrokenWall) {
        GameMap gameMap = params.getGameMap();
        Integer slefLocationX = params.getSlefLocationX() / 64;
        Integer slefLocationY = params.getSlefLocationY() / 64;
        List<NpcShortInfo> activeNpcs = gameMap.getActiveNpcs();
        String selfNpcId = params.getSelfNpcId();
        if (move.equals("TOP")) {
            slefLocationY--;
        } else if (move.equals("LEFT")) {
            slefLocationX--;
        } else if (move.equals("RIGHT")) {
            slefLocationX++;
        } else if (move.equals("DOWN")) {
            slefLocationY++;
        }
        int[][] map2 = map;
        int[][] map4 = map1;
        int[][] map3 = map;
        List<MagicBoxShortInfo> activeMagicBoxes = gameMap.getActiveMagicBoxes();
        double magicBoxScore = getMagicBoxScore(map2, activeMagicBoxes, slefLocationX, slefLocationY);
        //广度优先搜索最近的可食用物，并据此获得得分
        double canBrokenScore = getCanBrokenScore(map4, canBrokenWall, slefLocationX, slefLocationY);
        //广度优先搜索搜索可破坏物，并据此获得分数
        double npcScore = getNpcScore(map3, activeNpcs, slefLocationX, slefLocationY, selfNpcId);
        //广度优先搜索搜索最近的人，
        return magicBoxScore + canBrokenScore + npcScore;
    }

    private double getMagicBoxScore(int[][] map, List<MagicBoxShortInfo> activeMagicBoxes, Integer slefLocationX, Integer slefLocationY) {
        int distance = 0;
        if (activeMagicBoxes.size() == 0) return distance;
        for (MagicBoxShortInfo activeMagicBox : activeMagicBoxes) {
            int[] temp = new int[2];
            temp[0] = activeMagicBox.getRow();
            temp[1] = activeMagicBox.getCol();
            distance += getDistance(map, temp, slefLocationX, slefLocationY);
        }
        return 1 / distance * 10000 * 3;
    }

    private double getCanBrokenScore(int[][] map1, List<int[]> canBrokenWalls, Integer slefLocationX, Integer slefLocationY) {
        int distance = 0;
        if (canBrokenWalls.size() == 0) return distance;
        for (int[] canBrokenWall : canBrokenWalls) {
            distance += getDistance(map1, canBrokenWall, slefLocationX, slefLocationY);
        }
        return 1 / distance * 10000 * 1;
    }

    private double getNpcScore(int[][] map, List<NpcShortInfo> activeNpcs, Integer slefLocationX, Integer slefLocationY, String selfNpcId) {
        int distance = 0;
        if (activeNpcs.size() == 0) return distance;
        for (NpcShortInfo activeNpc : activeNpcs) {
            if (activeNpc.getNpcId().equals(selfNpcId)) {
                continue;
            }
            int[] temp = new int[2];
            temp[0] = activeNpc.getRow();
            temp[1] = activeNpc.getCol();
            distance += getDistance(map, temp, slefLocationX, slefLocationY);
        }
        return 1 / distance * 10000 * 0.5;
    }

    /**
     * @param map
     * @param activeMagicBox 目的地址
     * @param slefLocationX  主角位置
     * @param slefLocationY  主角位置
     * @return
     */
    private int getDistance(int[][] map, int[] activeMagicBox, Integer slefLocationX, Integer slefLocationY) {
        int[][] lastPath = new int[map.length][map[0].length];
        lastPath[0][0] = map.length * map[0].length;
        findMinWay(map, slefLocationY, slefLocationX, lastPath, 0, activeMagicBox);
        return lastPath[0][0];
    }

    public static void refreshPath(int[][] map, int[][] lastPath, int nowDepth) {
        //先清空原有路径
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                lastPath[i][j] = 0;
            }
        }

        //再复制新的最短路径
        lastPath[0][0] = nowDepth;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 2) {
                    lastPath[i][j] = map[i][j];
                }
            }
        }
    }

    //判断从当前位置map[i][j]是否能找到路到达终点的路
    //depth表示递归深度（路的长度）
    public static void findMinWay(int[][] map, int i, int j, int[][] lastPath, int nowDepth, int[] activeMagicBox) {
        //比较nowDepth和lastPath[0][0]的大小，如果目前走的距离已经比最短路径长，则返回false
        //但是，不将当前位置置为2，而是不做处理，只是剪掉了当前路径这种选择
        if (nowDepth >= lastPath[0][0]) {
            return;
        } else {//继续尝试
            if (map[activeMagicBox[0]][activeMagicBox[1]] != 1 && i == activeMagicBox[0] && j == activeMagicBox[1]) {//已经找到出口，并且这条路比之前的最短路径都短，则记录路径，并更新最短距离
                lastPath[0][0] = nowDepth;
                refreshPath(map, lastPath, nowDepth);
                return;
            } else {//还在找路
                if (map[i][j] == 0) {//当前路不是障碍物，可以尝试走
                    //先假定当前位置可以走通
                    map[i][j] = 2;
                    //再寻找下一个位置，递归到目标
                    findMinWay(map, i + 1, j, lastPath, nowDepth + 1, activeMagicBox);
                    findMinWay(map, i, j + 1, lastPath, nowDepth + 1, activeMagicBox);
                    findMinWay(map, i - 1, j, lastPath, nowDepth + 1, activeMagicBox);
                    findMinWay(map, i, j - 1, lastPath, nowDepth + 1, activeMagicBox);
                    map[i][j] = 0;
                }
            }
        }
    }

    /**
     * 判断是否超过数组范围 true 是 false 否
     */
    private Boolean isOver(RequestParam requestParam, int x, int y) {
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
     * 运行主函数，返回最终的前进方向
     *
     * @param params 入参
     * @return 最终前进的方向
     */
    public String run(RequestParam params) {
        List<String> moves = dontMove(params);
        return bestMove(params, moves);
    }
}
