package com.eastmoney.bomberman.controller;

import com.eastmoney.bomberman.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author lifei
 * @date 2022/10/22
 */
@RestController
@RequestMapping("player")
public class PlayerController {

    private PlayerService playerService;

    @Autowired
    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("action")
    public Map<String, Object> doAction(Map<String, Object> params) {
        return playerService.doAction();
    }

}
