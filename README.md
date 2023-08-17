![Logo App](https://github.com/LolloMagicMagia/IndoorNavigation/blob/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)


 # IndoorNavigation
Durante il nostro periodo di stage abbiamo cercato diverse tecnologie utilizzabili in un contesto android, occupandoci di 4 principali obbiettivi:

-Orientamento: calcolo del percorso.

-Localizzaizone: posizionamento all'interno di un edificio.

-Personalizzazione: per quanto riguarda il cambio di percorso dovute a delle specifiche per quanto riguarda l'utente.

-Mappatura: la creazione/aggiornamento di mappe.


## Tesi
Per quanto riguarda lo studio delle diverse tecnologie e delle diverse soluzioni invito a leggere la Tesi(Tesi.pdf) nella pagina principale del progetto github.

### Outdoor navigation

Per quanto riguarda l'outdoor andremo a utilizzare il servizio offerto da OpenStreetMap(Mappa mondiale gratuita dove sarà possibile la modifica), e grazie a Osmdroid (libreria) potremo mostrarla all'interno della nostra applicazione android. 

Tramite una barra di ricerca potremo andare a cercare il path tra edifici e aule, andando a mostrare il routing esterno e la posizione dell'aula all'interno dell'edificio.(con la possibilità di cambiare piano)

Per quanto riguarda il calcolo del percorso:

<img src="https://github.com/LolloMagicMagia/IndoorNavigation/blob/main/screen/percorso.png" width="320">

Per quanto riguarda il cambio piano:

<img src="https://github.com/LolloMagicMagia/IndoorNavigation/blob/main/screen/cambioFlor1.png" width="320">

Come possiamo vedere il cambio piano andrà a togliere il marker dove prima era segnata l'aula.

<img src="https://github.com/LolloMagicMagia/IndoorNavigation/blob/main/screen/cambioFloor2.png" width="320">

Senza il wi-fi la nostra applicazione funzionerà ma andrà a mostrare una mappa non aggiornata.
Questa mappa è stata scaricata tramite un sw esterno chiamato "Maperative", il quale, ci ha permesso di scaricare il tileset riguardante l'università Milano-Bicocca. La funzionalità di ricerca continuerà ad esistere ma il calcolo del percorso automatico esterno non funzionerà più poichè esso funzionava tramite delle call API a OSM stesso.

### Indoor navigation

La soluzione adottata (lato indoor) non presenta l'utilizzo di tool di terze parti per la navigazione e il tracciamento del percorso.

La mappa dell'edificio è stata importata come un png, successivamente sono stati selezionati i punti di interesse sulla mappa, come stanze e corridoi, rappresentandoli inizialmente sotto forma di coordinate.

Questi punti saranno poi i nodi che, assieme alle relazioni tra questi (gli archi), andranno a comporre il grafo che sarà utile alla navigazione.

Ora sono dunque presenti: la mappa, i punti di interesse e un grafo che consente di organizzare questi punti in una struttura che siamo in grado di manipolare. L'ultima cosa essenziale che rimane è il sistema di tracciamento effettivo delle linee sulla mappa che andranno a comporre il percorso vero e proprio.
Queste linee, che in realtà saranno poi rappresentate come un'unica polilinea, sono state tracciate principalmente tramite l'ausilio di Bitmap, Canvas e Paint.

I punti di interesse del grafo avranno degli attributi così che il percorso sia personalizzato per i diversi utenti, poichè esistono dei percorsi che possono non essere adatti alle diverse problematiche. 

Come possiamo vedere in questo fragment oltre alle diverse informazioni che può darci, avrà anche il compito di andare a segnalare i parametri che l'utente vuole che si prendano in considerazione non solo per quanto riguarda l'indoor ma anche per quanto riguarda l'outdoor:
<img src="https://github.com/LolloMagicMagia/IndoorNavigation/blob/main/screen/novit%C3%A0.png" width="320">

## Conclusioni

Tutto ciò di cui abbiamo descritto in questi capitoli sono dei riassunti riguardante il lavoro intrapreso durante lo stage durato 3 mesi.
Per coloro che vogliono effettivamente approfondire l'argomento consigliamo vivamente di leggere la tesi, poichè essa andrà a mostreare tutte le tecnologie studiate e non implementate in questa applicazione.
