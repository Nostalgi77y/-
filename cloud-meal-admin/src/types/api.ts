export interface ApiResponse<T> { code: string; message: string; data: T }
export interface Dish { id: number; categoryId: number; name: string; price: number; image?: string; description?: string; stock: number; status: number }
export interface OrderDetail { id: number; name: string; unitPrice: number; quantity: number; amount: number }
export interface Order { id: number; orderNumber: string; status: string; payStatus: string; amount: number; consignee: string; phone: string; address: string; createdTime: string; details: OrderDetail[] }
