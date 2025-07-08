# Script pour arrêter l'environnement microservices Docker
Write-Host "🛑 Arrêt de l'environnement microservices Docker..." -ForegroundColor Red

# Arrêter et supprimer tous les conteneurs
Write-Host "📦 Arrêt des conteneurs..." -ForegroundColor Yellow
docker-compose -f docker-compose-build-fixed.yml down

Write-Host "🧹 Nettoyage des volumes (optionnel)..." -ForegroundColor Yellow
Write-Host "   Si vous voulez supprimer toutes les données, exécutez:" -ForegroundColor Cyan
Write-Host "   docker-compose -f docker-compose-build-fixed.yml down -v" -ForegroundColor White

Write-Host "✅ Services arrêtés!" -ForegroundColor Green 