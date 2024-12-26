package com.zy.web;

import com.zy.DlykServerApplication;
import com.zy.model.TActivity;
import com.zy.model.TDicValue;
import com.zy.model.TProduct;
import com.zy.result.DicEnum;
import com.zy.result.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DicController {

    @GetMapping(value = "/api/dicValue/{typeCode}")
    public R dicData(@PathVariable(value = "typeCode") String typeCode) {
        if(typeCode.equals(DicEnum.ACTIVITY.getCode())){
            List<TActivity> tActivityList = (List<TActivity>) DlykServerApplication.cacheMap.get(typeCode);
            return R.OK(tActivityList);
        } else if(typeCode.equals(DicEnum.PRODUCT.getCode())) {
            List<TProduct> tProductList = (List<TProduct>) DlykServerApplication.cacheMap.get(typeCode);
            return R.OK(tProductList);
        } else {
            List<TDicValue> tDicValueList = (List<TDicValue>) DlykServerApplication.cacheMap.get(typeCode);
            return R.OK(tDicValueList);
        }
    }
}
