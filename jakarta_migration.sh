#!/bin/bash

# Jakarta EE 命名空间迁移脚本
# 将 javax.* 包替换为 jakarta.* 包

echo "开始Jakarta EE命名空间迁移..."

# 定义要替换的包映射
declare -A package_mappings=(
    ["javax.servlet"]="jakarta.servlet"
    ["javax.validation"]="jakarta.validation"
    ["javax.persistence"]="jakarta.persistence"
    ["javax.transaction"]="jakarta.transaction"
    ["javax.annotation"]="jakarta.annotation"
    ["javax.ws.rs"]="jakarta.ws.rs"
    ["javax.inject"]="jakarta.inject"
    ["javax.xml.bind"]="jakarta.xml.bind"
    ["javax.json"]="jakarta.json"
)

# 备份原始文件
backup_dir="jakarta_migration_backup_$(date +%Y%m%d_%H%M%S)"
echo "创建备份目录: $backup_dir"
mkdir -p "$backup_dir"

# 查找所有需要替换的Java文件
find . -name "*.java" -type f > java_files.tmp

# 统计替换情况
total_files=0
modified_files=0

while IFS= read -r file; do
    if [[ -f "$file" ]]; then
        total_files=$((total_files + 1))
        modified=false
        
        # 检查文件是否包含需要替换的包
        for javax_package in "${!package_mappings[@]}"; do
            jakarta_package="${package_mappings[$javax_package]}"
            
            if grep -q "$javax_package" "$file"; then
                # 备份原文件
                if [[ "$modified" == false ]]; then
                    backup_file="$backup_dir/${file#./}"
                    backup_dir_path=$(dirname "$backup_file")
                    mkdir -p "$backup_dir_path"
                    cp "$file" "$backup_file"
                    modified=true
                fi
                
                # 执行替换
                sed -i "s/${javax_package//./\\.}/${jakarta_package//./\\.}/g" "$file"
                echo "替换 $file: $javax_package → $jakarta_package"
            fi
        done
        
        if [[ "$modified" == true ]]; then
            modified_files=$((modified_files + 1))
        fi
    fi
done < java_files.tmp

# 清理临时文件
rm -f java_files.tmp

echo "Jakarta EE命名空间迁移完成!"
echo "总文件数: $total_files"
echo "修改文件数: $modified_files"
echo "备份目录: $backup_dir"

# 生成迁移报告
cat > jakarta_migration_report.md << EOF
# Jakarta EE 命名空间迁移报告

## 迁移统计
- 扫描文件总数: $total_files
- 修改文件数量: $modified_files
- 备份位置: $backup_dir

## 包映射关系
| 原包名 | 新包名 |
|--------|--------|
EOF

for javax_package in "${!package_mappings[@]}"; do
    jakarta_package="${package_mappings[$javax_package]}"
    echo "| $javax_package | $jakarta_package |" >> jakarta_migration_report.md
done

cat >> jakarta_migration_report.md << EOF

## 下一步操作
1. 验证编译: \`mvn clean compile\`
2. 运行测试: \`mvn test\`
3. 检查运行时异常
4. 如需回滚，使用备份文件恢复

*迁移时间: $(date)*
EOF

echo "迁移报告已生成: jakarta_migration_report.md"