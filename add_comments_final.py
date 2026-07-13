#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import os
import re

ENTITY_DIR = '/workspace/gms-server/src/main/java/org/gms/dao/entity'
MAPPER_DIR = '/workspace/gms-server/src/main/java/org/gms/dao/mapper'

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
    'dueyitems': ('快递物品', '存储快递包裹中的物品明细'),
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
    'notes': ('纸条', '存储角色之间发送的纸条消息'),
    'nxcode': ('NX兑换码', '存储NX充值兑换码信息'),
    'nxcode_items': ('NX兑换码物品', '存储NX兑换码可兑换的物品列表'),
    'nxcoupons': ('NX优惠券', '存储NX优惠券/礼品券数据'),
    'petignores': ('宠物忽略', '存储宠物忽略捡取的物品配置'),
    'pets': ('宠物', '存储角色宠物的详细信息'),
    'playerdiseases': ('角色异常状态', '存储角色当前的异常状态/疾病效果'),
    'playernpcs': ('玩家NPC', '存储玩家自定义NPC的配置数据'),
    'playernpcs_equip': ('玩家NPC装备', '存储玩家NPC的装备配置'),
    'playernpcs_field': ('玩家NPC地图', '存储玩家NPC在地图上的部署配置'),
    'plife': ('游戏生命', '存储游戏地图上的生命体（NPC、怪物、反应器等）配置'),
    'questactions': ('任务动作', '存储任务完成时执行的动作配置'),
    'questprogress': ('任务进度', '存储角色的任务进行进度信息'),
    'questrequirements': ('任务需求', '存储任务接取/完成的前置条件配置'),
    'queststatus': ('任务状态', '存储角色的任务当前状态'),
    'quickslotkeymapped': ('快速栏快捷键', '存储角色快速栏的快捷键映射配置'),
    'reactordrops': ('反应器掉落', '存储反应器触发时的掉落物品配置'),
    'reports': ('举报记录', '存储玩家提交的举报记录'),
    'responses': ('响应配置', '存储系统的响应配置数据'),
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
    'wishlists': ('愿望清单', '存储玩家的愿望清单数据'),
    'worldtransfers': ('世界转移', '存储角色跨服务器/世界转移的记录')
}

