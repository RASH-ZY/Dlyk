package com.zy.manager;

import com.zy.mapper.TClueMapper;
import com.zy.mapper.TCustomerMapper;
import com.zy.model.TClue;
import com.zy.model.TCustomer;
import com.zy.query.CustomerQuery;
import com.zy.result.R;
import com.zy.util.JWTUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
public class CustomerManager {
    @Resource
    private TClueMapper tClueMapper;

    @Resource
    private TCustomerMapper tCustomerMapper;

    @Transactional(rollbackFor = Exception.class)
    public Boolean convertCustomer(CustomerQuery customerQuery) {
        //验证该线索是否已转为客户
        TClue tClue = tClueMapper.selectByPrimaryKey(customerQuery.getClueId());
        if(tClue.getState() == -1) {
            throw new RuntimeException("该线索已转为客户");
        }

        //将线索对应客户插入客户表
        TCustomer tCustomer = new TCustomer();
        BeanUtils.copyProperties(customerQuery, tCustomer);
        tCustomer.setCreateTime(new Date());
        int loginUserId = JWTUtils.parseUserFromJWT(customerQuery.getToken()).getId();
        tCustomer.setCreateBy(loginUserId);

        int insert = tCustomerMapper.insertSelective(tCustomer);

        //线索表客户状态转换为-1(已转客户),线索表意向产品转换为提交的产品
        TClue clue = new TClue();
        clue.setState(-1);
        clue.setId(customerQuery.getClueId());
        clue.setIntentionProduct(customerQuery.getProduct());
        int update = tClueMapper.updateByPrimaryKeySelective(clue);

        return insert >= 1 && update >= 1;
    }
}
