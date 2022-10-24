package com.eastmoney.bomberman.controller;

import com.eastmoney.bomberman.model.RequestParam;
import com.eastmoney.bomberman.model.ResponseData;
import com.eastmoney.bomberman.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lifei
 * @date 2022/10/22
 */
@RestController
@RequestMapping("player")
public class PlayerController {
//
//    private PlayerService playerService;
//
//    @Autowired
//    public void setPlayerService(PlayerService playerService) {
//        this.playerService = playerService;
//    }
//
//    @PostMapping("action1")
//    public ResponseData doAction(@RequestBody RequestParam params) {
//        return playerService.doAction(params);
//    }

}
