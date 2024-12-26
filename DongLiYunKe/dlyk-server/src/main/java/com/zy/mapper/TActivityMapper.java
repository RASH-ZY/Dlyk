package com.zy.mapper;

import com.zy.commons.DataScope;
import com.zy.model.TActivity;
import com.zy.query.ActivityQuery;
import com.zy.query.BaseQuery;

import java.util.List;

public interface TActivityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TActivity record);

    int insertSelective(TActivity record);

    TActivity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TActivity record);

    int updateByPrimaryKey(TActivity record);

    @DataScope(tableAlias = "ta", tableField = "owner_id")
    List<TActivity> selectActivityByPage(ActivityQuery query);

    Object selectDetailByPrimaryKey(Integer id);

    List<TActivity> selectOngoingActivity();

    Integer selectByCount();
}