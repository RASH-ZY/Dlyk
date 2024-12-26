package com.zy.task;

import com.zy.DlykServerApplication;
import com.zy.model.TActivity;
import com.zy.model.TDicType;
import com.zy.model.TDicValue;
import com.zy.model.TProduct;
import com.zy.result.DicEnum;
import com.zy.service.ActivityService;
import com.zy.service.DicTypeService;
import com.zy.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableScheduling//开启定时任务
@Component
public class DataTask {
    //多种调度写法如下

    // @Scheduled(fixedDelay = 3000) // 确定的延迟时间，数字表示，单位是 毫秒；
    // @Scheduled(fixedDelayString = "3000") // 确定的延迟时间，字符串表示，单位是 毫秒；
    // @Scheduled(fixedRate = 3000) // 确定的速率/比例，数字表示，单位是 毫秒；
    // @Scheduled(fixedRateString = "3000") // 确定的速率/比例，字符串表示，单位是 毫秒；
    // @Scheduled(initialDelay = 3000, fixedRate = 3000) // initialDelay表示第一次执行延迟多久，后面是确定的速率/比例，数字表示，单位是 毫秒；
    // @Scheduled(initialDelayString = "3000", fixedRate = 3000) // initialDelay表示第一次执行延迟多久，后面是确定的速率/比例，字符串表示，单位是 毫秒；
    // @Scheduled(cron = "*/3 * * * * *", zone = "Asia/Shanghai", timeUnit = TimeUnit.MILLISECONDS)
    // @Scheduled(fixedDelayString = "${project.task.fixedDelay}") // 确定的延迟时间，数字表示，单位是 毫秒；
    // @Scheduled(cron = "${project.task.cron}", zone = "Asia/Shanghai", timeUnit = TimeUnit.MILLISECONDS)

    @Resource
    private DicTypeService dicTypeService;

    @Resource
    private ProductService tProductService;

    @Resource
    private ActivityService activityService;

    @Scheduled(fixedDelayString = "${project.task.delay}", zone = "Asia/Shanghai", timeUnit = TimeUnit.MILLISECONDS, initialDelay = 1000)
    public void task() {
         //定时任务的业务逻辑：查询数据库，并写入内存

         //逻辑一：查询字典列表并写入jvm内存
         //查询数据库 t_dic_type t_dic_value
         List<TDicType> tDicTypeList = dicTypeService.loadAllDicData();
         //写入jvm内存
         tDicTypeList.forEach(tDicType -> {
             String typeCode = tDicType.getTypeCode(); //key --> sex
             List<TDicValue> dicValueList = tDicType.getDicValueList(); //value --> '男', '女'
             DlykServerApplication.cacheMap.put(typeCode, dicValueList);
         });

         //逻辑二：查询正在销售产品列表并写入jvm内存
         //查询数据库 t_product t_dic_value
         List<TProduct> tOnSaleProductList = tProductService.loadOnSaleProduct();
         //写入jvm内存
         DlykServerApplication.cacheMap.put(DicEnum.PRODUCT.getCode(), tOnSaleProductList);

         //逻辑三：查询正在进行市场活动并写入jvm内存
         //查询数据库
         List<TActivity> tActivityList = activityService.getOngoingActivity();
         //写入jvm内存
         DlykServerApplication.cacheMap.put(DicEnum.ACTIVITY.getCode(), tActivityList);
     }
}
