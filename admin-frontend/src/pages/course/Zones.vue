<template>
  <div class="course-zones">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="专区名称">
          <el-input
            v-model="searchForm.zoneName"
            placeholder="请输入专区名称"
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
            新增专区
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
        <el-table-column prop="zoneName" label="专区名称" min-width="200" />
        <el-table-column prop="zoneDesc" label="专区描述" min-width="300" show-overflow-tooltip />
        <el-table-column prop="statusId" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.statusId === 1 ? 'success' : 'danger'">
              {{ row.statusId === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortNo" label="排序" width="80" />
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
      :title="editId ? '编辑专区' : '新增专区'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="专区名称" prop="zoneName">
          <el-input v-model="formData.zoneName" placeholder="请输入专区名称" />
        </el-form-item>
        <el-form-item label="专区描述" prop="zoneDesc">
          <el-input
            v-model="formData.zoneDesc"
            type="textarea"
            :rows="4"
            placeholder="请输入专区描述"
          />
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
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { getZonePage, saveZone, updateZone, deleteZone, getZoneById } from '@/api/course'
import type { Zone, ZonePageReq } from '@/api/course'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<Zone[]>([])
const dialogVisible = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

// 搜索表单
const searchForm = reactive({
  zoneName: ''
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单数据
const formData = reactive<Omit<Zone, 'id'> & { id?: number }>({
  zoneName: '',
  zoneDesc: '',
  statusId: 1,
  sortNo: 1
})

// 表单验证规则
const formRules = {
  zoneName: [
    { required: true, message: '请输入专区名称', trigger: 'blur' }
  ],
  zoneDesc: [
    { required: true, message: '请输入专区描述', trigger: 'blur' }
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
    const params: ZonePageReq = {
      pageCurrent: pagination.current,
      pageSize: pagination.size,
      zoneName: searchForm.zoneName || undefined
    }
    
    // 模拟数据
    const mockData = {
      list: [
        {
          id: 1,
          zoneName: '热门推荐',
          zoneDesc: '热门课程推荐专区，展示最受欢迎的课程',
          statusId: 1,
          sortNo: 1,
          gmtCreate: '2023-01-01 10:00:00'
        },
        {
          id: 2,
          zoneName: '新课上线',
          zoneDesc: '最新上线的课程专区，第一时间了解新课程',
          statusId: 1,
          sortNo: 2,
          gmtCreate: '2023-01-02 10:00:00'
        },
        {
          id: 3,
          zoneName: '限时优惠',
          zoneDesc: '限时优惠课程专区，优质课程低价学习',
          statusId: 1,
          sortNo: 3,
          gmtCreate: '2023-01-03 10:00:00'
        }
      ],
      total: 3
    }
    
    // const result = await getZonePage(params)
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
  searchForm.zoneName = ''
  pagination.current = 1
  loadData()
}

// 新增专区
const handleAdd = () => {
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

// 编辑专区
const handleEdit = async (row: Zone) => {
  editId.value = row.id!
  try {
    // const result = await getZoneById(row.id!)
    Object.assign(formData, row)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取专区信息失败')
  }
}

// 删除专区
const handleDelete = async (row: Zone) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除专区"${row.zoneName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // await deleteZone(row.id!)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除专区失败:', error)
    }
  }
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        submitLoading.value = true
        
        if (editId.value) {
          // await updateZone({ ...formData, id: editId.value })
          console.log('更新专区:', { ...formData, id: editId.value })
        } else {
          // await saveZone(formData)
          console.log('保存专区:', formData)
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
  formData.zoneName = ''
  formData.zoneDesc = ''
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
})
</script>

<style scoped>
.course-zones {
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

:deep(.el-table .el-table__cell) {
  padding: 8px 0;
}
</style>