package com.zy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zy.constant.Constants;
import com.zy.mapper.TClueRemarkMapper;
import com.zy.model.TClueRemark;
import com.zy.query.ClueRemarkQuery;
import com.zy.service.ClueRemarkService;
import com.zy.util.JWTUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ClueRemarkServiceImpl implements ClueRemarkService {

    @Resource
    private TClueRemarkMapper tClueRemarkMapper;

    @Override
    public Integer saveClueRemark(ClueRemarkQuery clueRemarkQuery) {
        TClueRemark tClueRemark = new TClueRemark();
        BeanUtils.copyProperties(clueRemarkQuery, tClueRemark);
        tClueRemark.setCreateTime(new Date());
        Integer loginUserId = JWTUtils.parseUserFromJWT(clueRemarkQuery.getToken()).getId();
        tClueRemark.setCreateBy(loginUserId);
        return tClueRemarkMapper.insertSelective(tClueRemark);
    }

    @Override
    public PageInfo<TClueRemark> getClueRemarkByPage(Integer current, ClueRemarkQuery clueRemarkQuery) {
        //设置PageHelper, 起始页 页大小
        PageHelper.startPage(current, Constants.PAGE_SIZE);

        //查询
        List<TClueRemark> clueRemarkList = tClueRemarkMapper.selectClueRemarkByPage(clueRemarkQuery);

        //封装分页数据到PageInfo
        PageInfo<TClueRemark> pageInfo = new PageInfo<>(clueRemarkList);

        return pageInfo;
    }
}
