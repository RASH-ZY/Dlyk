package com.zy.service;

import com.github.pagehelper.PageInfo;
import com.zy.model.TClueRemark;
import com.zy.query.ClueRemarkQuery;

public interface ClueRemarkService {
    Integer saveClueRemark(ClueRemarkQuery clueRemarkQuery);

    PageInfo<TClueRemark> getClueRemarkByPage(Integer current, ClueRemarkQuery clueRemarkQuery);
}
