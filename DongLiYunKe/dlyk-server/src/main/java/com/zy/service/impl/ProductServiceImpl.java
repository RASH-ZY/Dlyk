package com.zy.service.impl;

import com.zy.mapper.TProductMapper;
import com.zy.model.TProduct;
import com.zy.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private TProductMapper tProductMapper;

    @Override
    public List<TProduct> loadOnSaleProduct() {
        return tProductMapper.selectOnSaleProduct();
    }
}
