<template>
  <div class="course-categories">
    <!-- 操作区域 -->
    <el-card class="action-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="分类名称">
          <el-input
            v-model="searchForm.categoryName"
            placeholder="请输入分类名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button icon="Refresh" @click="handleReset">
            重置
          </el-button>
          <el-button type="success" icon="Plus" @click="handleAdd">
            新增分类
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
        <el-table-column prop="categoryName" label="分类名称" min-width="150" />
        <el-table-column prop="parentId" label="上级分类" width="120">
          <template #default="{ row }">
            {{ row.parentId === 0 ? '顶级分类' : getParentCategoryName(row.parentId) }}
          </template>
        </el-table-column>
        <el-table-column prop="categoryLogo" label="分类图标" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.categoryLogo"
              :src="row.categoryLogo"
              style="width: 40px; height: 40px"
              fit="cover"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="statusId" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.statusId === 1 ? 'success' : 'danger'">
              {{ row.statusId === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortNo" label="排序" width="80" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="gmtCreate" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.gmtCreate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
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
      :title="editId ? '编辑分类' : '新增分类'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="上级分类">
          <el-select
            v-model="formData.parentId"
            placeholder="请选择上级分类（可选）"
            clearable
            style="width: 100%"
          >
            <el-option label="顶级分类" :value="0" />
            <el-option
              v-for="category in categoryOptions"
              :key="category.id"
              :label="category.categoryName"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="formData.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类图标">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="handleUpload"
          >
            <img v-if="formData.categoryLogo" :src="formData.categoryLogo" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="状态" prop="statusId">
          <el-radio-group v-model="formData.statusId">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sortNo">
          <el-input-number
            v-model="formData.sortNo"
            :min="0"
            :max="999"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
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
import { getCategoryPage, getCategoryList, saveCategory, updateCategory, deleteCategory, getCategoryById } from '@/api/course'
import type { Category, CategoryPageReq } from '@/api/course'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<Category[]>([])
const categoryOptions = ref<Category[]>([])
const dialogVisible = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

// 搜索表单
const searchForm = reactive({
  categoryName: ''
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单数据
const formData = reactive<Omit<Category, 'id'> & { id?: number }>({
  categoryName: '',
  parentId: 0,
  categoryLogo: '',
  remark: '',
  statusId: 1,
  sortNo: 1
})

// 表单验证规则
const formRules = {
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ],
  statusId: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  sortNo: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
}

// 加载数据
const loadData = async () => {
  try {
    loading.value = true
    const params: CategoryPageReq = {
      pageCurrent: pagination.current,
      pageSize: pagination.size,
      categoryName: searchForm.categoryName || undefined
    }
    
    // 模拟数据
    const mockData = {
      list: [
        {
          id: 1,
          categoryName: '前端开发',
          parentId: 0,
          categoryLogo: 'https://via.placeholder.com/40',
          remark: '前端开发相关课程',
          statusId: 1,
          sortNo: 1,
          gmtCreate: '2023-01-01 10:00:00'
        },
        {
          id: 2,
          categoryName: 'Vue.js',
          parentId: 1,
          categoryLogo: 'https://via.placeholder.com/40',
          remark: 'Vue.js框架课程',
          statusId: 1,
          sortNo: 1,
          gmtCreate: '2023-01-02 10:00:00'
        },
        {
          id: 3,
          categoryName: '后端开发',
          parentId: 0,
          categoryLogo: '',
          remark: '后端开发相关课程',
          statusId: 1,
          sortNo: 2,
          gmtCreate: '2023-01-03 10:00:00'
        }
      ],
      total: 3
    }
    
    // const result = await getCategoryPage(params)
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
      { id: 3, categoryName: '后端开发', parentId: 0, categoryLogo: '', remark: '', statusId: 1, sortNo: 2 }
    ]
    // const result = await getCategoryList()
    // categoryOptions.value = result.filter(item => item.statusId === 1)
  } catch (error) {
    console.error('加载分类选项失败:', error)
  }
}

// 获取上级分类名称
const getParentCategoryName = (parentId: number): string => {
  if (parentId === 0) return '顶级分类'
  const parent = [...tableData.value, ...categoryOptions.value].find(item => item.id === parentId)
  return parent?.categoryName || '未知分类'
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 重置搜索
const handleReset = () => {
  searchForm.categoryName = ''
  pagination.current = 1
  loadData()
}

// 新增分类
const handleAdd = () => {
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = async (row: Category) => {
  editId.value = row.id!
  try {
    // const result = await getCategoryById(row.id!)
    Object.assign(formData, row)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取分类信息失败')
  }
}

// 删除分类
const handleDelete = async (row: Category) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除分类"${row.categoryName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // await deleteCategory(row.id!)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除分类失败:', error)
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
  // 这里应该调用上传接口
  // const formData = new FormData()
  // formData.append('file', options.file)
  // const result = await uploadFile(formData)
  
  // 模拟上传成功
  const reader = new FileReader()
  reader.onload = (e) => {
    formData.categoryLogo = e.target?.result as string
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
          // await updateCategory({ ...formData, id: editId.value })
          console.log('更新分类:', { ...formData, id: editId.value })
        } else {
          // await saveCategory(formData)
          console.log('保存分类:', formData)
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
  formData.categoryName = ''
  formData.parentId = 0
  formData.categoryLogo = ''
  formData.remark = ''
  formData.statusId = 1
  formData.sortNo = 1
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
.course-categories {
  padding: 20px;
}

.action-card {
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

:deep(.el-table .el-table__cell) {
  padding: 8px 0;
}
</style>