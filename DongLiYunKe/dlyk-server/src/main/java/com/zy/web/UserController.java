package com.zy.web;

import com.github.pagehelper.PageInfo;
import com.zy.model.TUser;
import com.zy.query.UserQuery;
import com.zy.result.R;
import com.zy.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 获取登录人信息
     * @param authentication
     * @return
     */
    @GetMapping(value = "/api/login/info")
    public R loginInfo(Authentication authentication){
        TUser tUser = (TUser)authentication.getPrincipal();
        return R.OK(tUser);
    }

    /**
     * 用户免登录
     * 请求若到达 controller，代表已经通过了 Filter，即 token验证通过，直接返回 R.OK
     * @return
     */
    @GetMapping(value = "/api/login/free")
    public R freeLogin(){
        return R.OK();
    }

    /**
     * 获取用户列表信息，分页
     * @param current
     * @return
     */
    @PreAuthorize(value = "hasAuthority('user:list')")
    @GetMapping(value = "/api/users")
    public R userPage(@RequestParam(value = "current", required = false) Integer current){
        //required = false 表示参数可以传 也可以不传 默认为true(必须传)
        if(current == null){
            current = 1;
        }
        PageInfo<TUser> pageInfo = userService.getUserByPage(current);
        System.out.println(pageInfo);
        return R.OK(pageInfo);
    }

    /**
     * 用户详情
     * @param id
     * @return
     */
    @PreAuthorize(value = "hasAuthority('user:view')")
    @GetMapping(value = "/api/user/{id}")
    public R useDetail(@PathVariable(value = "id") Integer id) {
        //@PathVariable接收路径中的参数id
        TUser tUser = userService.getUserById(id);
        return R.OK(tUser);
    }

    /**
     * 添加用户
     * @param userQuery
     * @return
     */
    @PreAuthorize(value = "hasAuthority('user:add')")
    @PostMapping(value = "/api/user")
    public R addUser(UserQuery userQuery, @RequestHeader(value = "Authorization") String token){
        userQuery.setToken(token);

        int save = userService.saveUser(userQuery);
        return save >= 1 ? R.OK() : R.FAIL();
    }

    /**
     * 编辑用户
     * @param userQuery
     * @param token
     * @return
     */
    @PreAuthorize(value = "hasAuthority('user:edit')")
    @PutMapping(value = "/api/user")
    public R editUser(UserQuery userQuery, @RequestHeader(value = "Authorization") String token){
        userQuery.setToken(token);

        int update = userService.updateUser(userQuery);
        return update >= 1 ? R.OK() : R.FAIL();
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @PreAuthorize(value = "hasAuthority('user:delete')")
    @DeleteMapping(value = "/api/user/{id}")
    public R deleteUser(@PathVariable(value = "id") Integer id){
        int del = userService.deleteUser(id);
        return del >= 1 ? R.OK() : R.FAIL();
    }

    /**
     * 批量删除用户
     * @param idStr
     * @return
     */
    @PreAuthorize(value = "hasAuthority('user:delete')")
    @DeleteMapping(value = "/api/user")
    public R batchDeleteUser(@RequestParam(value = "idStr") String idStr){
        //idStr="1,2,3,4"
        //字符串转数组
        List<String> idList = Arrays.asList(idStr.split(","));
        int batchDel = userService.batchDeleteUser(idList);
        return batchDel >= idList.size() ? R.OK() : R.FAIL();
    }

//    @GetMapping(value = "/api/owner")
//    public R owner() {
//        List<TUser> ownerList = userService.getOwnerList();
//        return R.OK(ownerList);
//    }
}
