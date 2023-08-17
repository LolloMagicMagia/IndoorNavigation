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

![Percorso](https://github.com/LolloMagicMagia/IndoorNavigation/blob/main/screen-cartella/destinazione%20edificio.jpg | larghezza=300)

Per quanto riguarda il cambio piano:

![Change Floor](https://github.com/LolloMagicMagia/IndoorNavigation/blob/main/screen-cartella/ChangePiano.jpg)

### Indoor navigation

La soluzione adottata (lato indoor) non presenta l'utilizzo di tool di terze parti per la navigazione e il tracciamento del percorso.

La mappa dell'edificio è stata importata come un png, successivamente sono stati selezionati i punti di interesse sulla mappa, come stanze e corridoi, rappresentandoli inizialmente sotto forma di coordinate.

Questi punti saranno poi i nodi che, assieme alle relazioni tra questi (gli archi), andranno a comporre il grafo che sarà utile alla navigazione.

Ora sono dunque presenti: la mappa, i punti di interesse e un grafo che consente di organizzare questi punti in una struttura che siamo in grado di manipolare. L'ultima cosa essenziale che rimane è il sistema di tracciamento effettivo delle linee sulla mappa che andranno a comporre il percorso vero e proprio.
Queste linee, che in realtà saranno poi rappresentate come un'unica polilinea, sono state tracciate principalmente tramite l'ausilio di Bitmap, Canvas e Paint.


