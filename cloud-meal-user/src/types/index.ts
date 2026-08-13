export interface ApiResponse<T> { code: string; message: string; data: T }
export interface Category { id:string; name:string; type:number; sort:number }
export interface Dish { id:string; categoryId:string; name:string; price:number; image?:string; description?:string; stock:number }
export interface CartItem { id:string; dishId:string; dishName:string; image?:string; unitPrice:number; quantity:number }
export interface Address { id:string; consignee:string; phone:string; province?:string; city?:string; district?:string; detail:string; isDefault:number }
export interface Coupon { id:string; name:string; thresholdAmount:number; discountAmount:number; validUntil:string; status:number }
export interface UserCoupon { userCouponId:string; couponId:string; name:string; thresholdAmount:number; discountAmount:number; validUntil:string; status:string; usable:boolean }
export interface OrderDetail { id:string; name:string; unitPrice:number; quantity:number; amount:number }
export interface Order { id:string; orderNumber:string; status:string; payStatus:string; originalAmount:number; discountAmount:number; amount:number; consignee:string; address:string; createdTime:string; details:OrderDetail[] }
