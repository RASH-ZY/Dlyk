package com.zy.web;

import com.github.pagehelper.PageInfo;
import com.zy.constant.Constants;
import com.zy.model.TActivity;
import com.zy.model.TUser;
import com.zy.query.ActivityQuery;
import com.zy.result.R;
import com.zy.service.ActivityService;
import com.zy.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ActivityController {

    @Resource
    private ActivityService activityService;

    @Resource
    private UserService userService;

    /**
     * 获取市场活动列表信息，分页
     * @param current
     * @return
     */
    @GetMapping(value = "/api/activity")
    public R activityPage(@RequestParam(value = "current", required = false) Integer current,
                          ActivityQuery activityQuery){
        //required = false 表示参数可以传 也可以不传 默认为true(必须传)
        if(current == null){
            current = 1;
        }
        PageInfo<TActivity> pageInfo = activityService.getActivityByPage(current, activityQuery);
        System.out.println(pageInfo);
        return R.OK(pageInfo);
    }

    /**
     * 获取负责人列表
     * @return
     */
    @GetMapping(value = "/api/owner")
    public R getOwnerList(){
        List<TUser> ownerList = userService.getOwnerList();
        return R.OK(ownerList);
    }

    /**
     * 录入市场活动
     * @param activityQuery
     * @param token
     * @return
     */
    @PostMapping(value = "/api/activity")
    public R addActivity(ActivityQuery activityQuery, @RequestHeader(value = Constants.TOKEN_NAME) String token){
        activityQuery.setToken(token);
        int save = activityService.saveActivity(activityQuery);
        return save >= 1 ? R.OK() : R.FAIL();
    }

    /**
     * 获取市场活动
     * @param id
     * @return
     */
    @GetMapping(value = "/api/activity/{id}")
    public R loadActivity(@PathVariable(value = "id") Integer id){
        return R.OK(activityService.getActivityById(id));
    }

    /**
     * 编辑市场活动
     * @param activityQuery
     * @param token
     * @return
     */
    @PutMapping(value = "/api/activity")
    public R editActivity(ActivityQuery activityQuery, @RequestHeader(value = Constants.TOKEN_NAME) String token){
        activityQuery.setToken(token);
        int update = activityService.updateActivity(activityQuery);
        return update >= 1 ? R.OK() : R.FAIL();
    }
}
