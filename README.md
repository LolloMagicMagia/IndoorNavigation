# ![Logo App](https://github.com/LolloMagicMagia/IndoorNavigation/blob/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png) Context-Aware Indoor Navigation for Android
Design, metodologia e tecnologie utilizzate

## 🎯 Obiettivo del progetto
L’obiettivo principale era realizzare un’app mobile Android in grado di guidare l’utente all’interno di edifici universitari, con percorsi personalizzati e intelligenti, anche in assenza di GPS. L'app è context-aware, cioè capace di adattare i percorsi in base alle esigenze dell’utente (evitare scale, zone affollate o percorsi inagibili) e supportare persone con disabilità o disagi psicologici. Nel nostro caso abbiamo voluto aggiungere il contesto Outdoor insieme all'indoor.

## 🧱 Metodologia progettuale
### 📐 Fasi principali
Analisi dei requisiti: raccolti tramite sondaggi mirati agli studenti (es. difficoltà di orientamento, accettazione della condivisione della posizione).

Studio delle tecnologie: valutazione comparativa di soluzioni di indoor navigation esistenti.

Proposte progettuali: valutazione tra uso di SDK esterni e sviluppo di una soluzione indipendente.

Prototipazione e implementazione: sviluppo di una mappa interattiva con percorsi calcolati automaticamente.

Testing e simulazione: valutazione della precisione e dell’affidabilità dell’applicazione in casi d’uso realistici.

## 🛠️ Tecnologie analizzate e implementate
### 🌐 Servizi di mapping valutati
MapsPeople: SDK professionale basato su Google Maps con tagging semantico, ma a pagamento.

MapBox: molto completo, ma richiede licenze.

MazeMap: soluzione avanzata con indoor 3D, integrabile ma non completamente open.

Navigine: offre un SDK indoor gratuito, incluso supporto a beacon e RFID.

OpenStreetMap (OSM): scelto per la navigazione esterna grazie alla compatibilità con:

OsmDroid per rendering e interazione Android.

JOSM per editing dei piani.

OpenLevelUp per visualizzazione indoor.

Maperitive e MOBAC per generare mappe offline.

## 📌 Tecnologie indoor adottate
Calcolo del percorso: 

<img src="https://github.com/LolloMagicMagia/IndoorNavigation/blob/main/screen/percorso.png" width="250">

Mentre il cambio piano, andrà a togliere il marker dove precedentemente dove l'aula era segnata:

<p align="left">
  <img src="https://github.com/LolloMagicMagia/IndoorNavigation/blob/main/screen/cambioFlor1.png" alt="Prototype 1" width="250" style="margin-right: 10px;">
  <img src="https://github.com/LolloMagicMagia/IndoorNavigation/blob/main/screen/cambioFloor2.png" alt="Prototype 2" width="250" style="margin-right: 10px;">
</p>

Senza il wi-fi la nostra applicazione funzionerà ma andrà a mostrare una mappa non aggiornata.
Questa mappa è stata scaricata tramite un sw esterno chiamato "Maperative", il quale, ci ha permesso di scaricare il tileset riguardante l'università Milano-Bicocca. La funzionalità di ricerca continuerà ad esistere ma il calcolo del percorso automatico esterno non funzionerà più poichè esso funzionava tramite delle call API a OSM stesso.

### 🧭 Navigazione Indoor (No Tool)
Utilizzo di immagini PNG dei piani e implementazione manuale dei percorsi.

Gestione della navigazione tramite un grafo disegnato su mappa con:

Nodi: punti significativi (aule, corridoi, scale).

Archi pesati: rappresentano la distanza o difficoltà.

Calcolo dei percorsi con:

Dijkstra (modificato per terminare al nodo di destinazione).

In alternativa: A* (per la versione in Unity con ambienti 3D).

## 📍 Posizionamento (Position No Tool)
Analisi e simulazione dei principali sistemi di indoor positioning:

Tecnologia	Metodo	Precisione	Note
UWB	Multi-Trilateration	altissima	costosa
Wi-Fi	RSS, ToA, AoA	media	popolare
Magnetometro	Fingerprinting geomagnetico	media	sensibile a disturbi
Bluetooth (beacon)	Trilaterazione RSS	media	richiede hardware
Vision-Based	SLAM, ARKit, Vuforia	variabile (1mm–1.5m)	utile per AR

✨ Affluenza: l’app è in grado di calcolare percorsi alternativi evitando zone affollate, sfruttando simulazione di dati di occupazione ambientale.

## 🧠 Algoritmi e soluzioni implementative
### 📍 Routing no Tool

<img src="https://github.com/LolloMagicMagia/IndoorNavigation/blob/main/screen/novit%C3%A0.png" width="250">

Creazione di un grafo pesato (non orientato) direttamente su immagine.

Uso di classi personalizzate per gestire nodi, archi e pesi.

Gestione di percorsi personalizzati:

Evita scale (per disabilità motorie).

Evita ascensori (per fobie).

Evita zone affollate.

### 🧠 Algoritmi
Dijkstra: usato per calcolare il percorso minimo, modificato per fermarsi al nodo di arrivo.

A*: usato per la versione tridimensionale in Unity (navigazione AR).

Algoritmi di affluenza: per gestire disattivazione dinamica dei nodi in base al carico delle zone.

## 🧩 Architettura finale dell'app
Interfaccia grafica in Android (PhotoView per zoom e interazioni).

Gestione coordinate indipendente dalla risoluzione grazie all’uso di rapporti (x/imgWidth, y/imgHeight).

Visualizzazione dei piani e mappa dinamica.

Integrazione con GPS per mappa esterna tramite OpenStreetMap.

## 🚀 Conclusioni
L'applicazione sviluppata ha dimostrato che è possibile costruire una soluzione completa di navigazione indoor senza dipendere da servizi esterni.

È stata progettata per essere accessibile, modulare e personalizzabile, adattandosi alle esigenze di utenti con diverse abilità.

Lo studio approfondito delle tecnologie e l’implementazione manuale hanno fornito un’esperienza concreta nelle sfide reali della navigazione indoor, un tema ancora oggi in forte espansione.

Un altra valida opzione sarebbe stato l'utilizzo di Navigine il quale permette un' implementazione piu' immediata di tutte quste componentistiche precedentemente descritte.
