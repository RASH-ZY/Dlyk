package com.zy.web;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.zy.constant.Constants;
import com.zy.model.TClue;
import com.zy.query.ClueQuery;
import com.zy.result.R;
import com.zy.service.ClueService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class ClueController {
    @Resource
    private ClueService clueService;

    /**
     * 线索分页列表查询
     * @param current
     * @return
     */
    @PreAuthorize(value = "hasAnyAuthority('clue:list')")
    @GetMapping(value = "/api/clues")
    public R cluePage(@RequestParam(value = "current", required = false) Integer current){
        if(current == null){
            current = 1;
        }
        PageInfo<TClue> pageInfo = clueService.getClueByPage(current);
        return R.OK(pageInfo);
    }

    /**
     * Excel数据导入数据库
     * @param file
     * @param token
     * @return
     * @throws IOException
     */
    @PreAuthorize(value = "hasAnyAuthority('clue:import')")
    @PostMapping(value = "/api/importExcel")
    //file的名字要和前端formData里面的名字相同，否则接收不到
    public R importExcel(MultipartFile file, @RequestHeader(value = Constants.TOKEN_NAME) String token) throws IOException {
        clueService.importExcel(file.getInputStream(), token);
        return R.OK();
    }

    /**
     * 验证手机号是否唯一
     * @param phone
     * @return
     */
    @GetMapping(value = "/api/clue/{phone}")
    public R checkPhone(@PathVariable(value = "phone") String phone) {
        Boolean check = clueService.checkPhone(phone);
        return check ? R.OK() : R.FAIL();
    }

    /**
     * 添加线索
     * @param clueQuery
     * @param token
     * @return
     */
    @PreAuthorize(value = "hasAnyAuthority('clue:add')")
    @PostMapping(value = "/api/clue")
    public R addClue(ClueQuery clueQuery, @RequestHeader(value = Constants.TOKEN_NAME) String token) {
        clueQuery.setToken(token);
        int save = clueService.saveClue(clueQuery);
        return save >= 1 ? R.OK() : R.FAIL();
    }

    /**
     * 查询线索详情
     * @param id
     * @return
     */
    @PreAuthorize(value = "hasAnyAuthority('clue:view')")
    @GetMapping(value = "/api/clue/detail/{id}")
    public R loadClue(@PathVariable(value = "id") Integer id) {
        TClue tClue = clueService.getClueById(id);
        return R.OK(tClue);
    }

    /**
     * 编辑线索信息
     * @param clueQuery
     * @param token
     * @return
     */
    @PreAuthorize(value = "hasAnyAuthority('clue:edit')")
    @PutMapping(value = "/api/clue")
    public R updateClue(ClueQuery clueQuery, @RequestHeader(value = Constants.TOKEN_NAME) String token) {
        clueQuery.setToken(token);
        Integer update = clueService.updateClue(clueQuery);
        return update >= 1 ? R.OK() : R.FAIL();
    }

    @PreAuthorize(value = "hasAnyAuthority('clue:delete')")
    @DeleteMapping(value = "/api/clue/{id}")
    public R delClue(@PathVariable(value = "id") Integer id) {
        int del = clueService.delClue(id);
        return del >= 1 ? R.OK() : R.FAIL();
    }
}
