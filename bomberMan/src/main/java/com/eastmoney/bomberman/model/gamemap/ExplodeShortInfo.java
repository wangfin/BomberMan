package com.eastmoney.bomberman.model.gamemap;

import lombok.Data;

/**
 * @author lifei
 * @date 2022/10/22
 */
@Data
public class ExplodeShortInfo {

    private Integer row;
    private Integer col;
    private Integer left;
    private Integer right;
    private Integer up;
    private Integer down;

}
