package com.zy.service;

import com.zy.result.NameValue;
import com.zy.result.SummaryData;

import java.util.List;

public interface StatisticService {

    SummaryData loadSummaryData();

    List<NameValue> loadSaleFunnelData();

    List<NameValue> sourcePieData();
}
