#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import os
import re

ENTITY_DIR = '/workspace/gms-server/src/main/java/org/gms/dao/entity'
MAPPER_DIR = '/workspace/gms-server/src/main/java/org/gms/dao/mapper'

# 完整的表名映射
TABLE_DESCRIPTIONS = {
    'accounts': ('账号信息', '存储游戏用户的账号基本信息、登录状态、充值点数等数据'),
    'alliance': ('家族联盟', '存储多个家族组成的联盟信息，包括联盟名称、容量、公告和职位名称设置'),
    'allianceguilds': ('联盟家族关联', '存储家族联盟与成员家族之间的多对多关联关系'),
    'area_info': ('区域信息', '存储角色在自定义区域的相关信息数据'),
    'autoban_config': ('自动封禁配置', '存储自动反作弊系统的封禁规则配置'),
    'bbs_replies': ('论坛回复', '存储家族论坛帖子的回复内容'),
    'bbs_threads': ('论坛帖子', '存储家族论坛的帖子主题信息'),
    'bosslog_daily': ('每日BOSS挑战记录', '记录角色每日挑战BOSS的尝试时间，用于每日BOSS重置机制'),
    'bosslog_weekly': ('每周BOSS挑战记录', '记录角色每周挑战BOSS的尝试时间，用于每周BOSS重置机制'),
    'buddies': ('好友关系', '存储角色之间的好友关系及好友分组信息'),
    'characters': ('游戏角色', '存储游戏角色的详细属性信息，包括等级、属性、位置、外观等'),
    'command_info': ('命令信息', '存储游戏内GM命令的配置信息，包括使用权限等级和命令语法'),
    'cooldowns': ('技能冷却', '存储角色技能的冷却时间信息'),
    'drop_data': ('怪物掉落数据', '存储特定怪物掉落物品的配置信息'),
    'drop_data_global': ('全局掉落数据', '存储按大陆区域配置的全局物品掉落信息'),
    'dueyitems': ('快递物品', '存储快递包裹中的物品明细，关联包裹与物品栏物品'),
    'dueypackages': ('快递包裹', '存储快递系统的包裹信息，包括寄件人、收件人、金币和留言'),
    'eventstats': ('活动统计', '存储游戏活动的统计数据'),
    'extend_value': ('扩展值', '存储角色或系统的扩展键值对数据'),
    'famelog': ('人气记录', '存储角色人气值变更的日志记录'),
    'family_character': ('家族成员', '存储家族与角色的关联关系及家族成员信息'),
    'family_entitlement': ('家族权限', '存储家族成员的权限配置信息'),
    'flyway_schema_history': ('数据库版本历史', 'Flyway数据库迁移工具的版本历史记录表'),
    'fredstorage': ('Fred仓库存储', '存储Fred仓库系统的物品数据'),
    'gachapon_reward': ('转蛋机奖励', '存储转蛋机的奖励物品配置'),
    'gachapon_reward_pool': ('转蛋机奖励池', '存储转蛋机的奖励池配置信息'),
    'game_config': ('游戏配置', '存储游戏服务器的各项配置参数'),
    'gifts': ('礼物', '存储角色之间赠送的礼物记录'),
    'guilds': ('家族', '存储游戏家族的基本信息、等级、公告等数据'),
    'hp_mp_alert': ('HP/MP警告', '存储角色HP/MP低血量警告配置'),
    'hwidaccounts': ('HWID账号关联', '存储硬件ID与账号的绑定关联关系'),
    'hwidbans': ('HWID封禁', '存储被封禁的硬件ID列表'),
    'inventoryequipment': ('装备栏物品', '存储角色装备栏中的装备物品详细属性'),
    'inventoryitems': ('物品栏物品', '存储角色物品栏中的所有物品数据'),
    'inventorymerchant': ('商人仓库物品', '存储个人商店/商人的物品库存'),
    'ipbans': ('IP封禁', '存储被封禁的IP地址列表'),
    'keymap': ('按键映射', '存储角色的自定义快捷键配置'),
    'lang_resources': ('多语言资源', '存储游戏多语言本地化文本资源'),
    'macbans': ('MAC封禁', '存储被封禁的MAC地址列表'),
    'macfilters': ('MAC过滤器', '存储MAC地址过滤规则配置'),
    'makercreatedata': ('制作创建数据', '存储Maker系统的创建物品数据配置'),
    'makerreagentdata': ('制作试剂数据', '存储Maker系统的制作试剂材料配置'),
    'makerrecipedata': ('制作配方数据', '存储Maker系统的制作配方配置'),
    'makerrewarddata': ('制作奖励数据', '存储Maker系统的制作奖励配置'),
    'marriages': ('婚姻关系', '存储游戏角色之间的婚姻关系信息'),
    'medalmaps': ('勋章地图', '存储勋章与地图的关联配置'),
    'modified_cash_item': ('修改的现金物品', '存储被修改过属性的现金物品数据'),
    'monsterbook': ('怪物手册', '存储角色怪物手册的收集进度信息'),
    'monstercarddata': ('怪物卡牌数据', '存储怪物卡牌的基础数据配置'),
    'mts_cart': ('MTS购物车', '存储MTS交易系统的购物车数据'),
    'mts_items': ('MTS物品', '存储MTS交易系统的物品数据'),
    'namechanges': ('改名记录', '存储角色名称变更的历史记录'),
    'newyear': ('新年活动', '存储新年活动的相关数据'),
    'notes': ('纸条/备忘录', '存储角色之间发送的纸条消息'),
    'nxcode': ('NX兑换码', '存储NX充值兑换码信息'),
    'nxcode_items': ('NX兑换码物品', '存储NX兑换码可兑换的物品列表'),
    'nxcoupons': ('NX优惠券', '存储NX优惠券/礼品券数据'),
    'petignores': ('宠物忽略', '存储宠物忽略捡取的物品配置'),
    'pets': ('宠物', '存储角色宠物的详细信息'),
    'playerdiseases': ('角色异常状态', '存储角色当前的异常状态/疾病效果'),
    'playernpcs': ('玩家NPC', '存储玩家自定义NPC的配置数据'),
    'playernpcs_equip': ('玩家NPC装备', '存储玩家NPC的装备配置'),
    'playernpcs_field': ('玩家NPC地图', '存储玩家NPC在地图上的部署配置'),
    'plife': ('游戏生命', '存储游戏地图上的生命物体（NPC、怪物、反应器等）配置'),
    'questactions': ('任务动作', '存储任务完成时执行的动作配置'),
    'questprogress': ('任务进度', '存储角色的任务进行进度信息'),
    'questrequirements': ('任务需求', '存储任务接取/完成的前置条件配置'),
    'queststatus': ('任务状态', '存储角色的任务当前状态'),
    'quickslotkeymapped': ('快速栏快捷键', '存储角色快速栏的快捷键映射配置'),
    'reactordrops': ('反应器掉落', '存储反应器触发时的掉落物品配置'),
    'reports': ('举报记录', '存储玩家提交的举报记录'),
    'responses': ('响应/回复', '存储系统的响应配置数据'),
    'rings': ('戒指', '存储角色佩戴的戒指数据，包括结婚戒指、好友戒指等'),
    'savedlocations': ('保存位置', '存储角色保存的传送点/复活点位置信息'),
    'server_queue': ('服务器队列', '存储服务器的排队等待数据'),
    'shopitems': ('商店物品', '存储商店中出售的物品配置'),
    'shops': ('商店', '存储游戏商店的基本信息配置'),
    'skillmacros': ('技能宏', '存储角色的技能宏配置'),
    'skills': ('技能', '存储角色已学习的技能及技能等级信息'),
    'specialcashitems': ('特殊现金物品', '存储特殊现金物品的配置数据'),
    'storages': ('仓库', '存储角色仓库的物品数据'),
    'trocklocations': ('传送石位置', '存储VIP传送石保存的地图位置'),
    'wishlists': ('愿望清单', '存储玩家的愿望清单/购物车数据'),
    'worldtransfers': ('世界转移', '存储角色跨服务器/世界转移的记录')
}

