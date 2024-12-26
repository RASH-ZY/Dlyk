<template>
  <el-button type="primary" @click="add" v-hasPermission="'user:add'">添加用户</el-button>
  <el-button type="danger" @click="batchDel" v-hasPermission="'user:delete'">批量删除</el-button>
  <el-table
      :data="userList"
      style="width: 100%"
      @selection-change="handleSelectionChange">
    <el-table-column type="selection" width="55" />
    <el-table-column type="index" label="序号" width="60"/>
    <el-table-column property="loginAct" label="账号" show-overflow-tooltip />
    <el-table-column property="name" label="姓名" show-overflow-tooltip />
    <el-table-column property="phone" label="手机" show-overflow-tooltip />
    <el-table-column property="email" label="邮箱" show-overflow-tooltip />
    <el-table-column property="createTime" label="创建时间" show-overflow-tooltip />
    <el-table-column label="操作"  width="180px" >
      <template #default="scope">
        <el-button class="op_button" type="primary" @click="view(scope.row.id)" v-hasPermission="'user:view'" >详情</el-button>
        <el-button class="op_button" type="success" @click="edit(scope.row.id)" v-hasPermission="'user:edit'" >编辑</el-button>
        <el-button class="op_button" type="danger" @click="del(scope.row.id)" v-hasPermission="'user:delete'" >删除</el-button>
      </template>
    </el-table-column>
  </el-table>

  <!-- 分页 -->
  <el-pagination
      background
      layout="prev, pager, next"
      :page-size="pageSize"
      :total="total"
      @prev-click="toPage"
      @next-click="toPage"
      @current-change="toPage"/>

  <!-- 新增用户弹窗 -->
  <el-dialog v-model="userDialogVisible" :title="userQuery.id > 0 ? '编辑用户':'新增用户'" width="55%" draggable>

    <el-form ref="userRefForm" :model="userQuery" :rules="userRules" label-width="110px">
      <el-form-item label="账号" prop="loginAct">
        <el-input v-model="userQuery.loginAct" />
      </el-form-item>

      <el-form-item label="密码" v-if="userQuery.id > 0">
        <el-input v-model="userQuery.loginPwd" type="password"/>
      </el-form-item>

      <el-form-item label="密码" prop="loginPwd" v-else>
        <el-input v-model="userQuery.loginPwd" type="password"/>
      </el-form-item>

      <el-form-item label="姓名" prop="name">
        <el-input v-model="userQuery.name" />
      </el-form-item>

      <el-form-item label="手机" prop="phone">
        <el-input v-model="userQuery.phone" />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="userQuery.email" />
      </el-form-item>

      <el-form-item label="账号未过期" prop="accountNoExpired">
        <el-select v-model="userQuery.accountNoExpired" placeholder="请选择">
          <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="密码未过期" prop="credentialsNoExpired">
        <el-select v-model="userQuery.credentialsNoExpired" placeholder="请选择">
          <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="账号未锁定" prop="accountNoLocked">
      <el-select v-model="userQuery.accountNoLocked" placeholder="请选择">
        <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"/>
      </el-select>
    </el-form-item>
      <el-form-item label="账号是否启用" prop="accountEnabled">
        <el-select v-model="userQuery.accountEnabled" placeholder="请选择">
          <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"/>
        </el-select>
      </el-form-item>

    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="userDialogVisible = false">返回</el-button>
        <el-button type="primary" @click="userSubmit">提交</el-button>
      </div>
    </template>
  </el-dialog>

</template>

<script>
import {doDelete, doGet, doPost, doPut} from "../http/httpRequest.js";
import {messageConfirm, messageTip} from "../util/util.js";

