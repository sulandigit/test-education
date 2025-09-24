#!/bin/bash

# Spring Boot 3.x 单元测试验证脚本

echo "=================================="
echo "Spring Boot 3.x 单元测试验证开始"
echo "=================================="

# 设置颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查Java版本
echo -e "${YELLOW}1. 检查Java版本...${NC}"
if command -v java &> /dev/null; then
    java_version=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
    echo "当前Java版本: $java_version"
    if [[ $java_version == 17* ]]; then
        echo -e "${GREEN}✅ Java 17版本检查通过${NC}"
    else
        echo -e "${RED}❌ 需要Java 17，当前版本：$java_version${NC}"
        echo "请参考 JAVA_17_SETUP_GUIDE.md 安装Java 17"
    fi
else
    echo -e "${RED}❌ Java未安装或不在PATH中${NC}"
fi

echo ""

# 编译验证
echo -e "${YELLOW}2. 执行Maven编译验证...${NC}"
if mvn clean compile -q; then
    echo -e "${GREEN}✅ Maven编译成功${NC}"
else
    echo -e "${RED}❌ Maven编译失败${NC}"
    echo "请检查Java版本和依赖配置"
    exit 1
fi

echo ""

# 依赖树检查
echo -e "${YELLOW}3. 检查依赖冲突...${NC}"
mvn dependency:tree -q > dependency_tree.log 2>&1
if grep -q "conflicts\|duplicate" dependency_tree.log; then
    echo -e "${YELLOW}⚠️  发现依赖冲突，请查看 dependency_tree.log${NC}"
else
    echo -e "${GREEN}✅ 依赖检查通过${NC}"
fi

echo ""

# 单元测试执行
echo -e "${YELLOW}4. 执行单元测试...${NC}"

# 统计测试结果
total_modules=0
passed_modules=0
failed_modules=0

# 测试各个模块
modules=(
    "roncoo-education-common"
    "roncoo-education-feign" 
    "roncoo-education-service/roncoo-education-service-system"
    "roncoo-education-service/roncoo-education-service-user"
    "roncoo-education-service/roncoo-education-service-course"
)

for module in "${modules[@]}"; do
    if [ -d "$module" ]; then
        echo "  测试模块: $module"
        total_modules=$((total_modules + 1))
        
        cd "$module"
        if mvn test -q > "../test_${module//\//_}.log" 2>&1; then
            echo -e "    ${GREEN}✅ $module 测试通过${NC}"
            passed_modules=$((passed_modules + 1))
        else
            echo -e "    ${RED}❌ $module 测试失败${NC}"
            failed_modules=$((failed_modules + 1))
            echo "    详细日志: test_${module//\//_}.log"
        fi
        cd - > /dev/null
    fi
done

echo ""

# XXL-Job模块测试（如果Java环境就绪）
echo -e "${YELLOW}5. 测试XXL-Job模块...${NC}"
if [ -d "roncoo-job" ]; then
    cd roncoo-job
    if mvn test -q > "../test_roncoo_job.log" 2>&1; then
        echo -e "  ${GREEN}✅ roncoo-job 测试通过${NC}"
    else
        echo -e "  ${RED}❌ roncoo-job 测试失败${NC}"
        echo "  详细日志: test_roncoo_job.log"
    fi
    cd - > /dev/null
fi

echo ""

# 生成测试报告
echo -e "${YELLOW}6. 生成测试报告...${NC}"

cat > unit_test_report.md << EOF
# Spring Boot 3.x 单元测试报告

## 测试执行时间
- **执行时间**: $(date)
- **环境**: $(uname -s) $(uname -r)
- **Java版本**: $(java -version 2>&1 | head -n1 | cut -d'"' -f2)

## 测试统计
- **总模块数**: $total_modules
- **通过模块**: $passed_modules
- **失败模块**: $failed_modules
- **成功率**: $(( passed_modules * 100 / total_modules ))%

## 模块测试结果

EOF

for module in "${modules[@]}"; do
    if [ -d "$module" ]; then
        if [ -f "test_${module//\//_}.log" ]; then
            if grep -q "BUILD SUCCESS" "test_${module//\//_}.log"; then
                echo "- ✅ **$module**: 测试通过" >> unit_test_report.md
            else
                echo "- ❌ **$module**: 测试失败" >> unit_test_report.md
            fi
        fi
    fi
done

cat >> unit_test_report.md << EOF

## 框架兼容性验证

### JUnit 5验证
- JUnit Jupiter 5.10.1版本兼容性测试

### Mockito验证  
- Mockito 5.7.0版本功能验证

### Spring Boot Test验证
- Spring Boot 3.2.10测试框架集成验证

## 建议

EOF

if [ $failed_modules -eq 0 ]; then
    cat >> unit_test_report.md << EOF
🎉 **所有测试通过！** Spring Boot 3.x升级的单元测试验证成功。

### 下一步操作
1. 执行集成测试验证
2. 进行性能基准测试
3. 部署到测试环境验证

EOF
else
    cat >> unit_test_report.md << EOF
⚠️ **发现测试失败**，需要进一步排查和修复。

### 修复建议
1. 检查失败模块的测试日志
2. 验证Jakarta EE命名空间迁移是否完整
3. 确认Spring Boot 3.x兼容性配置
4. 更新测试用例中的过时API调用

### 常见问题
- **编译错误**: 通常是Jakarta EE命名空间问题
- **运行时错误**: 可能是Spring Boot 3.x配置变更
- **Mock失败**: Mockito版本兼容性问题

EOF
fi

echo -e "${GREEN}✅ 测试报告已生成: unit_test_report.md${NC}"

echo ""
echo "=================================="
echo "Spring Boot 3.x 单元测试验证完成"
echo "=================================="

if [ $failed_modules -eq 0 ]; then
    echo -e "${GREEN}🎉 所有测试通过！${NC}"
    exit 0
else
    echo -e "${RED}❌ 有 $failed_modules 个模块测试失败${NC}"
    exit 1
fi