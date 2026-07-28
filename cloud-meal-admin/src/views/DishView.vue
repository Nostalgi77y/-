<script setup lang="ts">
import { onMounted, ref } from 'vue'
import http from '@/utils/request'
import type { ApiResponse, Dish } from '@/types/api'
const dishes = ref<Dish[]>([]); const loading = ref(false)
async function load() { loading.value = true; try { const { data } = await http.get<ApiResponse<Dish[]>>('/admin/dishes'); dishes.value = data.data } finally { loading.value = false } }
onMounted(load)
</script>
<template><div><div class="page-heading"><div><h1>菜品管理</h1><p>维护价格、库存与上下架状态</p></div><el-button type="primary">新增菜品</el-button></div><el-card class="content-card"><el-table :data="dishes" v-loading="loading"><el-table-column prop="name" label="菜品" min-width="160" /><el-table-column prop="price" label="价格"><template #default="s">¥ {{ Number(s.row.price).toFixed(2) }}</template></el-table-column><el-table-column prop="stock" label="库存" /><el-table-column prop="status" label="状态"><template #default="s"><el-tag :type="s.row.status ? 'success' : 'info'">{{ s.row.status ? '在售' : '停售' }}</el-tag></template></el-table-column><el-table-column label="操作"><el-button link type="primary">编辑</el-button></el-table-column></el-table></el-card></div></template>
