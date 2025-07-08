# Script simple pour démarrer les services microservices
Write-Host "🚀 Démarrage des services microservices..." -ForegroundColor Green

# Démarrage du service de commandes
Write-Host "📦 Démarrage du service de commandes (port 8081)..." -ForegroundColor Yellow
cmd /c "cd orderService && mvnw.cmd spring-boot:run" &

# Attendre un peu
Start-Sleep -Seconds 5

# Démarrage du service de cuisine  
Write-Host "🍳 Démarrage du service de cuisine (port 8082)..." -ForegroundColor Yellow
cmd /c "cd kitchenService && mvnw.cmd spring-boot:run" &

# Attendre un peu
Start-Sleep -Seconds 5

# Démarrage du service de livraison
Write-Host "🚚 Démarrage du service de livraison (port 8083)..." -ForegroundColor Yellow
cmd /c "cd DeliveryService && mvnw.cmd spring-boot:run" &

Write-Host "✅ Tous les services sont en cours de démarrage!" -ForegroundColor Green
Write-Host "🔗 URLs à tester dans 2 minutes:" -ForegroundColor Cyan
Write-Host "   - http://localhost:8081/menu" -ForegroundColor White
Write-Host "   - http://localhost:8081/orders" -ForegroundColor White
Write-Host "   - http://localhost:8082/actuator/health" -ForegroundColor White
Write-Host "   - http://localhost:8083/actuator/health" -ForegroundColor White 