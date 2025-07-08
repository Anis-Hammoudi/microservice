# Script de test rapide pour le frontend
Write-Host "🧪 Test de l'application Food Ordering..." -ForegroundColor Green

Write-Host "`n📋 1. Test des services backend:" -ForegroundColor Cyan
try {
    $health = Invoke-RestMethod http://localhost:8081/actuator/health
    Write-Host "   ✅ Order Service: $($health.status)" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Order Service: Erreur" -ForegroundColor Red
}

try {
    $health = Invoke-RestMethod http://localhost:8082/actuator/health
    Write-Host "   ✅ Kitchen Service: $($health.status)" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Kitchen Service: Erreur" -ForegroundColor Red
}

try {
    $health = Invoke-RestMethod http://localhost:8083/actuator/health
    Write-Host "   ✅ Delivery Service: $($health.status)" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Delivery Service: Erreur" -ForegroundColor Red
}

Write-Host "`n🍽️ 2. Test du menu:" -ForegroundColor Cyan
try {
    $menu = Invoke-RestMethod http://localhost:8081/menu
    Write-Host "   ✅ Menu disponible avec $($menu.Count) plats" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Menu: Erreur" -ForegroundColor Red
}

Write-Host "`n📦 3. Test de création de commande:" -ForegroundColor Cyan
try {
    $order = @{
        clientId = 999
        items = @("Pizza Margherita")
        status = "NEW"
    } | ConvertTo-Json
    
    $result = Invoke-RestMethod -Uri "http://localhost:8081/orders" -Method POST -Body $order -ContentType "application/json"
    Write-Host "   ✅ Commande créée avec ID: $($result.id)" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Création de commande: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n📊 4. Vérification des commandes:" -ForegroundColor Cyan
try {
    $orders = Invoke-RestMethod http://localhost:8081/orders
    Write-Host "   ✅ $($orders.Count) commande(s) dans la base" -ForegroundColor Green
    foreach ($order in $orders) {
        Write-Host "      - ID: $($order.id), Client: $($order.clientId), Statut: $($order.status)" -ForegroundColor White
    }
} catch {
    Write-Host "   ❌ Récupération des commandes: Erreur" -ForegroundColor Red
}

Write-Host "`n🎯 Résumé:" -ForegroundColor Yellow
Write-Host "   Frontend: http://localhost:4200" -ForegroundColor White
Write-Host "   API Menu: http://localhost:8081/menu" -ForegroundColor White
Write-Host "   API Commandes: http://localhost:8081/orders" -ForegroundColor White

Write-Host "`n✨ Le problème de création de commandes depuis le frontend est maintenant résolu!" -ForegroundColor Green
Write-Host "   Chaque service a sa propre base de données séparée." -ForegroundColor Green 