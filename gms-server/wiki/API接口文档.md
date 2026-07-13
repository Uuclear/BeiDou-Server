# GMS Server Web管理API接口文档

## 目录
1. [API概述](#api概述)
2. [认证接口](#认证接口)
3. [账号管理接口](#账号管理接口)
4. [角色管理接口](#角色管理接口)
5. [服务器管理接口](#服务器管理接口)
6. [物品相关接口](#物品相关接口)
7. [转蛋机管理接口](#转蛋机管理接口)
8. [配置管理接口](#配置管理接口)
9. [文件管理接口](#文件管理接口)
10. [通用响应格式](#通用响应格式)
11. [错误码说明](#错误码说明)

---

## API概述

### 版本控制
- 当前API版本：`v1`（通过`ApiConstant.LATEST`引用）
- 所有接口路径前缀包含版本号，例如：`/api/v1/...`

### 认证方式
- 采用 **JWT (JSON Web Token)** 认证
- 登录成功后获取Token
- 后续请求需在HTTP Header中携带：
  ```
  Authorization: Bearer <token>
  ```
- 认证机制：无状态Session（STATELESS），使用Spring Security + JWT过滤器
- 密码加密：BCrypt强哈希算法
- 公开接口（无需认证）：
  - `/auth/**` - 认证相关接口
  - `/swagger-ui/**` - Swagger文档
  - `/v3/api-docs/**` - OpenAPI文档
  - 前端静态资源

### 基础URL
- Web管理API基础路径：根据部署环境配置，默认端口参考应用配置
- 所有接口路径以模块名开头，例如：
  - 认证：`/auth/v1/...`
  - 账号：`/account/v1/...`
  - 角色：`/character/v1/...`

---

## 认证接口

### 1. 用户登录
验证用户名和密码，成功后返回JWT令牌。

- **URL**: `/auth/v1/login`
- **Method**: `POST`
- **请求体**:
```json
{
  "requestId": "可选，请求追踪ID",
  "data": {
    "username": "账号名",
    "password": "密码"
  }
}
```
- **响应**:
```json
{
  "code": 20000,
  "message": "success",
  "responseId": "响应ID",
  "data": {
    "token": "JWT令牌字符串"
  }
}
```

### 2. 用户登出
客户端清除token即可，服务端无状态。

- **URL**: `/auth/v1/logout`
- **Method**: `DELETE`
- **认证**: 需要
- **响应**:
```json
{
  "code": 20000,
  "message": "success",
  "responseId": "响应ID",
  "data": null
}
```

### 3. 刷新Token
使用旧的有效令牌换取新令牌。

- **URL**: `/auth/v1/refreshToken`
- **Method**: `GET`
- **认证**: 需要（在Header中携带旧Token）
- **请求头**: `Authorization: Bearer <旧token>`
- **响应**:
```json
{
  "code": 20000,
  "message": "success",
  "responseId": "响应ID",
  "data": {
    "token": "新JWT令牌字符串"
  }
}
```

---

## 账号管理接口

### 1. 获取当前登录用户信息
- **URL**: `/account/v1/info`
- **Method**: `GET`
- **认证**: 需要
- **响应**: 返回当前登录账号的完整信息（AccountsDO对象）

### 2. 分页查询账号列表
支持按账号ID、名称、登录时间、创建时间等条件过滤。

- **URL**: `/account/v1`
- **Method**: `GET`
- **认证**: 需要
- **请求参数（Query）**:
  | 参数名 | 类型 | 必填 | 说明 |
  |--------|------|------|------|
  | page | Integer | 否 | 页码，从1开始 |
  | size | Integer | 否 | 每页条数 |
  | id | Integer | 否 | 账号ID（精确匹配） |
  | name | String | 否 | 账号名称（模糊匹配） |
  | lastLoginStart | String | 否 | 最后登录开始时间 |
  | lastLoginEnd | String | 否 | 最后登录结束时间 |
  | createdAtStart | String | 否 | 创建时间开始 |
  | createdAtEnd | String | 否 | 创建时间结束 |
- **响应**: 分页账号列表

### 3. 注册新账号
- **URL**: `/account/v1`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "name": "账号名",
    "password": "密码",
    "pin": "PIN码（可选）",
    "pic": "PIC码（可选）",
    "birthday": "生日日期",
    "characterslots": 3,
    "gender": 10,
    "email": "邮箱（可选）"
  }
}
```

### 4. 用户更新自己的账号资料
需要验证旧密码，新密码留空则不修改密码。

- **URL**: `/account/v1`
- **Method**: `PUT`
- **认证**: 需要
- **请求体**:
```json
{
  "data": {
    "oldPassword": "旧密码",
    "newPassword": "新密码（留空则不修改）",
    "pin": "新PIN码",
    "pic": "新PIC码",
    "email": "新邮箱",
    "nick": "昵称"
  }
}
```

### 5. GM更新指定账号的资料
可以修改所有账号字段，包括点券、权限等级等，需要账号离线。

- **URL**: `/account/v1/{id}`
- **Method**: `PUT`
- **认证**: 需要（GM权限）
- **路径参数**: `id` - 要修改的账号ID
- **请求体**:
```json
{
  "data": {
    "nxCredit": 点券数量,
    "maplePoint": 抵用券数量,
    "nxPrepaid": 预付费点券,
    "characterslots": 角色槽位,
    "gender": 性别,
    "banned": 是否封禁,
    "webadmin": GM等级,
    "mute": 是否禁言,
    "rewardpoints": 奖励点,
    "votepoints": 投票点
  }
}
```

### 6. 删除账号
会级联删除该账号下的所有角色。

- **URL**: `/account/v1/{id}`
- **Method**: `DELETE`
- **认证**: 需要（GM权限）
- **路径参数**: `id` - 要删除的账号ID

### 7. 重置账号在线状态
用于处理账号异常卡在登录状态的情况。

- **URL**: `/account/v1/{id}/reset/logged`
- **Method**: `PUT`
- **认证**: 需要（GM权限）
- **路径参数**: `id` - 要重置的账号ID

### 8. 封停账号
同时封禁在线角色的MAC、IP，并强制下线。

- **URL**: `/account/v1/{id}/ban`
- **Method**: `PUT`
- **认证**: 需要（GM权限）
- **路径参数**: `id` - 要封停的账号ID
- **请求体**:
```json
{
  "data": {
    "reason": "封禁原因"
  }
}
```

### 9. 解封账号
同时解封对应的MAC和IP封禁记录。

- **URL**: `/account/v1/{id}/unban`
- **Method**: `PUT`
- **认证**: 需要（GM权限）
- **路径参数**: `id` - 要解封的账号ID

---

## 角色管理接口

### 1. 调整玩家个人倍率
- **URL**: `/character/v1/updateRate`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "characterId": 角色ID,
    "extendName": "expRate | mesoRate | dropRate",
    "value": 倍率值
  }
}
```
- **extendName说明**:
  - `expRate`: 经验倍率
  - `mesoRate`: 金币倍率
  - `dropRate`: 掉落倍率

### 2. 重置玩家单个个人倍率
- **URL**: `/character/v1/resetRate`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "characterId": 角色ID,
    "extendName": "expRate | mesoRate | dropRate"
  }
}
```

### 3. 重置玩家所有个人倍率
- **URL**: `/character/v1/resetRates`
- **Method**: `GET`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "characterId": 角色ID
  }
}
```

### 4. 分页查询在线玩家列表
- **URL**: `/character/v1/online/list`
- **Method**: `POST`
- **认证**: 需要
- **请求体**:
```json
{
  "data": {
    "page": 页码,
    "size": 每页条数,
    "name": "角色名（可选，模糊查询）",
    "worldId": 大区ID（可选）
  }
}
```
- **响应**: 分页在线玩家列表，包含角色名、等级、职业、地图、频道等信息

### 5. 获取账号下角色列表
- **URL**: `/character/v1/account/{accountId}`
- **Method**: `GET`
- **认证**: 需要
- **路径参数**: `accountId` - 账号ID
- **响应**: 该账号下的角色列表

### 6. 删除角色
需要先检查角色是否在线，在线角色不允许删除。

- **URL**: `/character/v1/{cid}`
- **Method**: `DELETE`
- **认证**: 需要（GM权限）
- **路径参数**: `cid` - 角色ID

---

## 服务器管理接口

### 1. 停止所有服务
强制停止整个应用（包括Web服务器和游戏服务器）。

- **URL**: `/server/v1/shutdown`
- **Method**: `GET`
- **认证**: 需要（管理员权限）

### 2. 停止游戏服务器
不停止Web管理后台。

- **URL**: `/server/v1/stopServer`
- **Method**: `GET`
- **认证**: 需要（管理员权限）

### 3. 自定义停止游戏服务器
支持停服消息和倒计时。

- **URL**: `/server/v1/stopServerWithMsgAndInternal`
- **Method**: `POST`
- **认证**: 需要（管理员权限）
- **请求体**:
```json
{
  "data": {
    "message": "停服公告消息",
    "minutes": 倒计时分钟数
  }
}
```

### 4. 启动游戏服务器
- **URL**: `/server/v1/startServer`
- **Method**: `GET`
- **认证**: 需要（管理员权限）

### 5. 重启游戏服务器
- **URL**: `/server/v1/restartServer`
- **Method**: `GET`
- **认证**: 需要（管理员权限）

### 6. 查询服务器状态
- **URL**: `/server/v1/online`
- **Method**: `GET`
- **认证**: 需要
- **响应**:
```json
{
  "code": 20000,
  "data": true
}
```
- `true`: 游戏服务器在线
- `false`: 游戏服务器离线

### 7. 获取大区列表
- **URL**: `/server/v1/world/list`
- **Method**: `GET`
- **认证**: 需要
- **响应**: 所有大区（世界）列表

### 8. 获取频道列表
- **URL**: `/server/v1/channel/list`
- **Method**: `GET`
- **认证**: 需要
- **请求参数（Query）**: `worldId` - 大区ID
- **响应**: 指定大区下的频道列表，包含频道ID、名称、在线人数等

### 9. 查询服务器版本号
- **URL**: `/server/v1/version`
- **Method**: `GET`
- **认证**: 需要
- **响应**: 北斗版本号字符串

---

## GM命令管理接口

### 1. 查询命令库
从数据库分页查询命令库所有指令与状态。

- **URL**: `/command/v1/getCommandListFromDB`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "page": 页码,
    "size": 每页条数
  }
}
```

### 2. 更新命令状态
更新命令库中指定指令的状态（启用/禁用）。

- **URL**: `/command/v1/updateCommand`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "id": 命令ID,
    "enabled": 是否启用
  }
}
```

### 3. 重载事件脚本
复用GM命令代码重载服务器事件脚本，无需重启服务器。

- **URL**: `/command/v1/reloadEventsByGMCommand`
- **Method**: `GET`
- **认证**: 需要（GM权限）

### 4. 重装传送点
复用GM命令代码重装所有传送点，无需重启服务器。

- **URL**: `/command/v1/reloadPortalsByGMCommand`
- **Method**: `GET`
- **认证**: 需要（GM权限）

### 5. 重装地图
复用GM命令代码重装所有地图，并将在线玩家转移到新地图实例。

- **URL**: `/command/v1/reloadMapsByGMCommand`
- **Method**: `GET`
- **认证**: 需要（GM权限）

---

## 物品相关接口

### 背包管理接口

#### 1. 获取背包分类列表
- **URL**: `/inventory/v1/getInventoryTypeList`
- **Method**: `GET`
- **认证**: 需要
- **响应**: 所有背包分类列表（装备、消耗、设置、其他、现金等）

#### 2. 查询有物品的玩家列表
根据条件分页查询有背包物品的玩家列表。

- **URL**: `/inventory/v1/getCharacterList`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "page": 页码,
    "size": 每页条数,
    "characterId": 角色ID（可选）,
    "itemId": 物品ID（可选）,
    "inventoryType": 背包类型（可选）
  }
}
```

#### 3. 获取玩家背包物品
获取指定玩家指定背包分类下的所有物品。

- **URL**: `/inventory/v1/getInventoryList`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "characterId": 角色ID,
    "inventoryType": 背包类型
  }
}
```

#### 4. 修改背包物品
根据条件修改玩家背包物品。

- **URL**: `/inventory/v1/updateInventory`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**: 包含物品ID、物品属性、数量等修改信息

#### 5. 删除背包物品
根据条件删除玩家背包物品。

- **URL**: `/inventory/v1/deleteInventory`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**: 包含要删除的物品信息

### 物品发放接口

#### 给玩家发放资源
给指定玩家发放游戏资源（物品、点券、金币等）。

- **URL**: `/give/v1/resource`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "target": "目标玩家名或角色名",
    "type": "资源类型（item|meso|nxcredit|maplepoint|nxprepaid）",
    "id": 物品ID（发放物品时必填）,
    "quantity": 数量,
    "message": "发放消息（可选）"
  }
}
```

### 掉落管理接口

#### 1. 分页查询怪物掉落列表
- **URL**: `/drop/v1/getDropList`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "page": 页码,
    "size": 每页条数,
    "dropperId": 怪物ID（可选）,
    "itemId": 物品ID（可选）
  }
}
```

#### 2. 分页查询全局掉落列表
- **URL**: `/drop/v1/getGlobalDropList`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**: 同上

#### 3. 新增怪物掉落
- **URL**: `/drop/v1/addDropData`
- **Method**: `PUT`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "dropperid": 怪物ID,
    "itemid": 物品ID,
    "minimum_quantity": 最小数量,
    "maximum_quantity": 最大数量,
    "questid": 任务ID（0为无任务限制）,
    "chance": 掉率
  }
}
```
- **响应**: 返回新增的掉落记录ID

#### 4. 新增全局掉落
- **URL**: `/drop/v1/addGlobalDropData`
- **Method**: `PUT`
- **认证**: 需要（GM权限）
- **请求体**: 包含continent（大陆ID，-1为全球）等字段

#### 5. 更新怪物掉落
- **URL**: `/drop/v1/updateDropData`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**: 必须包含`id`字段，其他要修改的字段

#### 6. 更新全局掉落
- **URL**: `/drop/v1/updateGlobalDropData`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**: 同上

#### 7. 删除怪物掉落
- **URL**: `/drop/v1/deleteDropData/{id}`
- **Method**: `DELETE`
- **认证**: 需要（GM权限）
- **路径参数**: `id` - 掉落记录ID

#### 8. 删除全局掉落
- **URL**: `/drop/v1/deleteGlobalDropData/{id}`
- **Method**: `DELETE`
- **认证**: 需要（GM权限）
- **路径参数**: `id` - 全局掉落记录ID

### 商店管理接口

#### 1. 分页查询商店列表
- **URL**: `/shop/v1/getShopList`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "page": 页码,
    "size": 每页条数,
    "npcid": NPC ID（可选）,
    "shopid": 商店ID（可选）
  }
}
```

