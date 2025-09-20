<template>
  <div class="courses">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="课程名称">
          <el-input
            v-model="searchForm.courseName"
            placeholder="请输入课程名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="课程分类">
          <el-select
            v-model="searchForm.categoryId"
            placeholder="请选择课程分类"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="category in categoryOptions"
              :key="category.id"
              :label="category.categoryName"
              :value="category.id"
            />
          </el-select>
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
            新增课程
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
        <el-table-column prop="courseLogo" label="课程封面" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.courseLogo"
              :src="row.courseLogo"
              style="width: 60px; height: 40px"
              fit="cover"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="课程分类" width="120" />
        <el-table-column prop="teacherName" label="讲师" width="100" />
        <el-table-column prop="coursePrice" label="价格" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.coursePrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="courseSort" label="排序" width="80" />
        <el-table-column prop="statusId" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.statusId === 1 ? 'success' : 'danger'">
              {{ row.statusId === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="putawayTime" label="上架时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.putawayTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="info" size="small" @click="handleView(row)">
              查看
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
      :title="editId ? '编辑课程' : '新增课程'"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程名称" prop="courseName">
              <el-input v-model="formData.courseName" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程分类" prop="categoryId">
              <el-select v-model="formData.categoryId" placeholder="请选择课程分类" style="width: 100%">
                <el-option
                  v-for="category in categoryOptions"
                  :key="category.id"
                  :label="category.categoryName"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="讲师姓名" prop="teacherName">
              <el-input v-model="formData.teacherName" placeholder="请输入讲师姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程价格" prop="coursePrice">
              <el-input-number
                v-model="formData.coursePrice"
                :min="0"
                :precision="2"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="课程封面">
          <el-upload
            class="course-uploader"
            action="#"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="handleUpload"
          >
            <img v-if="formData.courseLogo" :src="formData.courseLogo" class="course-image" />
            <div v-else class="upload-placeholder">
              <el-icon class="upload-icon"><Plus /></el-icon>
              <div class="upload-text">点击上传课程封面</div>
            </div>
          </el-upload>
        </el-form-item>

        <el-form-item label="课程介绍">
          <el-input
            v-model="formData.courseIntroduce"
            type="textarea"
            :rows="4"
            placeholder="请输入课程介绍"
          />
        </el-form-item>

        <el-form-item label="讲师头像">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="handleTeacherUpload"
          >
            <img v-if="formData.teacherHead" :src="formData.teacherHead" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="讲师介绍">
          <el-input
            v-model="formData.teacherIntroduce"
            type="textarea"
            :rows="3"
            placeholder="请输入讲师介绍"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="课程排序" prop="courseSort">
              <el-input-number
                v-model="formData.courseSort"
                :min="0"
                :max="999"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="statusId">
              <el-radio-group v-model="formData.statusId">
                <el-radio :label="1">正常</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="上架时间" prop="putawayTime">
              <el-date-picker
                v-model="formData.putawayTime"
                type="datetime"
                placeholder="选择上架时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type UploadProps } from 'element-plus'
import { getCoursePage, saveCourse, updateCourse, deleteCourse, getCourseById } from '@/api/course'
import { getCategoryList } from '@/api/course'
import type { Course, CoursePageReq, Category } from '@/api/course'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<Course[]>([])
const categoryOptions = ref<Category[]>([])
const dialogVisible = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

// 搜索表单
const searchForm = reactive({
  courseName: '',
  categoryId: undefined as number | undefined,
  statusId: undefined as number | undefined
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单数据
const formData = reactive<Omit<Course, 'id'> & { id?: number }>({
  categoryId: 0,
  courseName: '',
  courseLogo: '',
  courseIntroduce: '',
  teacherName: '',
  teacherHead: '',
  teacherIntroduce: '',
  courseSort: 1,
  coursePrice: 0,
  putawayTime: '',
  statusId: 1
})

// 表单验证规则
const formRules = {
  courseName: [
    { required: true, message: '请输入课程名称', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择课程分类', trigger: 'change' }
  ],
  teacherName: [
    { required: true, message: '请输入讲师姓名', trigger: 'blur' }
  ],
  coursePrice: [
    { required: true, message: '请输入课程价格', trigger: 'blur' }
  ],
  courseSort: [
    { required: true, message: '请输入课程排序', trigger: 'blur' }
  ],
  statusId: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  putawayTime: [
    { required: true, message: '请选择上架时间', trigger: 'change' }
  ]
}

// 加载数据
const loadData = async () => {
  try {
    loading.value = true
    const params: CoursePageReq = {
      pageCurrent: pagination.current,
      pageSize: pagination.size,
      courseName: searchForm.courseName || undefined,
      categoryId: searchForm.categoryId,
      statusId: searchForm.statusId
    }
    
    // 模拟数据
    const mockData = {
      list: [
        {
          id: 1,
          categoryId: 1,
          categoryName: '前端开发',
          courseName: 'Vue.js 从入门到精通',
          courseLogo: 'https://via.placeholder.com/120x80',
          courseIntroduce: '这是一门非常好的Vue.js课程，适合初学者和进阶者',
          teacherName: '张老师',
          teacherHead: 'https://via.placeholder.com/80',
          teacherIntroduce: '资深前端开发工程师，拥有10年开发经验',
          courseSort: 1,
          coursePrice: 199.00,
          putawayTime: '2023-01-01 10:00:00',
          statusId: 1,
          gmtCreate: '2023-01-01 10:00:00'
        },
        {
          id: 2,
          categoryId: 2,
          categoryName: '后端开发',
          courseName: 'Spring Boot 微服务开发',
          courseLogo: 'https://via.placeholder.com/120x80',
          courseIntroduce: '深入学习Spring Boot微服务架构',
          teacherName: '李老师',
          teacherHead: 'https://via.placeholder.com/80',
          teacherIntroduce: '后端架构师，精通Java技术栈',
          courseSort: 2,
          coursePrice: 299.00,
          putawayTime: '2023-01-02 10:00:00',
          statusId: 1,
          gmtCreate: '2023-01-02 10:00:00'
        }
      ],
      total: 2
    }
    
    // const result = await getCoursePage(params)
    tableData.value = mockData.list
    pagination.total = mockData.total
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载分类选项
const loadCategoryOptions = async () => {
  try {
    // 模拟数据
    categoryOptions.value = [
      { id: 1, categoryName: '前端开发', parentId: 0, categoryLogo: '', remark: '', statusId: 1, sortNo: 1 },
      { id: 2, categoryName: '后端开发', parentId: 0, categoryLogo: '', remark: '', statusId: 1, sortNo: 2 }
    ]
    // const result = await getCategoryList()
    // categoryOptions.value = result.filter(item => item.statusId === 1)
  } catch (error) {
    console.error('加载分类选项失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置搜索
const handleReset = () => {
  searchForm.courseName = ''
  searchForm.categoryId = undefined
  searchForm.statusId = undefined
  pagination.current = 1
  loadData()
}

// 新增课程
const handleAdd = () => {
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

// 编辑课程
const handleEdit = async (row: Course) => {
  editId.value = row.id!
  try {
    // const result = await getCourseById(row.id!)
    Object.assign(formData, row)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取课程信息失败')
  }
}

// 查看课程
const handleView = (row: Course) => {
  ElMessage.info('查看课程功能待开发')
}

// 删除课程
const handleDelete = async (row: Course) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除课程"${row.courseName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // await deleteCourse(row.id!)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除课程失败:', error)
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

// 处理课程封面上传
const handleUpload = async (options: any) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    formData.courseLogo = e.target?.result as string
  }
  reader.readAsDataURL(options.file)
}

// 处理讲师头像上传
const handleTeacherUpload = async (options: any) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    formData.teacherHead = e.target?.result as string
  }
  reader.readAsDataURL(options.file)
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        submitLoading.value = true
        
        const submitData = {
          ...formData,
          putawayTime: typeof formData.putawayTime === 'string' 
            ? formData.putawayTime 
            : new Date(formData.putawayTime).toISOString()
        }
        
        if (editId.value) {
          // await updateCourse({ ...submitData, id: editId.value })
          console.log('更新课程:', { ...submitData, id: editId.value })
        } else {
          // await saveCourse(submitData)
          console.log('保存课程:', submitData)
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
  formData.categoryId = 0
  formData.courseName = ''
  formData.courseLogo = ''
  formData.courseIntroduce = ''
  formData.teacherName = ''
  formData.teacherHead = ''
  formData.teacherIntroduce = ''
  formData.courseSort = 1
  formData.coursePrice = 0
  formData.putawayTime = ''
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
  loadCategoryOptions()
})
</script>

<style scoped>
.courses {
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

.price {
  color: #f56c6c;
  font-weight: 600;
}

.course-uploader .course-image {
  width: 120px;
  height: 80px;
  display: block;
  border-radius: 4px;
}

.course-uploader .upload-placeholder {
  width: 120px;
  height: 80px;
  border: 1px dashed var(--el-border-color);
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: var(--el-transition-duration-fast);
}

.course-uploader .upload-placeholder:hover {
  border-color: var(--el-color-primary);
}

.upload-icon {
  font-size: 24px;
  color: #8c939d;
}

.upload-text {
  font-size: 12px;
  color: #8c939d;
  margin-top: 5px;
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

:deep(.el-table .el-table__cell) {
  padding: 8px 0;
}
</style>