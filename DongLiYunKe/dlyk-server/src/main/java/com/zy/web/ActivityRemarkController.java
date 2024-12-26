package com.zy.web;

import com.github.pagehelper.PageInfo;
import com.zy.constant.Constants;
import com.zy.model.TActivityRemark;
import com.zy.model.TUser;
import com.zy.query.ActivityRemarkQuery;
import com.zy.result.R;
import com.zy.service.ActivityRemarkService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
public class ActivityRemarkController {

    @Resource
    private ActivityRemarkService activityRemarkService;

    /**
     * 修改备注
     * @param activityRemarkQuery
     * @param token
     * @return
     */
    @PostMapping(value = "/api/activity/remark")
    public R addActivityRemark(@RequestBody ActivityRemarkQuery activityRemarkQuery, @RequestHeader(value = Constants.TOKEN_NAME) String token) {
        activityRemarkQuery.setToken(token);
        int save = activityRemarkService.saveActivityRemark(activityRemarkQuery);
        return save >= 1 ? R.OK() : R.FAIL();
    }

    /**
     * 获取备注分页列表信息
     * @param current
     * @param activityId
     * @return
     */
    @GetMapping(value = "/api/activity/remark")
    public R activityRemarkByPage(@RequestParam(value = "current", required = false) Integer current,
                                @RequestParam(value = "activityId") Integer activityId) {

        //使用query接收activityId
        ActivityRemarkQuery activityRemarkQuery = new ActivityRemarkQuery();
        activityRemarkQuery.setActivityId(activityId);

        if(current == null){
            current = 1;
        }
        PageInfo<TActivityRemark> pageInfo = activityRemarkService.getActivityRemarkByPage(current, activityRemarkQuery);
        return R.OK(pageInfo);
    }

    /**
     * 获取备注信息
     * @param id
     * @return
     */
    @GetMapping(value = "/api/activity/remark/{id}")
    public R activityRemarkById(@PathVariable(value = "id") Integer id) {
        TActivityRemark tActivityRemark = activityRemarkService.getActivityRemarkById(id);
        return R.OK(tActivityRemark);
    }

    /**
     * 编辑活动备注
     * @param activityRemarkQuery
     * @param token
     * @return
     */
    @PutMapping(value = "/api/activity/remark")
    public R editActivityRemark(@RequestBody ActivityRemarkQuery activityRemarkQuery, @RequestHeader(value = Constants.TOKEN_NAME) String token){
        activityRemarkQuery.setToken(token);
        int update = activityRemarkService.updateActivityRemark(activityRemarkQuery);
        return update >= 1 ? R.OK() : R.FAIL();
    }

    @DeleteMapping(value = "/api/activity/remark/{id}")
    public R editActivityRemark(@PathVariable(value = "id") Integer id){
        int delete = activityRemarkService.deleteActivityRemark(id);
        return delete >= 1 ? R.OK() : R.FAIL();
    }
}
