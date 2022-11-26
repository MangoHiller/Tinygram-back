# Tinygram InstaGreen

_Un backend d'API scalable basé sur Google Datastore et Google App Engine__

[Frontend disponible ici (repo séparé)](https://github.com/MangoHiller/Tinygram-front)

Demo disponible ici:

- [Front](https://front-dot-tinygram-2022-2023.ew.r.appspot.com/)
- [Direct API access](https://tinygram-2022-2023.ew.r.appspot.com/)
- [API Endpoints portal]()

# Installation

## Installer

### Conditions préalables

| Logiciel                 | version     |
| ------------------------ | ----------- |
| Java                     | 11.0        |
| Maven                    | 3.8         |
| Google Cloud             | SDK 363.0.0 |
| app-engine-java          | 1.9.91      |
| app-engine-python        | 1.9.96      |
| bq                       | 2.0.71      |
| cloud-datastore-emulator | 2.1.0       |
| core                     | 2021.10.29  |
| gsutil                   | 5.4         |

### Compte de service

Ajoutez les autorisations suivantes à un compte de service personnalisé que vous avez créé + votre compte de service App Engine :

- App Engine Admin
- Service Account Token Creator
- Storage Admin
- Storage Object Admin
- Storage Object Creator
- Storage Object Viewer
- Storage Transfer Viewer

Une fois le compte créé, créez une paire de clés et téléchargez-la sous forme de fichier JSON placé à la racine de ce dossier

### OAuth2

Configurez l'écran de consentement OAuth2, ajoutez votre e-mail en tant qu'utilisateur test.
Créez ensuite une paire d'identifiants Oauth2 avec les URI suivants :

- https://localhost
- http://localhost
- https://localhost:3000
- http://localhost:3000
- https://front-dot-{{YOUR_PROJECT_ID}}.ew.r.appspot.com
- http://front-dot-{{YOUR_PROJECT_ID}}.ew.r.appspot.com

Ajoutez votre ID client aux endroits suivants :

- `openapi.yaml` à la place de `x-google-audience`
- `src/main/java/com/tinyinsta/common/Constants.java` à la place de `WEB_CLIENT_ID`

### Cloud storage Bucket et Configuration CORS 

```bash
printf '[
  {
    "maxAgeSeconds": 60,
    "method": [
      "GET",
      "HEAD",
      "DELETE",
      "POST",
      "PUT"
    ],
    "origin": [
      "http://localhost:3000",
      "http://localhost",
      "https://localhost:3000",
      "https://localhost",
      "localhost",
      "localhost:3000",
      "http://front-dot-{{YOUR_PROJECT_ID}}.oa.r.appspot.com",
      "https://front-dot-{{YOUR_PROJECT_ID}}.oa.r.appspot.com"
    ],
    "responseHeader": [
      "Content-Type",
      "Access-Control-Allow-Origin",
      "x-goog-resumable"
    ]
  }
]' > cors.json

gsutil cors set cors.json gs://your_bucket_id
```

### Télécharger

```bash
git clone MangoHiller:instars-back
```

## Exécuter localement

### Définir les environment credentials

```bash
# Windows:
set GOOGLE_APPLICATION_CREDENTIALS=/ABSOLUTE/PATH/TO/name_of_your_service_account_priv_key.json

# Mac / Linux
export GOOGLE_APPLICATION_CREDENTIALS=/ABSOLUTE/PATH/TO/name_of_your_service_account_priv_key.json
```

### Démarrer le serveur de développement

```bash
mvn clean appengine:run # démarre le serveur sur le port 8080
```

- Le serveur Web est accessible à [localhost:8080](http://localhost:8080)

- L'explorateur d'API est situé à [localhost:8080/\_ah/api/explorer](http://localhost:8080/_ah/api/explorer)

## Déployer sur Google App Engine

### Créer et déployer la version sur Google App Engine

```bash
mvn clean appengine:deploy
```

### Générez le fichier OpenAPI pour générer le portail Google Endpoints

```bash
mvn endpoints-framework:openApiDocs
gcloud endpoints services deploy target/openapi-docs/openapi.json
```

# Notre approche pour faire scale un clone d'Instagram avec Google App Engine et Google Datastore

## Objectifs

- Les requêtes doivent être réactives (moins de 500 ms de latence)
- La complexité des requêtes doit être proportionnelle à la taille des résultats 
- L'application doit scale et prendre en charge autant que possible les conflits et les demandes simultanées

## Kinds

La plupart des propriétés des kinds s'expliquent d'elles-mêmes. Le seul qui pourrait être un peu complexe et nécessite quelques explications est "batchIndex".

Nous utilisons le batchIndex pour optimiser nos calculs de likes et de followers, cet index nous permet de savoir quel batch de UserFollower ou PostLiker est plein ou non. Sur cette base et connaissant la taille de nos batchs, nous pouvons facilement déterminer le nombre de followers / likes des entités respectives sans avoir à parcourir chaque batch.

On peut aussi obtenir rapidement un batch aléatoire qui n'est pas complet pour ajouter de nouveaux likes / nouveaux followers.

Nous pouvons créer dynamiquement de nouveaux batchs en fonction de la contention tout en gardant toujours un minimum d'un nombre donné de "batchs disponibles" (=batchs non complets).

### Post

![Kind Post](https://cdn.discordapp.com/attachments/1019963777740963961/1045844593763176458/post.PNG)

### User

![Kind User](https://cdn.discordapp.com/attachments/1019963777740963961/1045844594094510120/User.jpg)

### UserFollower

![Kind UserFollower](https://cdn.discordapp.com/attachments/1019963777740963961/1045844594325192744/userfollower.jpg)


## Téléchargement dans le Cloud storage

Nous avons utilisé les [recommandations Google](https://cloud.google.com/blog/products/storage-data-transfer/uploading-images-directly-to-cloud-storage-by-using-signed-url) sur la configuration des téléchargements via une URL signée. Le téléchargement suit le flux suivant :

![Upload flow](https://cdn.discordapp.com/attachments/458197576676671488/914133817135071252/upload_flow_diagram.png)
_Diagram generated with [diagram.codes](https://www.diagram.codes/)_

### Résultats sur l'appengine déployé

![post a picture performance](https://cdn.discordapp.com/attachments/1019963777740963961/1045809381918380185/image.png)

## Postez une photo en faisant varier le nombre d'abonnés

- Les tests ont été exécutés sur l'appengine déployé.
- Le nombre de followers variait entre (10, 100 et 500 followers)
- Les résultats sont basés sur une moyenne de 30 requêtes.

### Résultats sur l'appengine déployé

![post a picture performance](https://cdn.discordapp.com/attachments/1019963777740963961/1045816194608939008/image.png)

- Le temps total moyen oscille autour de 500ms
- La requête "Poster" est devenue beaucoup plus chronophage

# Contributors

|                                                    |                  |
| -------------------------------------------------- | ---------------- |
| [@MangoHiller](https://github.com/MangoHiller)     | Hugo LEGUILLIER  |
| [@miranovic](https://github.com/miranovic)         | Imran NAAR       |