FIELD_COMMENTS = {
    'id': '唯一ID',
    'name': '名称',
    'password': '密码',
    'pin': '二级密码PIN码',
    'pic': '角色选择密码PIC码',
    'loggedin': '登录状态：0-未登录，1-已登录',
    'lastlogin': '最后登录时间',
    'createdat': '创建时间',
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
    'rank1': '职位1名称',
    'rank2': '职位2名称',
    'rank3': '职位3名称',
    'rank4': '职位4名称',
    'rank5': '职位5名称',
    'allianceid': '联盟ID',
    'guildid': '家族ID',
    'charid': '角色ID',
    'characterid': '角色ID',
    'accountid': '账号ID',
    'area': '区域ID',
    'info': '信息内容',
    'type': '类型',
    'disabled': '是否禁用',
    'points': '积分',
    'expireTime': '过期时间',
    'description': '描述说明',
    'createTime': '创建时间',
    'updateTime': '更新时间',
    'replyid': '回复ID',
    'threadid': '帖子ID',
    'postercid': '发帖者角色ID',
    'timestamp': '时间戳',
    'content': '内容文本',
    'icon': '图标类型',
    'replycount': '回复数量',
    'startpost': '正文内容',
    'localthreadid': '本地帖子ID',
    'bosstype': 'BOSS类型标识',
    'attempttime': '挑战尝试时间',
    'buddyid': '好友角色ID',
    'pending': '待确认状态：0-已接受，1-待确认',
    'group': '分组名称',
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
    'fquest': '新手任务标记',
    'hair': '发型ID',
    'face': '脸型ID',
    'ap': '剩余能力点（AP）',
    'sp': '技能点分配字符串',
    'map': '当前地图ID',
    'spawnpoint': '出生点ID',
    'gm': 'GM权限等级',
    'party': '组队ID',
    'buddyCapacity': '好友容量上限',
    'createdate': '角色创建时间',
    'rank': '世界排名',
    'rankMove': '排名变动值',
    'jobRank': '职业排名',
    'jobRankMove': '职业排名变动值',
    'guildrank': '家族职位等级',
    'messengerid': '聊天群ID',
    'messengerposition': '聊天群职位',
    'mountlevel': '坐骑等级',
    'mountexp': '坐骑经验值',
    'mounttiredness': '坐骑疲劳度',
    'omokwins': '五子棋胜场数',
    'omoklosses': '五子棋败场数',
    'omokties': '五子棋平局数',
    'matchcardwins': '记忆卡牌胜场数',
    'matchcardlosses': '记忆卡牌败场数',
    'matchcardties': '记忆卡牌平局数',
    'merchantmesos': '个人商店金币',
    'hasmerchant': '是否开设个人商店',
    'equipslots': '装备栏槽位数',
    'useslots': '消耗栏槽位数',
    'setupslots': '设置栏槽位数',
    'etcslots': '其他栏槽位数',
    'familyId': '家族ID',
    'monsterbookcover': '怪物手册封面ID',
    'allianceRank': '联盟职位等级',
    'vanquisherStage': '征服者关卡进度',
    'ariantPoints': '阿里安特点数',
    'dojoPoints': '道场点数',
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
    'partySearch': '是否在组队搜索中',
    'jailexpire': '监禁到期时间',
    'syntax': '命令语法说明',
    'defaultLevel': '默认权限等级',
    'clazz': '命令处理类名',
    'enabled': '是否启用',
    'skillid': '技能ID',
    'length': '持续时长（毫秒）',
    'starttime': '开始时间戳',
    'dropperid': '掉落者ID（怪物ID）',
    'itemid': '物品ID',
    'minimumQuantity': '最小数量',
    'maximumQuantity': '最大数量',
    'questid': '关联任务ID',
    'chance': '掉落几率',
    'continent': '大陆ID',
    'comments': '备注说明',
    'packageid': '包裹ID',
    'inventoryitemid': '物品栏物品ID',
    'receiverid': '收件人角色ID',
    'sendername': '寄件人名称',
    'message': '消息内容',
    'checked': '状态：0-未读，1-已查看/已领取',
    'extendId': '扩展字段ID',
    'extendType': '扩展字段类型：11-账号，12-账号日清，13-账号周清；21-角色，22-角色日清，23-角色周清',
    'extendName': '扩展字段名称',
    'extendValue': '扩展字段值'
}

def get_field_comment(field_name, column_name=None):
    if field_name in FIELD_COMMENTS:
        return FIELD_COMMENTS[field_name]
    if column_name and column_name in FIELD_COMMENTS:
        return FIELD_COMMENTS[column_name]
    if field_name == 'serialVersionUID':
        return '序列化版本UID'
    return field_name

