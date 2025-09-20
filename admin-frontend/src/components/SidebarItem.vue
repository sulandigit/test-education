<template>
  <!-- 如果有子菜单 -->
  <el-sub-menu
    v-if="hasChildren"
    :index="String(item.id)"
  >
    <template #title>
      <el-icon v-if="item.icon">
        <component :is="item.icon" />
      </el-icon>
      <span>{{ item.menuNme }}</span>
    </template>
    
    <sidebar-item
      v-for="child in item.children"
      :key="child.id"
      :item="child"
    />
  </el-sub-menu>
  
  <!-- 如果没有子菜单 -->
  <el-menu-item
    v-else
    :index="item.menuUrl || String(item.id)"
  >
    <el-icon v-if="item.icon">
      <component :is="item.icon" />
    </el-icon>
    <template #title>
      <span>{{ item.menuNme }}</span>
    </template>
  </el-menu-item>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { MenuItem } from '@/types/api'

interface Props {
  item: MenuItem
}

const props = defineProps<Props>()

// 检查是否有子菜单
const hasChildren = computed(() => {
  return props.item.children && props.item.children.length > 0 && 
         props.item.children.some(child => child.isShow === 1)
})
</script>