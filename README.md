# 云膳外卖平台（Cloud Meal）

面向求职与技术实践的多端外卖点餐与订单履约平台。

## 工程组成

- `cloud-meal-server`：Spring Boot 3 后端
- `cloud-meal-admin`：Vue 3 商家管理端
- `cloud-meal-user`：uni-app 微信小程序用户端
- `deploy`：MySQL、Redis、RabbitMQ、MinIO 等本地基础设施
- `docs`：架构、运行、测试及后续迭代文档

## 已实现的第一版

- Spring Security + JWT 管理端/用户端认证
- 菜品分类、菜品管理、库存与 Redis 缓存
- 购物车、地址、下单、订单状态机
- 服务端价格重算、数据库原子扣库存、订单号幂等
- RabbitMQ 延迟关单与超时库存回补
- 模拟支付及支付状态条件更新
- WebSocket 新订单、支付和状态变化通知
- Vue 3 + TypeScript 商家管理端
- uni-app + Vue 3 + TypeScript 微信小程序端
- Docker Compose、Flyway、Actuator、OpenAPI 和 GitHub Actions

## 快速开始

请从 [`docs/运行手册.md`](docs/运行手册.md) 开始。默认管理端账号：`admin`，密码：`Admin@123456`。

> 默认密码、数据库密码与 JWT 密钥仅用于本地开发，部署前必须全部更换。