#### 2. 查询商店商品列表
根据商店ID分页获取该商店的商品列表。

- **URL**: `/shop/v1/getShopItemList`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "page": 页码,
    "size": 每页条数,
    "shopid": 商店ID
  }
}
```

#### 3. 查询商品详情
- **URL**: `/shop/v1/getShopItem/{id}`
- **Method**: `GET`
- **认证**: 需要（GM权限）
- **路径参数**: `id` - 商品ID

#### 4. 新增商品
- **URL**: `/shop/v1/addShopItem`
- **Method**: `PUT`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "shopid": 商店ID,
    "itemid": 物品ID,
    "price": 价格,
    "pitch": 0,
    "position": 排序位置
  }
}
```
- **响应**: 返回新增的商品ID

#### 5. 更新商品信息
- **URL**: `/shop/v1/updateShopItem`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**: 必须包含`id`字段

#### 6. 删除商品
- **URL**: `/shop/v1/deleteShopItem/{id}`
- **Method**: `DELETE`
- **认证**: 需要（GM权限）
- **路径参数**: `id` - 商品ID

### 现金商城接口

#### 1. 获取商城全部分类
从WZ文件中读取商品分类信息。

- **URL**: `/cashShop/v1/getAllCategoryList`
- **Method**: `GET`
- **认证**: 需要
- **响应**: 商城分类列表（大类、子类）

