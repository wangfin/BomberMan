package com.eastmoney.bomberman.service;

import com.eastmoney.bomberman.model.GameMap;
import com.eastmoney.bomberman.model.MoveType;
import com.eastmoney.bomberman.model.RequestParam;
import com.eastmoney.bomberman.model.gamemap.BoomShortInfo;
import com.eastmoney.bomberman.model.gamemap.ExplodeShortInfo;
import com.eastmoney.bomberman.model.gamemap.MagicBoxShortInfo;
import com.eastmoney.bomberman.model.gamemap.NpcShortInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.eastmoney.bomberman.aspect.Constant.curIndex;
import static com.eastmoney.bomberman.aspect.Constant.myBoomHistory;
@Slf4j
@Service
public class MoveService {
    /**
     * 不能走的方向
     * 1. 躲避炸弹（先躲自己的，躲别人）
     * 2. 障碍物判断
     *
     * @return 可以前往的方向
     */
    public List<String> dontMove(Boolean isBoom, RequestParam params) {
        // 输出结果
        Map<String, String> canMovesMap = new HashMap<>();
        canMovesMap.put(MoveType.STOP.getValue(), MoveType.STOP.getValue());
        canMovesMap.put(MoveType.LEFT.getValue(), MoveType.LEFT.getValue());
        canMovesMap.put(MoveType.TOP.getValue(), MoveType.TOP.getValue());
        canMovesMap.put(MoveType.RIGHT.getValue(), MoveType.RIGHT.getValue());
        canMovesMap.put(MoveType.DOWN.getValue(), MoveType.DOWN.getValue());

        // 如果实在是没地方可走了，那么就用这个
        Map<String, String> maybeCanMovesMap = new HashMap<>();
        maybeCanMovesMap.put(MoveType.STOP.getValue(), MoveType.STOP.getValue());
        maybeCanMovesMap.put(MoveType.LEFT.getValue(), MoveType.LEFT.getValue());
        maybeCanMovesMap.put(MoveType.TOP.getValue(), MoveType.TOP.getValue());
        maybeCanMovesMap.put(MoveType.RIGHT.getValue(), MoveType.RIGHT.getValue());
        maybeCanMovesMap.put(MoveType.DOWN.getValue(), MoveType.DOWN.getValue());

        // 自己所在位置的行列
        // row行数 col列数
        // x列数 y行数
        int selfLocationX = params.getSlefLocationX() / 64;
        int selfLocationY = params.getSlefLocationY() / 64;
        log.info("当前所在位置：selfLocationY = {}, selfLocationX = {}", selfLocationY, selfLocationX);
        // 地图信息
        GameMap gameMap = params.getGameMap();
        List<List<String>> mapList = gameMap.getMapList();

        // 1. 越界
        // 判断上下左右四个方向是否越界
        // 正上，行数-1，列数=
        if (isOver(params, selfLocationX, selfLocationY - 1)) {
            canMovesMap.remove(MoveType.TOP.getValue());
            maybeCanMovesMap.remove(MoveType.TOP.getValue());
        }
        // 正下，行数+1，列数=
        if (isOver(params, selfLocationX, selfLocationY + 1)) {
            canMovesMap.remove(MoveType.DOWN.getValue());
            maybeCanMovesMap.remove(MoveType.DOWN.getValue());
        }
        // 正左，列数-1，行数=
        if (isOver(params, selfLocationX - 1, selfLocationY)) {
            canMovesMap.remove(MoveType.LEFT.getValue());
            maybeCanMovesMap.remove(MoveType.LEFT.getValue());
        }
        // 正右，列数+1，行数=
        if (isOver(params, selfLocationX + 1, selfLocationY)) {
            canMovesMap.remove(MoveType.RIGHT.getValue());
            maybeCanMovesMap.remove(MoveType.RIGHT.getValue());
        }
        System.out.println("越界" + new ArrayList<>(canMovesMap.values()));

        // 2. 躲避自己的炸弹
        // 第一回合放炸弹，第二回合无事发生，第三回合爆炸（爆炸波持续一回合）
        // 第N回合的炸弹信息只有我们自己知道
        // 第N-1回合的炸弹信息会算到下面的炸弹信息
        // 第N-2回合的爆炸波会算到下面的爆炸波信息

        // 如果本回合放炸弹了
        if (isBoom) {
            log.info("第 curIndex={} 回合，我们释放炸弹，炸弹位置 selfLocationY = {}, selfLocationX = {}", curIndex, selfLocationY, selfLocationX);
            // 释放了炸弹，开始判断，我们当前就在炸弹点上
            // 且炸弹的上下左右也不能站，所以需要校验下个回合的位置，如果下下个回合还只能站在炸弹的上下左右，那么下回合这个方向就不能走

            // 只要释放了炸弹，就不能停留
            canMovesMap.remove(MoveType.STOP.getValue());
            maybeCanMovesMap.remove(MoveType.STOP.getValue());

            // row行数 col列数
            // x列数 y行数

            List<Boolean> canThisWayMove = new ArrayList<>();

            // 2.1.1 这回合向上走 当前位置Y-1，当前位置 X，判断这个位置的上左右是否能走通（障碍物，可破坏的障碍物，炸弹的爆炸范围都不能走）
            canThisWayMove.add(0, Boolean.TRUE);
            canThisWayMove.add(1, Boolean.TRUE);
            canThisWayMove.add(2, Boolean.TRUE);
            // 上 Y-2，X；左Y-1，X-1；右Y-1，X+1
            // 向上之后能否继续向上走
            if (!isOver(params, selfLocationX, selfLocationY - 2)) {
                if (mapList.get(selfLocationY - 2).get(selfLocationX).charAt(0) == '0' ||
                        mapList.get(selfLocationY - 2).get(selfLocationX).charAt(0) == '2') {
                    canThisWayMove.set(0, Boolean.FALSE);
                }
            }
            // 炸弹
//            if ((!isOver(params, selfLocationX, selfLocationY - 3) && mapList.get(selfLocationY - 3).get(selfLocationX).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX - 1, selfLocationY - 2) && mapList.get(selfLocationY - 2).get(selfLocationX - 1).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX + 1, selfLocationY - 2) && mapList.get(selfLocationY - 2).get(selfLocationX + 1).charAt(0) == '9')) {
//                canMovesMap.remove(MoveType.TOP.getValue());
//                canMovesMap.remove(MoveType.STOP.getValue());
//            }

            // 向上之后能否继续向左走
            if (!isOver(params, selfLocationX - 1, selfLocationY - 1)) {
                if (mapList.get(selfLocationY - 1).get(selfLocationX - 1).charAt(0) == '0' ||
                        mapList.get(selfLocationY - 1).get(selfLocationX - 1).charAt(0) == '2') {
                    canThisWayMove.set(1, Boolean.FALSE);
                }
            }
            // 炸弹
//            if ((!isOver(params, selfLocationX - 2, selfLocationY - 1) && mapList.get(selfLocationY - 1).get(selfLocationX - 2).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX - 1, selfLocationY) && mapList.get(selfLocationY).get(selfLocationX - 1).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX - 1, selfLocationY - 2) && mapList.get(selfLocationY - 2).get(selfLocationX - 1).charAt(0) == '9')) {
//                canMovesMap.remove(MoveType.TOP.getValue());
//                canMovesMap.remove(MoveType.STOP.getValue());
//            }

            // 障碍物
            if (!isOver(params, selfLocationX + 1, selfLocationY - 1)) {
                if (mapList.get(selfLocationY - 1).get(selfLocationX + 1).charAt(0) == '0' ||
                        mapList.get(selfLocationY - 1).get(selfLocationX + 1).charAt(0) == '2' ) {
                    canThisWayMove.set(2, Boolean.FALSE);
                }
            }
            // 炸弹
//            if ((!isOver(params, selfLocationX + 1, selfLocationY) && mapList.get(selfLocationY).get(selfLocationX + 1).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX + 1, selfLocationY - 2) && mapList.get(selfLocationY - 2).get(selfLocationX + 1).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX + 2, selfLocationY - 1) && mapList.get(selfLocationY - 1).get(selfLocationX + 2).charAt(0) == '9')) {
//                canMovesMap.remove(MoveType.TOP.getValue());
//                canMovesMap.remove(MoveType.STOP.getValue());
//            }

            // 综合判断
            if (!canThisWayMove.get(0) && !canThisWayMove.get(1) && !canThisWayMove.get(2)){
                canMovesMap.remove(MoveType.TOP.getValue());
                maybeCanMovesMap.remove(MoveType.TOP.getValue());

            }

            // 2.2.2 这回合向下走 当前位置Y+1，当前位置 X，判断这个位置的下左右是否能走通（障碍物，可破坏的障碍物，炸弹的爆炸范围都不能走）
            canThisWayMove.set(0, Boolean.TRUE);
            canThisWayMove.set(1, Boolean.TRUE);
            canThisWayMove.set(2, Boolean.TRUE);
            // 下 Y+2，X；左Y+1，X-1；右Y+1，X+1
            // 障碍物
            if (!isOver(params, selfLocationX, selfLocationY + 2)) {
                if (mapList.get(selfLocationY + 2).get(selfLocationX).charAt(0) == '0' ||
                        mapList.get(selfLocationY + 2).get(selfLocationX).charAt(0) == '2') {
                    canThisWayMove.set(0, Boolean.FALSE);
                }
            }
            // 炸弹
//            if ((!isOver(params, selfLocationX, selfLocationY + 3) && mapList.get(selfLocationY + 3).get(selfLocationX).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX - 1, selfLocationY + 2) && mapList.get(selfLocationY + 2).get(selfLocationX - 1).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX + 1, selfLocationY + 2) && mapList.get(selfLocationY + 2).get(selfLocationX + 1).charAt(0) == '9')) {
//                canMovesMap.remove(MoveType.DOWN.getValue());
//                canMovesMap.remove(MoveType.STOP.getValue());
//            }

            // 障碍物
            if (!isOver(params, selfLocationX - 1, selfLocationY + 1)) {
                if (mapList.get(selfLocationY + 1).get(selfLocationX - 1).charAt(0) == '0' ||
                        mapList.get(selfLocationY + 1).get(selfLocationX - 1).charAt(0) == '2') {
                    canThisWayMove.set(1, Boolean.FALSE);
                }
            }
            // 炸弹
//            if ((!isOver(params, selfLocationX - 2, selfLocationY + 1) && mapList.get(selfLocationY + 1).get(selfLocationX - 2).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX - 1, selfLocationY) && mapList.get(selfLocationY).get(selfLocationX - 1).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX - 1, selfLocationY + 2) && mapList.get(selfLocationY + 2).get(selfLocationX - 1).charAt(0) == '9')) {
//                canMovesMap.remove(MoveType.DOWN.getValue());
//                canMovesMap.remove(MoveType.STOP.getValue());
//            }

            // 障碍物
            if (!isOver(params, selfLocationX + 1, selfLocationY + 1)) {
                if (mapList.get(selfLocationY + 1).get(selfLocationX + 1).charAt(0) == '0' ||
                        mapList.get(selfLocationY + 1).get(selfLocationX + 1).charAt(0) == '2') {
                    canThisWayMove.set(2, Boolean.FALSE);
                }
            }
            // 炸弹
//            if ((!isOver(params, selfLocationX + 1, selfLocationY) && mapList.get(selfLocationY).get(selfLocationX + 1).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX + 1, selfLocationY + 2) && mapList.get(selfLocationY + 2).get(selfLocationX + 1).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX + 2, selfLocationY + 1) && mapList.get(selfLocationY + 1).get(selfLocationX + 2).charAt(0) == '9')) {
//                canMovesMap.remove(MoveType.DOWN.getValue());
//                canMovesMap.remove(MoveType.STOP.getValue());
//            }

            // 综合判断
            if (!canThisWayMove.get(0) && !canThisWayMove.get(1) && !canThisWayMove.get(2)){
                canMovesMap.remove(MoveType.DOWN.getValue());
                maybeCanMovesMap.remove(MoveType.DOWN.getValue());
            }

            // 2.2.3 这回合向左走 当前位置Y，当前位置 X - 1，判断这个位置的左上下是否能走通（障碍物，可破坏的障碍物，炸弹的爆炸范围都不能走）
            canThisWayMove.set(0, Boolean.TRUE);
            canThisWayMove.set(1, Boolean.TRUE);
            canThisWayMove.set(2, Boolean.TRUE);
            // 左 X-2，Y；上X-1，Y-1；下X-1，Y+1
            // 障碍物
            if (!isOver(params, selfLocationX - 2, selfLocationY)) {
                if (mapList.get(selfLocationY).get(selfLocationX - 2).charAt(0) == '0' ||
                        mapList.get(selfLocationY).get(selfLocationX - 2).charAt(0) == '2') {
                    canThisWayMove.set(0, Boolean.FALSE);
                }
            }
            // 炸弹
//            if ((!isOver(params, selfLocationX - 3, selfLocationY) && mapList.get(selfLocationY).get(selfLocationX - 3).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX - 2, selfLocationY + 1) && mapList.get(selfLocationY + 1).get(selfLocationX - 2).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX - 2, selfLocationY - 1) && mapList.get(selfLocationY - 1).get(selfLocationX - 2).charAt(0) == '9')) {
//                canMovesMap.remove(MoveType.LEFT.getValue());
//                canMovesMap.remove(MoveType.STOP.getValue());
//            }

            // 障碍物
            if (!isOver(params, selfLocationX - 1, selfLocationY - 1)) {
                if (mapList.get(selfLocationY - 1).get(selfLocationX - 1).charAt(0) == '0' ||
                        mapList.get(selfLocationY - 1).get(selfLocationX - 1).charAt(0) == '2') {
                    canThisWayMove.set(1, Boolean.FALSE);
                }
            }
            // 炸弹
//            if ((!isOver(params, selfLocationX - 1, selfLocationY - 2) && mapList.get(selfLocationY - 2).get(selfLocationX - 1).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX - 2, selfLocationY - 1) && mapList.get(selfLocationY - 1).get(selfLocationX - 2).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX, selfLocationY - 1) && mapList.get(selfLocationY - 1).get(selfLocationX).charAt(0) == '9')) {
//                canMovesMap.remove(MoveType.LEFT.getValue());
//                canMovesMap.remove(MoveType.STOP.getValue());
//            }

            // 障碍物
            if (!isOver(params, selfLocationX - 1, selfLocationY + 1)) {
                if (mapList.get(selfLocationY + 1).get(selfLocationX - 1).charAt(0) == '0' ||
                        mapList.get(selfLocationY + 1).get(selfLocationX - 1).charAt(0) == '2') {
                    canThisWayMove.set(2, Boolean.FALSE);
                }
            }
            // 炸弹
//            if ((!isOver(params, selfLocationX - 1, selfLocationY + 2) && mapList.get(selfLocationY + 2).get(selfLocationX - 1).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX - 2, selfLocationY + 1) && mapList.get(selfLocationY + 1).get(selfLocationX - 2).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX, selfLocationY + 1) && mapList.get(selfLocationY + 1).get(selfLocationX).charAt(0) == '9')) {
//                canMovesMap.remove(MoveType.LEFT.getValue());
//                canMovesMap.remove(MoveType.STOP.getValue());
//            }

            // 综合判断
            if (!canThisWayMove.get(0) && !canThisWayMove.get(1) && !canThisWayMove.get(2)){
                canMovesMap.remove(MoveType.LEFT.getValue());
                maybeCanMovesMap.remove(MoveType.LEFT.getValue());
            }

            // 2.2.4 这回合向右走 当前位置Y，当前位置 X + 1，判断这个位置的右上下是否能走通（障碍物，可破坏的障碍物，炸弹的爆炸范围都不能走）
            canThisWayMove.set(0, Boolean.TRUE);
            canThisWayMove.set(1, Boolean.TRUE);
            canThisWayMove.set(2, Boolean.TRUE);
            // 右 X+2，Y；上X+1，Y-1；下X+1，Y+1
            // 障碍物
            if (!isOver(params, selfLocationX + 2, selfLocationY)) {
                if (mapList.get(selfLocationY).get(selfLocationX + 2).charAt(0) == '0' ||
                        mapList.get(selfLocationY).get(selfLocationX + 2).charAt(0) == '2') {
                    canThisWayMove.set(0, Boolean.FALSE);
                }
            }
            // 炸弹
//            if ((!isOver(params, selfLocationX + 3, selfLocationY) && mapList.get(selfLocationY).get(selfLocationX + 3).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX + 2, selfLocationY + 1) && mapList.get(selfLocationY + 1).get(selfLocationX + 2).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX + 2, selfLocationY - 1) && mapList.get(selfLocationY - 1).get(selfLocationX + 2).charAt(0) == '9')) {
//                canMovesMap.remove(MoveType.RIGHT.getValue());
//                canMovesMap.remove(MoveType.STOP.getValue());
//            }

            // 障碍物
            if (!isOver(params, selfLocationX + 1, selfLocationY - 1)) {
                if (mapList.get(selfLocationY - 1).get(selfLocationX + 1).charAt(0) == '0' ||
                        mapList.get(selfLocationY - 1).get(selfLocationX + 1).charAt(0) == '2') {
                    canThisWayMove.set(1, Boolean.FALSE);
                }
            }
            // 炸弹
//            if ((!isOver(params, selfLocationX + 1, selfLocationY - 2) && mapList.get(selfLocationY - 2).get(selfLocationX + 1).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX + 2, selfLocationY - 1) && mapList.get(selfLocationY - 1).get(selfLocationX + 2).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX, selfLocationY - 1) && mapList.get(selfLocationY - 1).get(selfLocationX).charAt(0) == '9')) {
//                canMovesMap.remove(MoveType.RIGHT.getValue());
//                canMovesMap.remove(MoveType.STOP.getValue());
//            }

            // 障碍物
            if (!isOver(params, selfLocationX + 1, selfLocationY + 1)) {
                if (mapList.get(selfLocationY + 1).get(selfLocationX + 1).charAt(0) == '0' ||
                        mapList.get(selfLocationY + 1).get(selfLocationX + 1).charAt(0) == '2') {
                    canThisWayMove.set(2, Boolean.FALSE);
                }
            }
            // 炸弹
//            if ((!isOver(params, selfLocationX + 1, selfLocationY + 2) && mapList.get(selfLocationY + 2).get(selfLocationX + 1).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX + 2, selfLocationY + 1) && mapList.get(selfLocationY + 1).get(selfLocationX + 2).charAt(0) == '9') ||
//                    (!isOver(params, selfLocationX, selfLocationY + 1) && mapList.get(selfLocationY + 1).get(selfLocationX).charAt(0) == '9')) {
//                canMovesMap.remove(MoveType.LEFT.getValue());
//                canMovesMap.remove(MoveType.STOP.getValue());
//            }

            // 综合判断
            if (!canThisWayMove.get(0) && !canThisWayMove.get(1) && !canThisWayMove.get(2)){
                canMovesMap.remove(MoveType.RIGHT.getValue());
                maybeCanMovesMap.remove(MoveType.RIGHT.getValue());
            }

        }
        System.out.println("躲自己炸弹" + new ArrayList<>(canMovesMap.values()));


        // 3. 躲避其他人的炸弹
        // 当从输入中获取炸弹信息的时候，也就是敌人放下炸弹的第二回合，那么该炸弹会在第三回合，也就是下次爆炸
        // 所以见到炸弹直接逃跑即可

        // 循环遍历目前角色所在位置周边的炸弹信息
        // 正上、正下、正左、正右
        // 左上、右上、左下、右下
        List<BoomShortInfo> boomShortInfoList = gameMap.getActiveBooms();
        log.info("第 curIndex={} 回合，炸弹信息 boomShortInfoList = {}", curIndex, boomShortInfoList);
        for (BoomShortInfo boomShortInfo : boomShortInfoList) {
            // 判断下是不是自己上个回合放的炸弹
            if (null != myBoomHistory.get(curIndex - 1) &&
                    Objects.equals(boomShortInfo.getRow(), myBoomHistory.get(curIndex - 1).getRow()) &&
                    Objects.equals(boomShortInfo.getCol(), myBoomHistory.get(curIndex - 1).getCol())){

                // 正上，行数-1，列数=
                if (Objects.equals(boomShortInfo.getRow(), selfLocationY - 1) &&
                        Objects.equals(boomShortInfo.getCol(), selfLocationX)) {
                    canMovesMap.remove(MoveType.TOP.getValue());
                    canMovesMap.remove(MoveType.STOP.getValue());
                    maybeCanMovesMap.remove(MoveType.TOP.getValue());
                    maybeCanMovesMap.remove(MoveType.STOP.getValue());
                }
                // 正下，行数+1，列数=
                if (Objects.equals(boomShortInfo.getRow(), selfLocationY + 1) &&
                        (Objects.equals(boomShortInfo.getCol(), selfLocationX))) {
                    canMovesMap.remove(MoveType.DOWN.getValue());
                    canMovesMap.remove(MoveType.STOP.getValue());
                    maybeCanMovesMap.remove(MoveType.DOWN.getValue());
                    maybeCanMovesMap.remove(MoveType.STOP.getValue());
                }
                // 正左，列数-1，行数=
                if (Objects.equals(boomShortInfo.getCol(), selfLocationX - 1) &&
                        Objects.equals(boomShortInfo.getRow(), selfLocationY)) {
                    canMovesMap.remove(MoveType.LEFT.getValue());
                    canMovesMap.remove(MoveType.STOP.getValue());
                    maybeCanMovesMap.remove(MoveType.LEFT.getValue());
                    maybeCanMovesMap.remove(MoveType.STOP.getValue());
                }
                // 正右，列数+1，行数=
                if (Objects.equals(boomShortInfo.getCol(), selfLocationX + 1) &&
                        Objects.equals(boomShortInfo.getRow(), selfLocationY)) {
                    canMovesMap.remove(MoveType.RIGHT.getValue());
                    canMovesMap.remove(MoveType.STOP.getValue());
                    maybeCanMovesMap.remove(MoveType.RIGHT.getValue());
                    maybeCanMovesMap.remove(MoveType.STOP.getValue());
                }

                // 左上，行数-1，列数-1；右上，行数-1，列数+1
                if (Objects.equals(boomShortInfo.getRow(), selfLocationY - 1) &&
                        (Objects.equals(boomShortInfo.getCol(), selfLocationX - 1) ||
                                Objects.equals(boomShortInfo.getCol(), selfLocationX + 1))) {
                    canMovesMap.remove(MoveType.TOP.getValue());
                    maybeCanMovesMap.remove(MoveType.TOP.getValue());
                }
                // 左下，行数-1，列数-1；右下，行数+1，列数+1
                if (Objects.equals(boomShortInfo.getRow(), selfLocationY + 1) &&
                        (Objects.equals(boomShortInfo.getCol(), selfLocationX - 1) ||
                                Objects.equals(boomShortInfo.getCol(), selfLocationX + 1))) {
                    canMovesMap.remove(MoveType.DOWN.getValue());
                    maybeCanMovesMap.remove(MoveType.DOWN.getValue());
                }
            } else {
                // 不是自己的炸弹，就嗯闯
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
        }
        System.out.println("躲别人炸弹" + new ArrayList<>(canMovesMap.values()));

        // 4. 爆炸波判断
        List<ExplodeShortInfo> explodeShortInfoList = gameMap.getActiveExplodes();
        log.info("第 curIndex={} 回合，爆炸波信息 explodeShortInfoList = {}", curIndex, explodeShortInfoList);
        for (ExplodeShortInfo explodeShortInfo : explodeShortInfoList) {
            // 判断是不是自己炸弹的爆炸波
            if (null != myBoomHistory.get(curIndex - 2) &&
                    Objects.equals(explodeShortInfo.getRow(), myBoomHistory.get(curIndex - 2).getRow()) &&
                    Objects.equals(explodeShortInfo.getCol(), myBoomHistory.get(curIndex - 2).getCol())){
                // 4.1 爆炸源在角色四角
                // 爆炸源在左上、右上
                // 爆炸源在左上，爆炸波源的row = 当前位置Y-1；col =当前位置的X-1
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY - 1) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX - 1)) {
                    canMovesMap.remove(MoveType.TOP.getValue());
                    canMovesMap.remove(MoveType.LEFT.getValue());
                    maybeCanMovesMap.remove(MoveType.TOP.getValue());
                    maybeCanMovesMap.remove(MoveType.LEFT.getValue());
                }
                // 爆炸源在右上，爆炸波源的row = 当前位置Y-1；col=当前位置的X+1
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY - 1) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX + 1)) {
                    canMovesMap.remove(MoveType.TOP.getValue());
                    canMovesMap.remove(MoveType.RIGHT.getValue());
                    maybeCanMovesMap.remove(MoveType.TOP.getValue());
                    maybeCanMovesMap.remove(MoveType.RIGHT.getValue());
                }
                // 爆炸源在左下、右下
                // 爆炸源在左下，爆炸波源的row = 当前位置Y+1；col=当前位置的X-1
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY + 1) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX - 1)) {
                    canMovesMap.remove(MoveType.DOWN.getValue());
                    canMovesMap.remove(MoveType.LEFT.getValue());
                    maybeCanMovesMap.remove(MoveType.DOWN.getValue());
                    maybeCanMovesMap.remove(MoveType.LEFT.getValue());
                }
                // 爆炸源在右下，爆炸波源的row = 当前位置Y+1；col=当前位置的X+1
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY + 1) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX + 1)) {
                    canMovesMap.remove(MoveType.DOWN.getValue());
                    canMovesMap.remove(MoveType.RIGHT.getValue());
                    maybeCanMovesMap.remove(MoveType.DOWN.getValue());
                    maybeCanMovesMap.remove(MoveType.RIGHT.getValue());
                }

                // 4.2 爆炸源在角色正方向上
                // 正上方，爆炸波源的row = 当前位置Y-1；col = 当前位置的X
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY - 2) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX)) {
                    // 覆盖范围只有1格，不能向上走或者停留
                    canMovesMap.remove(MoveType.TOP.getValue());
                    maybeCanMovesMap.remove(MoveType.TOP.getValue());
                }
                // 正下方，爆炸波源的row = 当前位置Y+1；col = 当前位置的X
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY + 2) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX)) {
                    // 覆盖范围只有1格，不能向下走或者停留
                    canMovesMap.remove(MoveType.DOWN.getValue());
                    maybeCanMovesMap.remove(MoveType.DOWN.getValue());
                }
                // 正左方，爆炸波源的row = 当前位置Y；col = 当前位置的X - 1
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX - 2)) {
                    // 覆盖范围只有1格，不能向左走或者停留
                    canMovesMap.remove(MoveType.LEFT.getValue());
                    maybeCanMovesMap.remove(MoveType.LEFT.getValue());
                }
                // 正右方，爆炸波源的row = 当前位置Y；col = 当前位置的X + 1
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX + 2)) {
                    // 覆盖范围只有1格，不能向右走或者停留
                    canMovesMap.remove(MoveType.RIGHT.getValue());
                    maybeCanMovesMap.remove(MoveType.RIGHT.getValue());
                }

            } else {
                // 不是自己的
                // 4.1 爆炸源在角色四角
                // 爆炸源在左上、右上
                // 爆炸源在左上，爆炸波源的row = 当前位置Y-1；col =当前位置的X-1
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY - 1) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX - 1)) {
                    canMovesMap.remove(MoveType.TOP.getValue());
                    canMovesMap.remove(MoveType.LEFT.getValue());
                }
                // 爆炸源在右上，爆炸波源的row = 当前位置Y-1；col=当前位置的X+1
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY - 1) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX + 1)) {
                    canMovesMap.remove(MoveType.TOP.getValue());
                    canMovesMap.remove(MoveType.RIGHT.getValue());
                }
                // 爆炸源在左下、右下
                // 爆炸源在左下，爆炸波源的row = 当前位置Y+1；col=当前位置的X-1
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY + 1) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX - 1)) {
                    canMovesMap.remove(MoveType.DOWN.getValue());
                    canMovesMap.remove(MoveType.LEFT.getValue());
                }
                // 爆炸源在右下，爆炸波源的row = 当前位置Y+1；col=当前位置的X+1
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY + 1) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX + 1)) {
                    canMovesMap.remove(MoveType.DOWN.getValue());
                    canMovesMap.remove(MoveType.RIGHT.getValue());
                }

                // 4.2 爆炸源在角色正方向上
                // 正上方，爆炸波源的row = 当前位置Y-1；col = 当前位置的X
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY - 2) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX)) {
                    // 覆盖范围只有1格，不能向上走或者停留
                    canMovesMap.remove(MoveType.TOP.getValue());
                }
                // 正下方，爆炸波源的row = 当前位置Y+1；col = 当前位置的X
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY + 2) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX)) {
                    // 覆盖范围只有1格，不能向下走或者停留
                    canMovesMap.remove(MoveType.DOWN.getValue());
                }
                // 正左方，爆炸波源的row = 当前位置Y；col = 当前位置的X - 1
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX - 2)) {
                    // 覆盖范围只有1格，不能向左走或者停留
                    canMovesMap.remove(MoveType.LEFT.getValue());
                }
                // 正右方，爆炸波源的row = 当前位置Y；col = 当前位置的X + 1
                if (Objects.equals(explodeShortInfo.getRow(), selfLocationY) &&
                        Objects.equals(explodeShortInfo.getCol(), selfLocationX + 2)) {
                    // 覆盖范围只有1格，不能向右走或者停留
                    canMovesMap.remove(MoveType.RIGHT.getValue());
                }
            }
        }
        System.out.println("躲爆炸波" + new ArrayList<>(canMovesMap.values()));

        // 5. 躲避障碍物
        // 不可破坏的障碍物，0开头
        // 防止Map数组越界
        if (!isOver(params, selfLocationX, selfLocationY - 1)) {
            // 正上，行数-1，列数=
            if (mapList.get(selfLocationY - 1).get(selfLocationX).charAt(0) == '0' ||
                    mapList.get(selfLocationY - 1).get(selfLocationX).charAt(0) == '2') {
                canMovesMap.remove(MoveType.TOP.getValue());
                maybeCanMovesMap.remove(MoveType.TOP.getValue());
            }
            if (mapList.get(selfLocationY - 1).get(selfLocationX).charAt(0) == '8'){
                canMovesMap.remove(MoveType.TOP.getValue());
            }
        }
        if (!isOver(params, selfLocationX, selfLocationY + 1)) {
            // 正下，行数+1，列数=
            if (mapList.get(selfLocationY + 1).get(selfLocationX).charAt(0) == '0' ||
                    mapList.get(selfLocationY + 1).get(selfLocationX).charAt(0) == '2' ) {
                canMovesMap.remove(MoveType.DOWN.getValue());
                maybeCanMovesMap.remove(MoveType.DOWN.getValue());
            }
            if (mapList.get(selfLocationY + 1).get(selfLocationX).charAt(0) == '8'){
                canMovesMap.remove(MoveType.DOWN.getValue());
            }
        }
        if (!isOver(params, selfLocationX - 1, selfLocationY)) {
            // 正左，列数-1，行数=
            if (mapList.get(selfLocationY).get(selfLocationX - 1).charAt(0) == '0' ||
                    mapList.get(selfLocationY).get(selfLocationX - 1).charAt(0) == '2') {
                canMovesMap.remove(MoveType.LEFT.getValue());
                maybeCanMovesMap.remove(MoveType.LEFT.getValue());
            }
            if (mapList.get(selfLocationY).get(selfLocationX - 1).charAt(0) == '8'){
                canMovesMap.remove(MoveType.LEFT.getValue());
            }
        }
        if (!isOver(params, selfLocationX + 1, selfLocationY)) {
            // 正右，列数+1，行数=
            if (mapList.get(selfLocationY).get(selfLocationX + 1).charAt(0) == '0' ||
                    mapList.get(selfLocationY).get(selfLocationX + 1).charAt(0) == '2' ) {
                canMovesMap.remove(MoveType.RIGHT.getValue());
                maybeCanMovesMap.remove(MoveType.RIGHT.getValue());
            }
            if (mapList.get(selfLocationY).get(selfLocationX - 1).charAt(0) == '8'){
                canMovesMap.remove(MoveType.RIGHT.getValue());
            }
        }
        System.out.println("躲障碍物" + new ArrayList<>(canMovesMap.values()));

        // 6. 躲避敌人
        List<NpcShortInfo> activeNpcList = gameMap.getActiveNpcs();
        for (NpcShortInfo npcShortInfo: activeNpcList){
            // 正上，行数-2，列数=
            if (Objects.equals(npcShortInfo.getRow(), selfLocationY - 1) &&
                    Objects.equals(npcShortInfo.getCol(), selfLocationX)){
                canMovesMap.remove(MoveType.TOP.getValue());
            }
            // 正下，行数+2，列数=
            if (Objects.equals(npcShortInfo.getRow(), selfLocationY + 1) &&
                    Objects.equals(npcShortInfo.getCol(), selfLocationX)){
                canMovesMap.remove(MoveType.DOWN.getValue());
            }
            // 正左，列数-2，行数=
            if (Objects.equals(npcShortInfo.getRow(), selfLocationY) &&
                    Objects.equals(npcShortInfo.getCol(), selfLocationX - 1)){
                canMovesMap.remove(MoveType.LEFT.getValue());
            }
            // 正右，列数+2，行数=
            if (Objects.equals(npcShortInfo.getRow(), selfLocationY) &&
                    Objects.equals(npcShortInfo.getCol(), selfLocationX + 1)){
                canMovesMap.remove(MoveType.LEFT.getValue());
            }
            // 左上，x-1，y-1
            if (Objects.equals(npcShortInfo.getRow(), selfLocationY - 1) &&
                    Objects.equals(npcShortInfo.getCol(), selfLocationX - 1)){
                canMovesMap.remove(MoveType.LEFT.getValue());
                canMovesMap.remove(MoveType.TOP.getValue());
            }
            // 右上，x+1，y-1
            if (Objects.equals(npcShortInfo.getRow(), selfLocationY - 1) &&
                    Objects.equals(npcShortInfo.getCol(), selfLocationX + 1)){
                canMovesMap.remove(MoveType.RIGHT.getValue());
                canMovesMap.remove(MoveType.TOP.getValue());
            }
            // 左下，x-1，y+1
            if (Objects.equals(npcShortInfo.getRow(), selfLocationY + 1) &&
                    Objects.equals(npcShortInfo.getCol(), selfLocationX - 1)){
                canMovesMap.remove(MoveType.LEFT.getValue());
                canMovesMap.remove(MoveType.DOWN.getValue());
            }
            // 右下，x+1，y+1
            if (Objects.equals(npcShortInfo.getRow(), selfLocationY + 1) &&
                    Objects.equals(npcShortInfo.getCol(), selfLocationX + 1)){
                canMovesMap.remove(MoveType.RIGHT.getValue());
                canMovesMap.remove(MoveType.DOWN.getValue());
            }

        }
        System.out.println("躲敌人" + new ArrayList<>(canMovesMap.values()));

        if (canMovesMap.size() == 0) {
            // 说明没有可以走的方向，先随机给一个吧
            // 这边只能选择从别人的爆炸波或者炸弹中间穿过
            log.info("没有可选方向");
            return new ArrayList<>(maybeCanMovesMap.values());
        } else {
            return new ArrayList<>(canMovesMap.values());
        }
    }

    /**
     * 根据地图和给定的X Y，判断该X Y 是否为障碍物
     * 障碍物包括  不可炸毁障碍物 可炸毁障碍物 炸弹下回合会爆炸的地方
     * TRUE-是障碍物 FALSE-不是障碍物
     *
     * @param requestParam
     * @param x
     * @param y
     */
    public Boolean isObstacles(RequestParam requestParam, List<List<String>> mapList, int x, int y) {
        Boolean res = false;
        // 不可通过的地点
        if (!isOver(requestParam, x, y) &&
                (mapList.get(y).get(x).charAt(0) == '0' ||
                        mapList.get(y).get(x).charAt(0) == '2')) {
            res = true;
        }
        // 会被下个回合的炸弹炸到
        if (!isOver(requestParam, x, y) &&
                mapList.get(y).get(x - 1).charAt(0) == '9' ||
                mapList.get(y).get(x + 1).charAt(0) == '9' ||
                mapList.get(y + 1).get(x).charAt(0) == '9' ||
                mapList.get(y - 1).get(x).charAt(0) == '9') {
            res = true;
        }

        return res;
    }

    /**
     * 从可选的方向里面选出最优前进方向
     *
     * @param params 入参
     * @param moves  可选方向
     * @return
     */
    private final Random random = new Random();
    public String bestMove(RequestParam params, List<String> moves) {
        //首先确定可以走的方向：List<String>
        if(moves.size()==0) return MoveType.values()[random.nextInt(5)].getValue();
        String bestMove = moves.get(0);
        double score = 10000;
        GameMap gameMap = params.getGameMap();
        //将地图进行可走和不可走进行区分
        List<List<String>> mapList = gameMap.getMapList();
        List<int[]> canBrokenWall = new ArrayList<>();
        int[][] map = new int[gameMap.getMapRows()][gameMap.getMapCols()];
        int[][] map1 = new int[gameMap.getMapRows()][gameMap.getMapCols()];
        for (int i = 0; i < gameMap.getMapRows(); i++) {
            for (int j = 0; j < gameMap.getMapCols(); j++) {
                int temp = Integer.valueOf(String.valueOf(mapList.get(i).get(j).charAt(0)));
                int temp1 = 0;
                if(temp == 0){
                    temp = 1;
                    temp1 = 1;
                }else if (temp == 1) {
                    temp = 0;
                    temp1 = 0;
                } else if (temp == 2) {
                    temp = 1;
                    temp1 = 0;
                    int[] loc = new int[2];
                    loc[0] = i;
                    loc[1] = j;
                    canBrokenWall.add(loc);
                } else if (temp == 3) {
                    temp = 1;
                    temp1 = 0;
                } else if (temp == 8){
                    temp = 0;
                    temp1 = 0;
                }else if (temp == 9){
                    temp = 1;
                    temp1 = 1;
                }
                map[i][j] = temp;
                map1[i][j] = temp1;
            }
        }
        for (String move : moves) {
            double scoreMove = getMoveScore(map, map1, params, move, canBrokenWall);
            System.out.println(move +"："+ scoreMove);
            if (scoreMove < score) {
                bestMove = move;
                score = scoreMove;
            } else if (scoreMove == score) {
                bestMove = random.nextInt(2)%2 == 1 ? move :bestMove;
            }
            System.out.println(bestMove);
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
        if(magicBoxScore <= 5){
            return magicBoxScore;
        }
        //广度优先搜索最近的可食用物，并据此获得得分
        double canBrokenScore = getCanBrokenScore(map4, canBrokenWall, slefLocationX, slefLocationY);
        //广度优先搜索搜索可破坏物，并据此获得分数
//        double npcScore = getNpcScore(map3, activeNpcs, slefLocationX, slefLocationY, selfNpcId);
        double npcScore = 0;

        if(activeMagicBoxes.size() == 0 && canBrokenWall.size()== 0){
            boolean firstOne = isFirstOne(activeNpcs,selfNpcId);
            if(!firstOne){
                npcScore = getNpcScore(map3, activeNpcs, slefLocationX, slefLocationY, selfNpcId);
                return npcScore;
            }
        }
        //广度优先搜索搜索最近的人，
        return magicBoxScore + canBrokenScore + npcScore;
    }

    private boolean isFirstOne(List<NpcShortInfo> activeNpcs, String selfNpcId) {
        int score = 0;
        String firstId = "";
        for (NpcShortInfo activeNpc : activeNpcs) {
            int npcScore = Integer.valueOf(activeNpc.getScore());
            if(npcScore > score){
                score = npcScore;
                firstId = activeNpc.getNpcId();
            }
        }
        return firstId.equals(selfNpcId);
    }

    private double getMagicBoxScore(int[][] map, List<MagicBoxShortInfo> activeMagicBoxes, Integer slefLocationX, Integer slefLocationY) {
        int distance = 5000;
        if (activeMagicBoxes.size() == 0) return 1000;
        for (MagicBoxShortInfo activeMagicBox : activeMagicBoxes) {
            int[] temp = new int[2];
            temp[0] = activeMagicBox.getRow();
            temp[1] = activeMagicBox.getCol();
            distance = Math.min(getDistance(map, temp, slefLocationX, slefLocationY),distance);
        }
        return distance  * 1;
    }

    private double getCanBrokenScore(int[][] map1, List<int[]> canBrokenWalls, Integer slefLocationX, Integer slefLocationY) {
        int distance = 5000;
        if (canBrokenWalls.size() == 0) return 1000;
        for (int[] canBrokenWall : canBrokenWalls) {
            distance = Math.min(getDistance(map1, canBrokenWall, slefLocationX, slefLocationY),distance) ;
        }
        return  distance  * 2;
    }

    private double getNpcScore(int[][] map, List<NpcShortInfo> activeNpcs, Integer slefLocationX, Integer slefLocationY, String selfNpcId) {
        int distance = 5000;
        if (activeNpcs.size() == 0) return 1000;
        for (NpcShortInfo activeNpc : activeNpcs) {
            if (activeNpc.getNpcId().equals(selfNpcId)) {
                continue;
            }
            int[] temp = new int[2];
            temp[0] = activeNpc.getRow();
            temp[1] = activeNpc.getCol();
            distance = Math.min(getDistance(map, temp, slefLocationX, slefLocationY),distance);
        }
        return  distance  * 10;
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
        return lastPath[0][0] == map.length * map[0].length ? 50 : lastPath[0][0];
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
    public String run(Boolean isBoom , RequestParam params) {
        List<String> moves = dontMove(isBoom, params);
        return bestMove(params, moves);
    }
}
