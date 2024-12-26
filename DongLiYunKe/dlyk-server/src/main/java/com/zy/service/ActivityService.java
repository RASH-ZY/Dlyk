package com.zy.service;

import com.github.pagehelper.PageInfo;
import com.zy.model.TActivity;
import com.zy.query.ActivityQuery;

import java.util.List;

public interface ActivityService {
    PageInfo<TActivity> getActivityByPage(Integer current, ActivityQuery activityQuery);

    int saveActivity(ActivityQuery activityQuery);

    Object getActivityById(Integer id);

    int updateActivity(ActivityQuery activityQuery);

    List<TActivity> getOngoingActivity();
}
