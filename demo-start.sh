#!/bin/bash

set -e

echo "=========================================="
echo "  SleekFlow Todo API Demo 环境"
echo "=========================================="
echo ""

cd "$(dirname "$0")"

echo "📦 步骤 1/5: 启动 Docker MySQL..."
docker compose up -d mysql

echo "⏳ 步骤 2/5: 等待 MySQL 就绪..."
until docker compose exec -T mysql mysqladmin ping -h localhost --silent; do
  echo "   MySQL 正在启动..."
  sleep 2
done
echo "   ✅ MySQL 已就绪！"
echo ""

echo "🚀 步骤 3/5: 启动 Spring Boot 应用..."
nohup mvn spring-boot:run > /tmp/todo-app.log 2>&1 &
APP_PID=$!
echo "   应用 PID: $APP_PID"
echo ""

echo "⏳ 步骤 4/5: 等待应用启动..."
sleep 15

echo "🔍 步骤 5/5: 验证应用状态..."
if curl -s http://localhost:8080/actuator/health 2>/dev/null | grep -q "UP\|status"; then
  echo "   ✅ 应用启动成功！"
else
  echo "   ❌ 应用启动失败，检查日志："
  echo "   tail -f /tmp/todo-app.log"
  exit 1
fi
echo ""

echo "=========================================="
echo "  Demo 环境就绪！"
echo "=========================================="
echo ""
echo "📌 访问地址："
echo "   Swagger UI:  http://localhost:8080/swagger-ui.html"
echo "   Health Check: http://localhost:8080/actuator/health"
echo ""
echo "👤 演示用户："
echo "   Email:    demo@test.com"
echo "   Password: Demo123!"
echo ""
echo "📋 Postman Collection: "
echo "   ./postman/SleekFlow-TodoAPI.postman_collection.json"
echo ""
echo "📝 查看应用日志："
echo "   tail -f /tmp/todo-app.log"
echo ""
echo "🛑 停止服务："
echo "   docker compose down"
echo "   kill $APP_PID"
echo ""
