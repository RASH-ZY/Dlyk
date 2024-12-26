package com.zy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zy.constant.Constants;
import com.zy.mapper.TActivityRemarkMapper;
import com.zy.model.TActivityRemark;
import com.zy.query.ActivityRemarkQuery;
import com.zy.query.BaseQuery;
import com.zy.service.ActivityRemarkService;
import com.zy.util.JWTUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ActivityRemarkServiceImpl implements ActivityRemarkService {

    @Resource
    private TActivityRemarkMapper tActivityRemarkMapper;

    @Override
    public int saveActivityRemark(ActivityRemarkQuery activityRemarkQuery) {
        TActivityRemark tActivityRemark = new TActivityRemark();

        BeanUtils.copyProperties(activityRemarkQuery, tActivityRemark);

        tActivityRemark.setCreateTime(new Date());

        Integer loginUserId = JWTUtils.parseUserFromJWT(activityRemarkQuery.getToken()).getId();
        tActivityRemark.setCreateBy(loginUserId);

        return tActivityRemarkMapper.insertSelective(tActivityRemark);
    }

    @Override
    public PageInfo<TActivityRemark> getActivityRemarkByPage(Integer current, ActivityRemarkQuery activityRemarkQuery) {
        //设置PageHelper
        PageHelper.startPage(current, Constants.PAGE_SIZE);
        //查询
        List<TActivityRemark> tActivityRemarkList = tActivityRemarkMapper.selectActivityRemarkByPage(activityRemarkQuery);
        //封装分页数据至pageInfo
        PageInfo<TActivityRemark> pageInfo = new PageInfo(tActivityRemarkList);
        return pageInfo;
    }

    @Override
    public TActivityRemark getActivityRemarkById(Integer id) {
        return tActivityRemarkMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateActivityRemark(ActivityRemarkQuery activityRemarkQuery) {
        TActivityRemark tActivityRemark = new TActivityRemark();
        BeanUtils.copyProperties(activityRemarkQuery, tActivityRemark);
        tActivityRemark.setEditTime(new Date());
        Integer loginUserId = JWTUtils.parseUserFromJWT(activityRemarkQuery.getToken()).getId();
        tActivityRemark.setEditBy(loginUserId);
        return tActivityRemarkMapper.updateByPrimaryKeySelective(tActivityRemark);
    }

    @Override
    public int deleteActivityRemark(Integer id) {
        //逻辑删除 不删数据 只修改状态
        //物理删除 删除数据

        //这里采用逻辑删除 修改delete字段(置为1) 根据id
        TActivityRemark tActivityRemark = new TActivityRemark();
        tActivityRemark.setId(id);
        tActivityRemark.setDeleted(1);
        return tActivityRemarkMapper.updateByPrimaryKeySelective(tActivityRemark);
    }
}
