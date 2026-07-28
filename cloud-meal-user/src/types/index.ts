export interface ApiResponse<T> { code: string; message: string; data: T }
export interface Category { id:number; name:string; type:number; sort:number }
export interface Dish { id:number; categoryId:number; name:string; price:number; image?:string; description?:string; stock:number }
export interface CartItem { id:number; dishId:number; dishName:string; image?:string; unitPrice:number; quantity:number }
export interface Address { id:number; consignee:string; phone:string; province?:string; city?:string; district?:string; detail:string; isDefault:number }
export interface OrderDetail { id:number; name:string; unitPrice:number; quantity:number; amount:number }
export interface Order { id:number; orderNumber:string; status:string; payStatus:string; amount:number; consignee:string; address:string; createdTime:string; details:OrderDetail[] }
