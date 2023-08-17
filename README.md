![Logo GitHub](https://github.com/username/repository/blob/main/images/github-logo.png)


 # IndoorNavigation
Durante il nostro periodo di stage abbiamo cercato diverse tecnologie utilizzabili in un contesto android, occupandoci di 4 principali obbiettivi:

-Orientamento: calcolo del percorso.

-Localizzaizone: posizionamento all'interno di un edificio.

-Personalizzazione: per quanto riguarda il cambio di percorso dovute a delle specifiche per quanto riguarda l'utente.

-Mappatura: la creazione/aggiornamento di mappe.


## Tesi
Per quanto riguarda lo studio delle diverse tecnologie e delle diverse soluzioni invito a leggere la Tesi(Tesi.pdf) nella pagina principale del progetto github.

### Outdoor navigation

In questa applicazione abbiamo usato OpenStreetMap con l'aiuto di Josm per andare ad aggiungere le varie strade che google maps non andava a rappresentare.
Abbiamo creato una nuova activity per poter creare la navigazione interna non essendo direttamente implementato in OpenStreetMap.
Ho aggiunto molte funzionalità simili a google maps per quanto riguarda il routing esterno che avviene in automatico.

### Indoor navigation

La soluzione adottata (lato indoor) non presenta l'utilizzo di tool di terze parti per la navigazione e il tracciamento del percorso.

La mappa dell'edificio è stata importata come un png, successivamente sono stati selezionati i punti di interesse sulla mappa, come stanze e corridoi, rappresentandoli inizialmente sotto forma di coordinate.

Questi punti saranno poi i nodi che, assieme alle relazioni tra questi (gli archi), andranno a comporre il grafo che sarà utile alla navigazione.

Ora sono dunque presenti: la mappa, i punti di interesse e un grafo che consente di organizzare questi punti in una struttura che siamo in grado di manipolare. L'ultima cosa essenziale che rimane è il sistema di tracciamento effettivo delle linee sulla mappa che andranno a comporre il percorso vero e proprio.
Queste linee, che in realtà saranno poi rappresentate come un'unica polilinea, sono state tracciate principalmente tramite l'ausilio di Bitmap, Canvas e Paint.


