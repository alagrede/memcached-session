# memcached-session
External session storage with memcached for Spring application

# Objectif
Ce projet permet de facilement externaliser une session depuis un Tomcat ou Jboss vers un serveur ou cluster de serveur memcached. Entièrement production ready, cette lib remplace avantageusement le mécanisme replication session de Jboss ou bien Hazelcache car plus configurable.  

Cette librairie est:
* __Complétement indépendante__ du serveur d'application
* Facilement adaptable
* Scalable
* __Débrayable__ à chaud

## Pourquoi memcached
(serveur de stockage des sessions)
* Rapide (en RAM)
* Peut se monter en cluster (scalable)
* Simple à installer et maintenir
* Administrable
* Eprouvé

## Spymemcached
(client memcached java )
* Compresse les données
* Résiste aux pannes serveurs et réseaux
  * Répartition automatique de la charge
  * Reconnexion automatique aux serveurs perdus
  * Implémente des algorithmes éprouvés de failover

* Possède de nombreuses optimisations pour gérer un débit très élevé
  * Peut effectuer les opérations en asynchrone
  * Multiplex les opérations 

# Fonctionnement
Possibilité de mettre en place un cluster de memcached grâce à un algorithme de "Consistent Hashing"
* Les serveurs memcached sont disposés sur un cercle
* Les données de session sont stockées sur le memcached le plus proche sur le cercle
![Image of Yaktocat](http://lagrede.alwaysdata.net/site_media/github/memcached/consistent_hashing_2.png)

## Création d'une session
![Image of Yaktocat](http://lagrede.alwaysdata.net/site_media/github/memcached/creation_session.png)

## Récupération d'une session
![Image of Yaktocat](http://lagrede.alwaysdata.net/site_media/github/memcached/fail_1.png)


* Sauvegarde de la session uniquement lors d’un changement
* Exclure des backups les données inutiles
  * ex: les compteurs de notifications
* Alléger au maximum la session
* Garder moins longtemps les sessions inactives

* Conservation du mécanisme de « sticky tag »
  * Permet de ne récupérer la session dans memcached qu’en cas de bascule Jboss
