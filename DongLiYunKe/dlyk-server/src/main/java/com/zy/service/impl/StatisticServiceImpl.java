package com.zy.service.impl;

import com.zy.manager.StatisticManager;
import com.zy.result.NameValue;
import com.zy.result.SummaryData;
import com.zy.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private StatisticManager statisticManager;

    @Override
    public SummaryData loadSummaryData() {
        return statisticManager.loadSummaryData();
    }

    @Override
    public List<NameValue> loadSaleFunnelData() {
        return statisticManager.loadSaleFunnelData();
    }

    @Override
    public List<NameValue> sourcePieData() {
        return statisticManager.loadSourcePieData();
    }
}
