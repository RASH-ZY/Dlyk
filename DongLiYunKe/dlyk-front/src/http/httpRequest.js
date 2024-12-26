/**
 * 封装doGet、doPost、doPut、doDelete方法
 */
import axios from 'axios';
import {getTokenName, messageConfirm, messageTip, removeToken} from "../util/util.js";
import {ElMessage, ElMessageBox} from "element-plus";
//定义后端接口地址前缀
axios.defaults.baseURL = "http://localhost:8089";

// doGet请求
export function doGet (url, params) {
    return axios ({
        method: 'get', // 请求方式
        url: url, // 请求路径
        params: params, //get求参数为params
        dataType: 'json'
    })
}

// doPost请求
export function doPost (url, data) {
    return axios ({
        method: 'post', // 请求方式
        url: url, // 请求路径
        data: data, //post求参数为data
        dataType: 'json'
    })
}

// doPut请求
export function doPut (url, data) {
    return axios ({
        method: 'put', // 请求方式
        url: url, // 请求路径
        data: data, //put请求参数为data
        dataType: 'json'
    })
}

// doDelete请求
export function doDelete (url, params) {
    return axios ({
        method: 'delete', // 请求方式
        url: url, // 请求路径
        params: params, //delete求参数为params
        dataType: 'json'
    })
}

// 添加请求拦截器
axios.interceptors.request.use( (config) => {
    // 在发送请求之前做些什么，在请求头中放一个token（jwt），传给后端接口
    let token = window.sessionStorage.getItem(getTokenName());
    if(!token){
        //sessionStorage中token不存在 为空，尝试从localStorage获取token
        token = window.localStorage.getItem(getTokenName());
        if(token){
            //token不为空，代表用户选择了"记住我"，rememberMe置为true并写入请求头
            config.headers['rememberMe'] = true;
        }
    }
    if(token){
        //将token赋给请求头中的Authorization
        config.headers['Authorization'] = token;
    }
    return config;
},  (error) => {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
axios.interceptors.response.use( (response) => {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么，拦截token验证的结果，进行相应的提示和页面跳转
    if(response.data.code > 900){ //code>900说明token验证未通过
        //前端给用户提示，跳转至登录页面
        messageConfirm(response.data.msg+', 是否重新登录？')
        .then(() => { //用户点击确定 执行该代码块
            //后端token有问题，前端就没必要存在该token了
            removeToken()
            //跳转登录页
            window.location.href = "/";
        }).catch(() => { //用户点击取消 执行该代码块
           messageTip("取消登录", "warning");
        })
        return ;
    }
    return response;
}, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
});