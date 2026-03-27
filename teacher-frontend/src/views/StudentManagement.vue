<template>
  <div class="student-management">
    <!-- 页面标题和操作按钮 -->
    <div class="page-header">
      <div class="header-left">
        <h2>学生管理</h2>
        <p>管理您的所有学生信息</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleAddStudent">
          <el-icon><Plus /></el-icon>
          新增学生
        </el-button>
        <el-button type="success" @click="handleBatchImport">
          <el-icon><Upload /></el-icon>
          批量导入
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="学生姓名">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入学生姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="学生邮箱">
          <el-input
            v-model="searchForm.email"
            placeholder="请输入学生邮箱"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="学生状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="在读" value="active" />
            <el-option label="已毕业" value="graduated" />
            <el-option label="暂停" value="suspended" />
          </el-select>
        </el-form-item>
        <el-form-item label="所选课程">
          <el-select
            v-model="searchForm.courseId"
            placeholder="请选择课程"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="course in courseOptions"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 学生列表 -->
    <el-card class="table-card">
      <div class="table-header">
        <div class="table-actions">
          <el-checkbox
            v-model="selectAll"
            :indeterminate="isIndeterminate"
            @change="handleSelectAll"
          >
            全选
          </el-checkbox>
          <el-button
            type="danger"
            size="small"
            :disabled="selectedStudents.length === 0"
            @click="handleBatchDelete"
          >
            批量删除
          </el-button>
        </div>
      </div>

      <el-table
        :data="studentList"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="学生ID" width="80" />
        <el-table-column label="学生信息" min-width="200">
          <template #default="{ row }">
            <div class="student-info">
              <el-avatar :size="40" :src="row.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="info-content">
                <div class="student-name">{{ row.name }}</div>
                <div class="student-email">{{ row.email }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            <el-tag :type="row.gender === '男' ? 'primary' : 'danger'" size="small">
              {{ row.gender }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="已选课程" min-width="200">
          <template #default="{ row }">
            <div class="course-tags">
              <el-tag
                v-for="course in row.courses"
                :key="course.id"
                size="small"
                style="margin-right: 4px; margin-bottom: 4px"
              >
                {{ course.name }}
              </el-tag>
              <span v-if="row.courses.length === 0" class="no-course">暂无课程</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinDate" label="入学时间" width="120" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">
              <el-icon><View /></el-icon>
            </el-button>
            <el-button type="text" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button type="text" size="small" @click="handleAssignCourse(row)">
              <el-icon><Plus /></el-icon>
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

    <!-- 新增/编辑学生对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="studentFormRef"
        :model="studentForm"
        :rules="studentRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学生姓名" prop="name">
              <el-input v-model="studentForm.name" placeholder="请输入学生姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="studentForm.gender">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学生邮箱" prop="email">
              <el-input v-model="studentForm.email" placeholder="请输入学生邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="studentForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="出生日期" prop="birthday">
          <el-date-picker
            v-model="studentForm.birthday"
            type="date"
            placeholder="请选择出生日期"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="学生地址" prop="address">
          <el-input v-model="studentForm.address" placeholder="请输入学生地址" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="studentForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
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

    <!-- 分配课程对话框 -->
    <el-dialog
      v-model="assignDialogVisible"
      title="分配课程"
      width="500px"
    >
      <div class="assign-content">
        <p>为学生 <strong>{{ currentStudent?.name }}</strong> 分配课程：</p>
        <el-checkbox-group v-model="selectedCourses">
          <div class="course-options">
            <el-checkbox
              v-for="course in courseOptions"
              :key="course.id"
              :label="course.id"
              class="course-option"
            >
              <div class="course-item">
                <div class="course-name">{{ course.name }}</div>
                <div class="course-desc">{{ course.description }}</div>
              </div>
            </el-checkbox>
          </div>
        </el-checkbox-group>
      </div>
      
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignSubmit" :loading="assignSubmitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'StudentManagement',
  setup() {
    const loading = ref(false)
    const dialogVisible = ref(false)
    const assignDialogVisible = ref(false)
    const submitting = ref(false)
    const assignSubmitting = ref(false)
    const studentFormRef = ref(null)
    const isEdit = ref(false)

    // 搜索表单
    const searchForm = reactive({
      name: '',
      email: '',
      status: '',
      courseId: null
    })

    // 分页信息
    const pagination = reactive({
      current: 1,
      size: 10,
      total: 0
    })

    // 学生列表
    const studentList = ref([])
    const selectedStudents = ref([])
    const currentStudent = ref(null)
    const selectedCourses = ref([])

    // 课程选项
    const courseOptions = ref([
      { id: 1, name: 'Vue.js 3.0 开发实战', description: '从零开始学习Vue.js 3.0' },
      { id: 2, name: 'JavaScript 高级程序设计', description: '深入理解JavaScript核心概念' },
      { id: 3, name: 'Node.js 后端开发', description: '使用Node.js构建后端应用' },
      { id: 4, name: 'React Native 移动开发', description: '跨平台移动应用开发' }
    ])

    // 学生表单
    const studentForm = reactive({
      id: null,
      name: '',
      email: '',
      phone: '',
      gender: '男',
      birthday: null,
      address: '',
      remark: '',
      status: 'active'
    })

    // 表单验证规则
    const studentRules = {
      name: [
        { required: true, message: '请输入学生姓名', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入学生邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入联系电话', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
      ],
      gender: [
        { required: true, message: '请选择性别', trigger: 'change' }
      ]
    }

    const dialogTitle = ref('新增学生')

    // 全选相关
    const selectAll = ref(false)
    const isIndeterminate = computed(() => {
      const selectedCount = selectedStudents.value.length
      return selectedCount > 0 && selectedCount < studentList.value.length
    })

    // 获取学生列表
    const getStudentList = () => {
      loading.value = true
      // 模拟API调用
      setTimeout(() => {
        studentList.value = [
          {
            id: 1,
            name: '张三',
            email: 'zhangsan@example.com',
            phone: '13800138001',
            gender: '男',
            avatar: '',
            birthday: '1998-05-15',
            address: '北京市朝阳区',
            remark: '学习积极主动',
            status: 'active',
            joinDate: '2024-01-15',
            courses: [
              { id: 1, name: 'Vue.js 3.0 开发实战' },
              { id: 2, name: 'JavaScript 高级程序设计' }
            ]
          },
          {
            id: 2,
            name: '李四',
            email: 'lisi@example.com',
            phone: '13800138002',
            gender: '女',
            avatar: '',
            birthday: '1999-03-20',
            address: '上海市浦东新区',
            remark: '基础扎实',
            status: 'active',
            joinDate: '2024-01-18',
            courses: [
              { id: 3, name: 'Node.js 后端开发' }
            ]
          },
          {
            id: 3,
            name: '王五',
            email: 'wangwu@example.com',
            phone: '13800138003',
            gender: '男',
            avatar: '',
            birthday: '1997-12-10',
            address: '广州市天河区',
            remark: '已完成前端课程学习',
            status: 'graduated',
            joinDate: '2023-09-01',
            courses: [
              { id: 1, name: 'Vue.js 3.0 开发实战' },
              { id: 2, name: 'JavaScript 高级程序设计' },
              { id: 4, name: 'React Native 移动开发' }
            ]
          },
          {
            id: 4,
            name: '赵六',
            email: 'zhaoliu@example.com',
            phone: '13800138004',
            gender: '女',
            avatar: '',
            birthday: '2000-08-25',
            address: '深圳市南山区',
            remark: '暂停学习',
            status: 'suspended',
            joinDate: '2024-02-01',
            courses: []
          }
        ]
        pagination.total = studentList.value.length
        loading.value = false
      }, 500)
    }

    // 获取状态标签样式
    const getStatusType = (status) => {
      const types = {
        active: 'success',
        graduated: 'info',
        suspended: 'warning'
      }
      return types[status] || 'info'
    }

    // 获取状态标签文本
    const getStatusLabel = (status) => {
      const labels = {
        active: '在读',
        graduated: '已毕业',
        suspended: '暂停'
      }
      return labels[status] || '未知'
    }

    // 搜索
    const handleSearch = () => {
      pagination.current = 1
      getStudentList()
    }

    // 重置搜索
    const handleReset = () => {
      Object.assign(searchForm, {
        name: '',
        email: '',
        status: '',
        courseId: null
      })
      pagination.current = 1
      getStudentList()
    }

    // 分页大小改变
    const handleSizeChange = (size) => {
      pagination.size = size
      getStudentList()
    }

    // 当前页改变
    const handleCurrentChange = (current) => {
      pagination.current = current
      getStudentList()
    }

    // 全选
    const handleSelectAll = (val) => {
      selectedStudents.value = val ? [...studentList.value] : []
    }

    // 选择改变
    const handleSelectionChange = (selection) => {
      selectedStudents.value = selection
      selectAll.value = selection.length === studentList.value.length
    }

    // 新增学生
    const handleAddStudent = () => {
      isEdit.value = false
      dialogTitle.value = '新增学生'
      Object.assign(studentForm, {
        id: null,
        name: '',
        email: '',
        phone: '',
        gender: '男',
        birthday: null,
        address: '',
        remark: '',
        status: 'active'
      })
      dialogVisible.value = true
    }

    // 批量导入
    const handleBatchImport = () => {
      ElMessage.info('批量导入功能开发中...')
    }

    // 查看学生
    const handleView = (row) => {
      ElMessage.info(`查看学生详情：${row.name}`)
    }

    // 编辑学生
    const handleEdit = (row) => {
      isEdit.value = true
      dialogTitle.value = '编辑学生'
      Object.assign(studentForm, { 
        ...row,
        birthday: row.birthday ? new Date(row.birthday) : null
      })
      dialogVisible.value = true
    }

    // 分配课程
    const handleAssignCourse = (row) => {
      currentStudent.value = row
      selectedCourses.value = row.courses.map(course => course.id)
      assignDialogVisible.value = true
    }

    // 删除学生
    const handleDelete = (row) => {
      ElMessageBox.confirm(
        `确定要删除学生"${row.name}"吗？此操作不可恢复。`,
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        const index = studentList.value.findIndex(item => item.id === row.id)
        if (index > -1) {
          studentList.value.splice(index, 1)
          ElMessage.success('删除成功')
        }
      })
    }

    // 批量删除
    const handleBatchDelete = () => {
      ElMessageBox.confirm(
        `确定要删除选中的 ${selectedStudents.value.length} 名学生吗？此操作不可恢复。`,
        '批量删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        const selectedIds = selectedStudents.value.map(item => item.id)
        studentList.value = studentList.value.filter(item => !selectedIds.includes(item.id))
        selectedStudents.value = []
        selectAll.value = false
        ElMessage.success('批量删除成功')
      })
    }

    // 提交表单
    const handleSubmit = () => {
      studentFormRef.value?.validate((valid) => {
        if (valid) {
          submitting.value = true
          // 模拟API调用
          setTimeout(() => {
            if (isEdit.value) {
              const index = studentList.value.findIndex(item => item.id === studentForm.id)
              if (index > -1) {
                studentList.value[index] = { 
                  ...studentForm,
                  birthday: studentForm.birthday ? studentForm.birthday.toISOString().split('T')[0] : '',
                  courses: studentList.value[index].courses
                }
              }
              ElMessage.success('更新成功')
            } else {
              const newStudent = {
                ...studentForm,
                id: Date.now(),
                avatar: '',
                birthday: studentForm.birthday ? studentForm.birthday.toISOString().split('T')[0] : '',
                joinDate: new Date().toISOString().split('T')[0],
                courses: []
              }
              studentList.value.unshift(newStudent)
              ElMessage.success('创建成功')
            }
            submitting.value = false
            dialogVisible.value = false
          }, 1000)
        }
      })
    }

    // 分配课程提交
    const handleAssignSubmit = () => {
      assignSubmitting.value = true
      // 模拟API调用
      setTimeout(() => {
        const student = studentList.value.find(item => item.id === currentStudent.value.id)
        if (student) {
          student.courses = courseOptions.value.filter(course => 
            selectedCourses.value.includes(course.id)
          )
        }
        assignSubmitting.value = false
        assignDialogVisible.value = false
        ElMessage.success('课程分配成功')
      }, 1000)
    }

    // 关闭对话框
    const handleDialogClose = () => {
      studentFormRef.value?.resetFields()
    }

    onMounted(() => {
      getStudentList()
    })

    return {
      loading,
      dialogVisible,
      assignDialogVisible,
      submitting,
      assignSubmitting,
      studentFormRef,
      isEdit,
      searchForm,
      pagination,
      studentList,
      selectedStudents,
      currentStudent,
      selectedCourses,
      courseOptions,
      studentForm,
      studentRules,
      dialogTitle,
      selectAll,
      isIndeterminate,
      getStatusType,
      getStatusLabel,
      handleSearch,
      handleReset,
      handleSizeChange,
      handleCurrentChange,
      handleSelectAll,
      handleSelectionChange,
      handleAddStudent,
      handleBatchImport,
      handleView,
      handleEdit,
      handleAssignCourse,
      handleDelete,
      handleBatchDelete,
      handleSubmit,
      handleAssignSubmit,
      handleDialogClose
    }
  }
}
</script>

