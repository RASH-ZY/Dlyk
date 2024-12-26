<template>
  <el-container>
    <el-aside width="200px">
      <img src="../assets/loginBox-i-ou09hf.svg">
      <p class="imgTitle">
        欢迎使用动力云客系统
      </p>
    </el-aside>

    <el-main>
      <div class="LoginTitle">欢迎登录</div>
<!--      <br>-->
      <el-form ref="loginRefForm" :model="user" :rules="loginRules" label-width="120px">
        <el-form-item label="账号" prop="loginAct">
          <el-input v-model="user.loginAct" />
        </el-form-item>

        <el-form-item label="密码" prop="loginPwd">
          <el-input v-model="user.loginPwd" type="password"/>
        </el-form-item>

        <el-form-item>
          <el-button @click="login" type="primary">登录</el-button>
        </el-form-item>

        <el-form-item>
          <el-checkbox label="记住我" v-model="user.rememberMe" />
        </el-form-item>
      </el-form>

    </el-main>
  </el-container>
</template>

<script>
import {doGet, doPost} from "../http/httpRequest.js";
import {ElMessage} from "element-plus";
import {getTokenName, messageTip, removeToken} from "../util/util.js";

export default {
  // 组件名字
  name: "LoginView",

  /* 定义页面使用到的变量 */
  data() {
    return {
      //对象变量定义：{}
      user: {},
      //字符串变量定义：''
      name: '',
      //数字变量定义：0
      age: 0,
      //数组变量定义：[]
      arr: [],
      //List集合对象定义：[{}]
      userList: [{}],
      loginRules: {
        //定义账号验证规则，规则可以有多个，为数组
        loginAct: [
          { required: true, message: '请输入登录账号', trigger: 'blur' },
        ],
        //定义密码验证规则，规则可以有多个，为数组
        loginPwd: [
          { required: true, message: '请输入登录密码', trigger: 'blur' },
          { min: 6, max: 16, message: '登录密码长度为6-16位', trigger: 'blur' },
        ],
      },
    }
  },

  //页面渲染的时候会触发钩子函数
  mounted() {
    this.freeLogin();
  },

  /* 页面中要使用到的js函数，在methods属性中定义 */
  methods: {
    //登录函数
    login() {
      // alert(this.user.rememberMe)
      //提交前验证表单输入框的合法性
      this.$refs.loginRefForm.validate( (isValid) =>{
        //isValid是验证结果，是一个bool值
        if (isValid) {
          //验证通过，提交登录请求
          let formData = new FormData();
          formData.append("loginAct", this.user.loginAct);
          formData.append("loginPwd", this.user.loginPwd);
          formData.append("rememberMe", this.user.rememberMe);

          doPost("/api/login", formData).then( (resp) => {
            // console.log(resp);
            if(resp.data.code === 200){
              //登录成功
              messageTip("登录成功", "success");
              //删除历史localStorage与sessionStorage中的token
              removeToken();
              //前端存储jwt, 判断是否"记住我"
              if(this.user.rememberMe === true){
                //localStorage更换标签页时所存放的数据仍然有效, sessionStorage更换标签页时所存放的数据失效
                window.localStorage.setItem(getTokenName(), resp.data.data);
              } else {
                window.sessionStorage.setItem(getTokenName(), resp.data.data);
              }
              //跳转到系统主页面
              window.location.href = "/dashboard";
            }else{
              //登录失败
              messageTip("登录失败", "error")
            }
          })
        }
      })
    },

    //免登录
    freeLogin() {
      let token = window.localStorage.getItem(getTokenName());
      if(token){ //token存在
        doGet("/api/login/free").then(resp => {
          //token匹配成功，跳转到首页，即免登录
          if(resp.data.code === 200){
            window.location.href = "/dashboard"
          }
        })
      }
    },
  },


}
</script>

<style scoped>
.el-aside {
  background: #1a1a1a;
  width: 40%;
  text-align: center;
}
.el-main {
  /* calc(): 计算屏幕的高度 */
  height: calc(100vh);
}
img {
  height: 413px;
}
.imgTitle {
  color: #f9f9f9;
  font-size: 28px;
}
.el-form {
  width: 60%;
  margin: auto;
}
.LoginTitle {
  text-align: center;
  margin-top: 100px;
  margin-bottom: 25px;
  font-weight: bold;
}
.el-button {
  width: 100%;
}
</style>