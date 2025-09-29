<template>
  <div class="system-menus">
    <!-- 操作区域 -->
    <el-card class="action-card">
      <el-button type="success" icon="Plus" @click="handleAdd">
        新增菜单
      </el-button>
      <el-button icon="Refresh" @click="loadData">
        刷新
      </el-button>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        row-key="id"
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="menuNme" label="菜单名称" min-width="200">
          <template #default="{ row }">
            <el-icon v-if="row.icon" style="margin-right: 5px">
              <component :is="row.icon" />
            </el-icon>
            {{ row.menuNme }}
          </template>
        </el-table-column>
        <el-table-column prop="menuUrl" label="菜单路径" min-width="200" />
        <el-table-column prop="authValue" label="权限标识" min-width="150" />
        <el-table-column prop="isShow" label="显示状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isShow === 1 ? 'success' : 'info'">
              {{ row.isShow === 1 ? '显示' : '隐藏' }}
            </el-tag>
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
        <el-table-column prop="remarks" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="success" size="small" @click="handleAddChild(row)">
              添加子菜单
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editId ? '编辑菜单' : '新增菜单'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="formData.parentId"
            :data="menuOptions"
            :props="{ label: 'menuNme', value: 'id' }"
            placeholder="请选择上级菜单（可选）"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuNme">
          <el-input v-model="formData.menuNme" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单路径">
          <el-input v-model="formData.menuUrl" placeholder="请输入菜单路径" />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="formData.authValue" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="菜单图标">
          <el-input v-model="formData.icon" placeholder="请输入图标名称">
            <template #append>
              <el-icon v-if="formData.icon">
                <component :is="formData.icon" />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="显示状态" prop="isShow">
          <el-radio-group v-model="formData.isShow">
            <el-radio :label="1">显示</el-radio>
            <el-radio :label="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="statusId">
          <el-radio-group v-model="formData.statusId">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="目标类型" prop="targetId">
          <el-select v-model="formData.targetId" placeholder="请选择目标类型">
            <el-option label="本窗口" :value="0" />
            <el-option label="新窗口" :value="1" />
          </el-select>
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
            v-model="formData.remarks"
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import type { MenuItem } from '@/types/api'

// 扩展菜单类型
interface MenuFormData extends Omit<MenuItem, 'id'> {
  id?: number
}

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref<MenuItem[]>([])
const dialogVisible = ref(false)
const editId = ref<number | null>(null)
const parentId = ref<number | null>(null)
const formRef = ref<FormInstance>()

// 表单数据
const formData = reactive<MenuFormData>({
  parentId: 0,
  menuNme: '',
  menuUrl: '',
  authValue: '',
  icon: '',
  remarks: '',
  statusId: 1,
  sortNo: 1,
  isShow: 1,
  targetId: 0
})

// 菜单选项（用于选择上级菜单）
const menuOptions = computed(() => {
  const buildOptions = (menus: MenuItem[], level = 0): any[] => {
    return menus.map(menu => ({
      id: menu.id,
      menuNme: '  '.repeat(level) + menu.menuNme,
      children: menu.children ? buildOptions(menu.children, level + 1) : undefined
    }))
  }
  return [{ id: 0, menuNme: '顶级菜单' }, ...buildOptions(tableData.value)]
})

// 表单验证规则
const formRules = {
  menuNme: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  isShow: [
    { required: true, message: '请选择显示状态', trigger: 'change' }
  ],
  statusId: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  targetId: [
    { required: true, message: '请选择目标类型', trigger: 'change' }
  ],
  sortNo: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
}

