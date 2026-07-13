#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import os
import re

ENTITY_DIR = '/workspace/gms-server/src/main/java/org/gms/dao/entity'
MAPPER_DIR = '/workspace/gms-server/src/main/java/org/gms/dao/mapper'

TABLE_DESC = {
    'accounts': ('账号信息', '存储游戏用户的账号基本信息、登录状态、充值点数等数据'),
    'alliance': ('家族联盟', '存储多个家族组成的联盟信息'),
    'allianceguilds': ('联盟家族关联', '存储联盟与成员家族的关联关系'),
    'area_info': ('区域信息', '存储角色在自定义区域的信息数据'),
    'autoban_config': ('自动封禁配置', '存储自动反作弊封禁规则配置'),
    'bbs_replies': ('论坛回复', '存储家族论坛帖子回复'),
    'bbs_threads': ('论坛帖子', '存储家族论坛帖子主题'),
    'bosslog_daily': ('每日BOSS记录', '记录角色每日BOSS挑战时间'),
    'bosslog_weekly': ('每周BOSS记录', '记录角色每周BOSS挑战时间'),
    'buddies': ('好友关系', '存储角色好友关系及分组'),
    'characters': ('游戏角色', '存储游戏角色详细属性'),
    'command_info': ('命令信息', '存储GM命令配置'),
    'cooldowns': ('技能冷却', '存储角色技能冷却时间'),
    'drop_data': ('怪物掉落', '存储怪物掉落物品配置'),
    'drop_data_global': ('全局掉落', '存储区域全局掉落配置'),
    'dueyitems': ('快递物品', '存储快递包裹物品明细'),
    'dueypackages': ('快递包裹', '存储快递包裹信息'),
    'eventstats': ('活动统计', '存储游戏活动统计数据'),
    'extend_value': ('扩展值', '存储扩展键值对数据'),
    'famelog': ('人气记录', '存储人气变更日志'),
    'family_character': ('家族成员', '存储家族成员信息'),
    'family_entitlement': ('家族权限', '存储家族成员权限配置'),
    'flyway_schema_history': ('数据库版本历史', 'Flyway迁移历史记录表'),
    'fredstorage': ('Fred仓库', '存储Fred仓库物品数据'),
    'gachapon_reward': ('转蛋奖励', '存储转蛋机奖励配置'),
    'gachapon_reward_pool': ('转蛋奖励池', '存储转蛋机奖励池配置'),
    'game_config': ('游戏配置', '存储游戏服务器配置参数'),
    'gifts': ('礼物', '存储角色间赠送礼物记录'),
    'guilds': ('家族', '存储游戏家族信息'),
    'hp_mp_alert': ('HPMP警告', '存储HP/MP低血量警告配置'),
    'hwidaccounts': ('HWID账号关联', '存储硬件ID与账号绑定'),
    'hwidbans': ('HWID封禁', '存储被封禁的硬件ID'),
    'inventoryequipment': ('装备栏物品', '存储装备栏物品详细属性'),
    'inventoryitems': ('物品栏物品', '存储所有物品栏物品'),
    'inventorymerchant': ('商人仓库', '存储个人商店物品库存'),
    'ipbans': ('IP封禁', '存储被封禁的IP地址'),
    'keymap': ('按键映射', '存储自定义快捷键配置'),
    'lang_resources': ('多语言资源', '存储多语言本地化文本'),
    'macbans': ('MAC封禁', '存储被封禁的MAC地址'),
    'macfilters': ('MAC过滤器', '存储MAC地址过滤规则'),
    'makercreatedata': ('制作创建数据', 'Maker系统创建物品配置'),
    'makerreagentdata': ('制作试剂数据', 'Maker系统试剂材料配置'),
    'makerrecipedata': ('制作配方数据', 'Maker系统配方配置'),
    'makerrewarddata': ('制作奖励数据', 'Maker系统奖励配置'),
    'marriages': ('婚姻关系', '存储角色婚姻关系信息'),
    'medalmaps': ('勋章地图', '存储勋章地图关联配置'),
    'modified_cash_item': ('修改现金物品', '存储被修改的现金物品'),
    'monsterbook': ('怪物手册', '存储怪物手册收集进度'),
    'monstercarddata': ('怪物卡牌数据', '存储怪物卡牌基础数据'),
    'mts_cart': ('MTS购物车', '存储MTS购物车数据'),
    'mts_items': ('MTS物品', '存储MTS交易物品数据'),
    'namechanges': ('改名记录', '存储角色改名历史'),
    'newyear': ('新年活动', '存储新年活动数据'),
    'notes': ('纸条', '存储角色间纸条消息'),
    'nxcode': ('NX兑换码', '存储NX充值兑换码'),
    'nxcode_items': ('NX兑换码物品', '存储兑换码可兑换物品'),
    'nxcoupons': ('NX优惠券', '存储NX优惠券数据'),
    'petignores': ('宠物忽略', '存储宠物忽略捡取配置'),
    'pets': ('宠物', '存储角色宠物详细信息'),
    'playerdiseases': ('角色异常状态', '存储角色异常状态效果'),
    'playernpcs': ('玩家NPC', '存储玩家自定义NPC配置'),
    'playernpcs_equip': ('玩家NPC装备', '存储玩家NPC装备配置'),
    'playernpcs_field': ('玩家NPC地图', '存储玩家NPC地图部署'),
    'plife': ('游戏生命', '存储地图生命体配置'),
    'questactions': ('任务动作', '存储任务完成动作配置'),
    'questprogress': ('任务进度', '存储角色任务进度'),
    'questrequirements': ('任务需求', '存储任务前置条件配置'),
    'queststatus': ('任务状态', '存储角色任务状态'),
    'quickslotkeymapped': ('快速栏快捷键', '存储快速栏快捷键映射'),
    'reactordrops': ('反应器掉落', '存储反应器掉落物品配置'),
    'reports': ('举报记录', '存储玩家举报记录'),
    'responses': ('响应配置', '存储系统响应配置'),
    'rings': ('戒指', '存储角色佩戴的戒指数据'),
    'savedlocations': ('保存位置', '存储角色保存的位置点'),
    'server_queue': ('服务器队列', '存储服务器排队数据'),
    'shopitems': ('商店物品', '存储商店出售物品配置'),
    'shops': ('商店', '存储游戏商店信息'),
    'skillmacros': ('技能宏', '存储角色技能宏配置'),
    'skills': ('技能', '存储角色已学技能及等级'),
    'specialcashitems': ('特殊现金物品', '存储特殊现金物品配置'),
    'storages': ('仓库', '存储角色仓库物品数据'),
    'trocklocations': ('传送石位置', '存储VIP传送石位置'),
    'wishlists': ('愿望清单', '存储愿望清单数据'),
    'worldtransfers': ('世界转移', '存储跨服务器转移记录')
}

