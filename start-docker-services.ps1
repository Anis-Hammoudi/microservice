# Script pour démarrer l'environnement microservices avec Docker
Write-Host "🐳 Démarrage de l'environnement microservices avec Docker..." -ForegroundColor Green

# Vérifier que Docker est en cours d'exécution
Write-Host "🔍 Vérification de Docker..." -ForegroundColor Yellow
try {
    docker version | Out-Null
    Write-Host "✅ Docker est disponible" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker n'est pas en cours d'exécution. Veuillez démarrer Docker Desktop." -ForegroundColor Red
    exit 1
}

# Nettoyer les anciens conteneurs si nécessaire
Write-Host "🧹 Nettoyage des anciens conteneurs..." -ForegroundColor Yellow
docker-compose -f docker-compose-build-fixed.yml down -v

# Construire et démarrer tous les services
Write-Host "🚀 Construction et démarrage des services..." -ForegroundColor Yellow
docker-compose -f docker-compose-build-fixed.yml up --build -d

# Attendre que les services soient prêts
Write-Host "⏳ Attendre que les services soient prêts..." -ForegroundColor Yellow
Write-Host "   Cela peut prendre quelques minutes pour la première fois..." -ForegroundColor Cyan

# Vérifier le statut des services
Start-Sleep -Seconds 30
Write-Host "📊 Statut des services:" -ForegroundColor Cyan
docker-compose -f docker-compose-build-fixed.yml ps

Write-Host "" -ForegroundColor White
Write-Host "✅ Services démarrés! Voici les URLs:" -ForegroundColor Green
Write-Host "🔐 AuthService: http://localhost:8084/auth" -ForegroundColor White
Write-Host "📦 OrderService: http://localhost:8081/orders" -ForegroundColor White
Write-Host "🍳 KitchenService: http://localhost:8082/actuator/health" -ForegroundColor White
Write-Host "🚚 DeliveryService: http://localhost:8083/actuator/health" -ForegroundColor White
Write-Host "" -ForegroundColor White
Write-Host "📊 Bases de données PostgreSQL:" -ForegroundColor Cyan
Write-Host "   - auth_db: localhost:5435" -ForegroundColor White
Write-Host "   - orders: localhost:5432" -ForegroundColor White
Write-Host "   - kitchen: localhost:5433" -ForegroundColor White
Write-Host "   - delivery: localhost:5434" -ForegroundColor White
Write-Host "" -ForegroundColor White
Write-Host "🌐 Pour démarrer le frontend:" -ForegroundColor Cyan
Write-Host "   cd food-ordering-app" -ForegroundColor White
Write-Host "   npm start" -ForegroundColor White
Write-Host "   Puis ouvrir http://localhost:4200" -ForegroundColor White
Write-Host "" -ForegroundColor White
Write-Host "📝 Pour voir les logs:" -ForegroundColor Cyan
Write-Host "   docker-compose -f docker-compose-build-fixed.yml logs -f [service-name]" -ForegroundColor White
Write-Host "" -ForegroundColor White
Write-Host "🛑 Pour arrêter tous les services:" -ForegroundColor Red
Write-Host "   docker-compose -f docker-compose-build-fixed.yml down" -ForegroundColor White 