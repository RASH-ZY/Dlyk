/* 在vue框架中导入createApp函数，带{}一般是函数，不带{}一般是组件 */
import { createApp } from 'vue'

/* 在element-plus框架中带入ElementPlus组件 */
import ElementPlus from 'element-plus'

/* 导入国际化的中文包 */
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

/* 导入element-plus样式 */
import 'element-plus/dist/index.css'

/* 导入router组件 */
import router from './router/router.js'

/* 导入图标组件 */
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

/* 在./App.vue页面中导入App组件（根组件） */
import App from './App.vue'
import {doGet} from "./http/httpRequest.js";

let app = createApp(App)

/* 注册图标 */
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

//自定义指令
//el：指令所绑定到的页面dom元素。这可以用于直接操作DOM。
//binding：是一个对象，里面包含很多属性，重点看value属性：传递给指令的值。我们传的是 clue:delete 这个值
app.directive("hasPermission",  (el, binding) => {
    // 这会在 `mounted` 和 `updated` 时都调用
    // 每个el元素中的v-hasPermission都会调用该函数
    doGet("/api/login/info", {}).then(resp => {
        //获取用户权限列表
        let user = resp.data.data;
        let permissionList = user.permissionList;

        let flag = false;

        //遍历权限集合，匹配当前dom元素是否匹配对应的权限
        for (let key in permissionList) {
            //拥有该权限，结束for循环
            if (permissionList[key] === binding.value) {
                flag = true;
                break;
            }
        }

        //遍历结束，仍未发现拥有该权限，删除dom元素及其子元素
        if (!flag) {
            //没有权限，把页面元素隐藏掉
            //el.style.display = 'none';

            //把没有权限的按钮dom元素删除
            el.parentNode && el.parentNode.removeChild(el)
        }
    })
})

/* 利用createApp函数创建vue应用，mount意为将该vue挂载到id为app的div中 */
app.use(ElementPlus, {locale: zhCn}).use(router).mount('#app')
