# IndoorNavigation

In questa applicazione abbiamo usato OpenStreetMap con l'aiuto di Josm per andare ad aggiungere le varie strade che google maps non andava a rappresentare.
Abbiamo creato una nuova activity per poter creare la navigazione interna non essendo direttamente implementato in OpenStreetMap.
Ho aggiunto molte funzionalità simili a google maps per quanto riguarda il routing esterno che avviene in automatico.

##da risolvere

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
