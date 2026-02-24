🎬 Tracker de Films & Séries TV
📌 Description

Tracker de Films & Séries TV est une application bureau développée en JavaFX permettant de gérer une bibliothèque personnelle de contenus audiovisuels.

L'utilisateur peut :

Ajouter, modifier et supprimer des films et séries

Suivre la progression des séries (saisons / épisodes)

Attribuer des notes personnelles

Organiser une watchlist

Filtrer et rechercher des contenus

Visualiser des statistiques

L'application est connectée à une base de données PostgreSQL hébergée sur Neon.

🛠 Technologies utilisées

Java JDK 21+

JavaFX 21+

FXML

PostgreSQL (Neon Cloud)

JDBC

Maven

IntelliJ IDEA

🏗 Architecture du projet

Le projet suit le modèle MVC (Modèle – Vue – Contrôleur) :

FXML (Vue)
   ↓
Controller
   ↓
ApiService
   ↓
ApiClient (Connexion BD)
   ↓
Neon PostgreSQL
📂 Structure du projet
src/
 ├── main/
 │   ├── java/com.example.trackerfilmsseries/
 │   │   ├── MainApp.java
 │   │   ├── controller/
 │   │   ├── model/
 │   │   ├── service/
 │   │   └── util/
 │   └── resources/com/example/trackerfilmsseries/
 │       ├── view/
 │       ├── css/
 │       └── images/
🗄 Structure de la base de données
Table genres

id

nom

Table contenus

id

titre

type (FILM / SERIE)

annee_sortie

realisateur

synopsis

genre_id

statut (A_VOIR / EN_COURS / VU)

note (0 à 5)

watchlist

date_ajout

Table progression_series

contenu_id

saisons_totales

saisons_vues

episodes_totaux

episodes_vus

✨ Fonctionnalités principales
🎬 Gestion des contenus (CRUD)

Ajouter un film ou une série

Modifier un contenu existant

Supprimer un contenu

Affichage dynamique dans un TableView

📺 Suivi de progression

Suivi des saisons et épisodes

Barre de progression dynamique


🔎 Recherche & filtres

Filtrer par genre

Filtrer par statut

Recherche par mot-clé

Rafraîchissement dynamique

⭐ Système de notation

Attribution d’une note de 1 à 5

📌 Watchlist

Marquer un contenu à regarder


🚀 Installation

Cloner le projet

Configurer la connexion Neon dans ApiClient

Exécuter les requêtes SQL de création des tables

Lancer MainApp


👩‍💻 Auteur

Projet réalisé dans le cadre du cours de développement d’applications Java.
