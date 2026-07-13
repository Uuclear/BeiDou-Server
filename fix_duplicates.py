#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import os
import re

ENTITY_DIR = "/workspace/gms-server/src/main/java/org/gms/dao/entity"
MAPPER_DIR = "/workspace/gms-server/src/main/java/org/gms/dao/mapper"

def remove_serialVersionUID_comment(content):
    """移除serialVersionUID的注释，这是标准字段不需要注释"""
    content = re.sub(
        r'@Serial\s*/\*\*\s*\n\s*\* 序列化版本UID\s*\n\s*\*/\s*\n\s*private static final long serialVersionUID',
        '@Serial\n    private static final long serialVersionUID',
        content
    )
    return content

def fix_field_comments(content):
    """处理字段注释，将注释移到注解之前，移除重复注释"""
    lines = content.split('\n')
    result = []
    i = 0
    while i < len(lines):
        line = lines[i]
        
        # 检测Javadoc注释开始
        if re.match(r'^\s*/\*\*', line):
            # 收集注释块
            comment_lines = []
            while i < len(lines) and not re.match(r'^\s*\*/', lines[i]):
                comment_lines.append(lines[i])
                i += 1
            if i < len(lines):
                comment_lines.append(lines[i])
                i += 1
            
            comment_block = '\n'.join(comment_lines)
            
            # 收集后续的注解
            annotations = []
            while i < len(lines):
                stripped = lines[i].strip()
                if stripped.startswith('@') and not stripped.startswith('@Serial'):
                    annotations.append(lines[i])
                    i += 1
                elif stripped == '':
                    i += 1
                else:
                    break
            
            # 现在看后面是字段声明还是重复注释
            # 检查是否紧接着又有一个注释
            next_is_comment = False
            temp_i = i
            if temp_i < len(lines):
                next_stripped = lines[temp_i].strip()
                if next_stripped == '/**' or re.match(r'^\s*/\*\*', lines[temp_i]):
                    next_is_comment = True
            
            # 如果有重复注释，跳过下一个注释块
            if next_is_comment:
                while temp_i < len(lines) and not re.match(r'^\s*\*/', lines[temp_i]):
                    temp_i += 1
                if temp_i < len(lines):
                    temp_i += 1
                i = temp_i
            
            # 添加注释块
            result.append(comment_block)
            # 添加注解
            for ann in annotations:
                result.append(ann)
        else:
            result.append(line)
            i += 1
    
    return '\n'.join(result)

def fix_file(filepath):
    """修复单个文件"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original = content
    
    # 移除serialVersionUID注释
    content = remove_serialVersionUID_comment(content)
    
    # 修复字段注释位置和重复
    content = fix_field_comments(content)
    
    # 清理连续的空行
    content = re.sub(r'\n{3,}', '\n\n', content)
    
    if content != original:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False

def process_directory(dirpath):
    count = 0
    for filename in os.listdir(dirpath):
        if filename.endswith('.java'):
            filepath = os.path.join(dirpath, filename)
            if fix_file(filepath):
                count += 1
                print(f"已修复: {filename}")
    return count

if __name__ == '__main__':
    entity_count = process_directory(ENTITY_DIR)
    mapper_count = process_directory(MAPPER_DIR)
    print(f"\n完成！实体类修复: {entity_count}, Mapper修复: {mapper_count}")
