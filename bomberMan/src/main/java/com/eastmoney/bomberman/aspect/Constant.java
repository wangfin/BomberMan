package com.eastmoney.bomberman.aspect;

import com.eastmoney.bomberman.model.RequestParam;
import com.eastmoney.bomberman.model.ResponseData;
import com.eastmoney.bomberman.model.gamemap.BoomShortInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * @author lifei
 * @date 2022/10/23
 */
public class Constant {

    private Constant() {
    }

    public static final Integer BLOCK_SIZE = 64;

    /**
     * 请求数据
     */
    public static List<RequestParam> reqHistory = new LinkedList<>();

    /**
     * 从 0 开始
     */
    public static Integer curIndex = -1;
    public static Integer curRow = -1;
    public static Integer curCol = -1;

    /**
     * 当前想要移动的方向
     */
    public static String curWantMove = "";

    /**
     * 返回数据后，维护的变量
     */
    public static List<ResponseData> respHistory = new LinkedList<>();
    public static Integer stopTimes = 0;
    public static Integer noBoomTimes = 0;
    public static List<BoomShortInfo> myBoomHistory = new LinkedList<>();
    public static String curGameId = "";

}
