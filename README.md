# myEPF

Emploi du temps et bulletin pour les élèves de l'EPF école d'ingénieur-e-s.

## Fonctionnement de l'edt
On crée une webview avec laquelle on se connecte à my.epf.fr en JS (obligatoire pour créer les cookies de connexion)

####@branch master

Puis on simule le clic sur "Emploi du temps" (obligatoire pour créer les cookies de mydata.epf.fr)

Et enfin on va chercher l'url magique {hidden} qui retourne un json de la semaine

####@branch new-archi

Depuis le 15 05, on va récupérer le main json sur {hidden} qui envoie tous les cours de l'année, c'est bien plus fiable/stable.

## Fonctionnement du bulletin
on crée les mêmes cookies

puis on se rend sur {hidden}

## Fonctionnement de la photo + nom prénom
on crée les cookies

on se rend sur pegasus, et on parse le html

## Nommage des commits
chaque commit est précédé d'un symbole qui signifie :

\+ un ajout

\- une suppression

\* une modification/fix
