# BeProDuckTive

BeProDuckTive è un'applicazione di gestione dei task (TO-DO List) sviluppata in Kotlin, seguendo il paradigma Model-View-ViewModel (MVVM). Il progetto utilizza Dagger Hilt per la Dependency Injection, e mira a seguire le best practices di programmazione Android e ad utilizzare tecnologie moderne per offrire un'esperienza utente ottimale.

## Caratteristiche

L'applicazione consente agli utenti di gestire i propri task quotidiani in modo efficiente. Include le seguenti funzionalità:

- **Firebase Authentication**: Gestione della creazione e dell'autenticazione degli utenti tramite Firebase.
- **Room Persistence Library**: Gestione dei task localmente attraverso la libreria Room.
- **Schermate Principali**: Task, Daily View Task, Progetti, Timer, Grafici.
- **Foreground Service**: Permette al timer di funzionare in background.
- **Libreria MPAndroid chart**: Utilizzata per creare grafici basati sul completamento dei task.
- **Menu Drawer**: Permette di navigare tra le varie schermate e di effettuare il logout.
- **Ricerca e Ordinamento dei Task**: Possibilità di cercare i task per nome e di ordinare i task secondo diverse opzioni.

## Schermate

1. **Schermata dei Task**: Visualizza i task legati a specifici progetti. L'utente può vedere i task associati a ogni progetto.
2. **Schermata Daily View Task**: Mostra il giorno attuale e i giorni successivi. Cliccando su un determinato giorno, si possono visualizzare i task con scadenza in quel giorno.
3. **Schermata dei Progetti**: Permette di visualizzare i progetti esistenti e di aggiungerne di nuovi.
4. **Schermata Timer**: Ogni task ha un'icona dedicata che, una volta cliccata, avvia un pomodoro timer. Viene mostrata una notifica con il tempo rimanente, e cliccandoci sopra, si torna alla schermata del timer relativa al task selezionato.
5. **Schermata Grafici**: Visualizza grafici basati sul completamento dei task odierni e degli ultimi 7 giorni.
