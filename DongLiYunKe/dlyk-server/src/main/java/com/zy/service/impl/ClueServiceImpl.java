package com.zy.service.impl;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zy.config.listener.UploadDataListener;
import com.zy.constant.Constants;
import com.zy.mapper.TClueMapper;
import com.zy.model.TClue;
import com.zy.query.BaseQuery;
import com.zy.query.ClueQuery;
import com.zy.result.R;
import com.zy.service.ClueService;
import com.zy.util.JWTUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class ClueServiceImpl implements ClueService {

    @Resource
    private TClueMapper tClueMapper;

    @Override
    public PageInfo<TClue> getClueByPage(Integer current) {
        PageHelper.startPage(current, Constants.PAGE_SIZE);
        List<TClue> list = tClueMapper.selectClueByPage(BaseQuery.builder().build());
        PageInfo<TClue> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public void importExcel(InputStream in, String token) {
        //链式编程，3个参数, 第一个参数是要读取的Excel文件，第二个参数是Excel模板类，第三个参数是文件读取的监听器
        EasyExcel.read(in, TClue.class, new UploadDataListener(tClueMapper, token))
                .sheet()
                .doRead();
    }

    @Override
    public Boolean checkPhone(String phone) {
        Integer check = tClueMapper.selectByPhone();
        return check <= 0;
    }

    @Override
    public int saveClue(ClueQuery clueQuery) {
        TClue tClue = new TClue();
        BeanUtils.copyProperties(clueQuery, tClue);

        tClue.setCreateTime(new Date());

        Integer LoginUserId = JWTUtils.parseUserFromJWT(clueQuery.getToken()).getId();
        tClue.setCreateBy(LoginUserId);

        return tClueMapper.insertSelective(tClue);
    }

    @Override
    public TClue getClueById(Integer id) {
        return tClueMapper.selectDetailById(id);
    }

    @Override
    public Integer updateClue(ClueQuery clueQuery) {
        TClue tClue = new TClue();
        BeanUtils.copyProperties(clueQuery, tClue);
        tClue.setCreateTime(new Date());
        Integer loginUserId = JWTUtils.parseUserFromJWT(clueQuery.getToken()).getId();
        tClue.setCreateBy(loginUserId);
        return tClueMapper.updateByPrimaryKeySelective(tClue);
    }

    @Override
    public int delClue(Integer id) {
        return tClueMapper.deleteByPrimaryKey(id);
    }
}
