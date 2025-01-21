# Exploration du domaine : Un DSL inspiré de SQL

## 1. Pourquoi ce domaine est intéressant ?

Le traitement et l’interrogation des bases de données sont au cœur de nombreuses applications modernes. Le langage SQL est une référence incontournable, mais il présente des limitations qui rendent sa manipulation écrasante pour des utilisateurs moins techniques ou qui travaillent dans des cas d'utilisation particuliers (visualisation ou traitement customisé des résultats par exemple).

Un DSL orienté SQL simplifié pourrait offrir :

- **Une syntaxe intuitive à usage restreint**, à la portée d'un plus large public.
- **Des fonctionnalités ciblées** sur les besoins d’affichage, de filtrage et d’agrégation dans des contextes bien définis.
- **Une meilleure intégration à des outils existants**, grâce à l'utilisation de Scala comme langage hôte, et une compatibilité aisée avec des écosystèmes backend modernes (par ex. Spark, JDBC).

En résumé, créer un DSL dans ce domaine permettrait de combiner simplicité et efficacité tout en lissant les lacunes de SQL brut en termes d’expressivité et de complexité de compréhension au premier abord pour les non-experts.

## 2. Problème spécifique résolu par le DSL

Le DSL ciblé résout principalement les problèmes suivants :

- **Simplification Syntaxique** : SQL est riche mais souvent redondant et complexe à appréhender dans des scénarios simples (filtrage basique, calculs agrégés, etc.). Un DSL peut proposer des raccourcis plus intuitifs pour les cas courants.
- **Filtrage Flexible et Lisible** : Création d’une syntaxe qui favorise la lisibilité et limite la manipulation d’erreurs en instaurant des structures fortement typées.
- **Prise en Charge Modulaire** : Un DSL adapté permet l’ajout de couches supplémentaires pour exporter des données vers d'autres systèmes sans changer le cœur logique.
- **Interopérabilité** : Une intégration fluide avec Scala permet à des développeurs familiarisés avec son écosystème d'exploiter la puissance de leur langage tout en évitant une rupture dans leur workflow existant.

## 3. Objectifs et avantages de l'utilisation du DSL

### Objectifs

- Fournir un moyen **expressif, concis et typé** de construire des requêtes sans les complications directes d'une syntaxe SQL complète.
- Favoriser un **apprentissage rapide** de la syntaxe grâce à sa conception modulaire et compréhensible.
- Faciliter l’**interopérabilité** entre requêtes simples et visualisation directe (par exemple : CSV, export JSON, graphiques).

### Avantages attendus

- **Réduction des erreurs syntaxiques**.
- **Adoption accrue** pour des non-initiés ou des utilisateurs occasionnels de requêtes.
- **Compatibilité avec des flux modernes** de traitement ou d'agrégation (élaboration d'API backend en Scala, etc.).