# 完整的字段注释映射
FIELD_COMMENTS = {
    'id': '唯一ID',
    'name': '名称',
    'password': '密码',
    'pin': '二级密码PIN码',
    'pic': '角色选择密码PIC码',
    'loggedin': '登录状态：0-未登录，1-已登录',
    'lastlogin': '最后登录时间',
    'createdat': '账号创建时间',
    'birthday': '生日日期',
    'banned': '是否被封禁',
    'banreason': '封禁原因',
    'macs': '绑定的MAC地址列表',
    'nxCredit': 'NX信用点余额',
    'maplePoint': '枫叶点余额',
    'nxPrepaid': 'NX预充值点数余额',
    'characterslots': '可创建角色槽位数量',
    'gender': '性别：0-女，1-男',
    'tempban': '临时封禁到期时间',
    'greason': '封禁原因代码',
    'tos': '是否已同意服务条款',
    'sitelogged': '网站登录标识',
    'webadmin': '网站管理员权限等级',
    'nick': '昵称',
    'mute': '禁言状态：0-未禁言，大于0-禁言时长',
    'email': '注册邮箱',
    'ip': 'IP地址',
    'rewardpoints': '奖励积分',
    'votepoints': '投票积分',
    'hwid': '硬件ID（HWID）',
    'language': '语言设置',
    'capacity': '容量上限',
    'notice': '公告内容',
    'rank1': '职位1名称（盟主）',
    'rank2': '职位2名称（副盟主）',
    'rank3': '职位3名称（长老）',
    'rank4': '职位4名称（成员）',
    'rank5': '职位5名称（新手）',
    'allianceid': '联盟ID',
    'guildid': '家族ID',
    'charid': '角色ID',
    'characterid': '角色ID',
    'accountid': '账号ID',
    'area': '区域ID',
    'info': '信息内容',
    'type': '类型',
    'disabled': '是否禁用：true=禁用',
    'points': '积分',
    'expireTime': '过期时间（毫秒）',
    'description': '描述说明',
    'createTime': '创建时间',
    'updateTime': '更新时间',
    'replyid': '回复唯一ID',
    'threadid': '帖子ID',
    'postercid': '发帖者角色ID',
    'timestamp': '时间戳',
    'content': '内容文本',
    'icon': '图标类型',
    'replycount': '回复数量',
    'startpost': '帖子正文内容',
    'localthreadid': '家族内本地帖子ID',
    'bosstype': 'BOSS类型标识',
    'attempttime': '挑战尝试时间',
    'buddyid': '好友角色ID',
    'pending': '好友请求状态：0-已接受，1-待确认',
    'group': '好友分组名称',
    'world': '服务器世界ID',
    'level': '等级',
    'exp': '经验值',
    'gachaexp': '转蛋经验值',
    'attrStr': '力量属性（STR）',
    'attrDex': '敏捷属性（DEX）',
    'attrLuk': '运气属性（LUK）',
    'attrInt': '智力属性（INT）',
    'hp': '当前HP',
    'mp': '当前MP',
    'maxhp': '最大HP',
    'maxmp': '最大MP',
    'meso': '金币数量',
    'hpMpUsed': 'HP/MP已使用量',
    'job': '职业ID',
    'skincolor': '皮肤颜色',
    'fame': '人气值',
    'fquest': '新手任务完成标记',
    'hair': '发型ID',
    'face': '脸型ID',
    'ap': '剩余能力点（AP）',
    'sp': '技能点（SP）分配字符串',
    'map': '当前地图ID',
    'spawnpoint': '出生点ID',
    'gm': 'GM权限等级',
    'party': '组队ID',
    'buddyCapacity': '好友列表容量上限',
    'createdate': '角色创建时间',
    'rank': '世界总排名',
    'rankMove': '排名变动值',
    'jobRank': '职业排名',
    'jobRankMove': '职业排名变动值',
    'guildrank': '家族内职位等级',
    'messengerid': '聊天群ID',
    'messengerposition': '聊天群内职位',
    'mountlevel': '坐骑等级',
    'mountexp': '坐骑经验值',
    'mounttiredness': '坐骑疲劳度',
    'omokwins': '五子棋胜场数',
    'omoklosses': '五子棋败场数',
    'omokties': '五子棋平局数',
    'matchcardwins': '记忆卡牌胜场数',
    'matchcardlosses': '记忆卡牌败场数',
    'matchcardties': '记忆卡牌平局数',
    'merchantmesos': '个人商店存储的金币',
    'hasmerchant': '是否开设个人商店',
    'equipslots': '装备栏槽位数量',
    'useslots': '消耗栏槽位数量',
    'setupslots': '设置栏槽位数量',
    'etcslots': '其他栏槽位数量',
    'familyId': '家族ID',
    'monsterbookcover': '怪物手册封面怪物ID',
    'allianceRank': '联盟内职位等级',
    'vanquisherStage': '征服者关卡进度',
    'ariantPoints': '阿里安特点数',
    'dojoPoints': '武陵道场点数',
    'lastDojoStage': '最后到达的道场关卡',
    'finishedDojoTutorial': '是否完成道场教程',
    'vanquisherKills': '征服者击杀数',
    'summonValue': '召唤数值',
    'partnerId': '伴侣角色ID',
    'marriageItemId': '结婚戒指物品ID',
    'reborns': '重生次数',
    'pqpoints': '组队任务点数',
    'dataString': '扩展数据字符串',
    'lastLogoutTime': '最后下线时间',
    'lastExpGainTime': '最后获得经验时间',
    'partySearch': '是否在组队搜索列表中',
    'jailexpire': '监禁到期时间戳',
    'syntax': '命令语法说明',
    'defaultLevel': '默认权限等级',
    'clazz': '命令处理类完整类名',
    'enabled': '是否启用',
    'skillid': '技能ID',
    'length': '持续时长（毫秒）',
    'starttime': '开始时间戳',
    'dropperid': '掉落者ID（怪物ID）',
    'itemid': '物品ID',
    'minimumQuantity': '最小掉落/堆叠数量',
    'maximumQuantity': '最大掉落/堆叠数量',
    'questid': '关联任务ID，0表示无任务限制',
    'chance': '掉落几率（分母）',
    'continent': '大陆ID',
    'comments': '备注说明',
    'packageid': '包裹唯一ID',
    'inventoryitemid': '物品栏物品ID',
    'receiverid': '收件人角色ID',
    'sendername': '寄件人角色名称',
    'message': '留言消息内容',
    'checked': '状态：0-未读，1-已查看/已领取',
    'uniqueid': '物品唯一ID',
    'position': '物品在栏位中的位置',
    'inventorytype': '物品栏类型：1-装备，2-消耗，3-设置，4-其他，5-现金',
    'quantity': '物品数量',
    'owner': '物品所有者',
    'petid': '宠物ID',
    'flag': '物品标记/锁状态',
    'expiration': '物品过期时间',
    'giftfrom': '礼物赠送者',
    'ringid': '戒指唯一ID',
    'partnerid': '伴侣角色ID',
    'itemname': '物品名称',
    'address': '地址信息',
    'mac': 'MAC地址',
    'hwid': '硬件ID',
    'reason': '原因',
    'character': '角色名称',
    'startmap': '起始地图ID',
    'endmap': '目标地图ID',
    'face': '脸型',
    'hair': '发型',
    'skin': '皮肤',
    'def': '防御力',
    'atk': '攻击力',
    'matk': '魔法攻击力',
    'mdef': '魔法防御力',
    'acc': '命中率',
    'avo': '回避率',
    'hands': '手数',
    'speed': '速度',
    'jump': '跳跃力',
    'hpR': 'HP回复量',
    'mpR': 'MP回复量',
    'int': '智力',
    'str': '力量',
    'dex': '敏捷',
    'luk': '运气',
    'hp': 'HP',
    'mp': 'MP',
    'slot': '槽位',
    'upgradeSlots': '可升级次数',
    'level': '物品等级/强化等级',
    'str': '力量加成',
    'dex': '敏捷加成',
    'int': '智力加成',
    'luk': '运气加成',
    'vit': '体力加成',
    'watk': '武器攻击力',
    'wdef': '武器防御力',
    'matk': '魔法攻击力',
    'mdef': '魔法防御力',
    'acc': '命中',
    'avoid': '回避',
    'hands': '攻速',
    'speed': '移速',
    'jump': '跳跃',
    'skill': '技能ID',
    'value': '数值/值',
    'cashid': '现金物品ID',
    'sn': '序列号SN',
    'accountid': '账号ID',
    'worldid': '世界ID',
    'gender': '性别',
    'skincolor': '肤色',
    'mixbasehair': '混合基础发型',
    'mixstathair': '混合统计发型',
    'mixhaircolor': '混合染发颜色',
    'charismaexp': '魅力经验',
    'insightexp': '洞察力经验',
    'willexp': '意志力经验',
    'craftexp': '手技经验',
    'senseexp': '感性经验',
    'charmexp': '魅力经验',
    'jobid': '职业ID',
    'victimid': '受害者角色ID',
    'reporterid': '举报者角色ID',
    'time': '时间',
    'report': '举报内容',
    'severity': '严重程度',
    'victim': '受害者名称',
    'reporter': '举报者名称',
    'key': '键/配置键',
    'code': '代码/兑换码',
    'items': '物品列表',
    'retailer': '零售商',
    'expirydate': '到期日期',
    'coupon': '优惠券编号',
    'point': '点数',
    'period': '有效期',
    'active': '是否激活',
    'remaining': '剩余数量/时间',
    'uniqueid': '唯一ID',
    'fmid': '家族成员ID',
    'seniorMessage': '长老寄语',
    'grade': '等级/年级',
    'allianceId': '联盟ID',
    'signid': '签名ID',
    'signature': '签名',
    'memberid': '成员ID',
    'entitlementid': '权限ID',
    'region': '区域',
    'installed': '是否已安装',
    'script': '脚本名称',
    'x': 'X坐标',
    'y': 'Y坐标',
    'f': '朝向/标记位',
    'hide': '是否隐藏',
    'mobtime': '怪物刷新时间',
    'cy': '点击Y坐标',
    'f0': 'F0标记',
    'f1': 'F1标记',
    'limitedname': '限制名称',
    'filepath': '文件路径',
    'battleexp': '战斗经验',
    'mapid': '地图ID',
    'reactorid': '反应器ID',
    'data': '数据内容',
    'quest': '任务ID',
    'state': '状态',
    'completionTime': '完成时间',
    'customData': '自定义数据',
    'customData2': '自定义数据2',
    'forfeited': '是否已放弃',
    'questid': '任务ID',
    'status': '状态值',
    'skillid': '技能ID',
    'macroidx': '宏索引',
    'macroid': '宏ID',
    'keyconfig': '按键配置',
    'action': '动作ID',
    'savedlocation': '保存位置类型',
    'portal': '传送点ID',
    'addedtime': '添加时间',
    'duration': '持续时间',
    'visible': '是否可见',
    'sellerid': '卖家角色ID',
    'buyerid': '买家角色ID',
    'price': '价格',
    'slots': '槽位数量',
    'modifier': '修改器',
    'sellprice': '出售价格',
    'itemid': '物品ID',
    'discountprice': '折扣价格',
    'stock': '库存数量',
    'tokencoins': '代币数量',
    'dbid': '数据库ID',
    'characterid': '角色ID',
    'stat': '属性值',
    'skillevel': '技能等级',
    'masterlevel': ' master等级',
    'expiration': '过期时间',
    'cooldown': '冷却时间',
    'chalk': '标记',
    'married': '是否已婚',
    'marriageitemid': '结婚物品ID',
    'ringid': '戒指ID',
    'partnerid': '伴侣ID',
    'petid': '宠物ID',
    'name': '名称',
    'level': '等级',
    'closeness': '亲密度',
    'fullness': '饱食度',
    'sex': '性别',
    'seconds': '秒数',
    'petindex': '宠物索引',
    'buddyid': '好友ID',
    'pending': '待确认',
    'groupname': '分组名称',
    'keymap': '按键映射',
    'hotkey': '热键',
    'type': '类型',
    'action': '动作',
    'extendId': '扩展字段ID',
    'extendType': '扩展字段类型：11-账号，12-账号日清，13-账号周清；21-角色，22-角色日清，23-角色周清',
    'extendName': '扩展字段名称',
    'extendValue': '扩展字段值',
    'source': '来源',
    'success': '是否成功',
    'fame': '人气值',
    'when': '时间',
    'reason': '原因',
    'installation_step': '安装步骤版本',
    'version': '版本号',
    'description': '描述',
    'script': '脚本',
    'checksum': '校验值',
    'installed_by': '安装者',
    'installed_on': '安装时间',
    'execution_time': '执行时间',
    'success': '是否成功',
    'storageid': '仓库ID',
    'accountid': '账号ID',
    'world': '世界',
    'slots': '槽位数',
    'meso': '金币',
    'viprock': 'VIP传送石',
    'mapid': '地图ID',
    'world': '世界ID',
    'connection': '连接信息',
    'total': '总数/总计',
    'inQueue': '排队中',
    'releaseType': '发布类型',
    'category': '分类',
    'gender': '性别',
    'sale': '是否出售',
    'count': '数量',
    'priority': '优先级',
    'period': '周期/有效期',
    'reqlevel': '需求等级',
    'maplepoints': '枫叶点',
    'mesos': '金币',
    'itemid': '物品ID',
    'quantity': '数量',
    'startday': '开始日期',
    'days': '天数',
    'endday': '结束日期',
    'year': '年份',
    'msg': '消息内容',
    'gift': '礼物ID',
    'to_name': '收件人名称',
    'from_name': '寄件人名称',
    'message': '消息',
    'sn': '序列号',
    'ringid': '戒指ID',
    'partnerid': '伴侣ID',
    'partnername': '伴侣名称',
    'itemid': '物品ID',
    'event': '事件/活动',
    'instance': '实例'
}

