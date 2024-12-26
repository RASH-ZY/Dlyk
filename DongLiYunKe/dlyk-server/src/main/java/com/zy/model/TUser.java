package com.zy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;

/**
 * 用户表
 * t_user
 */
@Data
public class TUser implements UserDetails, Serializable {
    /**
     * 主键，自动增长，用户ID
     */
    private Integer id;

    /**
     * 登录账号
     */
    private String loginAct;

    /**
     * 登录密码
     */
    private String loginPwd;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户手机
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 账户是否没有过期，0已过期 1正常
     */
    private Integer accountNoExpired;

    /**
     * 密码是否没有过期，0已过期 1正常
     */
    private Integer credentialsNoExpired;

    /**
     * 账号是否没有锁定，0已锁定 1正常
     */
    private Integer accountNoLocked;

    /**
     * 账号是否启用，0禁用 1启用
     */
    private Integer accountEnabled;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 编辑人
     */
    private Integer editBy;

    /**
     * 最近登录时间
     */
    private Date lastLoginTime;

    //========================================================
    /**
     * 角色List 多对多的关系，使用List接收
     */
    private List<String> roleList;

    /**
     * 权限标识符List （功能List）-- button
     */
    private List<String> permissionList;

    /**
     * 菜单List -- menu
     */
    private List<TPermission> menuPermissionList;

    /**
     * 一对一关联
     */
    private TUser createByDO;
    private TUser editByDO;



    private static final long serialVersionUID = 1L;
    //------------------------实现UserDetails的七个方法--------------------------------

    /**
     * 将用户的角色信息和权限信息添加到List中
     * TokenVerifyFilter中会通过该方法将用户权限List添加到安全框架上下文
     * 同时会将用户基本信息、密码也存入安全框架上下文
     * UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(tUser, tUser.getLoginPwd(), tUser.getAuthorities());
     * SecurityContextHolder.getContext().setAuthentication(authenticationToken);
     * @return list
     */
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();

        if(!ObjectUtils.isEmpty(this.getRoleList())){
            //该用户所扮演的角色，添加到list
            this.getRoleList().forEach(role -> {
                list.add(new SimpleGrantedAuthority(role));
            });
        }

        if(!ObjectUtils.isEmpty(this.getPermissionList())){
            //该用户所拥有的权限，添加到list
            this.getPermissionList().forEach(permission -> {
                list.add(new SimpleGrantedAuthority(permission));
            });
        }

        return list;
    }

    /**
     * 获取用户密码
     * @return 用户密码
     */
    @JsonIgnore
    @Override
    public String getPassword() {
        return this.getLoginPwd();
    }

    /**
     * 获取用户名
     * @return 用户名
     */
    @JsonIgnore
    @Override
    public String getUsername() {
        return this.getLoginAct();
    }

    /**
     * 账号是否过期 1未过期 0已过期
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return this.getAccountNoExpired() == 1;
    }

    /**
     * 账号是否锁定 1未锁定 0已锁定
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return this.getAccountNoLocked() == 1;
    }

    /**
     * 密码是否过期 1未过期 0已过期
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return this.getCredentialsNoExpired() == 1;
    }

    /**
     * 账号是否启用 1启用中 0未启用
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return this.getAccountEnabled() == 1;
    }
}