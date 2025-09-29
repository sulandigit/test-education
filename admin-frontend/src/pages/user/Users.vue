<template>
  <div class="users">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="手机号">
          <el-input
            v-model="searchForm.mobile"
            placeholder="请输入手机号"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input
            v-model="searchForm.nickname"
            placeholder="请输入昵称"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.statusId"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button icon="Refresh" @click="handleReset">
            重置
          </el-button>
          <el-button type="success" icon="Plus" @click="handleAdd">
            新增用户
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userHead" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar
              :src="row.userHead"
              :size="40"
              icon="User"
            />
          </template>
        </el-table-column>
        <el-table-column prop="mobile" label="手机号" width="130" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="userAge" label="年龄" width="80" />
        <el-table-column prop="userSex" label="性别" width="80">
          <template #default="{ row }">
            <el-tag :type="row.userSex === 1 ? 'primary' : 'success'">
              {{ row.userSex === 1 ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="courseCounts" label="课程数" width="100">
          <template #default="{ row }">
            <el-text type="primary">{{ row.courseCounts }}</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="remarkCounts" label="评论数" width="100">
          <template #default="{ row }">
            <el-text type="warning">{{ row.remarkCounts }}</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="statusId" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.statusId === 1 ? 'success' : 'danger'">
              {{ row.statusId === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gmtCreate" label="注册时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.gmtCreate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="info" size="small" @click="handleView(row)">
              详情
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
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
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editId ? '编辑用户' : '新增用户'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="formData.mobile" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="handleUpload"
          >
            <img v-if="formData.userHead" :src="formData.userHead" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄" prop="userAge">
              <el-input-number
                v-model="formData.userAge"
                :min="1"
                :max="150"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="userSex">
              <el-radio-group v-model="formData.userSex">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="statusId">
          <el-radio-group v-model="formData.statusId">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 用户详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="用户详情"
      width="600px"
    >
      <div v-if="currentUser" class="user-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentUser.mobile }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ currentUser.userAge }}岁</el-descriptions-item>
          <el-descriptions-item label="性别">
            <el-tag :type="currentUser.userSex === 1 ? 'primary' : 'success'">
              {{ currentUser.userSex === 1 ? '男' : '女' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentUser.statusId === 1 ? 'success' : 'danger'">
              {{ currentUser.statusId === 1 ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="购买课程">{{ currentUser.courseCounts }}门</el-descriptions-item>
          <el-descriptions-item label="评论数量">{{ currentUser.remarkCounts }}条</el-descriptions-item>
          <el-descriptions-item label="注册时间" :span="2">
            {{ formatDate(currentUser.gmtCreate) }}
          </el-descriptions-item>
          <el-descriptions-item label="头像" :span="2">
            <el-avatar
              :src="currentUser.userHead"
              :size="80"
              icon="User"
            />
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type UploadProps } from 'element-plus'
import { getUserPage, saveUser, updateUser, deleteUser, getUserById } from '@/api/user'
import type { User, UserPageReq, UserSaveReq, UserUpdateReq } from '@/api/user'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<User[]>([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const editId = ref<number | null>(null)
const currentUser = ref<User | null>(null)
const formRef = ref<FormInstance>()

// 搜索表单
const searchForm = reactive({
  mobile: '',
  nickname: '',
  statusId: undefined as number | undefined
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单数据
const formData = reactive<UserSaveReq & UserUpdateReq>({
  id: 0,
  mobile: '',
  nickname: '',
  userHead: '',
  userAge: 18,
  userSex: 1,
  statusId: 1
})

// 表单验证规则
const formRules = {
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  userAge: [
    { required: true, message: '请输入年龄', trigger: 'blur' }
  ],
  userSex: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  statusId: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 加载数据
const loadData = async () => {
  try {
    loading.value = true
    const params: UserPageReq = {
      pageCurrent: pagination.current,
      pageSize: pagination.size,
      mobile: searchForm.mobile || undefined,
      nickname: searchForm.nickname || undefined,
      statusId: searchForm.statusId
    }
    
    // 模拟数据
    const mockData = {
      list: [
        {
          id: 1,
          mobile: '13800138000',
          nickname: '张同学',
          userHead: 'https://via.placeholder.com/80',
          userAge: 25,
          userSex: 1,
          remarkCounts: 12,
          courseCounts: 5,
          statusId: 1,
          gmtCreate: '2023-01-01 10:00:00'
        },
        {
          id: 2,
          mobile: '13800138001',
          nickname: '李同学',
          userHead: 'https://via.placeholder.com/80',
          userAge: 22,
          userSex: 2,
          remarkCounts: 8,
          courseCounts: 3,
          statusId: 1,
          gmtCreate: '2023-01-02 10:00:00'
        },
        {
          id: 3,
          mobile: '13800138002',
          nickname: '王同学',
          userHead: '',
          userAge: 28,
          userSex: 1,
          remarkCounts: 15,
          courseCounts: 8,
          statusId: 0,
          gmtCreate: '2023-01-03 10:00:00'
        }
      ],
      total: 3
    }
    
    // const result = await getUserPage(params)
    tableData.value = mockData.list
    pagination.total = mockData.total
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置搜索
const handleReset = () => {
  searchForm.mobile = ''
  searchForm.nickname = ''
  searchForm.statusId = undefined
  pagination.current = 1
  loadData()
}

// 新增用户
const handleAdd = () => {
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = async (row: User) => {
  editId.value = row.id!
  try {
    // const result = await getUserById(row.id!)
    Object.assign(formData, row)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

// 查看用户详情
const handleView = (row: User) => {
  currentUser.value = row
  detailVisible.value = true
}

// 删除用户
const handleDelete = async (row: User) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户"${row.nickname}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // await deleteUser(row.id!)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
    }
  }
}

// 上传图片前验证
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片!')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 处理图片上传
const handleUpload = async (options: any) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    formData.userHead = e.target?.result as string
  }
  reader.readAsDataURL(options.file)
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        submitLoading.value = true
        
        if (editId.value) {
          // 编辑
          const params: UserUpdateReq = {
            id: editId.value,
            mobile: formData.mobile,
            nickname: formData.nickname,
            userHead: formData.userHead,
            userAge: formData.userAge,
            userSex: formData.userSex,
            statusId: formData.statusId
          }
          // await updateUser(params)
          console.log('更新用户:', params)
        } else {
          // 新增
          const params: UserSaveReq = {
            mobile: formData.mobile,
            nickname: formData.nickname,
            userHead: formData.userHead,
            userAge: formData.userAge,
            userSex: formData.userSex,
            statusId: formData.statusId
          }
          // await saveUser(params)
          console.log('保存用户:', params)
        }
        
        ElMessage.success(editId.value ? '编辑成功' : '新增成功')
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

// 重置表单
const resetForm = () => {
  formData.id = 0
  formData.mobile = ''
  formData.nickname = ''
  formData.userHead = ''
  formData.userAge = 18
  formData.userSex = 1
  formData.statusId = 1
}

// 格式化日期
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 组件挂载时加载数据
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.users {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}

.avatar-uploader .avatar {
  width: 80px;
  height: 80px;
  display: block;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  text-align: center;
}

.user-detail {
  padding: 20px 0;
}

:deep(.el-table .el-table__cell) {
  padding: 8px 0;
}
</style>