def get_field_comment(field_name, column_name=None):
    """获取字段的中文注释"""
    if field_name in FIELD_COMMENTS:
        return FIELD_COMMENTS[field_name]
    if column_name and column_name in FIELD_COMMENTS:
        return FIELD_COMMENTS[column_name]
    return field_name

def fix_entity_file(file_path):
    """修复实体类文件的注释位置并完善注释"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original_content = content
    
    # 首先，清理掉之前错误插入的注释（注解和字段之间的注释）
    # 模式：@xxx注解后紧接/** ... */然后是字段声明
    content = re.sub(
        r'(@\w+(?:\([^)]*\))?\s*\n)(/\*\*\s*\n\s*\*\s*[^*]+\s*\n\s*\*/\s*\n)',
        r'\2\1',
        content
    )
    
    # 处理多个注解的情况（比如@Column之后还有@Id等）
    for _ in range(3):
        content = re.sub(
            r'(/\*\*\s*\n\s*\*\s*[^*]+\s*\n\s*\*/\s*\n)((?:@\w+(?:\([^)]*\))?\s*\n)+)(\s*/\*\*\s*\n\s*\*\s*[^*]+\s*\n\s*\*/\s*\n)',
            r'\1\2',
            content
        )
    
    # 获取表名
    table_match = re.search(r'@Table\("([^"]+)"\)', content)
    if not table_match:
        return False
    table_name = table_match.group(1)
    
    table_desc_cn, table_detail_cn = TABLE_DESCRIPTIONS.get(table_name, (table_name, f'对应数据库表 {table_name}'))
    
    # 修复/替换类注释
    old_class_pattern = r'/\*\*\s*\n\s*\*.*?实体类.*?\*/'
    new_class_javadoc = f'/**\n * {table_desc_cn}实体类，对应数据库表 {table_name}。\n * {table_detail_cn}。\n *\n * @author sleep\n * @since 2024-05-24\n */'
    
    if re.search(old_class_pattern, content, re.DOTALL):
        content = re.sub(old_class_pattern, new_class_javadoc, content, flags=re.DOTALL)
    
    lines = content.split('\n')
    new_lines = []
    i = 0
    
    while i < len(lines):
        line = lines[i]
        
        # 检测是否是字段声明行（private/protected/public开头，以分号结尾）
        field_match = re.match(r'^(\s*)(private|protected|public)\s+([\w<>\[\],\s?]+)\s+(\w+)\s*(=.*?)?;', line)
        
        if field_match:
            indent = field_match.group(1)
            field_type = field_match.group(3).strip()
            field_name = field_match.group(4)
            
            # 收集该字段前面的所有注解
            annotations = []
            j = len(new_lines) - 1
            while j >= 0:
                prev_line = new_lines[j].strip()
                if prev_line.startswith('@') or (prev_line == '' and j > 0 and new_lines[j-1].strip().startswith('@')):
                    annotations.insert(0, new_lines[j])
                    new_lines.pop(j)
                    j -= 1
                elif prev_line.startswith('/*') or prev_line.startswith('*') or prev_line.endswith('*/'):
                    # 已有注释，停止回溯
                    break
                elif prev_line == '' or prev_line.startswith('//'):
                    j -= 1
                else:
                    break
            
            # 检查是否已有字段注释
            has_comment = False
            if new_lines and new_lines[-1].strip().endswith('*/'):
                # 向上检查3行内是否有Javadoc开始
                for k in range(max(0, len(new_lines)-3), len(new_lines)):
                    if new_lines[k].strip().startswith('/**'):
                        has_comment = True
                        break
            
            # 获取@Column中的列名
            column_name = None
            for ann in annotations:
                col_match = re.search(r'@Column\("([^"]+)"\)', ann)
                if col_match:
                    column_name = col_match.group(1)
                    break
            
            # 获取字段注释文本
            comment_text = get_field_comment(field_name, column_name)
            
            # 特殊字段
            if field_name == 'serialVersionUID':
                comment_text = '序列化版本UID'
            
            if not has_comment:
                new_lines.append(f'{indent}/**')
                new_lines.append(f'{indent} * {comment_text}')
                new_lines.append(f'{indent} */')
            
            # 添加注解
            for ann in annotations:
                new_lines.append(ann)
        
        new_lines.append(line)
        i += 1
    
    new_content = '\n'.join(new_lines)
    
    if new_content != original_content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        return True
    return False

def fix_mapper_file(file_path):
    """修复Mapper文件"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original_content = content
    
    # 获取Entity名
    mapper_match = re.search(r'public interface (\w+) extends BaseMapper<(\w+)>', content)
    if not mapper_match:
        return False
    entity_name = mapper_match.group(2)
    
    # 从Entity名和Entity的@Table注解获取真实表名
    entity_file = os.path.join(ENTITY_DIR, f'{entity_name}.java')
    table_name = None
    if os.path.exists(entity_file):
        with open(entity_file, 'r', encoding='utf-8') as ef:
            econtent = ef.read()
        tm = re.search(r'@Table\("([^"]+)"\)', econtent)
        if tm:
            table_name = tm.group(1)
    
    if not table_name:
        entity_lower = entity_name.replace('DO', '').lower()
        table_name = entity_lower
    
    table_desc_cn, _ = TABLE_DESCRIPTIONS.get(table_name, (table_name, f'对{table_name}表的数据操作'))
    
    # 修复类注释
    old_class_pattern = r'/\*\*\s*\n\s*\*.*?(映射层|Mapper接口|数据访问).*?\*/'
    new_class_javadoc = f'/**\n * {table_desc_cn}数据访问Mapper接口，对应数据库表 {table_name}。\n * 继承MyBatis-Flex的BaseMapper获得基础CRUD能力。\n *\n * @author sleep\n * @since 2024-05-24\n */'
    
    if re.search(old_class_pattern, content, re.DOTALL):
        content = re.sub(old_class_pattern, new_class_javadoc, content, flags=re.DOTALL)
    elif '数据访问Mapper接口' not in content:
        content = re.sub(
            r'(public interface \w+ extends BaseMapper<\w+>)',
            f'{new_class_javadoc}\n\\1',
            content
        )
    
    # 为自定义方法添加注释（简单处理，确保方法前有注释即可）
    lines = content.split('\n')
    new_lines = []
    i = 0
    
    while i < len(lines):
        line = lines[i]
        
        # 匹配SQL注解+方法的模式，确保注释在注解前
        sql_anno_match = re.match(r'^(\s*)@(Select|Update|Insert|Delete)\(', line)
        
        if sql_anno_match:
            indent = sql_anno_match.group(1)
            anno_type = sql_anno_match.group(2)
            
            # 收集整个SQL注解和方法
            block_start = len(new_lines)
            block_lines = [line]
            j = i + 1
            
            # 收集完整注解（多行SQL）
            while j < len(lines):
                block_lines.append(lines[j])
                if ';' in lines[j]:
                    break
                j += 1
            
            method_line_idx = len(block_lines) - 1
            while method_line_idx >= 0 and '(' not in block_lines[method_line_idx]:
                method_line_idx -= 1
            
            if method_line_idx >= 0:
                method_line = block_lines[method_line_idx]
                method_match = re.search(r'(\w+(?:<[^>]+>)?)\s+(\w+)\s*\(', method_line)
                
                if method_match:
                    ret_type = method_match.group(1)
                    method_name = method_match.group(2)
                    
                    # 检查前面是否已有注释
                    has_comment = False
                    if new_lines and new_lines[-1].strip().endswith('*/'):
                        for k in range(max(0, len(new_lines)-3), len(new_lines)):
                            if new_lines[k].strip().startswith('/**'):
                                has_comment = True
                                break
                    
                    if not has_comment:
                        # 生成方法描述
                        name_lower = method_name.lower()
                        if 'select' in name_lower or 'find' in name_lower or 'get' in name_lower or 'query' in name_lower or anno_type == 'Select':
                            m_desc = f'查询{table_desc_cn}数据'
                        elif 'insert' in name_lower or 'add' in name_lower or 'save' in name_lower or 'create' in name_lower or anno_type == 'Insert':
                            m_desc = f'新增{table_desc_cn}数据'
                        elif 'update' in name_lower or 'modify' in name_lower or 'set' in name_lower or anno_type == 'Update':
                            m_desc = f'更新{table_desc_cn}数据'
                        elif 'delete' in name_lower or 'remove' in name_lower or anno_type == 'Delete':
                            m_desc = f'删除{table_desc_cn}数据'
                        else:
                            m_desc = f'{table_desc_cn}数据操作：{method_name}'
                        
                        new_lines.append(f'{indent}/**')
                        new_lines.append(f'{indent} * {m_desc}')
                        
                        # 提取参数
                        params_match = re.search(r'\((.*?)\)', method_line)
                        if params_match:
                            params_str = params_match.group(1).strip()
                            if params_str:
                                params = [p.strip() for p in params_str.split(',')]
                                for p in params:
                                    parts = p.split()
                                    if len(parts) >= 2:
                                        p_name = parts[-1]
                                        new_lines.append(f'{indent} * @param {p_name} 参数')
                        
                        if ret_type != 'void':
                            new_lines.append(f'{indent} * @return 操作结果')
                        
                        new_lines.append(f'{indent} */')
            
            for bl in block_lines:
                new_lines.append(bl)
            i = j + 1
            continue
        
        new_lines.append(line)
        i += 1
    
    new_content = '\n'.join(new_lines)
    
    if new_content != original_content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        return True
    return False

def main():
    entity_count = 0
    mapper_count = 0
    
    print('修复实体类文件注释...')
    for filename in os.listdir(ENTITY_DIR):
        if filename.endswith('.java'):
            file_path = os.path.join(ENTITY_DIR, filename)
            try:
                if fix_entity_file(file_path):
                    entity_count += 1
                    print(f'  已修复: {filename}')
            except Exception as e:
                print(f'  修复失败 {filename}: {e}')
                import traceback
                traceback.print_exc()
    
    print('\n修复Mapper接口文件注释...')
    for filename in os.listdir(MAPPER_DIR):
        if filename.endswith('.java'):
            file_path = os.path.join(MAPPER_DIR, filename)
            try:
                if fix_mapper_file(file_path):
                    mapper_count += 1
                    print(f'  已修复: {filename}')
            except Exception as e:
                print(f'  修复失败 {filename}: {e}')
                import traceback
                traceback.print_exc()
    
    print(f'\n修复完成！共修复了 {entity_count} 个实体类文件和 {mapper_count} 个Mapper接口文件。')

if __name__ == '__main__':
    main()
