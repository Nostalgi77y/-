export interface ApiResponse<T> { code: string; message: string; data: T }
export interface Dish { id: string; categoryId: string; name: string; price: number; image?: string; description?: string; stock: number; status: number }
export interface Category { id: string; name: string; type: number; sort: number; status: number }
export interface OrderDetail { id: string; name: string; unitPrice: number; quantity: number; amount: number }
export interface Order { id: string; orderNumber: string; status: string; payStatus: string; amount: number; consignee: string; phone: string; address: string; createdTime: string; details: OrderDetail[] }
