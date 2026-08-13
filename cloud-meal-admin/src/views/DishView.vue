<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules, type UploadRequestOptions, type UploadRawFile } from 'element-plus'
import http from '@/utils/request'
import type { ApiResponse, Category, Dish } from '@/types/api'

interface FileUploadData { url: string }

const dishes = ref<Dish[]>([])
const categories = ref<Category[]>([])
const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const dialogVisible = ref(false)
const editingId = ref<string | null>(null)
const formRef = ref<FormInstance>()
const form = reactive({ categoryId: undefined as string | undefined, name: '', price: 0, image: '', description: '', stock: 0, status: 1 })
const rules: FormRules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  price: [{ required: true, type: 'number', min: 0.01, message: '价格必须大于 0', trigger: 'blur' }],
  stock: [{ required: true, type: 'number', min: 0, message: '库存不能小于 0', trigger: 'blur' }],
}

async function load() {
  loading.value = true
  try {
    const [dishResponse, categoryResponse] = await Promise.all([
      http.get<ApiResponse<Dish[]>>('/admin/dishes'),
      http.get<ApiResponse<Category[]>>('/user/categories'),
    ])
    dishes.value = dishResponse.data.data
    categories.value = categoryResponse.data.data
  } finally { loading.value = false }
}

function resetForm() {
  Object.assign(form, { categoryId: categories.value[0]?.id, name: '', price: 0, image: '', description: '', stock: 0, status: 1 })
}

function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

function openEdit(dish: Dish) {
  editingId.value = dish.id
  Object.assign(form, { categoryId: dish.categoryId, name: dish.name, price: Number(dish.price), image: dish.image || '', description: dish.description || '', stock: dish.stock, status: dish.status })
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

function beforeUpload(file: UploadRawFile) {
  const allowed = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'].includes(file.type)
  if (!allowed) ElMessage.error('仅支持 JPG、PNG、WebP 或 GIF 图片')
  else if (file.size > 5 * 1024 * 1024) ElMessage.error('图片大小不能超过 5MB')
  return allowed && file.size <= 5 * 1024 * 1024
}

async function uploadImage(options: UploadRequestOptions) {
  uploading.value = true
  try {
    const body = new FormData()
    body.append('file', options.file)
    const response = await http.post<ApiResponse<FileUploadData>>('/admin/files/images', body)
    form.image = response.data.data.url
    options.onSuccess(response.data)
    ElMessage.success('图片上传成功')
  } catch (error) {
    options.onError(error as Error)
  } finally { uploading.value = false }
}

async function save() {
  if (!await formRef.value?.validate()) return
  saving.value = true
  try {
    const payload = { ...form, categoryId: form.categoryId as string }
    if (editingId.value) await http.put(`/admin/dishes/${editingId.value}`, payload)
    else await http.post('/admin/dishes', payload)
    ElMessage.success(editingId.value ? '菜品修改成功，用户端数据已同步' : '菜品添加成功，用户端已可查看')
    dialogVisible.value = false
    await load()
  } finally { saving.value = false }
}

function categoryName(id: string) { return categories.value.find(item => item.id === id)?.name || '未分类' }
onMounted(load)
</script>

<template>
  <div>
    <div class="page-heading">
      <div><h1>菜品管理</h1><p>维护图片、价格、库存与上下架状态，保存后同步刷新用户端数据</p></div>
      <el-button type="primary" @click="openCreate">新增菜品</el-button>
    </div>
    <el-card class="content-card">
      <el-table :data="dishes" v-loading="loading">
        <el-table-column label="图片" width="92"><template #default="scope"><el-image class="dish-thumb" :src="scope.row.image" fit="cover"><template #error><div class="image-empty">暂无</div></template></el-image></template></el-table-column>
        <el-table-column prop="name" label="菜品" min-width="150" />
        <el-table-column label="分类" min-width="120"><template #default="scope">{{ categoryName(scope.row.categoryId) }}</template></el-table-column>
        <el-table-column prop="price" label="价格"><template #default="scope">¥ {{ Number(scope.row.price).toFixed(2) }}</template></el-table-column>
        <el-table-column prop="stock" label="库存" />
        <el-table-column prop="status" label="状态"><template #default="scope"><el-tag :type="scope.row.status ? 'success' : 'info'">{{ scope.row.status ? '在售' : '停售' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="100"><template #default="scope"><el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button></template></el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑菜品' : '新增菜品'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px">
        <el-form-item label="菜品名称" prop="name"><el-input v-model="form.name" maxlength="100" /></el-form-item>
        <el-form-item label="所属分类" prop="categoryId"><el-select v-model="form.categoryId" style="width:100%"><el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-row :gutter="16"><el-col :span="12"><el-form-item label="价格" prop="price"><el-input-number v-model="form.price" :min="0.01" :precision="2" style="width:100%" /></el-form-item></el-col><el-col :span="12"><el-form-item label="库存" prop="stock"><el-input-number v-model="form.stock" :min="0" style="width:100%" /></el-form-item></el-col></el-row>
        <el-form-item label="菜品图片">
          <div class="upload-row">
            <el-image v-if="form.image" class="upload-preview" :src="form.image" fit="cover" />
            <el-upload :show-file-list="false" accept="image/jpeg,image/png,image/webp,image/gif" :before-upload="beforeUpload" :http-request="uploadImage">
              <el-button :loading="uploading">{{ form.image ? '重新上传' : '选择图片上传' }}</el-button>
            </el-upload>
            <el-button v-if="form.image" link type="danger" @click="form.image=''">移除</el-button>
          </div>
          <div class="upload-tip">支持 JPG、PNG、WebP、GIF，最大 5MB</div>
        </el-form-item>
        <el-form-item label="菜品描述"><el-input v-model="form.description" type="textarea" :rows="3" maxlength="500" show-word-limit /></el-form-item>
        <el-form-item label="销售状态"><el-radio-group v-model="form.status"><el-radio :value="1">在售</el-radio><el-radio :value="0">停售</el-radio></el-radio-group></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="saving" :disabled="uploading" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.dish-thumb{width:56px;height:56px;border-radius:10px}.image-empty{width:56px;height:56px;display:grid;place-items:center;background:#eef2ef;color:#8a948f;font-size:12px}.upload-row{display:flex;align-items:center;gap:12px}.upload-preview{width:96px;height:96px;border-radius:12px;border:1px solid #e1e7e3}.upload-tip{color:#8a948f;font-size:12px;margin-top:6px}
</style>
