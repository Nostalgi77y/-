<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
const route = useRoute(); const router = useRouter(); const auth = useAuthStore()
function logout() { auth.logout(); router.push('/login') }
</script>
<template>
  <el-container class="app-shell">
    <el-aside width="220px" class="sidebar">
      <div class="brand"><span class="brand-mark">膳</span><span>云膳商家中心</span></div>
      <el-menu :default-active="route.path" router class="menu">
        <el-menu-item index="/dashboard">经营概览</el-menu-item>
        <el-menu-item index="/dishes">菜品管理</el-menu-item>
        <el-menu-item index="/orders">订单管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar"><span>门店运营工作台</span><el-button text @click="logout">{{ auth.name }} · 退出</el-button></el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>
