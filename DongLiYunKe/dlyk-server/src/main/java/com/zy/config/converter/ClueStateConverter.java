package com.zy.config.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.zy.DlykServerApplication;
import com.zy.model.TDicValue;
import com.zy.result.DicEnum;

import java.util.List;

/**
 * 线索状态 转换器
 */
public class ClueStateConverter implements Converter<Integer> {
    /**
     * Excel：将来联系 --> Java：6
     * Excel：失去联系 --> java：7
     * ...
     * @param cellData
     * @param contentProperty
     * @param globalConfiguration
     * @return
     * @throws Exception
     */
    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        //cellData：Excel中的单元格数据（）

        //1.查询cellData在数据库中对应的值是多少
        //由于该类不是IoC容器中的Bean，无法注入Mapper，即无法查询数据库
        //因此设置定时任务，查询数据存储到内存中，从内存中查询数据
        String cellClueStateName = cellData.getStringValue();

        //在jvm内存中获取数据集合，根据type_code：DicEnum.CLUESTATE.getCode()
        List<TDicValue> tDicValueList = (List<TDicValue>) DlykServerApplication.cacheMap.get(DicEnum.CLUESTATE.getCode());

        //遍历集合
        for (TDicValue tDicValue : tDicValueList) {
            Integer id = tDicValue.getId();
            String typeValue = tDicValue.getTypeValue();

            //Excel数据与t_dic_value表中type_value匹配成功 转换为对应id（完成数据转换 转成id）
            if(cellClueStateName.equals(typeValue)){
                return id;
            }
        }
        //遍历结束仍未找到Excel对应数据库中的数据 返回-1
        return -1;
    }
}
