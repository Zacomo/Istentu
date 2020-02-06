# Istentapp

Istentapp è un'applicazione android per la gestione dei propri impegni.

[![Platform](https://img.shields.io/badge/Platform-Android-brightgreen)](https://www.android.com/) <br>
[![Languages](https://img.shields.io/badge/Language-Java-blue)](https://www.java.com/)

## Caratteristiche
### Creazione e modifica di un'attività
L'applicazione consente di creare, modificare o eliminare un'attività.
Per attività si intende un'azione che può essere svolta in un breve lasso di tempo (in un giorno al massimo). <br>
Ogni attività:
- ha una priorità che va da 1 a 5
- appartiene a una classe (tra quelle di default o tra quelle create dall'utente)
- ha una data di scadenza
- ha uno stato tra tre possibili (in attesa, in corso, completata)

Inoltre le attività possono essere:
- ordinate in base al nome, alla priorità e alla data di scadenza
- filtrate in base alla classe a cui appartengono o al loro stato (completate o non completate)
### Notifiche
L'app manda una notifica all'utente quando un'attività ha raggiunto la sua scadenza. Dalla notifica è possibile
- rinviare un'attività selezionando una data e un'ora futura
- segnare l'attività come "in corso", nel caso in cui l'utente stia iniziando a lavorarci

E' presente inoltre un filtro per le notifiche, in modo da ricevere le notifiche solo per attività che hanno una priorità da un certo valore in su
(per esempio è possibile attivare le notifiche per attività con priorità maggiore o uguale a 2).
### Grafici di utilizzo
Sono presenti alcuni grafici che mostrano dei dati inerenti alle attività inserite nell'app:
- grafico sul numero di attività inserite per priorità
- grafico sul numero di attività per stato

### Lingue
L'app al momento supporta le seguenti lingue:
- Italiano
- Inglese (default)
