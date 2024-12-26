package com.zy.service;

import com.github.pagehelper.PageInfo;
import com.zy.model.TCustomer;
import com.zy.query.CustomerQuery;
import com.zy.result.CustomerExcel;

import java.util.List;

public interface CustomerService {
    Boolean convertCustomer(CustomerQuery customerQuery);

    PageInfo<TCustomer> getCustomerByPage(Integer current);

    List<CustomerExcel> getCustomerByExcel(List<String> idList);
}