FIELD_CN = {
    'id': '唯一ID', 'name': '名称', 'password': '密码', 'pin': '二级密码PIN码',
    'pic': '角色选择密码PIC码', 'loggedin': '登录状态', 'lastlogin': '最后登录时间',
    'createdat': '创建时间', 'birthday': '生日日期', 'banned': '是否被封禁',
    'banreason': '封禁原因', 'macs': '绑定MAC地址列表', 'nxCredit': 'NX信用点余额',
    'maplePoint': '枫叶点余额', 'nxPrepaid': 'NX预充值点数余额',
    'characterslots': '可创建角色槽位数', 'gender': '性别', 'tempban': '临时封禁到期时间',
    'greason': '封禁原因代码', 'tos': '是否同意服务条款', 'sitelogged': '网站登录标识',
    'webadmin': '网站管理员权限等级', 'nick': '昵称', 'mute': '禁言状态',
    'email': '注册邮箱', 'ip': 'IP地址', 'rewardpoints': '奖励积分', 'votepoints': '投票积分',
    'hwid': '硬件ID', 'language': '语言设置', 'capacity': '容量上限', 'notice': '公告内容',
    'allianceid': '联盟ID', 'guildid': '家族ID', 'charid': '角色ID', 'characterid': '角色ID',
    'accountid': '账号ID', 'area': '区域ID', 'info': '信息内容', 'type': '类型',
    'disabled': '是否禁用', 'points': '积分', 'expireTime': '过期时间', 'description': '描述说明',
    'createTime': '创建时间', 'updateTime': '更新时间', 'replyid': '回复ID',
    'threadid': '帖子ID', 'postercid': '发帖者角色ID', 'timestamp': '时间戳', 'content': '内容文本',
    'icon': '图标类型', 'replycount': '回复数量', 'startpost': '正文内容',
    'localthreadid': '本地帖子ID', 'bosstype': 'BOSS类型标识', 'attempttime': '挑战尝试时间',
    'buddyid': '好友角色ID', 'pending': '待确认状态', 'group': '分组名称', 'world': '服务器世界ID',
    'level': '等级', 'exp': '经验值', 'gachaexp': '转蛋经验值', 'attrStr': '力量属性STR',
    'attrDex': '敏捷属性DEX', 'attrLuk': '运气属性LUK', 'attrInt': '智力属性INT',
    'hp': '当前HP', 'mp': '当前MP', 'maxhp': '最大HP', 'maxmp': '最大MP', 'meso': '金币数量',
    'hpMpUsed': 'HP/MP已使用量', 'job': '职业ID', 'skincolor': '皮肤颜色', 'fame': '人气值',
    'hair': '发型ID', 'face': '脸型ID', 'ap': '能力点AP', 'sp': '技能点SP分配', 'map': '当前地图ID',
    'spawnpoint': '出生点ID', 'gm': 'GM权限等级', 'party': '组队ID', 'buddyCapacity': '好友容量上限',
    'createdate': '角色创建时间', 'rank': '世界排名', 'rankMove': '排名变动值',
    'jobRank': '职业排名', 'jobRankMove': '职业排名变动值', 'guildrank': '家族职位等级',
    'messengerid': '聊天群ID', 'messengerposition': '聊天群职位', 'mountlevel': '坐骑等级',
    'mountexp': '坐骑经验值', 'mounttiredness': '坐骑疲劳度', 'itemid': '物品ID',
    'questid': '关联任务ID', 'chance': '掉落几率', 'continent': '大陆ID', 'comments': '备注说明',
    'packageid': '包裹ID', 'inventoryitemid': '物品栏物品ID', 'receiverid': '收件人角色ID',
    'sendername': '寄件人名称', 'message': '消息内容', 'checked': '已查看/已领取状态',
    'skillid': '技能ID', 'length': '持续时长毫秒', 'starttime': '开始时间戳',
    'dropperid': '掉落者ID（怪物ID）', 'minimumQuantity': '最小数量', 'maximumQuantity': '最大数量',
    'syntax': '命令语法说明', 'defaultLevel': '默认权限等级', 'clazz': '命令处理类名',
    'enabled': '是否启用', 'serialVersionUID': '序列化版本UID', 'extendId': '扩展字段ID',
    'extendType': '扩展字段类型', 'extendName': '扩展字段名称', 'extendValue': '扩展字段值'
}

