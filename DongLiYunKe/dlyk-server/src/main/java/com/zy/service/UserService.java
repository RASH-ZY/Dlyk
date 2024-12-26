package com.zy.service;

import com.github.pagehelper.PageInfo;
import com.zy.model.TUser;
import com.zy.query.UserQuery;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {
    /**
     * 重写UserDetailsService的loadUserByUsername方法
     * 处理用户登录和授权
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    PageInfo<TUser> getUserByPage(Integer current);

    TUser getUserById(Integer id);

    int saveUser(UserQuery userQuery);

    int updateUser(UserQuery userQuery);

    int deleteUser(Integer id);

    int batchDeleteUser(List<String> idList);

    List<TUser> getOwnerList();
}
