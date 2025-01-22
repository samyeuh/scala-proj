# Projet DSL inspiré de SQL

## 📖 Présentation du projet

Ce projet consiste à développer un **DSL interne en Scala 3**, inspiré de SQL, qui simplifie l’interrogation et la manipulation de bases de données. Il s'adresse à des utilisateurs souhaitant exécuter des requêtes sans la complexité habituelle du langage SQL complet.  Ce DSL sera apprécié des novices comme des plus expérimentés pour sa simplicité d'exécution.

### 🤔 Pourquoi ce domaine est intéressant ?

Le traitement et l’interrogation des bases de données sont au cœur de nombreuses applications modernes. Le langage SQL est une référence incontournable, mais il présente des limitations qui rendent sa manipulation écrasante pour des utilisateurs moins techniques ou qui travaillent dans des cas d'utilisation particuliers (visualisation ou traitement customisé des résultats par exemple).

Un DSL orienté SQL simplifié pourrait offrir :

- **Une syntaxe intuitive à usage restreint**, à la portée d'un plus large public.
- **Des fonctionnalités ciblées** sur les besoins d’affichage, de filtrage et d’agrégation dans des contextes bien définis.
- **Une meilleure intégration à des outils existants**, grâce à l'utilisation de Scala comme langage hôte, et une compatibilité aisée avec des écosystèmes backend modernes (par ex. Spark, JDBC).

En résumé, créer un DSL dans ce domaine permettrait de combiner simplicité et efficacité tout en atténuant la vison de SQL  en termes d’expressivité et de complexité de compréhension au premier abord pour les non-experts.


### 🎯 Objectif principal

Offrir une syntaxe claire, intuitive et typée pour effectuer des requêtes, tout en intégrant Des fonctionnalités intéressantes et modernes.


#### 🔬 Problème spécifique résolu par le DSL

Le DSL ciblé résout principalement les problèmes suivants :

- **Simplification Syntaxique** : SQL est riche mais souvent redondant et complexe à appréhender dans des scénarios simples (filtrage basique, calculs agrégés, etc.). Un DSL peut proposer des raccourcis plus intuitifs pour les cas courants.
- **Filtrage Flexible et Lisible** : Création d’une syntaxe qui favorise la lisibilité et limite la manipulation d’erreurs en instaurant des structures fortement typées.
- **Prise en Charge Modulaire** : Un DSL adapté permet l’ajout de couches supplémentaires pour exporter des données vers d'autres systèmes sans changer le cœur logique.
- **Interopérabilité** : Une intégration fluide avec Scala permet à des développeurs familiarisés avec son écosystème d'exploiter la puissance de leur langage tout en évitant une rupture dans leur workflow existant.

#### ⚡ Objectifs et avantages de l'utilisation du DSL

- Fournir un moyen **expressif, concis et typé** de construire des requêtes sans les complications directes d'une syntaxe SQL complète.
- Favoriser un **apprentissage rapide** de la syntaxe grâce à sa conception modulaire et compréhensible.
- Faciliter l’**interopérabilité** entre requêtes simples et visualisation directe (par exemple : CSV, ( export JSON, graphiques en futur évolution)).


- **Réduction des erreurs syntaxiques**.
- **Adoption accrue** pour des non-initiés ou des utilisateurs occasionnels de requêtes.
- **Compatibilité avec des flux modernes** de traitement ou d'agrégation (élaboration d'API backend en Scala, etc.).


### 🚀 Fonctionnalités principales

- Créer une table (`create`)  
- Insérer des données (`add`)  
- Sélectionner des colonnes (`select`)  
- Appliquer des filtres (`filter`)
- Trier les résultats (`orderBy`)
- Limiter le nombre de résultats (`limit`) 

---

## 💡 Décisions de conception

### 1. Pourquoi un DSL ?
- **Limitation de SQL classique :** Syntaxe complexe et difficilement lisible pour les non-initiés.  
- **Simplicité et expressivité :** Un DSL permet une syntaxe naturelle et une intégration fluide avec l’écosystème Scala. Dans l'objectif de le rendre encore plus accessible. 
- **Typage sûr :** Réduction des erreurs grâce à des constructions fortement typées.

### 2. Principes suivis
- **Programmation fonctionnelle :**
  - Utilisation d'objets immuables pour représenter les requêtes.
  - Constructions fortement typées pour valider les opérations au moment de la compilation.  
- **Modularité :**
  - Un design modulaire pour permettre des extensions futures, telles que des plugins pour les formats des fichiers etudiés supplémentaires.  
- **Caractéristiques de Scala 3 :**
  - **Métodes d’extension** : Simplifient l’écriture d’une syntaxe claire.
  - **Enums** : Représentation des types SQL standard (comme `Int`, `String`).

---

## 🛠️ Instructions d’installation et d’exécution

### Pré-requis
- **Scala 3** installé  
- Un environnement configuré pour exécuter des projets Scala (exemple : IntelliJ IDEA ou sbt)

### Étapes pour exécuter le projet
1. **Clonez le dépôt :**
   ```bash
   git clone <lien-du-repo>
   cd <nom-du-repo>
   ```
2. **Compiler le projet: **
   ```bash
     sbt compile
   ```
3. **Lancer le projet: **
   ```bash
    sbt run
   ```
### Pour apprendre à utiliser des démonstrations de commande, sont disponibles via  la commande help
---

## ✅ Tests

Les tests couvrent :  
- Les cas courants, comme les sélections, filtres, et tri puis biensur la création, l'insertion.  
- Les cas limites, comme des colonnes ou conditions inexistantes, l'existance des tables et attribut avant d'agir.  

Les tests sont écrits avec pour garantir :  
1. La validité des fonctionnalités principales.  
2. La robustesse de la syntaxe.



---

## ✍️ Auteurs

- **Cyril TAKAM, Samy BOUHAMIDI, Dgili, Enzo SISINNI**   
- [Lien vers le projet]  
