# Guide de test - Authentification complète

## 🚀 Démarrage des services

### Option 1: Services locaux (développement)

1. **Démarrer tous les services backend :**
   ```powershell
   .\start-services-simple.ps1
   ```

2. **Attendre que tous les services soient démarrés** (environ 2-3 minutes)

3. **Démarrer le frontend :**
   ```bash
   cd food-ordering-app
   npm start
   ```

4. **Ouvrir l'application :** http://localhost:4200

### Option 2: Services Docker (production-like) 🐳

1. **Démarrer Docker Desktop**

2. **Démarrer tous les services avec Docker :**
   ```powershell
   .\start-docker-services.ps1
   ```

3. **Attendre que tous les conteneurs soient prêts** (environ 3-5 minutes pour la première fois)

4. **Démarrer le frontend :**
   ```bash
   cd food-ordering-app
   npm start
   ```

5. **Ouvrir l'application :** http://localhost:4200

> **Note :** Avec Docker, les bases de données sont sur des ports différents :
> - auth_db: localhost:5435
> - orders: localhost:5432  
> - kitchen: localhost:5433
> - delivery: localhost:5434

## 🧪 Test du workflow complet

### 1. **Inscription d'un nouveau client**
   - Aller sur http://localhost:4200/register
   - Remplir le formulaire :
     - Email : `client@test.com`
     - Type de compte : `Client (pour commander)`
     - Mot de passe : `password123`
     - Confirmer le mot de passe : `password123`
   - Cliquer sur "Créer mon compte"
   - ✅ **Résultat attendu :** Message de succès + redirection vers login

### 2. **Connexion**
   - Utiliser les identifiants créés
   - ✅ **Résultat attendu :** Redirection vers l'accueil avec navbar affichant "Bonjour, client@test.com (CLIENT)"

### 3. **Passer une commande**
   - Ajouter des plats au panier
   - Cliquer sur "Commander"
   - ✅ **Résultat attendu :** Commande créée avec votre ID client automatiquement défini

### 4. **Vérifier la commande dans la base**
   - Ouvrir votre client PostgreSQL
   - Base : `orders`
   - Table : `orders`
   - ✅ **Résultat attendu :** Nouvelle ligne avec votre `client_id` (ID de votre utilisateur dans `auth_db.app_user`)

### 5. **Suivi des commandes**
   - Aller sur "Suivi Commande"
   - ✅ **Résultat attendu :** Liste de vos commandes uniquement

### 6. **Déconnexion**
   - Cliquer sur "Déconnexion"
   - ✅ **Résultat attendu :** Redirection vers login + navbar redevient publique

## 🔧 Services démarrés

### Services locaux
- **AuthService** : http://localhost:8084 (base: `auth_db` sur 5432)
- **OrderService** : http://localhost:8081 (base: `orders` sur 5432)
- **KitchenService** : http://localhost:8082
- **DeliveryService** : http://localhost:8083
- **Frontend** : http://localhost:4200

### Services Docker 🐳
- **AuthService** : http://localhost:8084 (base: `auth_db` sur 5435)
- **OrderService** : http://localhost:8081 (base: `orders` sur 5432)  
- **KitchenService** : http://localhost:8082 (base: `kitchen` sur 5433)
- **DeliveryService** : http://localhost:8083 (base: `delivery` sur 5434)
- **Frontend** : http://localhost:4200
- **Kafka** : localhost:9092
- **Zookeeper** : localhost:2181

## 🗃️ Structure des bases de données

### auth_db.app_user
- `id` : ID utilisateur (utilisé comme clientId dans les commandes)
- `email` : Email de connexion
- `password` : Mot de passe crypté
- `role` : CLIENT / CHEF / LIVREUR

### orders.orders
- `id` : ID commande
- `client_id` : ID utilisateur (foreign key vers auth_db.app_user.id)
- `items` : Liste des plats commandés
- `status` : Statut de la commande

## 🔐 Test des rôles

### Client (CLIENT)
- Peut : S'inscrire, se connecter, passer des commandes, voir ses commandes
- Ne peut pas : Voir les commandes des autres

### Chef (CHEF)
- Peut : Se connecter, voir toutes les commandes
- Ne peut pas : Passer de commandes

### Livreur (LIVREUR) 
- Peut : Se connecter, voir toutes les commandes
- Ne peut pas : Passer de commandes

## 🛠️ Résolution de problèmes

### ERREUR : Token invalide
- Vérifier que AuthService (port 8084) est démarré
- Vérifier que les clés JWT sont identiques dans AuthService et OrderService

### ERREUR : Commande non créée
- Vérifier les logs du OrderService
- S'assurer que l'utilisateur est bien connecté (role CLIENT)

### ERREUR : Base de données
- Vérifier PostgreSQL sur localhost:5432
- Créer les bases `auth_db` et `orders` si nécessaire 