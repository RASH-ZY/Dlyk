package com.zy.web;

import com.zy.result.NameValue;
import com.zy.result.R;
import com.zy.result.SummaryData;
import com.zy.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据统计 首页
 */
@RestController
public class StatisticController{

    @Autowired
    private StatisticService statisticService;

    /**
     * 获取统计数据
     * @return
     */
    @GetMapping(value = "/api/summary/data")
    public R summaryData() {
        SummaryData summaryData = statisticService.loadSummaryData();
        return R.OK(summaryData);
    }

    /**
     * 获取漏斗图数据，格式为：
     * [
     *     {value: 20, name: '成交'},
     *     {value: 60, name: '交易'},
     *     {value: 80, name: '客户'},
     *     {value: 100, name: '线索'},
     * ]
     * 新建一个对象 NameValue 传输数据
     * @return
     */
    @GetMapping(value = "/api/saleFunnel/data")
    public R saleFunnelData() {
        List<NameValue> nameValueList = statisticService.loadSaleFunnelData();
        return R.OK(nameValueList);
    }

    /**
     * 获取饼状图数据，格式为：
     * [
     *     {value: 1048, name: '知乎'},
     *     {value: 269, name: '朋友圈'},
     *     {value: 635, name: '公众号'},
     * ]
     * 新建一个对象 NameValue 传输数据
     * @return
     */
    @GetMapping(value = "/api/sourcePie/data")
    public R sourcePieData() {
        List<NameValue> nameValueList = statisticService.sourcePieData();
        return R.OK(nameValueList);
    }
}