export default {
  name: "UserView",

  //注入父级页面的属性，函数...
  inject: ["reload"],


  data() {
    return {
      //用户列表信息
      userList: [{}],
      //页大小
      pageSize: "",
      //分页数据总数
      total: "",
      userDialogVisible: false,
      //用户表单对象
      userQuery: {},
      //定义用户验证规则
      userRules: {
        loginAct: [
          { required: true, message: '请输入登录账号', trigger: 'blur' },
        ],
        loginPwd: [
          { required: true, message: '请输入登录密码', trigger: 'blur' },
          { min: 6, max: 16, message: '登录密码长度为6-16位', trigger: 'blur' },
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { pattern: /^[\u4e00-\u9fa5]+$/, message: '姓名必须是中文', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
            // ^：表示字符串的开始。
            // 1：手机号必须以 1 开头。
            // [3-9]：第二位数字必须是 3 到 9 之间的数字（即 3, 4, 5, 6, 7, 8, 9）。
            // \d{9}：接下来的 9 位是任意数字（0-9）。
            // $：表示字符串的结束。
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式格式有误', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
            // ^：表示字符串的开始。
            // [a-zA-Z0-9._%+-]+：匹配本地部分（即 @ 之前的部分），允许字母、数字、点 (.)、下划线 (_)、百分号 (%)、加号 (+) 和减号 (-)。+ 表示至少出现一次。
            // @：匹配 @ 符号。
            // [a-zA-Z0-9.-]+：匹配域名部分（即 @ 之后、. 之前的部分），允许字母、数字、点 (.) 和减号 (-)。+ 表示至少出现一次。
            // \.[a-zA-Z]{2,}：匹配顶级域名（如 .com、.org、.net 等），要求顶级域名至少包含两个字母。\. 匹配字面的点字符，{2,} 表示至少两个字母。
            // $：表示字符串的结束。
          { pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '邮箱格式有误', trigger: 'blur' }
        ],
        accountNoExpired: [
          { required: true, message: '请选择', trigger: 'blur' },
        ],
        credentialsNoExpired: [
          { required: true, message: '请选择', trigger: 'blur' },
        ],
        accountNoLocked: [
          { required: true, message: '请选择', trigger: 'blur' },
        ],
        accountEnabled: [
          { required: true, message: '请选择', trigger: 'blur' },
        ],

      },
      options: [
        { label: '是', value: 1 },
        { label: '否', value: 0 },
      ],
      userIdArray: [],
    }
  },

  mounted() {
    this.getData(1);
  },

  methods: {
    //勾选、取消勾选，触发该函数
    handleSelectionChange(selectDataArray) {
      console.log(selectDataArray)
      //每次勾选时，清空之前勾选的内容
      this.userIdArray = [];
      //循环 将id写入useridArray
      selectDataArray.forEach(data => {
        let userId = data.id
        this.userIdArray.push(userId)
      })
    },

    //查询用户列表信息
    getData(current){
      doGet("/api/users", {
        current: current //当前查询第几页
      }).then( resp => {
        if(resp.data.code === 200){
          console.log(resp)
          this.userList = resp.data.data.list;
          this.pageSize = resp.data.data.pageSize;
          this.total = resp.data.data.total;
        }
      })
    },

    //分页函数（current -- element-plus组件传来的，当前页）
    toPage(current) {
      this.getData(current)
    },
    //用户详情，跳转路由
    view(id) {
      console.log(id);
      //跳转到 /dashboard/user/1 路由上
      this.$router.push("/dashboard/user/" + id)
    },
    //新增用户,弹出弹窗
    add() {
      this.userDialogVisible = true;
      this.userQuery = {}
    },
    //提交信息 添加/编辑用户
    userSubmit(current) {
      let formData = new FormData();
      //循环追加userQuery数据写入formData
      for(let field in this.userQuery){
        formData.append(field, this.userQuery[field])
      }
      this.$refs.userRefForm.validate((isValid) => {
        if(isValid){
          if(this.userQuery.id>0){
            //编辑用户
            doPut("/api/user", formData).then(resp => {
              if(resp.data.code === 200) {
                messageTip("编辑成功", "success")
                this.userDialogVisible = false;
                this.reload();
              }else{
                messageTip("编辑失败", "error")
              }
            })
          } else {
            //添加用户
            doPost("/api/user", formData).then(resp => {
              if(resp.data.code === 200) {
                messageTip("添加成功", "success")
                this.userDialogVisible = false;
                this.reload();
              }else{
                messageTip("添加失败", "error")
              }
            })
          }
        } else {
          messageTip("请检验表单数据格式", "warning");
        }
      })
    },
    //编辑用户，数据回显
    edit(id) {
      this.userDialogVisible = true;
      //数据回显
      this.loadUser(id);
    },
    //编辑用户时查询该用户信息
    loadUser(id){
      doGet("/api/user/"+id).then(resp => {
        this.userQuery = resp.data.data;
        //不显示密码
        this.userQuery.loginPwd = '';
      })
    },
    //删除用户
    del(id) {
      messageConfirm("确认删除？").then(() => {
        doDelete("/api/user/"+id, {}).then(resp => {
          if(resp.data.code === 200){
            messageTip("删除成功", "success")
            //页面刷新
            this.reload();
          } else {
            messageTip("删除失败, "+resp.data.msg, "error")
          }
        })
      }).catch(() => {
        messageTip("取消删除", "warning")
      })
    },
    //批量删除
    batchDel() {
      if(this.userIdArray.length <= 0){
        messageTip("请选择要删除的用户", "warning")
        return ;
      }
      //数组userIdArray[1,2,3,4] --> 字符串"1,2,3,4"
      let idStr = this.userIdArray.join(',')

      messageConfirm("确认删除？").then(() => {
        //批量删除
        doDelete("/api/user", {
          idStr: idStr,
        }).then((resp) => {
          if(resp.data.code === 200){
            messageTip("批量删除成功", "success")
            //页面刷新
            this.reload();
          } else {
            messageTip("批量删除失败, "+resp.data.msg, "error")
          }
        })
      }).catch(() => {
        messageTip("取消批量删除", "warning")
      })
    },
  }

}
</script>

<style scoped>
.el-table{
  margin-top: 12px;
}
.el-pagination{
  margin-top: 12px;
}
.op_button{
  width: 40px;
  height: 25px;
  font-size: x-small
}
.el-select{
  width: 100%;
}
</style>