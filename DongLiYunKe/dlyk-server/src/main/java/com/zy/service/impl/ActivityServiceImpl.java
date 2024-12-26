package com.zy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zy.constant.Constants;
import com.zy.mapper.TActivityMapper;
import com.zy.model.TActivity;
import com.zy.model.TUser;
import com.zy.query.ActivityQuery;
import com.zy.query.BaseQuery;
import com.zy.service.ActivityService;
import com.zy.util.JWTUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Resource
    private TActivityMapper tActivityMapper;

    @Override
    public PageInfo<TActivity> getActivityByPage(Integer current, ActivityQuery activityQuery) {
        PageHelper.startPage(current, Constants.PAGE_SIZE);
        List<TActivity> list = tActivityMapper.selectActivityByPage(activityQuery);
        PageInfo<TActivity> info = new PageInfo<>(list);
        return info;
    }

    @Override
    public int saveActivity(ActivityQuery activityQuery) {
        TActivity tActivity = new TActivity();

        //activityQuery属性复制到tActivity中
        BeanUtils.copyProperties(activityQuery, tActivity);

        //设置创建时间
        tActivity.setCreateTime(new Date());

        //负责人Id 即 登录人Id 从token中获取
        Integer loginUserId = JWTUtils.parseUserFromJWT(activityQuery.getToken()).getId();
        tActivity.setCreateBy(loginUserId);

        return tActivityMapper.insertSelective(tActivity);
    }

    @Override
    public Object getActivityById(Integer id) {
        return tActivityMapper.selectDetailByPrimaryKey(id);
    }

    @Override
    public int updateActivity(ActivityQuery activityQuery) {
        TActivity tActivity = new TActivity();

        //复制属性
        BeanUtils.copyProperties(activityQuery, tActivity);

        if(tActivity.getEndTime().getTime()-tActivity.getStartTime().getTime()>0){
            //编辑时间
            tActivity.setEditTime(new Date());

            //解析token 获取登录人Id
            Integer loginUserId = JWTUtils.parseUserFromJWT(activityQuery.getToken()).getId();
            tActivity.setEditBy(loginUserId);

            return tActivityMapper.updateByPrimaryKeySelective(tActivity);
        } else {
            return 0;
        }
    }

    @Override
    public List<TActivity> getOngoingActivity() {
        return tActivityMapper.selectOngoingActivity();
    }
}