// 模拟API调用
const getMenuList = async () => {
  // 模拟菜单树数据
  return [
    {
      id: 1,
      parentId: 0,
      menuNme: '系统管理',
      menuUrl: '/system',
      authValue: 'system',
      icon: 'Setting',
      remarks: '系统管理模块',
      statusId: 1,
      sortNo: 1,
      isShow: 1,
      targetId: 0,
      children: [
        {
          id: 11,
          parentId: 1,
          menuNme: '用户管理',
          menuUrl: '/system/users',
          authValue: 'system:user',
          icon: 'User',
          remarks: '系统用户管理',
          statusId: 1,
          sortNo: 1,
          isShow: 1,
          targetId: 0
        },
        {
          id: 12,
          parentId: 1,
          menuNme: '角色管理',
          menuUrl: '/system/roles',
          authValue: 'system:role',
          icon: 'UserFilled',
          remarks: '角色管理',
          statusId: 1,
          sortNo: 2,
          isShow: 1,
          targetId: 0
        },
        {
          id: 13,
          parentId: 1,
          menuNme: '菜单管理',
          menuUrl: '/system/menus',
          authValue: 'system:menu',
          icon: 'Menu',
          remarks: '菜单管理',
          statusId: 1,
          sortNo: 3,
          isShow: 1,
          targetId: 0
        }
      ]
    },
    {
      id: 2,
      parentId: 0,
      menuNme: '课程管理',
      menuUrl: '/course',
      authValue: 'course',
      icon: 'Reading',
      remarks: '课程管理模块',
      statusId: 1,
      sortNo: 2,
      isShow: 1,
      targetId: 0,
      children: [
        {
          id: 21,
          parentId: 2,
          menuNme: '分类管理',
          menuUrl: '/course/categories',
          authValue: 'course:category',
          icon: 'FolderOpened',
          remarks: '课程分类管理',
          statusId: 1,
          sortNo: 1,
          isShow: 1,
          targetId: 0
        },
        {
          id: 22,
          parentId: 2,
          menuNme: '课程管理',
          menuUrl: '/course/courses',
          authValue: 'course:course',
          icon: 'VideoPlay',
          remarks: '课程管理',
          statusId: 1,
          sortNo: 2,
          isShow: 1,
          targetId: 0
        }
      ]
    }
  ]
}

const saveMenu = async (data: MenuFormData) => {
  console.log('保存菜单:', data)
  return 'success'
}

const updateMenu = async (data: MenuFormData) => {
  console.log('更新菜单:', data)
  return 'success'
}

const deleteMenu = async (id: number) => {
  console.log('删除菜单:', id)
  return 'success'
}

const getMenuById = async (id: number) => {
  // 从tableData中找到对应的菜单
  const findMenu = (menus: MenuItem[]): MenuItem | null => {
    for (const menu of menus) {
      if (menu.id === id) return menu
      if (menu.children) {
        const found = findMenu(menu.children)
        if (found) return found
      }
    }
    return null
  }
  return findMenu(tableData.value)
}

// 加载数据
const loadData = async () => {
  try {
    loading.value = true
    const result = await getMenuList()
    tableData.value = result
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 新增菜单
const handleAdd = () => {
  editId.value = null
  parentId.value = null
  resetForm()
  dialogVisible.value = true
}

// 添加子菜单
const handleAddChild = (row: MenuItem) => {
  editId.value = null
  parentId.value = row.id
  resetForm()
  formData.parentId = row.id
  dialogVisible.value = true
}

// 编辑菜单
const handleEdit = async (row: MenuItem) => {
  editId.value = row.id
  try {
    const result = await getMenuById(row.id)
    if (result) {
      Object.assign(formData, result)
    }
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取菜单信息失败')
  }
}

// 删除菜单
const handleDelete = async (row: MenuItem) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除菜单"${row.menuNme}"吗？删除后其子菜单也将被删除！`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteMenu(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除菜单失败:', error)
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
          await updateMenu({ ...formData, id: editId.value })
        } else {
          await saveMenu(formData)
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
  formData.parentId = parentId.value || 0
  formData.menuNme = ''
  formData.menuUrl = ''
  formData.authValue = ''
  formData.icon = ''
  formData.remarks = ''
  formData.statusId = 1
  formData.sortNo = 1
  formData.isShow = 1
  formData.targetId = 0
}

// 组件挂载时加载数据
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.system-menus {
  padding: 20px;
}

.action-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

:deep(.el-table .el-table__cell) {
  padding: 8px 0;
}
</style>