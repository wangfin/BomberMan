package com.eastmoney.bomberman.model;

import lombok.Data;

/**
 * @author lifei
 * @date 2022/10/22
 */
@Data
public class RequestParam {

    private Integer myScore;
    private String selfNpcId;
    private Integer slefLocationX;
    private Integer slefLocationY;
    private GameMap gameMap;

}
