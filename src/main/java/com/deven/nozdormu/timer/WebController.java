package com.deven.nozdormu.timer;

import com.deven.nozdormu.timer.dto.PageCmd;
import com.deven.nozdormu.timer.dto.PageRes;
import com.deven.nozdormu.timer.dto.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author seven up
 * @date 2023年05月18日 11:12 AM
 */
@Controller
@RequestMapping("/web")
public class WebController {

    @Autowired
    private JdbcManager jdbcManager;

    @RequestMapping("/index")
    public String index() {
        System.out.println("------ index ---------");
        return "index";
    }

    @GetMapping("/page")
    @ResponseBody
    public PageRes page(String uniqueKey,Integer status,Integer page,Integer perPage) {
        PageCmd cmd = new PageCmd();
        cmd.setPage(page);
        cmd.setSize(perPage);
        cmd.setUniqueKey(uniqueKey);
        cmd.setStatus(status);

        return jdbcManager.page(cmd);
    }







}
