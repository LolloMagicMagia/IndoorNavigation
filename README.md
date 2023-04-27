# IndoorNavigation

## Struttura della soluzione adottata

### Outdoor navigation

In questa applicazione abbiamo usato OpenStreetMap con l'aiuto di Josm per andare ad aggiungere le varie strade che google maps non andava a rappresentare.
Abbiamo creato una nuova activity per poter creare la navigazione interna non essendo direttamente implementato in OpenStreetMap.
Ho aggiunto molte funzionalità simili a google maps per quanto riguarda il routing esterno che avviene in automatico.

### Indoor navigation

La soluzione adottata non presenta l'utilizzo di tool di terze parti per la navigazione e il tracciamento del percorso.
L'ambiente di sviluppo è Android Studio.

La mappa dell'edificio è stata importata come un png, successivamente sono stati selezionati i punti di interesse sulla mappa, come stanze e corridoi, rappresentandoli inizialmente sotto forma di coordinate.

Questi punti saranno poi i nodi che, assieme alle relazioni tra questi (gli archi), andranno a comporre il grafo che sarà utile alla navigazione.

Ora sono dunque presenti: la mappa, i punti di interesse e un grafo che consente di organizzare questi punti in una struttura che siamo in grado di manipolare. L'ultima cosa essenziale che rimane è il sistema di tracciamento effettivo delle linee sulla mappa che andranno a comporre il percorso vero e proprio.
Queste linee, che in realtà saranno poi rappresentate come un'unica polilinea, sono state tracciate principalmente tramite l'ausilio di Bitmap, Canvas e Paint. Sarà successivamente spiegato come tracciamo le linee evitando gli ostacoli.

## Da risolvere

vedere se il routing automatico riesce a selezionare i path in base alla condizione dell'utente.

## Idee e implementazioni future e in corso

#### 1. Gestione del cammino in situazioni particolari (in corso)

Tra le specifiche del progetto vi è la gestione del percorso per persone con disabilità e/o con problematiche per cui non si potesse fisicamente percorrere una certa zona dell'ateneo. Tra gli esempi che ci sono stati forniti vi erano:

* persone con disabilità per cui non è possibile percorrere le scale
* persone con fobie per cui è preferibile o è meglio evitare totalmente gli ascensori
* persone con fobie tali che mettono a disagio il soggetto in ambienti molto affollati

Per la gestione di questi percorsi "personalizzati" sarebbe possibile dare agli archi degli attributi che ne identificano il tipo di percorso. Ad esempio un arco che attraversa delle scale potrebbe avere un attributo che specifica la presenza di quest'ultime.
Nell'applicazione quindi potrebbero essere integrati dei sistemi simili a quelli presenti ad esempio in Google Maps per la gestione del percorso senza pedaggi, senza traghetti, ecc. Nello stesso modo potrebbe esserci nell'applicazione un sistema per selezionare quali tipi di "archi" sarebbe meglio evitare o meno.

#### 2. Integrazioni di animazioni più eleborate all'interno della mappa (futura)

Dato che si tratta di un'app per la navigazione, e considerando l'ipotesi di implementare sistemi di tracciamento della posizione all'interno dell'ateneo, sarebbe interessante, dal punto di vista dell'interfaccia e dell'esperienza utente, avere un indicatore che mostri la posizione in tempo reale agli utenti che utilizzano l'applicazione. Inoltre, potrebbe essere interessante creare un'animazione che rappresenti l'utente all'interno del percorso selezionato, mentre questo viene "seguito" man mano che l'utente si sposta lungo il cammino.

#### 3. Integrazione o unione con un sistema per la navigazione outdoor  (completato parzialmente)

Poiché l'ateneo si estende su diversi edifici, è necessario gestire la navigazione tra un edificio e l'altro. Una possibile soluzione da attuare potrebbe essere unire l'approccio descritto in questa relazione con quello basato su Open Street Map, che fornisce servizi di navigazione outdoor. 
Un'altra possibile soluzione potrebbe essere quella di integrare questo tipo di navigazione tramite le API di Google Maps.


#### 4. integrazione o unione con un sistema per la realtà aumentata (futura)
