<template>
  <el-container>
    <!-- 左侧 -->
    <el-aside :width= "isCollapse ? '64px' : '200px'">
      <div class="menuTitle">@云客管理系统</div>
      <el-menu
          active-text-color="#ffd04b"
          background-color="#334157"
          class="el-menu-vertical-demo"
          style="border-right: solid 0px"
          text-color="#fff"
          :default-active="currentRouterPath"
          :unique-opened="true"
          :collapse="isCollapse"
          :collapse-transition="false"
          :router="true"
          @open="handleOpen"
          @close="handleClose">

        <!-- 线索管理菜单 -->
        <el-sub-menu v-for="(menuPermission, index) in user.menuPermissionList"
                     :index="index"
                     :key="menuPermission.id">
          <!--一级菜单-->
          <template #title>
            <el-icon><component :is="menuPermission.icon" /></el-icon>
            <span>{{menuPermission.name}}</span>
          </template>

          <!--二级菜单-->
          <el-menu-item v-for="subPermission in menuPermission.subPermissionList "
                        :key="subPermission.id"
                        :index="subPermission.url">
            <el-icon><component :is="subPermission.icon" /></el-icon>
            {{subPermission.name}}
          </el-menu-item>
        </el-sub-menu>

      </el-menu>
    </el-aside>

    <!-- 右侧 -->
    <el-container class="rightContent">
      <!-- 右侧 上 -->
      <el-header>
        <el-icon class="show" @click="showMenu"><Fold /></el-icon>
        <el-dropdown :hide-on-click="false">
          <span class="el-dropdown-link">
            {{ user.name }}
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>我的密码</el-dropdown-item>
              <el-dropdown-item>修改密码</el-dropdown-item>
              <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <!-- 右侧 中 -->
      <el-main>
        <router-view v-if="isRouterAlive"/>
      </el-main>

      <!-- 右侧 下 -->
      <el-footer>@版权所有 2024 RASH...ZY</el-footer>
    </el-container>
  </el-container>
</template>

<script>
import {doGet} from "../http/httpRequest.js";
import {messageConfirm, messageTip, removeToken} from "../util/util.js";

export default {
  name: "DashboardView",

  data () {
    return {
      //控制左侧菜单水平折叠，true折叠，false收起
      isCollapse: false,
      //登录用户对象 初始值为空
      user: {},
      isRouterAlive: true,
      //当前访问的路由路径
      currentRouterPath : ''
    }
  },

  provide() {
    return {
      //提供一个函数，要求是箭头函数
      reload: ()=> {
        this.isRouterAlive = false; //隐藏路由对应页面
        this.$nextTick(function () { //$nextTick 数据更新了，dom中渲染，自动调用该函数
          this.isRouterAlive = true; //显示路由对应页面
        })
      },
    }
  },

  //vue生命周期中的一个钩子函数，页面渲染后执行
  mounted() {
    //加载当前登录用户
    this.loadLoginUser();
    //加载当前菜单路由
    this.loadCurrentRouterPath();
  },

  methods: {
    //左侧菜单 水平折叠
    showMenu(){
      this.isCollapse = !this.isCollapse;
    },
    //加载当前登录用户
    loadLoginUser(){
      doGet("/api/login/info", {}).then((resp) => {
        console.log(resp)
        //将用户信息写入user
        this.user = resp.data.data;
      })
    },
    //退出登录
    logout() {
      doGet("/api/logout", {}).then(resp => {
        if(resp.data.code === 200){
          //退出成功 删除token 提示信息 跳转登录
          removeToken();
          messageTip("退出成功", "success");
          window.location.href = "/";
        } else {
          messageConfirm('退出异常, 是否强制退出？')
              .then(() => { //用户点击确定 执行该代码块
                //后端token有问题，前端就没必要存在该token了
                removeToken()
                //跳转登录页
                window.location.href = "/";
              }).catch(() => { //用户点击取消 执行该代码块
            messageTip("取消强制退出", "warning");
          })
        }
      })
    },
    //加载当前菜单路由
    // loadCurrentRouterPath() {
    //   let path = this.$route.path;
    //   let arrPath = path.split("/");
    //   if(arrPath.length > 3) {
    //     this.currentRouterPath = "/" + arrPath[1] + "/" + arrPath[2];
    //   } else {
    //     this.currentRouterPath = path;
    //   }
    //   alert(this.currentRouterPath);
    // },
    //加载当前路由路径
    loadCurrentRouterPath() {
      let path = this.$route.path; //   /dashboard/activity/add
      let arr = path.split("/"); //   [  ,dashboard, activity, add]
      if (arr.length > 3) {
        this.currentRouterPath = "/" + arr[1] + "/" + arr[2];
      } else {
        this.currentRouterPath = path;
      }
    }
  }
}
</script>

<style scoped>
.el-aside {
  background: #1a1a1a;
}

.el-header {
  background: #dbeffa;
  line-height: 35px;
  height: 35px;
}

.el-footer {
  background: #dbeffa;
  height: 35px;
  line-height: 35px;
  text-align: center;
}

.rightContent {
  height: calc(100vh);
}
.menuTitle{
  height: 35px;
  color: #f9f9f9;
  line-height: 35px;
  text-align: center;
}
.show{
  cursor: pointer;
}
.el-dropdown{
  float: right;
  line-height: 35px;
}
</style>