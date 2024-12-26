package com.zy.web;

import com.github.pagehelper.PageInfo;
import com.zy.constant.Constants;
import com.zy.model.TClue;
import com.zy.model.TClueRemark;
import com.zy.query.ClueRemarkQuery;
import com.zy.result.R;
import com.zy.service.ClueRemarkService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClueRemarkController {

    @Resource
    private ClueRemarkService clueRemarkService;

    /**
     * 添加线索历史记录
     * @param clueRemarkQuery
     * @param token
     * @return
     */
    @PostMapping(value = "/api/clue/remark")
    public R addClueRemark(@RequestBody ClueRemarkQuery clueRemarkQuery, @RequestHeader(value = Constants.TOKEN_NAME) String token) {
        clueRemarkQuery.setToken(token);
        Integer save = clueRemarkService.saveClueRemark(clueRemarkQuery);
        return save >= 1 ? R.OK() : R.FAIL();
    }

    @GetMapping(value = "/api/clue/remark")
    public R clueRemarkPage(@RequestParam(value = "current", required = false) Integer current,
                            @RequestParam(value = "clueId") Integer clueId) {
        ClueRemarkQuery clueRemarkQuery = new ClueRemarkQuery();
        clueRemarkQuery.setClueId(clueId);

        if(current == null){
            current = 1;
        }

        PageInfo<TClueRemark> pageInfo = clueRemarkService.getClueRemarkByPage(current, clueRemarkQuery);
        return R.OK(pageInfo);
    }
}
