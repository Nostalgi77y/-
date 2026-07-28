import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { request } from '@/utils/request'
import type { CartItem } from '@/types'

export const useCartStore = defineStore('cart', () => {
  const items=ref<CartItem[]>([]); const loading=ref(false)
  const totalQuantity=computed(()=>items.value.reduce((sum,item)=>sum+item.quantity,0))
  const totalPrice=computed(()=>items.value.reduce((sum,item)=>sum+Number(item.unitPrice)*item.quantity,0))
  async function load(){ loading.value=true; try{items.value=await request<CartItem[]>({url:'/user/cart',method:'GET'})}finally{loading.value=false} }
  async function add(dishId:number){await request<void>({url:'/user/cart',method:'POST',data:{dishId,quantity:1}});await load()}
  async function clear(){await request<void>({url:'/user/cart',method:'DELETE'});items.value=[]}
  return {items,loading,totalQuantity,totalPrice,load,add,clear}
})
