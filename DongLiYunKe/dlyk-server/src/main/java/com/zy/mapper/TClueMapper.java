package com.zy.mapper;

import com.zy.model.TClue;
import com.zy.query.BaseQuery;
import com.zy.result.NameValue;

import java.util.List;

public interface TClueMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TClue record);

    int insertSelective(TClue record);

    TClue selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TClue record);

    int updateByPrimaryKey(TClue record);

    List<TClue> selectClueByPage(BaseQuery build);

    void saveClue(List<TClue> tClueList);

    Integer selectByPhone();

    TClue selectDetailById(Integer id);

    Integer selectClueByCount();

    List<NameValue> selectBySource();
}