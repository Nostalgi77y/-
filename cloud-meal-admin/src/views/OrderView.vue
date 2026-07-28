<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import http from '@/utils/request'
import { ElNotification } from 'element-plus'
import type { ApiResponse, Order } from '@/types/api'
const orders = ref<Order[]>([]); const loading = ref(false)
const labels: Record<string,string> = {PENDING_PAYMENT:'待付款',PENDING_ACCEPTANCE:'待接单',PREPARING:'制作中',PENDING_DELIVERY:'待配送',DELIVERING:'配送中',COMPLETED:'已完成',CANCELLED:'已取消'}
async function load(){ loading.value=true; try{ const {data}=await http.get<ApiResponse<Order[]>>('/admin/orders'); orders.value=data.data }finally{loading.value=false} }
async function next(row: Order, status:string){ await http.put(`/admin/orders/${row.id}/status/${status}`); await load() }
let socket: WebSocket | undefined
onMounted(() => {
  load()
  const protocol = location.protocol === 'https:' ? 'wss' : 'ws'
  socket = new WebSocket(`${protocol}://${location.host}/api/ws/orders`)
  socket.onmessage = event => {
    const message = JSON.parse(event.data)
    ElNotification({ title: '订单提醒', message: message.message, type: 'success' })
    load()
  }
})
onUnmounted(() => socket?.close())
</script>
<template><div><div class="page-heading"><div><h1>订单管理</h1><p>处理接单、制作与配送状态</p></div><el-button @click="load">刷新</el-button></div><el-card class="content-card"><el-table :data="orders" v-loading="loading"><el-table-column prop="orderNumber" label="订单号" min-width="190"/><el-table-column prop="consignee" label="顾客"/><el-table-column prop="amount" label="金额"><template #default="s">¥ {{ s.row.amount }}</template></el-table-column><el-table-column prop="status" label="状态"><template #default="s"><el-tag>{{ labels[s.row.status] }}</el-tag></template></el-table-column><el-table-column label="操作" min-width="180"><template #default="s"><el-button v-if="s.row.status==='PENDING_ACCEPTANCE'" type="primary" size="small" @click="next(s.row,'PREPARING')">接单</el-button><el-button v-if="s.row.status==='PREPARING'" size="small" @click="next(s.row,'PENDING_DELIVERY')">制作完成</el-button><el-button v-if="s.row.status==='PENDING_DELIVERY'" size="small" @click="next(s.row,'DELIVERING')">开始配送</el-button><el-button v-if="s.row.status==='DELIVERING'" size="small" @click="next(s.row,'COMPLETED')">完成</el-button></template></el-table-column></el-table></el-card></div></template>
