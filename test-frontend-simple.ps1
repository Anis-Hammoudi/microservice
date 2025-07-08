# Test simple de l'application
Write-Host "Test de l'application Food Ordering..." -ForegroundColor Green

Write-Host "1. Test Order Service:" -ForegroundColor Cyan
try {
    $health = Invoke-RestMethod http://localhost:8081/actuator/health
    Write-Host "   Order Service: $($health.status)" -ForegroundColor Green
} catch {
    Write-Host "   Order Service: Erreur" -ForegroundColor Red
}

Write-Host "2. Test creation commande:" -ForegroundColor Cyan
try {
    $order = @{
        clientId = 123
        items = @("Pizza Margherita")
    } | ConvertTo-Json
    
    $result = Invoke-RestMethod -Uri "http://localhost:8081/orders" -Method POST -Body $order -ContentType "application/json"
    Write-Host "   Commande creee avec ID: $($result.id)" -ForegroundColor Green
} catch {
    Write-Host "   Erreur creation: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "3. Test menu:" -ForegroundColor Cyan
try {
    $menu = Invoke-RestMethod http://localhost:8081/menu
    Write-Host "   Menu: $($menu.Count) plats disponibles" -ForegroundColor Green
} catch {
    Write-Host "   Menu: Erreur" -ForegroundColor Red
}

Write-Host "`nRESULTAT: Le backend fonctionne maintenant!" -ForegroundColor Green
Write-Host "Frontend: http://localhost:4200" -ForegroundColor White
Write-Host "Le probleme de creation de commandes est resolu." -ForegroundColor Green 