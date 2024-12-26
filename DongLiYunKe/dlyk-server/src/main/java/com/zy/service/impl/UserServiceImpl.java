package com.zy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zy.constant.Constants;
import com.zy.manager.RedisManager;
import com.zy.mapper.TPermissionMapper;
import com.zy.mapper.TRoleMapper;
import com.zy.mapper.TUserMapper;
import com.zy.model.TPermission;
import com.zy.model.TRole;
import com.zy.model.TUser;
import com.zy.query.BaseQuery;
import com.zy.query.UserQuery;
import com.zy.service.UserService;
import com.zy.util.CacheUtils;
import com.zy.util.JWTUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private TUserMapper tUserMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private TRoleMapper tRoleMapper;

    @Resource
    private TPermissionMapper tPermissionMapper;

    @Resource
    private RedisManager redisManager;

    /**
     * 登录查询
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TUser tUser = tUserMapper.selectByLoginAct(username);
        if (tUser == null) {
            throw new UsernameNotFoundException("登录账号不存在");
        }
        //查询当前用户角色
        List<TRole> tRoleList = tRoleMapper.selectByUserId(tUser.getId());
        //字符串的角色列表
        List<String> stringRoleList = new ArrayList<>();
        tRoleList.forEach(tRole -> {
            stringRoleList.add(tRole.getRole());
        });

        tUser.setRoleList(stringRoleList); //设置用户角色

        //查询该用户有哪些菜单权限(menu)
        List<TPermission> menuPermissionList = tPermissionMapper.selectMenuPermissionByUserId(tUser.getId());
        tUser.setMenuPermissionList(menuPermissionList); //设置用户菜单权限 （菜单权限 menu）

        //查询该用户有哪些功能权限(button)
        List<TPermission> buttonPermissionList = tPermissionMapper.selectButtonPermissionByUserId(tUser.getId());
        List<String> stringPermissionList = new ArrayList<>();
        buttonPermissionList.forEach(tPermission -> {
            stringPermissionList.add(tPermission.getCode());
        });
        tUser.setPermissionList(stringPermissionList); //设置用户权限标识符（功能权限 button）

        return tUser;
    }

    /**
     * 分页查询
     * @param current
     * @return
     */
    @Override
    public PageInfo<TUser> getUserByPage(Integer current) {
        PageHelper.startPage(current, Constants.PAGE_SIZE);
        List<TUser> list = tUserMapper.selectUserByPage(BaseQuery.builder().build());
        PageInfo<TUser> info = new PageInfo<>(list);
        return info;
    }

    @Override
    public TUser getUserById(Integer id) {
        return tUserMapper.selectDetailById(id);
    }

    @Override
    public int saveUser(UserQuery userQuery) {
        TUser tUser = new TUser();

        //userQuery数据赋复制到tUser,要求两个对象属性名，属性类型相同
        BeanUtils.copyProperties(userQuery, tUser);

        //密码加密 设置创建时间
        tUser.setLoginPwd(passwordEncoder.encode(userQuery.getLoginPwd()));
        tUser.setCreateTime(new Date());

        //登录人id(创建人)
        tUser.setCreateBy(JWTUtils.parseUserFromJWT(userQuery.getToken()).getId());
        return tUserMapper.insertSelective(tUser);
    }

    @Override
    public int updateUser(UserQuery userQuery) {
        TUser tUser = new TUser();

        //userQuery数据赋复制到tUser,要求两个对象属性名，属性类型相同
        BeanUtils.copyProperties(userQuery, tUser);

        if(StringUtils.hasText(userQuery.getLoginPwd())){
            //存在密码，则进行加密
            tUser.setLoginPwd(passwordEncoder.encode(userQuery.getLoginPwd()));
        }

        //设置编辑时间
        tUser.setEditTime(new Date());

        //登录人id(创建人)
        tUser.setEditBy(JWTUtils.parseUserFromJWT(userQuery.getToken()).getId());
        return tUserMapper.updateByPrimaryKeySelective(tUser);
    }

    @Override
    public int deleteUser(Integer id) {
        return tUserMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int batchDeleteUser(List<String> idList) {
        return tUserMapper.deleteByIdList(idList);
    }

    @Override
    public List<TUser> getOwnerList() {
        //先从Redis查询
        //没有则从MySQL查询，并存入Redis（5min过期）
        return CacheUtils.getCacheData(
                //生产，Redis中查询数据
                () -> {
                    return (List<TUser>) redisManager.getValue(Constants.REDIS_OWNER_KEY);
                },
                //生产，MySQL中查询数据
                () -> {
                    return tUserMapper.selectByOwner();
                },
                //消费，写入Redis
                (t) -> {
                    redisManager.setValue(Constants.REDIS_OWNER_KEY, t);
                });
    }
}
