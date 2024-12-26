<template>
  <el-form ref="activityRefForm" :model="activityQuery" :rules="activityRules" label-width="110px">
    <el-form-item label="负责人" prop="loginAct">
      <el-select v-model="activityQuery.ownerId" class="width" placeholder="请选择" >
        <el-option
            v-for="item in ownerOptions"
            :key="item.value"
            :label="item.name"
            :value="item.id"/>
      </el-select>
    </el-form-item>

    <el-form-item label="活动名称" prop="name" >
      <el-input v-model="activityQuery.name" class="width" placeholder="请输入活动名称" />
    </el-form-item>

    <el-form-item label="开始时间" prop="startTime">
      <el-date-picker
          v-model="activityQuery.startTime"
          style="width: 100%"
          type="datetime"
          placeholder="请选择开始时间"
          value-format="YYYY-MM-DD HH:mm:ss"/>
    </el-form-item>

    <el-form-item label="结束时间" prop="endTime">
      <el-date-picker
          v-model="activityQuery.endTime"
          style="width: 100%"
          type="datetime"
          placeholder="请选择结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"/>
    </el-form-item>

    <el-form-item label="活动预算" prop="cost" >
      <el-input v-model="activityQuery.cost" class="width" placeholder="请输入活动预算" />
    </el-form-item>

    <el-form-item label="活动描述" prop="description">
      <el-input
          v-model="activityQuery.description"
          style="width: 100%"
          :rows="6"
          type="textarea"
          placeholder="请输入活动描述"/>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="activitySubmit">提交</el-button>
      <el-button @click="goBack">返回</el-button>
    </el-form-item>

  </el-form>
</template>

<script>
import {doGet, doPost, doPut} from "../http/httpRequest.js";
import {goBack, messageTip} from "../util/util.js";

export default {
  name: "ActivityRecordView",

  inject: ["reload"],

  data() {
    return {
      //市场活动表单对象
      activityQuery: {},
      //市场活动表单验证规则
      activityRules: {
        ownerId: [
          { required: true, message: '请选择负责人', trigger: 'blur' },
        ],
        name: [
          { required: true, message: '请输入活动名称', trigger: 'blur' },
        ],
        startTime: [
          { required: true, message: '请选择开始时间', trigger: 'blur' },
        ],
        endTime: [
          { required: true, message: '请选择结束时间', trigger: 'blur' },
          // {
          //   validator: (rule, value, callback) => {
          //     if (value && this.activityQuery.startDate) {
          //       const startDate = new Date(this.activityQuery.startDate);
          //       const endDate = new Date(value);
          //       if (startDate >= endDate) {
          //         callback(new Error('结束日期必须大于开始日期'));
          //       } else {
          //         callback();
          //       }
          //     } else {
          //       callback();
          //     }
          //   },
          //   trigger: 'change'
          // }
        ],
        cost: [
          { required: true, message: '请输入活动预算', trigger: 'blur' },
          { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入整数或者保留至多两位小数', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入活动描述', trigger: 'blur' },
          { min: 5, max: 255, message: '活动描述长度为5-255个字符', trigger: 'blur' },
        ]
      },
      //负责人下拉选项
      ownerOptions: [{}],
    }
  },

  mounted() {
    this.loadOwner();
    this.loadActivity();
  },

  methods: {
    //加载负责人列表 -- 为下拉列表提供默认选择
    loadOwner() {
      doGet("/api/owner", {}).then(resp => {
        if(resp.data.code === 200) {
          this.ownerOptions = resp.data.data;
        }
      })
    },
    goBack,
    //录入/编辑市场活动，提交表单
    activitySubmit() {
      // alert(this.activityQuery.id)
      let formData = new FormData();
      //将activityQuery数据追加至formData以表单形式提交数据
      for(let field in this.activityQuery) {
        if (this.activityQuery[field]){
          //只追加不为空的数据(解决返回到后端时，null值转换其他数据类型出现异常的问题)
          formData.append(field, this.activityQuery[field])
        }
      }
      console.log(formData)
      //检验表单校验是否通过
      this.$refs.activityRefForm.validate(isValid => {
        // alert(this.activityQuery.startTime+this.activityQuery.endTime);
        if(isValid) {
          //校验通过
          if (this.activityQuery.id > 0){
            //编辑市场活动
            doPut("/api/activity", formData).then(resp => {
              if(resp.data.code === 200) {
                messageTip("编辑成功", "success")
                //跳转到市场活动列表页
                this.$router.push("/dashboard/activity")
              }else{
                messageTip("编辑失败", "error")
              }
            })
          } else {
            //录入市场活动
            doPost("/api/activity", formData).then(resp => {
              if (resp.data.code === 200) {
                messageTip("提交成功", "success");
                //跳转到市场活动列表页
                this.$router.push("/dashboard/activity")
              } else {
                messageTip("提交失败", "error");
              }
            })
          }
        } else {
          //校验失败
          messageTip("请检验表单数据格式", "warning");
        }
      })
    },
    //加载市场活动数据
    loadActivity() {
      //获取动态路由中的id
      let id = this.$route.params.id;
      //id存在，即是编辑操作
      if(id){
        //获取当前市场活动数据
        doGet("/api/activity/" + id, {}).then(resp => {
          if(resp.data.code === 200){
            this.activityQuery = resp.data.data;
          }
        })
      }
    }
  }
}
</script>


<style scoped>
.width{
  width: 100%;
}
</style>