<style lang="scss" scoped>
.student-management {
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

    .header-right {
      .el-button {
        margin-left: 12px;
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
    .table-header {
      margin-bottom: 16px;

      .table-actions {
        display: flex;
        align-items: center;
        gap: 12px;
      }
    }

    .student-info {
      display: flex;
      align-items: center;

      .info-content {
        margin-left: 12px;

        .student-name {
          font-weight: 500;
          color: #2c3e50;
          margin-bottom: 4px;
        }

        .student-email {
          font-size: 12px;
          color: #8492a6;
        }
      }
    }

    .course-tags {
      .no-course {
        color: #8492a6;
        font-size: 12px;
      }
    }

    .pagination-wrapper {
      margin-top: 20px;
      text-align: right;
    }
  }

  .assign-content {
    .course-options {
      max-height: 300px;
      overflow-y: auto;

      .course-option {
        display: block;
        width: 100%;
        margin-bottom: 12px;

        :deep(.el-checkbox__label) {
          width: 100%;
        }

        .course-item {
          padding: 8px 0;

          .course-name {
            font-weight: 500;
            color: #2c3e50;
            margin-bottom: 4px;
          }

          .course-desc {
            font-size: 12px;
            color: #8492a6;
          }
        }
      }
    }
  }

  :deep(.el-button.is-text) {
    padding: 0;
    margin-right: 8px;

    &:last-child {
      margin-right: 0;
    }

    &.delete-btn {
      color: #f56c6c;
    }
  }
}
</style>