#### 2. 分类查询商品列表
根据分类分页查询商品列表，支持按上架状态、物品ID过滤，固定每页10条。

- **URL**: `/cashShop/v1/getCommodityByCategory`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "category": 大类ID,
    "subcategory": 子类ID,
    "onSale": 是否上架（可选）,
    "itemId": 物品ID（可选）
  }
}
```

#### 3. 查询商品详情
- **URL**: `/cashShop/v1/getCommodityBySn/{sn}`
- **Method**: `GET`
- **认证**: 需要（GM权限）
- **路径参数**: `sn` - 商品序列号

#### 4. 上架商品
设置商品状态为上架（onSale=1），并更新商品信息。

- **URL**: `/cashShop/v1/onSale`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**: 包含商品SN、价格、数量、有效期等信息

#### 5. 下架商品
设置商品状态为下架（onSale=0）。

- **URL**: `/cashShop/v1/offSale`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "sn": 商品SN
  }
}
```

#### 6. 批量上架商品
批量上架商品并统一修改属性，支持批量修改价格、数量、有效期。

- **URL**: `/cashShop/v1/batchOnSale`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "snList": [商品SN列表],
    "modifyType": "修改类型（price|count|period）",
    "modifyValue": 修改值
  }
}
```

---

## 转蛋机管理接口

### 1. 分页查询奖池列表
- **URL**: `/gachapon/v1/getPools`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "page": 页码,
    "size": 每页条数,
    "gachaponId": 转蛋机ID（可选）,
    "name": "奖池名称（可选）"
  }
}
```

