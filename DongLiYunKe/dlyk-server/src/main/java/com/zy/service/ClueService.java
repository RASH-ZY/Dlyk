package com.zy.service;

import com.github.pagehelper.PageInfo;
import com.zy.model.TClue;
import com.zy.query.ClueQuery;
import com.zy.result.R;

import java.io.InputStream;

public interface ClueService {
    PageInfo<TClue> getClueByPage(Integer current);

    void importExcel(InputStream inputStream, String token);

    Boolean checkPhone(String phone);

    int saveClue(ClueQuery clueQuery);

    TClue getClueById(Integer id);

    Integer updateClue(ClueQuery clueQuery);

    int delClue(Integer id);
}
