#!/bin/bash

API_BASE="http://localhost:8080/api/v1"

echo "=========================================="
echo "  SleekFlow API 演示场景"
echo "=========================================="
echo ""

# === 场景 1: 用户注册和登录 ===
echo "📝 场景 1: 用户注册和登录"
echo ""

echo "   1.1 注册用户..."
REGISTER_RESPONSE=$(curl -s -X POST $API_BASE/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"bob@example.com","username":"bob","password":"Bob123!"}')
echo "   Response:"
echo "   $REGISTER_RESPONSE"
echo ""

echo "   1.2 用户登录..."
TOKEN=$(curl -s -X POST $API_BASE/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"bob@example.com","password":"Bob123!"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "   Token: ${TOKEN:0:50}..."
echo ""

# === 场景 2: 创建待办事项 ===
echo "📝 场景 2: 创建待办事项"
echo ""

echo "   2.1 创建第一个待办..."
TODO_1=$(curl -s -X POST $API_BASE/todos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"代码评审 PR#42","description":"审查后合并代码","priority":"HIGH"}')
echo "   Response:"
echo "   $TODO_1"
echo ""

echo "   2.2 创建第二个待办..."
TODO_2=$(curl -s -X POST $API_BASE/todos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"准备面试演示","description":"准备演示环境","priority":"MEDIUM","dueDate":"2026-06-15T10:00:00"}')
echo "   Response:"
echo "   $TODO_2"
echo ""

# === 场景 3: 查询和更新待办 ===
echo "📝 场景 3: 查询和更新待办事项"
echo ""

echo "   3.1 查询所有待办..."
ALL_TODOS=$(curl -s -X GET $API_BASE/todos \
  -H "Authorization: Bearer $TOKEN")
echo "   Response:"
echo "   $ALL_TODOS"
echo ""

echo "   3.2 更新待办状态为进行中..."
TODO_ID_1=$(echo $TODO_1 | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
UPDATE_TODO=$(curl -s -X PUT $API_BASE/todos/$TODO_ID_1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"status":"IN_PROGRESS"}')
echo "   Response:"
echo "   $UPDATE_TODO"
echo ""

# === 场景 4: 标签管理 ===
echo "📝 场景 4: 标签管理"
echo ""

echo "   4.1 创建标签..."
TAG_1=$(curl -s -X POST $API_BASE/tags \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"工作"}')
echo "   Response:"
echo "   $TAG_1"
echo ""

TAG_ID=$(echo $TAG_1 | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
TODO_ID_2=$(echo $TODO_2 | grep -o '"id":"[^"]*"' | cut -d'"' -f4)

echo "   4.2 为待办添加标签..."
ADD_TAG=$(curl -s -X PUT $API_BASE/todos/$TODO_ID_2 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"tags\":[\"$TAG_ID\"]}")
echo "   Response:"
echo "   $ADD_TAG"
echo ""

echo "=========================================="
echo "  演示完成！"
echo "=========================================="
echo ""
echo "📌 查看所有操作记录:"
ACTIVITIES=$(curl -s -X GET $API_BASE/todos/$TODO_ID_1/activities \
  -H "Authorization: Bearer $TOKEN")
echo "   Response:"
echo "   $ACTIVITIES"