def process_entity(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 获取表名
    tm = re.search(r'@Table\("([^"]+)"\)', content)
    if not tm:
        return False
    table = tm.group(1)
    desc, detail = TABLE_DESC.get(table, (table, f'对应数据库表 {table}'))
    
    # 替换类注释（处理空行和作者信息的变体）
    old_class_javadoc = r'/\*\*\s*\n\s*\*\s*实体类。\s*\n\s*\*\s*\n\s*\*\s*@author.*?\n\s*\*\s*@since.*?\n\s*\*/'
    
    new_javadoc = f'''/**
 * {desc}实体类，对应数据库表 {table}。
 * {detail}。
 *
 * @author sleep
 * @since 2024-05-24
 */'''
    
    content = re.sub(old_class_javadoc, new_javadoc, content, flags=re.DOTALL)
    
    # 处理已有注释的情况（比如AutobanConfigDO）
    if f'{desc}实体类' not in content:
        # 尝试替换已有中文注释的类javadoc
        old_cn = r'/\*\*\s*\n\s*\*.*?实体类.*?\*/'
        if re.search(old_cn, content, re.DOTALL):
            content = re.sub(old_cn, new_javadoc, content, flags=re.DOTALL)
        else:
            # 在@Table前插入
            content = re.sub(r'(@Table\("[^"]+"\))', f'{new_javadoc}\\n\\1', content)
    
    # 现在处理字段 - 找所有private字段声明行
    lines = content.split('\n')
    result = []
    
    for line in lines:
        # 匹配字段
        fm = re.match(r'^(\s*)(private|protected|public)\s+(?:static\s+)?(?:final\s+)?([\w<>,\[\]\s?]+)\s+(\w+)\s*(=.*?)?;\s*$', line)
        if fm:
            indent = fm.group(1)
            field_name = fm.group(4)
            
            # 获取注释
            cmt = FIELD_CN.get(field_name, field_name)
            
            # 检查前一行是不是注解（@开头），如果是且前前行不是注释，需要特殊处理
            # 简单处理：在字段行前插入注释，即使位置不完美也比没有强
            # 但要确保不会重复添加
            prev_line = result[-1].strip() if result else ''
            if not prev_line.endswith('*/'):
                result.append(f'{indent}/**')
                result.append(f'{indent} * {cmt}')
                result.append(f'{indent} */')
        
        result.append(line)
    
    content = '\n'.join(result)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    return True

def process_mapper(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 获取Entity名
    em = re.search(r'extends BaseMapper<(\w+)>', content)
    if not em:
        return False
    entity = em.group(1)
    
    # 获取表名
    table = entity.replace('DO', '').lower()
    # 特殊映射
    table_map = {
        'areainfo': 'area_info', 'autobanconfig': 'autoban_config', 'bbsreplies': 'bbs_replies',
        'bbsthreads': 'bbs_threads', 'bosslogdaily': 'bosslog_daily', 'bosslogweekly': 'bosslog_weekly',
        'commandinfo': 'command_info', 'dropdata': 'drop_data', 'dropdataglobal': 'drop_data_global',
        'familycharacter': 'family_character', 'familyentitlement': 'family_entitlement',
        'flywayschemahistory': 'flyway_schema_history', 'gachaponreward': 'gachapon_reward',
        'gachaponrewardpool': 'gachapon_reward_pool', 'gameconfig': 'game_config',
        'hpmpalert': 'hp_mp_alert', 'hwidaccounts': 'hwidaccounts', 'hwidbans': 'hwidbans',
        'inventoryequipment': 'inventoryequipment', 'inventoryitems': 'inventoryitems',
        'inventorymerchant': 'inventorymerchant', 'ipbans': 'ipbans', 'langresources': 'lang_resources',
        'macbans': 'macbans', 'macfilters': 'macfilters', 'makercreatedata': 'makercreatedata',
        'makerreagentdata': 'makerreagentdata', 'makerrecipedata': 'makerrecipedata',
        'makerrewarddata': 'makerrewarddata', 'modifiedcashitem': 'modified_cash_item',
        'monsterbook': 'monsterbook', 'monstercarddata': 'monstercarddata', 'mtscart': 'mts_cart',
        'mtsitems': 'mts_items', 'namechanges': 'namechanges', 'nxcode': 'nxcode',
        'nxcodeitems': 'nxcode_items', 'nxcoupons': 'nxcoupons', 'petignores': 'petignores',
        'playerdiseases': 'playerdiseases', 'playernpcs': 'playernpcs',
        'playernpcsequip': 'playernpcs_equip', 'playernpcsfield': 'playernpcs_field',
        'questactions': 'questactions', 'questprogress': 'questprogress',
        'questrequirements': 'questrequirements', 'queststatus': 'queststatus',
        'quickslotkeymapped': 'quickslotkeymapped', 'reactordrops': 'reactordrops',
        'serverqueue': 'server_queue', 'shopitems': 'shopitems', 'skillmacros': 'skillmacros',
        'specialcashitems': 'specialcashitems', 'trocklocations': 'trocklocations',
        'worldtransfers': 'worldtransfers', 'fredstorage': 'fredstorage', 'newyear': 'newyear',
        'savedlocations': 'savedlocations'
    }
    table = table_map.get(table, table)
    
    desc, _ = TABLE_DESC.get(table, (table, f'对{table}表的数据访问'))
    
    # 替换类注释
    old_mapper_javadoc = r'/\*\*\s*\n\s*\*\s*映射层。\s*\n\s*\*\s*\n\s*\*\s*@author.*?\n\s*\*\s*@since.*?\n\s*\*/'
    new_mapper_javadoc = f'''/**
 * {desc}数据访问Mapper接口，对应数据库表 {table}。
 * 继承MyBatis-Flex的BaseMapper获得基础CRUD能力。
 *
 * @author sleep
 * @since 2024-05-24
 */'''
    
    content = re.sub(old_mapper_javadoc, new_mapper_javadoc, content, flags=re.DOTALL)
    
    if f'{desc}数据访问Mapper接口' not in content:
        content = re.sub(r'(public interface \w+ extends BaseMapper<\w+>)', f'{new_mapper_javadoc}\\n\\1', content)
    
    # 为SQL注解方法添加注释
    lines = content.split('\n')
    result = []
    
    for line in lines:
        am = re.match(r'^(\s*)@(Select|Update|Insert|Delete)\(', line)
        if am:
            indent = am.group(1)
            sql_type = am.group(2)
            
            # 检查是否已有注释
            prev = result[-1].strip() if result else ''
            if not prev.endswith('*/'):
                if sql_type == 'Select':
                    m_desc = f'查询{desc}数据'
                elif sql_type == 'Insert':
                    m_desc = f'新增{desc}数据'
                elif sql_type == 'Update':
                    m_desc = f'更新{desc}数据'
                else:
                    m_desc = f'删除{desc}数据'
                result.append(f'{indent}/**')
                result.append(f'{indent} * {m_desc}')
                result.append(f'{indent} */')
        
        result.append(line)
    
    content = '\n'.join(result)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    return True

def main():
    ec, mc = 0, 0
    for fn in sorted(os.listdir(ENTITY_DIR)):
        if fn.endswith('.java'):
            try:
                process_entity(os.path.join(ENTITY_DIR, fn))
                ec += 1
            except Exception as e:
                print(f'实体类失败 {fn}: {e}')
    
    for fn in sorted(os.listdir(MAPPER_DIR)):
        if fn.endswith('.java'):
            try:
                process_mapper(os.path.join(MAPPER_DIR, fn))
                mc += 1
            except Exception as e:
                print(f'Mapper失败 {fn}: {e}')
    
    print(f'完成！实体类: {ec}, Mapper: {mc}')

if __name__ == '__main__':
    main()