### 2. 创建或更新奖池
- **URL**: `/gachapon/v1/updatePool`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "id": 奖池ID（新增时不填）,
    "name": "奖池名称",
    "gachaponId": 转蛋机ID,
    "weight": 权重,
    "isPublic": 是否公共奖池,
    "prob": 概率,
    "startTime": "启用日期",
    "endTime": "结束日期（可选）",
    "notification": 是否喇叭通知,
    "comment": "备注"
  }
}
```

### 3. 删除奖池
- **URL**: `/gachapon/v1/deletePool`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "id": 奖池ID
  }
}
```

### 4. 获取奖品列表
获取指定奖池下的所有奖品列表。

- **URL**: `/gachapon/v1/getRewards`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "id": 奖池ID
  }
}
```

### 5. 创建或更新奖品
- **URL**: `/gachapon/v1/updateReward`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "id": 奖品ID（新增时不填）,
    "poolId": 奖池ID,
    "itemId": 道具ID,
    "quantity": 单次抽取数量,
    "comment": "备注"
  }
}
```

### 6. 删除奖品
- **URL**: `/gachapon/v1/deleteReward`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "id": 奖品ID
  }
}
```

---

## 配置管理接口

### 游戏配置接口

#### 1. 获取配置类型列表
获取配置参数的大类和类型列表。

- **URL**: `/config/v1/getConfigTypeList`
- **Method**: `GET`
- **认证**: 需要（GM权限）
- **响应**: 配置类型树（大类：world、server；子类：Core、Game Mechanics、Safe、Net、Debug、GM等）

#### 2. 分页获取配置列表
- **URL**: `/config/v1/getConfigList`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "page": 页码,
    "size": 每页条数,
    "configType": "配置大类（可选）",
    "configSubType": "配置子类（可选）",
    "configCode": "参数名（可选，模糊查询）"
  }
}
```

