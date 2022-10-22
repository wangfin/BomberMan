package com.eastmoney.bomberman.model.gamemap;

import lombok.Data;

/**
 * @author lifei
 * @date 2022/10/22
 */
@Data
public class NpcShortInfo {

    private Integer row;
    private Integer col;
    private String npcId;
    private Integer score;
    private Integer x;
    private Integer y;

}
