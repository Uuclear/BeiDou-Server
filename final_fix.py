#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import os
import re

ENTITY_DIR = '/workspace/gms-server/src/main/java/org/gms/dao/entity'
MAPPER_DIR = '/workspace/gms-server/src/main/java/org/gms/dao/mapper'

def final_fix_entity(file_path):
    """最终修复实体类：确保Javadoc在注解之前"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 移除重复注释，然后重新正确放置
    lines = content.split('\n')
    new_lines = []
    
    i = 0
    while i < len(lines):
        line = lines[i]
        
        # 检测是否是字段声明
        field_match = re.match(r'^(\s*)(private|protected|public)\s+', line)
        
        if field_match and line.rstrip().endswith(';'):
            indent = field_match.group(1)
            
            # 向前收集注解和现有注释
            annotations = []
            comments = []
            j = len(new_lines) - 1
            
            while j >= 0:
                curr_line = new_lines[j].rstrip()
                
                if curr_line.endswith('*/'):
                    # 找到一个注释块的结束
                    comment_block = []
                    while j >= 0 and not new_lines[j].lstrip().startswith('/**'):
                        comment_block.insert(0, new_lines[j])
                        new_lines.pop(j)
                        j -= 1
                    if j >= 0 and new_lines[j].lstrip().startswith('/**'):
                        comment_block.insert(0, new_lines[j])
                        new_lines.pop(j)
                        j -= 1
                    comments = comment_block
                    break
                elif curr_line.lstrip().startswith('@'):
                    annotations.insert(0, new_lines[j])
                    new_lines.pop(j)
                    j -= 1
                elif curr_line.strip() == '':
                    j -= 1
                else:
                    break
            
            # 添加注释、注解、字段
            if comments:
                for c in comments:
                    new_lines.append(c)
            for a in annotations:
                new_lines.append(a)
        
        new_lines.append(line)
        i += 1
    
    content = '\n'.join(new_lines)
    
    # 再次修复：处理@Id在注释前的情况
    # 模式: @Id...\n/** ... */\nprivate field  =>  /** ... */\n@Id...\nprivate field
    for _ in range(5):
        content = re.sub(
            r'((?:@\w+(?:\([^)]*\))?\s*\n)+)(\s*/\*\*\s*\n(?:\s*\*.*\n)+\s*\*/\s*\n)(\s*(?:@\w+(?:\([^)]*\))?\s*\n)*)(\s*private\s+)',
            r'\2\1\3\4',
            content
        )
    
    # 移除重复的Javadoc块（两个连续的/** ... */）
    content = re.sub(
        r'(/\*\*\s*\n(?:\s*\*.*\n)+\s*\*/\s*\n)(/\*\*\s*\n(?:\s*\*.*\n)+\s*\*/\s*\n)',
        r'\1',
        content
    )
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    return True

def final_fix_mapper(file_path):
    """最终修复Mapper：移除重复注释"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 移除重复的Javadoc块（两个连续的/** ... */）
    for _ in range(5):
        content = re.sub(
            r'(/\*\*\s*\n(?:\s*\*.*\n)+\s*\*/\s*\n)(/\*\*\s*\n(?:\s*\*.*\n)+\s*\*/\s*\n)',
            r'\1',
            content
        )
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    return True

def main():
    import os
    entity_count = 0
    mapper_count = 0
    
    print('最终修复实体类...')
    for filename in os.listdir(ENTITY_DIR):
        if filename.endswith('.java'):
            file_path = os.path.join(ENTITY_DIR, filename)
            try:
                final_fix_entity(file_path)
                entity_count += 1
            except Exception as e:
                print(f'  失败 {filename}: {e}')
    
    print('最终修复Mapper...')
    for filename in os.listdir(MAPPER_DIR):
        if filename.endswith('.java'):
            file_path = os.path.join(MAPPER_DIR, filename)
            try:
                final_fix_mapper(file_path)
                mapper_count += 1
            except Exception as e:
                print(f'  失败 {filename}: {e}')
    
    print(f'完成！处理了 {entity_count} 个实体类和 {mapper_count} 个Mapper。')

if __name__ == '__main__':
    main()