def process_entity(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    # 获取表名
    table_name = None
    for line in lines:
        m = re.search(r'@Table\("([^"]+)"\)', line)
        if m:
            table_name = m.group(1)
            break
    
    if not table_name:
        return False
    
    table_desc, table_detail = TABLE_DESCRIPTIONS.get(table_name, (table_name, f'对应数据库表 {table_name}'))
    
    new_lines = []
    i = 0
    
    # 处理类注释替换
    class_javadoc_replaced = False
    
    while i < len(lines):
        line = lines[i]
        
        # 检测并替换旧的类Javadoc
        if '/**' in line and not class_javadoc_replaced:
            # 检查是否是类级别的Javadoc
            j = i
            javadoc_lines = [line]
            while j < len(lines) and '*/' not in lines[j]:
                j += 1
                javadoc_lines.append(lines[j])
            if j < len(lines):
                # 检查这个Javadoc后面是否跟着@Data或类定义
                k = j + 1
                while k < len(lines) and (lines[k].strip().startswith('@') or lines[k].strip() == '' or 'class' not in lines[k]):
                    k += 1
                if k < len(lines) and 'class' in lines[k] and 'DO' in lines[k]:
                    indent = len(line) - len(line.lstrip())
                    indent_str = ' ' * indent
                    new_lines.append(f'{indent_str}/**\n')
                    new_lines.append(f'{indent_str} * {table_desc}实体类，对应数据库表 {table_name}。\n')
                    new_lines.append(f'{indent_str} * {table_detail}。\n')
                    new_lines.append(f'{indent_str} *\n')
                    new_lines.append(f'{indent_str} * @author sleep\n')
                    new_lines.append(f'{indent_str} * @since 2024-05-24\n')
                    new_lines.append(f'{indent_str} */\n')
                    i = j + 1
                    class_javadoc_replaced = True
                    continue
        
        # 检测字段声明（private开头，以;结尾的行）
        field_match = re.match(r'^(\s*)(private|protected|public)\s+[\w<>,\[\]\s?]+\s+(\w+)\s*(=.*?)?;\s*$', line)
        
        if field_match:
            indent = field_match.group(1)
            field_name = field_match.group(3)
            
            # 回溯收集这个字段前面的注解（以@开头的连续行）
            annotation_count = 0
            while len(new_lines) > 0 and new_lines[-1].strip().startswith('@'):
                annotation_count += 1
                if len(new_lines) > 1:
                    second_last = new_lines[-2].strip()
                    if second_last.startswith('/*') or second_last.endswith('*/') or second_last.startswith('*'):
                        break
                else:
                    break
            
            # 检查注解前是否已有Javadoc
            has_javadoc = False
            if annotation_count > 0 and len(new_lines) >= annotation_count + 1:
                check_idx = len(new_lines) - annotation_count - 1
                if check_idx >= 0 and new_lines[check_idx].rstrip().endswith('*/'):
                    has_javadoc = True
            elif annotation_count == 0 and len(new_lines) > 0 and new_lines[-1].rstrip().endswith('*/'):
                has_javadoc = True
            
            if not has_javadoc:
                # 查找@Column注解获取列名
                column_name = None
                for ann_line in new_lines[-annotation_count:] if annotation_count > 0 else []:
                    cm = re.search(r'@Column\("([^"]+)"\)', ann_line)
                    if cm:
                        column_name = cm.group(1)
                        break
                
                comment_text = get_field_comment(field_name, column_name)
                
                # 弹出注解，插入Javadoc，再放回去
                annotations = []
                for _ in range(annotation_count):
                    annotations.insert(0, new_lines.pop())
                
                new_lines.append(f'{indent}/**\n')
                new_lines.append(f'{indent} * {comment_text}\n')
                new_lines.append(f'{indent} */\n')
                
                for a in annotations:
                    new_lines.append(a)
        
        new_lines.append(line)
        i += 1
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.writelines(new_lines)
    
    return True

def process_mapper(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    # 找到Entity名
    entity_name = None
    table_name = None
    for line in lines:
        m = re.search(r'extends BaseMapper<(\w+)>', line)
        if m:
            entity_name = m.group(1)
            break
    
    if not entity_name:
        return False
    
    # 从entity文件读取表名
    entity_file = os.path.join(ENTITY_DIR, f'{entity_name}.java')
    if os.path.exists(entity_file):
        with open(entity_file, 'r', encoding='utf-8') as ef:
            for eline in ef:
                tm = re.search(r'@Table\("([^"]+)"\)', eline)
                if tm:
                    table_name = tm.group(1)
                    break
    
    if not table_name:
        table_name = entity_name.replace('DO', '').lower()
    
    table_desc, _ = TABLE_DESCRIPTIONS.get(table_name, (table_name, f'对{table_name}表的数据访问'))
    
    new_lines = []
    i = 0
    class_javadoc_replaced = False
    
    while i < len(lines):
        line = lines[i]
        
        # 替换旧的类Javadoc
        if '/**' in line and not class_javadoc_replaced:
            j = i
            javadoc_lines = [line]
            while j < len(lines) and '*/' not in lines[j]:
                j += 1
                javadoc_lines.append(lines[j])
            if j < len(lines):
                k = j + 1
                while k < len(lines) and ('interface' not in lines[k]):
                    k += 1
                if k < len(lines) and 'interface' in lines[k] and 'Mapper' in lines[k]:
                    indent = len(line) - len(line.lstrip())
                    indent_str = ' ' * indent
                    new_lines.append(f'{indent_str}/**\n')
                    new_lines.append(f'{indent_str} * {table_desc}数据访问Mapper接口，对应数据库表 {table_name}。\n')
                    new_lines.append(f'{indent_str} * 继承MyBatis-Flex的BaseMapper获得基础CRUD能力。\n')
                    new_lines.append(f'{indent_str} *\n')
                    new_lines.append(f'{indent_str} * @author sleep\n')
                    new_lines.append(f'{indent_str} * @since 2024-05-24\n')
                    new_lines.append(f'{indent_str} */\n')
                    i = j + 1
                    class_javadoc_replaced = True
                    continue
        
        # 检测带SQL注解的方法
        sql_anno_match = re.match(r'^(\s*)@(Select|Update|Insert|Delete)\(', line)
        
        if sql_anno_match:
            indent = sql_anno_match.group(1)
            anno_type = sql_anno_match.group(2)
            
            # 收集完整的注解和方法声明
            block_lines = [line]
            j = i + 1
            while j < len(lines):
                block_lines.append(lines[j])
                if ';' in lines[j]:
                    break
                j += 1
            
            # 找到方法名和返回类型
            method_line = block_lines[-1]
            method_match = re.search(r'(\w+(?:<[^>]+>)?)\s+(\w+)\s*\(', method_line)
            
            if method_match:
                ret_type = method_match.group(1)
                method_name = method_match.group(2)
                
                has_javadoc = len(new_lines) > 0 and new_lines[-1].rstrip().endswith('*/')
                
                if not has_javadoc:
                    name_lower = method_name.lower()
                    if 'select' in name_lower or 'find' in name_lower or 'get' in name_lower or 'query' in name_lower or anno_type == 'Select':
                        m_desc = f'查询{table_desc}数据'
                        m_return = '查询结果'
                    elif 'insert' in name_lower or 'add' in name_lower or 'save' in name_lower or anno_type == 'Insert':
                        m_desc = f'新增{table_desc}数据'
                        m_return = None
                    elif 'update' in name_lower or 'modify' in name_lower or 'set' in name_lower or anno_type == 'Update':
                        m_desc = f'更新{table_desc}数据'
                        m_return = None
                    elif 'delete' in name_lower or 'remove' in name_lower or anno_type == 'Delete':
                        m_desc = f'删除{table_desc}数据'
                        m_return = None
                    else:
                        m_desc = f'{table_desc}数据操作'
                        m_return = '操作结果'
                    
                    new_lines.append(f'{indent}/**\n')
                    new_lines.append(f'{indent} * {m_desc}\n')
                    
                    params_match = re.search(r'\((.*?)\)', method_line)
                    if params_match:
                        params_str = params_match.group(1).strip()
                        if params_str:
                            params = [p.strip() for p in params_str.split(',')]
                            for p in params:
                                parts = p.split()
                                if len(parts) >= 2:
                                    p_name = parts[-1]
                                    new_lines.append(f'{indent} * @param {p_name} 参数\n')
                    
                    if ret_type != 'void' and m_return:
                        new_lines.append(f'{indent} * @return {m_return}\n')
                    
                    new_lines.append(f'{indent} */\n')
            
            for bl in block_lines:
                new_lines.append(bl)
            i = j + 1
            continue
        
        new_lines.append(line)
        i += 1
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.writelines(new_lines)
    
    return True

def main():
    e_count = 0
    m_count = 0
    
    print('处理实体类...')
    for fn in sorted(os.listdir(ENTITY_DIR)):
        if fn.endswith('.java'):
            fp = os.path.join(ENTITY_DIR, fn)
            try:
                process_entity(fp)
                e_count += 1
            except Exception as e:
                print(f'  失败: {fn}: {e}')
    
    print('处理Mapper...')
    for fn in sorted(os.listdir(MAPPER_DIR)):
        if fn.endswith('.java'):
            fp = os.path.join(MAPPER_DIR, fn)
            try:
                process_mapper(fp)
                m_count += 1
            except Exception as e:
                print(f'  失败: {fn}: {e}')
    
    print(f'完成！处理了 {e_count} 个实体类和 {m_count} 个Mapper。')

if __name__ == '__main__':
    main()
