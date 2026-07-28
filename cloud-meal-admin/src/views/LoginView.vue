<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
const router = useRouter(); const auth = useAuthStore(); const loading = ref(false)
const form = reactive({ username: 'admin', password: 'Admin@123456' })
async function submit() { loading.value = true; try { await auth.login(form.username, form.password); router.push('/dashboard') } finally { loading.value = false } }
</script>
<template>
  <main class="login-page">
    <section class="login-intro"><div class="hero-label">CLOUD MEAL</div><h1>让每一笔订单<br>清晰抵达</h1><p>面向餐饮门店的数字化运营与订单履约平台</p></section>
    <el-card class="login-card" shadow="always"><h2>欢迎回来</h2><p>登录商家管理中心</p>
      <el-form label-position="top" @submit.prevent="submit"><el-form-item label="账号"><el-input v-model="form.username" /></el-form-item><el-form-item label="密码"><el-input v-model="form.password" type="password" show-password /></el-form-item><el-button type="primary" size="large" :loading="loading" style="width:100%" @click="submit">登录</el-button></el-form>
    </el-card>
  </main>
</template>
