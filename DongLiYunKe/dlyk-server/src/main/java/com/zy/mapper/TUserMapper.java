package com.zy.mapper;

import com.zy.commons.DataScope;
import com.zy.model.TUser;
import com.zy.query.BaseQuery;

import java.util.List;

public interface TUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TUser record);

    int insertSelective(TUser record);

    TUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TUser record);

    int updateByPrimaryKey(TUser record);

    TUser selectByLoginAct(String username);

    @DataScope(tableAlias = "tu", tableField = "id")
    List<TUser> selectUserByPage(BaseQuery query);

    TUser selectDetailById(Integer id);

    int deleteByIdList(List<String> idList);

    List<TUser> selectByOwner();
}