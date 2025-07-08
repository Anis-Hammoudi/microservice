# 🐳 Environnement Docker - Microservices Food Ordering

## Vue d'ensemble

Cette configuration Docker déploie une architecture microservices complète avec :

- **4 services backend** Spring Boot
- **4 bases de données** PostgreSQL séparées  
- **Apache Kafka** + Zookeeper pour la messagerie
- **Frontend Angular** (lancé séparément)

## 🚀 Démarrage rapide

```powershell
# Démarrer tous les services
.\start-docker-services.ps1

# Dans un autre terminal, démarrer le frontend
cd food-ordering-app
npm start
```

## 📊 Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   AuthService   │    │  OrderService   │    │ KitchenService  │    │DeliveryService  │
│   Port: 8084    │    │   Port: 8081    │    │   Port: 8082    │    │   Port: 8083    │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │                      │
┌─────────▼───────┐    ┌─────────▼───────┐    ┌─────────▼───────┐    ┌─────────▼───────┐
│   postgres-auth │    │postgres-orders  │    │postgres-kitchen │    │postgres-delivery│
│   Port: 5435    │    │   Port: 5432    │    │   Port: 5433    │    │   Port: 5434    │
└─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘
                                 │
                       ┌─────────▼───────┐
                       │      Kafka      │
                       │   Port: 9092    │
                       └─────────────────┘
```

## 🗃️ Bases de données

| Service | Base | Port Host | Container |
|---------|------|-----------|-----------|
| Auth | `auth_db` | 5435 | postgres-auth |
| Orders | `orders` | 5432 | postgres-orders |
| Kitchen | `kitchen` | 5433 | postgres-kitchen |
| Delivery | `delivery` | 5434 | postgres-delivery |

**Credentials :** `postgress` / `admin`

## 🔧 Commandes utiles

```powershell
# Voir les logs d'un service
docker-compose -f docker-compose-build-fixed.yml logs -f auth-service

# Rebuilder un service spécifique
docker-compose -f docker-compose-build-fixed.yml up --build auth-service

# Voir le statut des services
docker-compose -f docker-compose-build-fixed.yml ps

# Accéder à une base de données
docker exec -it pg-auth psql -U postgress -d auth_db

# Arrêter tous les services
.\stop-docker-services.ps1

# Supprimer tout (services + données)
docker-compose -f docker-compose-build-fixed.yml down -v
```

## 🔄 Workflow de développement

1. **Modifier le code** dans un service
2. **Rebuilder** le service : `docker-compose -f docker-compose-build-fixed.yml up --build [service-name]`
3. **Tester** avec le frontend sur http://localhost:4200

## 🐛 Debugging

### Voir les logs détaillés
```powershell
# Tous les services
docker-compose -f docker-compose-build-fixed.yml logs -f

# Service spécifique
docker-compose -f docker-compose-build-fixed.yml logs -f auth-service
```

### Accéder aux conteneurs
```powershell
# Shell dans un conteneur
docker exec -it auth-service bash

# Vérifier la connectivité réseau
docker exec -it order-service ping auth-service
```

### Redémarrer un service
```powershell
docker-compose -f docker-compose-build-fixed.yml restart auth-service
```

## ⚠️ Considérations importantes

1. **Première exécution** : Peut prendre 5-10 minutes
2. **Espace disque** : Environ 2-3 GB pour toutes les images
3. **RAM** : Recommandé 8 GB minimum
4. **Ports** : Vérifier qu'aucun autre service n'utilise les ports 5432-5435, 8081-8084, 9092

## 🔗 URLs importantes

- **AuthService API :** http://localhost:8084/auth
- **OrderService API :** http://localhost:8081/orders  
- **Frontend App :** http://localhost:4200
- **Kafka UI** (si ajouté) : http://localhost:8080

## 📝 Notes de production

Pour un déploiement en production, considérer :

- Variables d'environnement sécurisées
- Volumes persistants pour les données
- Load balancer nginx
- Monitoring avec Prometheus/Grafana
- Secrets management
- Health checks avancés 