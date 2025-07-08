# Script pour ajouter des donnees de test a l'application

Write-Host "🍽 Ajout de donnees de test..." -ForegroundColor Green

# Attendre que les services soient prets
Write-Host "⏳ Attente que les services soient prets..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# Ajouter des plats au menu
Write-Host "🍕 Ajout des plats au menu..." -ForegroundColor Cyan

$menuItems = @(
    @{
        name = "Pizza Margherita"
        description = "Pizza classique avec sauce tomate, mozzarella et basilic"
        price = 12.99
        category = "PIZZA"
        available = $true
    },
    @{
        name = "Pizza Pepperoni"
        description = "Pizza avec sauce tomate, mozzarella et pepperoni"
        price = 14.99
        category = "PIZZA"
        available = $true
    },
    @{
        name = "Burger Classic"
        description = "Burger avec steak hache, salade, tomate et sauce"
        price = 9.99
        category = "BURGER"
        available = $true
    },
    @{
        name = "Salade Cesar"
        description = "Salade romaine, parmesan, croutons et sauce Cesar"
        price = 8.99
        category = "SALADE"
        available = $true
    },
    @{
        name = "Pates Carbonara"
        description = "Pates fraiches avec lardons, oeufs et parmesan"
        price = 11.99
        category = "PATES"
        available = $true
    },
    @{
        name = "Coca-Cola"
        description = "Boisson rafraichissante 33cl"
        price = 2.50
        category = "BOISSON"
        available = $true
    }
)

foreach ($item in $menuItems) {
    try {
        $json = $item | ConvertTo-Json
        $response = Invoke-RestMethod -Uri "http://localhost:8081/menu" -Method POST -Body $json -ContentType "application/json"
        Write-Host "  ✅ Ajoute: $($item.name)" -ForegroundColor Green
    }
    catch {
        Write-Host "  ❌ Erreur pour $($item.name): $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n🎉 Donnees de test ajoutees!" -ForegroundColor Green
Write-Host "🔗 Testez maintenant:" -ForegroundColor Cyan
Write-Host "   - Menu: http://localhost:8081/menu" -ForegroundColor White
Write-Host "   - Frontend: http://localhost:4200" -ForegroundColor White

# Verifier le menu
Write-Host "`n📋 Verification du menu:" -ForegroundColor Cyan
try {
    $menu = Invoke-RestMethod http://localhost:8081/menu
    Write-Host "   Menu contient $($menu.Count) plats" -ForegroundColor Green
    foreach ($item in $menu) {
        Write-Host "   - $($item.name) ($($item.price) EUR)" -ForegroundColor White
    }
}
catch {
    Write-Host "   ❌ Impossible de recuperer le menu" -ForegroundColor Red
} 