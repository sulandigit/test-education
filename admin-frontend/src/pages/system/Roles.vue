<template>
  <div class="system-roles">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="角色名称">
          <el-input
            v-model="searchForm.roleName"
            placeholder="请输入角色名称"
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
            新增角色
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
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="remark" label="角色描述" min-width="200" show-overflow-tooltip />
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
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="warning" size="small" @click="handlePermission(row)">
              权限
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
      :title="editId ? '编辑角色' : '新增角色'"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
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
        <el-form-item label="角色描述">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
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

    <!-- 权限配置对话框 -->
    <el-dialog
      v-model="permissionVisible"
      title="权限配置"
      width="600px"
      @close="handlePermissionClose"
    >
      <el-tree
        ref="treeRef"
        :data="menuTree"
        :props="treeProps"
        show-checkbox
        node-key="id"
        :default-expanded-keys="expandedKeys"
        :default-checked-keys="checkedKeys"
      />
      
      <template #footer>
        <el-button @click="permissionVisible = false">取消</el-button>
        <el-button type="primary" :loading="permissionLoading" @click="handlePermissionSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, ElTree } from 'element-plus'

// 模拟角色数据类型
interface SysRole {
  id?: number
  roleName: string
  remark: string
  statusId: number
  sortNo: number
  gmtCreate?: string
  gmtModified?: string
}

interface RolePageReq {
  pageCurrent: number
  pageSize: number
  roleName?: string
}

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const permissionLoading = ref(false)
const tableData = ref<SysRole[]>([])
const dialogVisible = ref(false)
const permissionVisible = ref(false)
const editId = ref<number | null>(null)
const currentRole = ref<SysRole | null>(null)
const formRef = ref<FormInstance>()
const treeRef = ref<InstanceType<typeof ElTree>>()

// 搜索表单
const searchForm = reactive({
  roleName: ''
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单数据
const formData = reactive<SysRole>({
  roleName: '',
  remark: '',
  statusId: 1,
  sortNo: 1
})

// 权限树配置
const treeProps = {
  children: 'children',
  label: 'menuNme'
}

// 菜单树数据
const menuTree = ref<any[]>([])
const expandedKeys = ref<number[]>([])
const checkedKeys = ref<number[]>([])

// 表单验证规则
const formRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  statusId: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  sortNo: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
}

// 模拟API调用
const getRolePage = async (params: RolePageReq) => {
  // 模拟数据
  return {
    list: [
      {
        id: 1,
        roleName: '超级管理员',
        remark: '系统超级管理员，拥有所有权限',
        statusId: 1,
        sortNo: 1,
        gmtCreate: '2023-01-01 10:00:00'
      },
      {
        id: 2,
        roleName: '普通管理员',
        remark: '普通管理员，拥有部分权限',
        statusId: 1,
        sortNo: 2,
        gmtCreate: '2023-01-02 10:00:00'
      }
    ],
    total: 2
  }
}

const saveRole = async (data: SysRole) => {
  console.log('保存角色:', data)
  return 'success'
}

const updateRole = async (data: SysRole) => {
  console.log('更新角色:', data)
  return 'success'
}

const deleteRole = async (id: number) => {
  console.log('删除角色:', id)
  return 'success'
}

const getRoleById = async (id: number) => {
  const mockData = {
    id: 1,
    roleName: '超级管理员',
    remark: '系统超级管理员，拥有所有权限',
    statusId: 1,
    sortNo: 1
  }
  return mockData
}

// 加载数据
const loadData = async () => {
  try {
    loading.value = true
    const params: RolePageReq = {
      pageCurrent: pagination.current,
      pageSize: pagination.size,
      roleName: searchForm.roleName || undefined
    }
    
    const result = await getRolePage(params)
    tableData.value = result.list
    pagination.total = result.total
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
  searchForm.roleName = ''
  pagination.current = 1
  loadData()
}

// 新增角色
const handleAdd = () => {
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = async (row: SysRole) => {
  editId.value = row.id!
  try {
    const result = await getRoleById(row.id!)
    Object.assign(formData, result)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取角色信息失败')
  }
}

// 配置权限
const handlePermission = (row: SysRole) => {
  currentRole.value = row
  // 加载菜单树和角色权限
  loadMenuTree()
  loadRolePermissions(row.id!)
  permissionVisible.value = true
}

// 删除角色
const handleDelete = async (row: SysRole) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色"${row.roleName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteRole(row.id!)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除角色失败:', error)
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
          await updateRole({ ...formData, id: editId.value })
        } else {
          await saveRole(formData)
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
  formData.roleName = ''
  formData.remark = ''
  formData.statusId = 1
  formData.sortNo = 1
}

// 加载菜单树
const loadMenuTree = () => {
  // 模拟菜单树数据
  menuTree.value = [
    {
      id: 1,
      menuNme: '系统管理',
      children: [
        { id: 11, menuNme: '用户管理' },
        { id: 12, menuNme: '角色管理' },
        { id: 13, menuNme: '菜单管理' }
      ]
    },
    {
      id: 2,
      menuNme: '课程管理',
      children: [
        { id: 21, menuNme: '分类管理' },
        { id: 22, menuNme: '课程管理' },
        { id: 23, menuNme: '专区管理' }
      ]
    }
  ]
  expandedKeys.value = [1, 2]
}

// 加载角色权限
const loadRolePermissions = (roleId: number) => {
  // 模拟已选中的权限
  checkedKeys.value = [11, 12, 21]
}

// 提交权限配置
const handlePermissionSubmit = async () => {
  try {
    permissionLoading.value = true
    const checkedNodes = treeRef.value?.getCheckedKeys(false) || []
    const halfCheckedNodes = treeRef.value?.getHalfCheckedKeys() || []
    const allCheckedKeys = [...checkedNodes, ...halfCheckedNodes]
    
    console.log('保存权限:', {
      roleId: currentRole.value?.id,
      menuIds: allCheckedKeys
    })
    
    ElMessage.success('权限配置成功')
    permissionVisible.value = false
  } catch (error) {
    console.error('权限配置失败:', error)
  } finally {
    permissionLoading.value = false
  }
}

// 关闭权限对话框
const handlePermissionClose = () => {
  currentRole.value = null
  checkedKeys.value = []
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
.system-roles {
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