<template>
  <el-form :inline="true" :model="activityQuery" :rules="activityRules">
    <el-form-item label="负责人" style="width: 200px">
      <el-select
          v-model="activityQuery.ownerId"
          placeholder="请输入负责人"
          @click="loadOwner"
          clearable>
        <el-option
            v-for="item in ownerOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"/>
      </el-select>
    </el-form-item>

    <el-form-item label="活动名称">
      <el-input v-model="activityQuery.name" placeholder="请输入活动名称" clearable />
    </el-form-item>

    <el-form-item label="活动时间">
      <el-date-picker
          v-model="activityQuery.activityTime"
          type="datetimerange"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"/>
    </el-form-item>

    <el-form-item label="活动预算" prop="cost">
      <el-input v-model="activityQuery.cost" placeholder="请输入活动预算" clearable />
    </el-form-item>

    <el-form-item label="创建时间">
      <el-date-picker
          v-model="activityQuery.createTime"
          type="datetime"
          placeholder="请选择创建时间"
          value-format="YYYY-MM-DD HH:mm:ss"/>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="onSearch" >搜索</el-button>
      <el-button type="primary" @click="onReset" plain>重置</el-button>
    </el-form-item>
  </el-form>

  <el-button type="primary" @click="add">录入市场活动</el-button>
  <el-button type="danger" @click="batchDel">批量删除</el-button>
  <el-table
      :data="activityList"
      style="width: 100%"
      @selection-change="handleSelectionChange">
    <el-table-column type="selection" width="55" />
    <el-table-column type="index" label="序号" width="60" />
    <el-table-column property="ownerDO.name" label="负责人" show-overflow-tooltip />
    <el-table-column property="name" label="活动名称" show-overflow-tooltip />
    <el-table-column property="startTime" label="开始时间" show-overflow-tooltip />
    <el-table-column property="endTime" label="结束时间" show-overflow-tooltip />
    <el-table-column property="cost" label="活动预算" show-overflow-tooltip />
    <el-table-column property="createTime" label="创建时间" show-overflow-tooltip />
    <el-table-column label="操作"  width="180px">
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
</template>

<script>
import {doGet} from "../http/httpRequest.js";

export default {
  name: "ActivityView",
  data() {
    return {
      //市场活动搜索表单对象
      activityQuery: {},
      //市场活动列表对象
      activityList: [{
        ownerDO: {}
      }],
      //页大小
      pageSize: 0,
      //分页数据总数
      total: 0,
      ownerOptions: [{}],
      activityRules: {
        cost: [
          { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入整数或者保留至多两位小数', trigger: 'blur' }
        ],
      }
    }
  },

  mounted() {
    this.getData(1);
  },

  methods: {
    //查询市场活动列表信息
    getData(current){
      let startTime = '';
      let endTime = '';
      // console.log(this.activityQuery.activityTime)
      // console.log(typeof this.activityQuery.activityTime)
      for (let key in this.activityQuery.activityTime) {
        if (key === '0') {
          startTime = this.activityQuery.activityTime[key];
          // console.log('startTime = '+startTime)
        }
        if (key === '1') {
          endTime = this.activityQuery.activityTime[key];
          // console.log('endTime = '+endTime)
        }
      }

      doGet("/api/activity", {
        current : current, //当前查询第几页
        //6个搜索条件参数
        ownerId : this.activityQuery.ownerId,
        name : this.activityQuery.name,
        startTime : startTime,
        endTime  : endTime,
        cost : this.activityQuery.cost,
        createTime : this.activityQuery.createTime
      }).then( resp => {
        if(resp.data.code === 200){
          this.activityList = resp.data.data.list;
          this.pageSize = resp.data.data.pageSize;
          this.total = resp.data.data.total;
        }
      })
    },
    //分页函数（current -- element-plus组件传来的，当前页）
    toPage(current) {
      this.getData(current)
    },
    //加载负责人列表 -- 为下拉列表提供默认选择
    loadOwner() {
      doGet("/api/owner", {}).then(resp => {
        // console.log('this.activityQuery = ' + this.activityQuery)
        if(resp.data.code === 200) {
          this.ownerOptions = resp.data.data;
        }
      })
    },
    //搜索
    onSearch() {
      this.getData(1);
    },
    //录入市场活动，跳转路由
    add() {
      this.$router.push("/dashboard/activity/add")
    },
    //重置搜索框信息
    onReset() {
      this.activityQuery = {};
    },
    //市场活动 跳转路由
    view(id) {
      this.$router.push("/dashboard/activity/" + id);
    },
    //编辑市场活动信息, 跳转路由
    edit(id) {
      this.$router.push("/dashboard/activity/edit/" + id);
    },

  },
}
</script>


<style scoped>
.el-form{
  margin-bottom: 20px;
}
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
</style>