#### 3. 新增配置参数
- **URL**: `/config/v1/addConfig`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "configType": "参数类型",
    "configSubType": "参数子类型",
    "configClazz": "参数值Java类型",
    "configCode": "参数名",
    "configValue": "参数值",
    "configDesc": "参数描述"
  }
}
```

#### 4. 修改配置参数
- **URL**: `/config/v1/updateConfig`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**: 包含id和要修改的字段

#### 5. 删除单个配置
- **URL**: `/config/v1/deleteConfig/{id}`
- **Method**: `DELETE`
- **认证**: 需要（GM权限）
- **路径参数**: `id` - 配置ID

#### 6. 批量删除配置
- **URL**: `/config/v1/deleteConfigList`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": [配置ID1, 配置ID2, ...]
}
```

#### 7. 从YML导入配置
- **URL**: `/config/v1/importYml`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **Content-Type**: `multipart/form-data`
- **参数**: `file` - 上传的YML配置文件

#### 8. 导出配置为YML
- **URL**: `/config/v1/exportYml`
- **Method**: `GET`
- **认证**: 需要（GM权限）
- **响应**: YML文件下载

### 自动封禁配置接口

#### 1. 获取自动封禁配置列表
包含所有自动封禁类型的默认值和当前配置值。

- **URL**: `/autoban/v1/getConfigList`
- **Method**: `GET`
- **认证**: 需要（GM权限）
- **响应**: 自动封禁配置列表，包含封禁点数、过期时间、是否禁用、描述等

