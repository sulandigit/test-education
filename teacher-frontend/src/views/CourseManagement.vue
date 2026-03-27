<template>
  <div class="course-management">
    <!-- 页面标题和操作按钮 -->
    <div class="page-header">
      <div class="header-left">
        <h2>课程管理</h2>
        <p>管理您的所有课程内容</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleAddCourse">
          <el-icon><Plus /></el-icon>
          新增课程
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="课程名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入课程名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="课程状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="进行中" value="active" />
            <el-option label="已完成" value="completed" />
            <el-option label="暂停" value="paused" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程类型">
          <el-select
            v-model="searchForm.category"
            placeholder="请选择类型"
            clearable
            style="width: 150px"
          >
            <el-option label="前端开发" value="frontend" />
            <el-option label="后端开发" value="backend" />
            <el-option label="移动开发" value="mobile" />
            <el-option label="数据科学" value="data" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 课程列表 -->
    <el-card class="table-card">
      <el-table
        :data="courseList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="课程ID" width="80" />
        <el-table-column prop="name" label="课程名称" min-width="200">
          <template #default="{ row }">
            <div class="course-info">
              <div class="course-name">{{ row.name }}</div>
              <div class="course-desc">{{ row.description }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="课程类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getCategoryType(row.category)">
              {{ getCategoryLabel(row.category) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="studentCount" label="学生数量" width="100" />
        <el-table-column prop="duration" label="课程时长" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">
              <el-icon><View /></el-icon>
            </el-button>
            <el-button type="text" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button
              type="text"
              size="small"
              :class="row.status === 'active' ? 'pause-btn' : 'resume-btn'"
              @click="handleToggleStatus(row)"
            >
              <el-icon>
                <VideoPause v-if="row.status === 'active'" />
                <VideoPlay v-else />
              </el-icon>
            </el-button>
            <el-button
              type="text"
              size="small"
              class="delete-btn"
              @click="handleDelete(row)"
            >
              <el-icon><Delete /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑课程对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="courseFormRef"
        :model="courseForm"
        :rules="courseRules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="courseForm.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入课程描述"
          />
        </el-form-item>
        <el-form-item label="课程类型" prop="category">
          <el-select v-model="courseForm.category" placeholder="请选择课程类型">
            <el-option label="前端开发" value="frontend" />
            <el-option label="后端开发" value="backend" />
            <el-option label="移动开发" value="mobile" />
            <el-option label="数据科学" value="data" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程时长" prop="duration">
          <el-input v-model="courseForm.duration" placeholder="如：20小时" />
        </el-form-item>
        <el-form-item label="课程价格" prop="price">
          <el-input-number
            v-model="courseForm.price"
            :min="0"
            :precision="2"
            placeholder="0.00"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'CourseManagement',
  setup() {
    const loading = ref(false)
    const dialogVisible = ref(false)
    const submitting = ref(false)
    const courseFormRef = ref(null)
    const isEdit = ref(false)

    // 搜索表单
    const searchForm = reactive({
      name: '',
      status: '',
      category: ''
    })

    // 分页信息
    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0
    })

    // 课程列表
    const courseList = ref([])

    // 课程表单
    const courseForm = reactive({
      id: null,
      name: '',
      description: '',
      category: '',
      duration: '',
      price: 0,
      status: 'active'
    })

    // 表单验证规则
    const courseRules = {
      name: [
        { required: true, message: '请输入课程名称', trigger: 'blur' }
      ],
      description: [
        { required: true, message: '请输入课程描述', trigger: 'blur' }
      ],
      category: [
        { required: true, message: '请选择课程类型', trigger: 'change' }
      ],
      duration: [
        { required: true, message: '请输入课程时长', trigger: 'blur' }
      ],
      price: [
        { required: true, message: '请输入课程价格', trigger: 'blur' }
      ]
    }

    const dialogTitle = ref('新增课程')

    // 获取课程列表
    const getCourseList = () => {
      loading.value = true
      // 模拟API调用
      setTimeout(() => {
        courseList.value = [
          {
            id: 1,
            name: 'Vue.js 3.0 开发实战',
            description: '从零开始学习Vue.js 3.0的现代前端开发技术',
            category: 'frontend',
            studentCount: 45,
            duration: '20小时',
            price: 299.00,
            status: 'active',
            createTime: '2024-01-15 10:30:00'
          },
          {
            id: 2,
            name: 'JavaScript 高级程序设计',
            description: '深入理解JavaScript核心概念和高级特性',
            category: 'frontend',
            studentCount: 38,
            duration: '30小时',
            price: 399.00,
            status: 'active',
            createTime: '2024-01-10 14:20:00'
          },
          {
            id: 3,
            name: 'Node.js 后端开发',
            description: '使用Node.js构建高性能的后端应用程序',
            category: 'backend',
            studentCount: 25,
            duration: '25小时',
            price: 349.00,
            status: 'completed',
            createTime: '2024-01-05 09:15:00'
          },
          {
            id: 4,
            name: 'React Native 移动开发',
            description: '跨平台移动应用开发实战教程',
            category: 'mobile',
            studentCount: 18,
            duration: '28小时',
            price: 399.00,
            status: 'paused',
            createTime: '2024-01-01 16:45:00'
          }
        ]
        pagination.total = courseList.value.length
        loading.value = false
      }, 500)
    }

    // 获取类型标签样式
    const getCategoryType = (category) => {
      const types = {
        frontend: 'primary',
        backend: 'success',
        mobile: 'warning',
        data: 'info'
      }
      return types[category] || 'info'
    }

    // 获取类型标签文本
    const getCategoryLabel = (category) => {
      const labels = {
        frontend: '前端开发',
        backend: '后端开发',
        mobile: '移动开发',
        data: '数据科学'
      }
      return labels[category] || '未知'
    }

    // 获取状态标签样式
    const getStatusType = (status) => {
      const types = {
        active: 'success',
        completed: 'info',
        paused: 'warning'
      }
      return types[status] || 'info'
    }

    // 获取状态标签文本
    const getStatusLabel = (status) => {
      const labels = {
        active: '进行中',
        completed: '已完成',
        paused: '暂停'
      }
      return labels[status] || '未知'
    }

    // 搜索
    const handleSearch = () => {
      pagination.current = 1
      getCourseList()
    }

    // 重置搜索
    const handleReset = () => {
      Object.assign(searchForm, {
        name: '',
        status: '',
        category: ''
      })
      pagination.current = 1
      getCourseList()
    }

    // 分页大小改变
    const handleSizeChange = (size) => {
      pagination.size = size
      getCourseList()
    }

    // 当前页改变
    const handleCurrentChange = (current) => {
      pagination.current = current
      getCourseList()
    }

    // 新增课程
    const handleAddCourse = () => {
      isEdit.value = false
      dialogTitle.value = '新增课程'
      Object.assign(courseForm, {
        id: null,
        name: '',
        description: '',
        category: '',
        duration: '',
        price: 0,
        status: 'active'
      })
      dialogVisible.value = true
    }

    // 查看课程
    const handleView = (row) => {
      ElMessage.info(`查看课程：${row.name}`)
    }

    // 编辑课程  
    const handleEdit = (row) => {
      isEdit.value = true
      dialogTitle.value = '编辑课程'
      Object.assign(courseForm, { ...row })
      dialogVisible.value = true
    }

    // 切换课程状态
    const handleToggleStatus = (row) => {
      const newStatus = row.status === 'active' ? 'paused' : 'active'
      const action = newStatus === 'active' ? '恢复' : '暂停'
      
      ElMessageBox.confirm(
        `确定要${action}课程"${row.name}"吗？`,
        '确认操作',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        row.status = newStatus
        ElMessage.success(`课程${action}成功`)
      })
    }

    // 删除课程
    const handleDelete = (row) => {
      ElMessageBox.confirm(
        `确定要删除课程"${row.name}"吗？此操作不可恢复。`,
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        const index = courseList.value.findIndex(item => item.id === row.id)
        if (index > -1) {
          courseList.value.splice(index, 1)
          ElMessage.success('删除成功')
        }
      })
    }

    // 提交表单
    const handleSubmit = () => {
      courseFormRef.value?.validate((valid) => {
        if (valid) {
          submitting.value = true
          // 模拟API调用
          setTimeout(() => {
            if (isEdit.value) {
              const index = courseList.value.findIndex(item => item.id === courseForm.id)
              if (index > -1) {
                courseList.value[index] = { ...courseForm }
              }
              ElMessage.success('更新成功')
            } else {
              const newCourse = {
                ...courseForm,
                id: Date.now(),
                studentCount: 0,
                createTime: new Date().toLocaleString()
              }
              courseList.value.unshift(newCourse)
              ElMessage.success('创建成功')
            }
            submitting.value = false
            dialogVisible.value = false
          }, 1000)
        }
      })
    }

    // 关闭对话框
    const handleDialogClose = () => {
      courseFormRef.value?.resetFields()
    }

    onMounted(() => {
      getCourseList()
    })

    return {
      loading,
      dialogVisible,
      submitting,
      courseFormRef,
      isEdit,
      searchForm,
      pagination,
      courseList,
      courseForm,
      courseRules,
      dialogTitle,
      getCategoryType,
      getCategoryLabel,
      getStatusType,
      getStatusLabel,
      handleSearch,
      handleReset,
      handleSizeChange,
      handleCurrentChange,
      handleAddCourse,
      handleView,
      handleEdit,
      handleToggleStatus,
      handleDelete,
      handleSubmit,
      handleDialogClose
    }
  }
}
</script>

<style lang="scss" scoped>
.course-management {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;

    .header-left {
      h2 {
        margin: 0 0 8px 0;
        color: #2c3e50;
        font-size: 24px;
        font-weight: 600;
      }

      p {
        margin: 0;
        color: #8492a6;
        font-size: 14px;
      }
    }
  }

  .search-card {
    margin-bottom: 20px;

    .search-form {
      .el-form-item {
        margin-bottom: 0;
      }
    }
  }

  .table-card {
    .course-info {
      .course-name {
        font-weight: 500;
        color: #2c3e50;
        margin-bottom: 4px;
      }

      .course-desc {
        font-size: 12px;
        color: #8492a6;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        max-width: 300px;
      }
    }

    .pagination-wrapper {
      margin-top: 20px;
      text-align: right;
    }
  }

  :deep(.el-button.is-text) {
    padding: 0;
    margin-right: 8px;

    &:last-child {
      margin-right: 0;
    }

    &.pause-btn {
      color: #e6a23c;
    }

    &.resume-btn {
      color: #67c23a;
    }

    &.delete-btn {
      color: #f56c6c;
    }
  }
}
</style>