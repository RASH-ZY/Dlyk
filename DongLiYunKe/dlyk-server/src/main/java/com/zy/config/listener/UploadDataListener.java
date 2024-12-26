package com.zy.config.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.zy.mapper.TClueMapper;
import com.zy.model.TClue;
import com.zy.util.JSONUtils;
import com.zy.util.JWTUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 * 模板的读取类
 * 每读取一行Excel，触发一次该监听器中的invoke()方法，读取完毕触发doAfterAllAnalysed()方法
 * @author ZY
 */
// 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
@Slf4j
public class UploadDataListener implements ReadListener<TClue> {
    /**
     * 每隔100条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;

    /**
     * 创建一个缓存List，存放读取的Excel数据
     */
    private List<TClue> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * 假设这个是一个Mapper，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private TClueMapper tClueMapper;

    private String token;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param tClueMapper
     * @param token
     */
    public UploadDataListener(TClueMapper tClueMapper, String token) {
        this.tClueMapper = tClueMapper;
        this.token = token;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param tClue    one row value. It is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(TClue tClue, AnalysisContext context) {
        //打印读取到的每一条数据
        log.info("解析到一条数据:{}", JSONUtils.toJSON(tClue));

        //给读取到的TClue对象设置创建时间、创建人
        tClue.setCreateTime(new Date());

        Integer loginUserId = JWTUtils.parseUserFromJWT(token).getId();
        tClue.setCreateBy(loginUserId);

        //将该行数据写入缓存List
        cachedDataList.add(tClue);

        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 List
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库（BATCH_COUNT以外的数据）
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 缓存List中的数据存储到数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        tClueMapper.saveClue(cachedDataList);
        log.info("存储数据库成功！");
    }
}
