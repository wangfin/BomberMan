package com.eastmoney.bomberman.model;

import com.eastmoney.bomberman.model.gamemap.BoomShortInfo;
import com.eastmoney.bomberman.model.gamemap.ExplodeShortInfo;
import com.eastmoney.bomberman.model.gamemap.MagicBoxShortInfo;
import com.eastmoney.bomberman.model.gamemap.NpcShortInfo;
import lombok.Data;

import java.util.List;

/**
 * @author lifei
 * @date 2022/10/22
 */
@Data
public class GameMap {

    private Integer mapRows;
    private Integer mapCols;
    private List<List<String>> mapList;
    private List<BoomShortInfo> activeBooms;
    private List<ExplodeShortInfo> activeExplodes;
    private List<MagicBoxShortInfo> activeMagicBoxes;
    private List<NpcShortInfo> activeNpcs;

}
