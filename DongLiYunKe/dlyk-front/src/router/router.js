//vue-router依赖库导入createRouter, createWebHistory
import { createRouter, createWebHistory } from "vue-router";

//定义变量
let router = createRouter ({
    //路由历史
    history: createWebHistory(),

    //配置路由，一个数组，可以配置多个
    routes: [
        {
            //路由路径
            path: '/',
            //路径对应页面
            component : () => import('../view/LoginView.vue'),
        },
        {
            path: '/dashboard',
            component : () => import('../view/DashboardView.vue'),
            children: [
                {
                    //统计页面
                    path: '',
                    component : () => import('../view/StatisticView.vue'),
                },
                {
                    //子路由，路径不要'/'
                    path: 'user',
                    component : () => import('../view/UserView.vue'),
                },
                {
                    //子路由，路径不要'/', id是动态变量，即该路由为动态路由
                    path: 'user/:id',
                    component : () => import('../view/UserDetailView.vue'),
                },
                {
                    path: 'activity',
                    component : () => import('../view/ActivityView.vue'),
                },
                {
                    path: 'activity/add',
                    component : () => import('../view/ActivityRecordView.vue'),
                },
                {
                    path: 'activity/edit/:id',
                    component : () => import('../view/ActivityRecordView.vue'),
                },
                {
                    path: 'activity/:id',
                    component : () => import('../view/ActivityDetailView.vue'),
                },{
                    path: 'clue',
                    component : () => import('../view/ClueView.vue'),
                },
                {
                    path: 'clue/add',
                    component : () => import('../view/ClueRecordView.vue'),
                },
                {
                    path: 'clue/edit/:id',
                    component : () => import('../view/ClueRecordView.vue'),
                },{
                    path: 'clue/detail/:id',
                    component : () => import('../view/ClueDetailView.vue'),
                },
                {
                    path: 'customer',
                    component : () => import('../view/CustomerView.vue'),
                },
            ]
        },
    ]
})
export default router;