#### 2. 更新自动封禁配置
- **URL**: `/autoban/v1/updateConfig`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "type": "封禁类型",
    "points": 封禁点数,
    "expiration": 过期时间（分钟）,
    "disabled": 是否禁用,
    "description": "描述"
  }
}
```

---

## 文件管理接口

提供安全的文件树浏览、文件读取和写入功能，主要用于脚本文件管理。

### 1. 读取文件树结构
- **URL**: `/file/v1/tree`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "currentKey": "当前目录路径（根目录为空）"
  }
}
```
- **响应**: 文件树节点列表，包含文件名、路径、是否为目录等

### 2. 读取文件内容
- **URL**: `/file/v1/tree/read`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "currentKey": "文件所在目录路径",
    "title": "文件名"
  }
}
```
- **响应**: 文件内容字符串

### 3. 写入文件内容
- **URL**: `/file/v1/tree/write`
- **Method**: `POST`
- **认证**: 需要（GM权限）
- **请求体**:
```json
{
  "data": {
    "currentKey": "文件所在目录路径",
    "title": "文件名",
    "content": "要写入的文件内容"
  }
}
```
- **响应**: "写入成功"

---

## 通用功能接口

### 1. 查询装备基础属性
根据物品ID查询装备基础属性信息。

- **URL**: `/common/v1/getEquipmentInfoByItemId`
- **Method**: `POST`
- **认证**: 需要
- **请求体**:
```json
{
  "data": {
    "itemId": 物品ID
  }
}
```

### 2. 查询在线玩家总数
查询指定世界列表中当前在线的玩家总数。

- **URL**: `/common/v1/getAllWorldsOnlinePlayersCount`
- **Method**: `POST`
- **认证**: 需要
- **请求体**:
```json
{
  "data": {
    "worldIdList": [世界ID列表]
  }
}
```
- **响应**: 在线玩家总数

### 3. 游戏资料通用查询
根据ID或名称查询游戏内各种信息，包括物品、怪物、地图、NPC、技能等。

- **URL**: `/common/v1/informationSearch`
- **Method**: `POST`
- **认证**: 需要
- **请求体**:
```json
{
  "data": {
    "type": "查询类型（item|mob|map|npc|skill等）",
    "id": ID（可选）,
    "name": "名称（可选，模糊查询）"
  }
}
```
- **响应**: 查询结果列表

---

## 通用响应格式

所有API接口统一使用以下响应格式：

```json
{
  "code": 20000,
  "message": "success",
  "responseId": "550e8400-e29b-41d4-a716-446655440000",
  "data": { ... }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 响应状态码，20000表示成功 |
| message | String | 响应消息，成功为"success"，失败为错误信息 |
| responseId | String | 响应唯一标识ID（UUID），用于请求追踪 |
| data | Object | 响应数据，失败时为null |

### 请求体格式
所有POST/PUT请求统一使用SubmitBody包装：

```json
{
  "requestId": "可选，客户端自定义请求追踪ID",
  "data": { ... }
}
```

如果不提供requestId，服务端会自动生成。

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 20000 | 操作成功 |
| 40000 | 请求体格式不匹配 |
| 40001 | 请求方法不支持 |
| 40002 | 非法参数 |
| 40004 | 资源未找到 |
| 50000 | 服务器内部错误 |
| 50003 | 服务器繁忙 |

### 其他HTTP状态码
- 401: 未认证/Token失效
- 403: 无权限访问
- 404: 接口不存在
- 500: 服务器内部错误

所有错误响应同样遵循通用响应格式，message字段包含具体的错误描述信息。
