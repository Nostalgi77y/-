<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import http from '@/utils/request'
import type { ApiResponse } from '@/types/api'
interface Statistics { revenue:number; orderCount:number; onSaleDishCount:number; pendingOrderCount:number }
const data=ref<Statistics>({revenue:0,orderCount:0,onSaleDishCount:0,pendingOrderCount:0})
const cards=computed(()=>[{label:'今日营业额',value:`¥ ${Number(data.value.revenue).toFixed(2)}`,hint:'已支付订单'}, {label:'有效订单',value:String(data.value.orderCount),hint:'今日实时统计'}, {label:'在售菜品',value:String(data.value.onSaleDishCount),hint:'库存状态正常'}, {label:'待处理订单',value:String(data.value.pendingOrderCount),hint:'请及时处理'}])
onMounted(async()=>{const response=await http.get<ApiResponse<Statistics>>('/admin/statistics/today');data.value=response.data.data})
</script>
<template><div><div class="page-heading"><div><h1>经营概览</h1><p>快速了解今日门店经营情况</p></div><span class="status-pill">● 营业中</span></div><div class="metric-grid"><article v-for="card in cards" :key="card.label" class="metric-card"><span>{{ card.label }}</span><strong>{{ card.value }}</strong><small>{{ card.hint }}</small></article></div><el-card class="content-card"><template #header>系统能力</template><el-timeline><el-timeline-item type="success">商品、库存与缓存管理</el-timeline-item><el-timeline-item type="primary">订单状态机与延迟关单</el-timeline-item><el-timeline-item type="warning">WebSocket 来单提醒（建设中）</el-timeline-item></el-timeline></el-card></div></template>
