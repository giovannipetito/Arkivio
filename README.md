# Arkivio

# Build

Build, Execution, Deployment - Istant Run - Enable Instant Run... unchecked (da fare su tutte le app)

# lint

lint, o linter, è uno strumento che analizza il codice sorgente per evidenziare errori di programmazione,
bug, errori stilistici e costrutti sospetti. Il termine deriva dall'omonimo strumento Unix che esamina il
codice sorgente del linguaggio C.

Da terminale, esegui: ./gradlew lintDebug

Verrà generato il file lint-baseline.xml che conterrà tutti gli errori, bug e costrutti sospetti del codice.

Nota. Per passare dal terminale di Git al terminale di Android Studio, esegui: ./gradlew

# Push an existing Git repository

cd existing_repo
git remote rename origin old-origin
git remote add origingit@github.com:giovannipetito/Arkivio.git
git push -u origin --all
git push -u origin –tags

# Git

git branch <new_branch> // Crea un nuovo branch a partire dal branch corrente
git checkout <new_branch> // Passa sul nuovo branch

git config --global user.name "giovanni.petito" // Per cambiare username
git config --global user.email "gi.petito@gmail.com" // Per cambiare email

git config --list

git config user.name
giovanni.petito

git config user.email
giovanni.petito@gmail.com

ssh-keygen -t rsa -b 4096 -C "gi.petito@gmail.com"
ssh-keygen -t ed25519 -C "gi.petito@gmail.com"
ssh-keygen -o -t rsa -b 4096 -C gi.petito@gmail.com

# Git Merge

git merge <develop> // esegue il merge di develop sul branch corrente
:wq // Da fare dopo il merge

# Fetch

git fetch // Scarica il branch remoto
git checkout <nome_branch> // Passa sul branch remoto che ora è locale

# Switch da ssh a https e viceversa

git remote -v // Per vedere se sei in ssh o in https

git config --local -e // 1° Modo

git remote set-url origin <https o ssh> // 2° Modo

git remote set-url --push origin <https o ssh> // 3° Modo

4° Modo
premere i per entrare in editing mode, quando cancelli l'url premi ESC, per copiare la nuova url
premi p, quindi per salvare premi SHIFT + “:” e poi wq

Nota. Vai su "Gestione credenziali" per modificare/cancellare le credenziali di Gitlab.

# gitignore

Se Git ignora il file .gitignore:

Prima committa eventuali modifiche del codice in sospeso, quindi esegui:
git rm -r --cached .

Ciò rimuove tutti i file modificati dall'indice (staging area), poi esegui:
git add .
git commit -m ".gitignore is now working"

# Hash

Per generare l'Hash della firma di un'app:

keytool -exportcert -alias AndroidDebugKey -keystore <key_name> | openssl sha1 -binary | openssl base64

# Navigation components

Se vuoi entrare nel fragment B attraverso una push notification, ma l'azione per entrare nel fragment B
definita nel nav_graph è valida solo dal fragment A al fragment B, allora bisogna usare il seguente comando:

findNavController(R.id.nav_host_fragment).navigate(R.id.documentsScFiscaleDetail, it, navOptions.build())

Questa riga di codice innesca nel navigation il nuovo fragment. Se fai back, ripristina lo stato precedente
e quindi ritorni dove eri.