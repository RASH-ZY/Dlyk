package com.zy.service;

import com.github.pagehelper.PageInfo;
import com.zy.model.TActivityRemark;
import com.zy.query.ActivityRemarkQuery;

public interface ActivityRemarkService {
    int saveActivityRemark(ActivityRemarkQuery activityRemarkQuery);

    PageInfo<TActivityRemark> getActivityRemarkByPage(Integer current, ActivityRemarkQuery activityRemarkQuery);

    TActivityRemark getActivityRemarkById(Integer id);

    int updateActivityRemark(ActivityRemarkQuery activityRemarkQuery);

    int deleteActivityRemark(Integer id);
}
