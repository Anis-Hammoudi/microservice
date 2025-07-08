# Script pour démarrer tous les services microservices
Write-Host "🚀 Démarrage des services microservices..." -ForegroundColor Green

# Démarrage du service de commandes
Write-Host "📦 Démarrage du service de commandes (port 8081)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-Command", "cd 'orderService'; .\mvnw.cmd spring-boot:run"

# Attendre un peu avant de démarrer le suivant
Start-Sleep -Seconds 5

# Démarrage du service de cuisine
Write-Host "🍳 Démarrage du service de cuisine (port 8082)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-Command", "cd 'kitchenService'; .\mvnw.cmd spring-boot:run"

# Attendre un peu avant de démarrer le suivant
Start-Sleep -Seconds 5

# Démarrage du service de livraison
Write-Host "🚚 Démarrage du service de livraison (port 8083)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-Command", "cd 'DeliveryService'; .\mvnw.cmd spring-boot:run"

Write-Host "✅ Tous les services sont en cours de démarrage!" -ForegroundColor Green
Write-Host "🔗 URLs des services:" -ForegroundColor Cyan
Write-Host "   - Order Service: http://localhost:8081" -ForegroundColor White
Write-Host "   - Kitchen Service: http://localhost:8082" -ForegroundColor White
Write-Host "   - Delivery Service: http://localhost:8083" -ForegroundColor White
Write-Host ""
Write-Host "💡 Pour vérifier que les services fonctionnent:" -ForegroundColor Cyan
Write-Host "   curl http://localhost:8081/menu" -ForegroundColor White
Write-Host "   curl http://localhost:8081/orders" -ForegroundColor White 