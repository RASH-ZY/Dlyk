<template>
  <el-form ref="loginRefForm" :model="userDetail" label-width="120px">
    <el-form-item label="ID">
      <el-input v-model="userDetail.id" disabled/>
    </el-form-item>
<!--    <el-form-item label="ID">-->
<!--      <div class="detail">&nbsp;{{userDetail.id}}</div>-->
<!--    </el-form-item>-->

    <el-form-item label="账号">
      <div class="detail">&nbsp;{{userDetail.loginAct}}</div>
    </el-form-item>

    <el-form-item label="密码">
      <div class="detail">&nbsp;******</div>
    </el-form-item>

    <el-form-item label="姓名">
      <div class="detail">&nbsp;{{userDetail.name}}</div>
    </el-form-item>

    <el-form-item label="手机">
      <div class="detail">&nbsp;{{userDetail.phone}}</div>
    </el-form-item>

    <el-form-item label="邮箱">
      <div class="detail">&nbsp;{{userDetail.email}}</div>
    </el-form-item>

    <el-form-item label="账号未过期">
      <div class="detail">&nbsp;{{userDetail.accountNoExpired === 1 ? '是' : '否'}}</div>
    </el-form-item>

    <el-form-item label="密码未过期">
      <div class="detail">&nbsp;{{userDetail.credentialsNoExpired === 1 ? '是' : '否'}}</div>
    </el-form-item>

    <el-form-item label="账号未锁定">
      <div class="detail">&nbsp;{{userDetail.accountNoLocked === 1 ? '是' : '否'}}</div>
    </el-form-item>

    <el-form-item label="账号是否启用">
      <div class="detail">&nbsp;{{userDetail.accountEnabled  === 1 ? '是' : '否'}}</div>
    </el-form-item>

    <el-form-item label="创建时间">
      <div class="detail">&nbsp;{{userDetail.createTime}}</div>
    </el-form-item>

    <el-form-item label="创建人">
      <div class="detail">&nbsp;{{userDetail.createByDO.name}}</div>
    </el-form-item>

    <el-form-item label="编辑时间">
      <div class="detail">&nbsp;{{userDetail.editTime}}</div>
    </el-form-item>

    <el-form-item label="编辑人">
      <div class="detail">&nbsp;{{userDetail.editByDO.name}}</div>
    </el-form-item>

    <el-form-item label="最近登录时间">
      <div class="detail">&nbsp;{{userDetail.lastLoginTime}}</div>
    </el-form-item>

    <el-form-item>
      <el-button type="success" @click="goBack">返 回</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import {doGet} from "../http/httpRequest.js";
import {goBack} from "../util/util.js";

export default {
  name: "UserDetailView",

  data() {
    return {
      //用户详情对象
      userDetail: {
        createByDO: {},
        editByDO: {},
      },
    }
  },

  mounted() {
    this.loadUserDetail();
  },

  methods: {
    loadUserDetail(){
      let id = this.$route.params.id //这里的id要与动态路由中的id相同
      doGet("/api/user/" + id, {}).then(resp => {
        if(resp.data.code === 200){
          this.userDetail = resp.data.data
          if(!this.userDetail.createByDO){
            this.userDetail.createByDO = {};
          }
          if(!this.userDetail.editByDO){
            this.userDetail.editByDO = {};
          }
        }
      })
    },

    goBack,
  }
}
</script>


<style scoped>

</style>