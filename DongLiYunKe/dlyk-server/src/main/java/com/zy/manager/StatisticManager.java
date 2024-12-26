package com.zy.manager;

import com.zy.mapper.TActivityMapper;
import com.zy.mapper.TClueMapper;
import com.zy.mapper.TCustomerMapper;
import com.zy.mapper.TTranMapper;
import com.zy.result.NameValue;
import com.zy.result.SummaryData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class StatisticManager {

    @Autowired
    private TActivityMapper tActivityMapper;

    @Autowired
    private TClueMapper tClueMapper;

    @Autowired
    private TCustomerMapper tCustomerMapper;

    @Autowired
    private TTranMapper tTranMapper;

    public SummaryData loadSummaryData() {
        //有效的市场活动总数
        Integer effectiveActivityCount = tActivityMapper.selectOngoingActivity().size(); //偷懒了一下

        //总的市场活动数
        Integer totalActivityCount = tActivityMapper.selectByCount();

        //线索总数
        Integer totalClueCount = tClueMapper.selectClueByCount();

        //客户总数
        Integer totalCustomerCount = tCustomerMapper.selectByCount();

        //成功的交易额
        BigDecimal successTranAmount = tTranMapper.selectBySuccessTranAmount();

        //总的交易额（包含成功和不成功的）
        BigDecimal totalTranAmount = tTranMapper.selectByTotalTranAmount();

        //建造模式创建对象
        return SummaryData.builder()
                .effectiveActivityCount(effectiveActivityCount)
                .totalActivityCount(totalActivityCount)
                .totalClueCount(totalClueCount)
                .totalCustomerCount(totalCustomerCount)
                .successTranAmount(successTranAmount)
                .totalTranAmount(totalTranAmount)
                .build();
    }

    /** 查询以下四个数据的个数
     *  返回格式为
     *  [
     *      {value: 20, name: '成交'},
     *      {value: 60, name: '交易'},
     *      {value: 80, name: '客户'},
     *      {value: 100, name: '线索'},
     *  ]
     * @return
     */
    public List<NameValue> loadSaleFunnelData() {
        List<NameValue> nameValueList = new ArrayList<>();

        //成交总数
        Integer successTranCount = tTranMapper.selectBySuccessTranCount();
        //交易总数
        Integer tranCount = tTranMapper.selectByTranCount();
        //客户总数
        Integer customerCount = tCustomerMapper.selectByCount();
        //线索总数
        Integer clueCount = tClueMapper.selectClueByCount();

        NameValue successTran = NameValue.builder().name("成交").value(successTranCount).build();
        NameValue tran = NameValue.builder().name("交易").value(tranCount).build();
        NameValue customer = NameValue.builder().name("客户").value(customerCount).build();
        NameValue clue = NameValue.builder().name("线索").value(clueCount).build();

        nameValueList.add(successTran);
        nameValueList.add(tran);
        nameValueList.add(customer);
        nameValueList.add(clue);

        return nameValueList;

    }

    /**
     * 获取饼状图数据，格式为：个数, 来源
     * [
     *     {value: 1048, name: '知乎'},
     *     {value: 269, name: '朋友圈'},
     *     {value: 635, name: '公众号'},
     * ]
     * 新建一个对象 NameValue 传输数据
     * @return
     */
    public List<NameValue> loadSourcePieData() {
        return tClueMapper.selectBySource();
    }
}
