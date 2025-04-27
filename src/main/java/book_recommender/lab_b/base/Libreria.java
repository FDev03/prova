/**
 * Classe per la gestione delle librerie personali e dei relativi libri.
 * 
 * @author Alessio     	Gervasini 		Mat. 756181
 * @author Francesco 	Orsini Pio		Mat. 756954
 * @author Luca      	Borin        	Mat. 756563

 
 */
package book_recommender.lab_b.base;
import book_recommender.lab_b.nonservira.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;



public class Libreria {
         // Definizione dei codici ANSI per i colori
         public static final String RESET = "\033[0m";  // Resetta il colore
         public static final String ROSSO = "\033[0;31m";
         public static final String VERDE = "\033[0;32m";
   
         public static final String V = ""; // Carattere Unicode per il segno di spunta
         public static final String X = "\u2717 "; // Carattere Unicode per il segno di croce
    public String nomeLibreria;
    public List<String> libri;

    /**
     * Costruttore per creare una nuova libreria.
     *
     * @param nomeLibreria Il nome della libreria.
     */
    public Libreria(String nomeLibreria) {
        this.nomeLibreria = nomeLibreria;
        this.libri = new ArrayList<>();
    }

    /**
     * Aggiunge un libro alla libreria e la ordina.
     *
     * @param libro Il titolo del libro da aggiungere.
     * @return true se il libro è stato aggiunto, false se era già presente.
     */
    public boolean aggiungiLibro(String libro) {
        if (libri.contains(libro)) {
            return false; // Il libro è già presente nella libreria
        } else {
            libri.add(libro);
            bucketSort(libri); // Ordina la libreria dopo l'aggiunta del nuovo libro
            return true; // Il libro è stato aggiunto con successo
        }
    }

    /**
     * Ottiene il nome della libreria.
     *
     * @return Il nome della libreria.
     */
    public String getNomeLibreria() {
        return nomeLibreria;
    }

    /**
     * Ottiene la lista dei libri nella libreria.
     *
     * @return La lista dei libri.
     */
    public List<String> getLibri() {
        return libri;
    }

    /**
     * Registra una nuova libreria per un utente in un file CSV.
     *
     * @param utente   L'ID dell'utente.
     * @param libreria L'oggetto Libreria da registrare.
     */
    public static void registraLibreria(String utente, Libreria libreria) {
        String filename = Main.librerie_path;
        File file = new File(filename);
        boolean fileExists = file.exists();

        List<String> headers = new ArrayList<>();
        List<String> data = new ArrayList<>();
        StringBuilder caratteristicheLibreria = new StringBuilder();

        if (fileExists) {
            // Leggi le intestazioni e i dati esistenti
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line = reader.readLine();
                if (line != null) {
                    headers = new ArrayList<>(Arrays.asList(line.split("\t")));
                }

                while ((line = reader.readLine()) != null) {
                    data.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Se il file non esiste, crea le intestazioni di base
            headers.add("UserID");
            headers.add("Libreria");
        }

        // Aggiungi nuove colonne per i libri
        for (String book : libreria.getLibri()) {
            for (int bookIndex = 1; bookIndex <= 10; bookIndex++) {
                String bookHeader = "Libro" + bookIndex;
                if (!headers.contains(bookHeader)) {
                    headers.add(bookHeader);
                }
            }

            caratteristicheLibreria.append(book).append("\t");
        }

        // Rimuovi l'ultima tabulazione
        if (caratteristicheLibreria.length() > 0) {
            caratteristicheLibreria.setLength(caratteristicheLibreria.length() - 1);
        }

        // Scrivi l'intestazione e i dati aggiornati nel file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(String.join("\t", headers) + "\n");

            // Riscrivi i dati esistenti
            for (String rowData : data) {
                writer.write(rowData + "\n");
            }

            // Aggiungi la nuova riga con i dati della libreria
            String[] rowData = new String[headers.size()];
            rowData[0] = utente;
            rowData[1] = libreria.getNomeLibreria();
            String[] libri = caratteristicheLibreria.toString().split("\t");
            for (int i = 0; i < libri.length; i++) {
                rowData[2 + i] = libri[i].trim();
            }

            writer.write(String.join("\t", rowData) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Aggiunge un libro a una libreria esistente di un utente.
     *
     * @param userid       L'ID dell'utente.
     * @param nomeLibreria Il nome della libreria.
     * @param libro        Il titolo del libro da aggiungere.
     */
    public static void aggiungiLibroALibreria(String userid, String nomeLibreria, String libro) {
        List<String[]> librerie = leggiFileCsv(Main.librerie_path);
        List<String[]> nuoveLibrerie = new ArrayList<>();

        boolean libreriaTrovata = false;
        for (String[] libreria : librerie) {
            if (libreria.length >= 3 && libreria[0].equals(userid) && libreria[1].equals(nomeLibreria)) {
                String[] libri = libreria[2].split(",");
                List<String> listaLibri = new ArrayList<>(Arrays.asList(libri));
                if (!listaLibri.contains(libro)) {
                    listaLibri.add(libro);
                    bucketSort(listaLibri);
                    libreria[2] = String.join("\t", listaLibri);
                } else {
                    return; // Il libro è già presente nella libreria
                }
                libreriaTrovata = true;
            }
            nuoveLibrerie.add(libreria);
        }

        if (!libreriaTrovata) {
            String[] nuovaLibreria = { userid, nomeLibreria, libro };
            nuoveLibrerie.add(nuovaLibreria);
        }

        // Scrive le librerie aggiornate nel file CSV
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Main.librerie_path))) {
            for (String[] libreria : nuoveLibrerie) {
                writer.write(String.join("\t", libreria) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifica se un libro esiste nel file Data.csv.
     *
     * @param titolo Il titolo del libro da cercare.
     * @return true se il libro esiste, false altrimenti.
     */
    public static boolean libroEsisteInDataCsv(String titolo) {
        List<String[]> libri = leggiFileCsv2(Main.FILE_PATH);
        for (String[] datiLibro : libri) {
            if (datiLibro.length == 5 && datiLibro[0].equalsIgnoreCase(titolo)) {
                return true; // Il libro esiste nel file Data.csv
            }
        }
        return false; // Il libro non esiste nel file Data.csv
    }

    /**
     * Visualizza le librerie di un utente con i relativi libri.
     *
     * @param userid L'ID dell'utente.
     * @return Una stringa con i dettagli delle librerie e dei libri.
     */
    public static String visualizzaLibrerieConLibri(String userid) {
        StringBuilder risultato = new StringBuilder();
        Map<String, List<String>> librerieUtente = new LinkedHashMap<>();

        // Legge le librerie dal file CSV
        try (BufferedReader br = new BufferedReader(new FileReader(Main.librerie_path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] campi = line.split("\t");
                if (campi.length >= 2 && campi[0].equals(userid)) {
                    String nomeLibreria = campi[1];
                    List<String> libri = new ArrayList<>();
                    if (campi.length > 2) {
                        for (int i = 2; i < campi.length; i++) {
                            if (!campi[i].trim().equalsIgnoreCase("null") && !campi[i].trim().isEmpty()) {
                                libri.add(campi[i].trim());
                            }
                        }
                    }
                       
                    
                    librerieUtente.put(nomeLibreria, libri);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (librerieUtente.isEmpty()) {
            return "";
        }

        // Formatta le librerie e i libri per la visualizzazione
        int indexLibreria = 1;
        for (Map.Entry<String, List<String>> entry : librerieUtente.entrySet()) {
            risultato.append(indexLibreria).append(". ").append(entry.getKey()).append("\n");
            List<String> libri = entry.getValue();
            int indexLibro = 1;
            for (String libro : libri) {
                int indiceLibroNelCsv = getIndiceLibroNelCsv(userid, entry.getKey(), libro);
                if (indiceLibroNelCsv != -1) {
                    risultato.append("  ").append(indiceLibroNelCsv).append(". ").append(libro.trim()).append("\n");
                } else {
                    risultato.append("  ").append(indexLibro).append(". ").append(libro.trim()).append("\n");
                }
                indexLibro++;
            }
            indexLibreria++;
            }   
            System.out.print("\n"+VERDE+ V+"Ecco le tue librerie"+RESET+"\n");
             System.out.println("\n"+risultato.toString()+"\n");
        
            return risultato.toString();
        }
        
        /**
         * Ottiene l'indice di un libro nel file CSV.
         *
         * @param userid        L'ID dell'utente.
         * @param nomeLibreria  Il nome della libreria.
         * @param libroCercato  Il titolo del libro da cercare.
         * @return L'indice del libro, o -1 se non trovato.
         */
        private static int getIndiceLibroNelCsv(String userid, String nomeLibreria, String libroCercato) {
            List<String[]> librerie = leggiFileCsv(Main.librerie_path);
            @SuppressWarnings("unused")
            int indice = 1;
            for (String[] libreria : librerie) {
                if (libreria.length >= 3 && libreria[0].equals(userid) && libreria[1].equals(nomeLibreria)) {
                    for (int i = 2; i < libreria.length; i++) {
                        if (libreria[i].trim().equalsIgnoreCase(libroCercato.trim())) {
                            return i - 1; // Restituisce l'indice del libro, partendo da 1
                        }
                    }
                }
            }
            return -1; // Se non trova il libro, restituisce -1
        }
        
        /**
         * Ottiene il nome di una libreria per indice.
         *
         * @param userid L'ID dell'utente.
         * @param index  L'indice della libreria.
         * @return Il nome della libreria, o null se non trovato.
         */
        public static String getLibreriaByIndex(String userid, int index) {
            Map<String, List<String>> librerieUtente = new LinkedHashMap<>();
        
            // Legge le librerie dal file CSV
            try (BufferedReader br = new BufferedReader(new FileReader(Main.librerie_path))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] campi = line.split("\t", 3);
                    if (campi.length >= 2 && campi[0].equals(userid)) {
                        String nomeLibreria = campi[1];
                        List<String> libri = new ArrayList<>();
                        if (campi.length == 3) {
                            libri = Arrays.asList(campi[2].split("\t"));
                        }
                        librerieUtente.put(nomeLibreria, libri);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        
            int currentIndex = 1;
            for (String nomeLibreria : librerieUtente.keySet()) {
                if (currentIndex == index) {
                    return nomeLibreria;
                }
                currentIndex++;
            }
        
            return null;
        }
        
        /**
         * Ottiene un libro per indice nella libreria di un utente.
         *
         * @param userid       L'ID dell'utente.
         * @param nomeLibreria Il nome della libreria.
         * @param index        L'indice del libro.
         * @return Il titolo del libro, o null se non trovato.
         */
        public static String getLibroByIndex(String userid, String nomeLibreria, int index) {
            List<String[]> librerie = leggiFileCsv(Main.librerie_path);
        
            for (String[] libreria : librerie) {
                if (libreria.length >= 3 && libreria[0].equals(userid) && libreria[1].equals(nomeLibreria)) {
                    // Recupera i libri dalla terza colonna in poi
                    for (int i = 2; i < libreria.length; i++) {
                        String libro = libreria[i].trim();
                        if (!libro.isEmpty()&& !libro.equalsIgnoreCase("null")) {
                            // Se l'indice corrisponde, restituisci il libro
                            if (index == i - 1) { // L'indice parte da 1, quindi sottraiamo 1 per ottenere l'indice 0-based
                           
                        System.out.print("\n"+VERDE+ V+ "Libro selezionato: " + libro + RESET+"\n");
                                return libro;
                            }
                        }
                    }
                    // Se l'indice non corrisponde a nessun libro trovato
                    System.out.print("\n"+ROSSO+"Indice non valido. Riprova "+ RESET + "\n");
                    return null;
                }
            }
        
            // Se non trova la libreria
            System.out.println(ROSSO+X+"Libreria non trovata per userid: " + userid + ", nomeLibreria: " + nomeLibreria+RESET);
            return null;
        }
        
        /**
         * Legge un file CSV e restituisce i record come lista di array di stringhe.
         *
         * @param filePath Il percorso del file CSV.
         * @return La lista dei record.
         */
        public static List<String[]> leggiFileCsv(String filePath) {
            List<String[]> records = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split("\t"); // Utilizza il tab come delimitatore
                    for (int i = 0; i < values.length; i++) {
                        values[i] = values[i].replace("\"", "").trim(); // Rimuove le virgolette e gli spazi vuoti
                    }
                    records.add(values);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return records;
        }
        
        /**
         * Legge un file CSV e restituisce i record come lista di array di stringhe.
         *
         * @param filePath Il percorso del file CSV.
         * @return La lista dei record.
         */
        private static List<String[]> leggiFileCsv2(String filePath) {
            List<String[]> righe = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    righe.add(line.split("\t")); // Utilizza il tab come delimitatore
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return righe;
        }
        
        /**
         * Permette all'utente di selezionare una libreria.
         *
         * @param userid L'ID dell'utente.
         * @return Il nome della libreria selezionata, o null se non esiste.
         */
        public static String selezionaLibreria(String userid) {
                           try {
            // Imposta la codifica dell'output della console su UTF-8
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            e.printStackTrace();
        }
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            Map<Integer, String> librerieUtente = new HashMap<>();
            int index = 1;
        
            // Legge le librerie dal file CSV
            try{
             @SuppressWarnings("resource")
            BufferedReader br = new BufferedReader(new FileReader(Main.librerie_path));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] campi = line.split("\t");
                    if (campi.length >= 2 && campi[0].equals(userid)) {
                        librerieUtente.put(index, campi[1]);
                        System.out.println(index + "." + campi[1]);
                        index++;
                    }
                }
             }catch (IOException e) {
           System.out.print("");

            }
        
            if (librerieUtente.isEmpty()) {
                System.out.println("\n"+ROSSO+X+"Non hai librerie registrate. Creane una nuova prima di selezionarne una."+RESET);
                return null;
            }
        
            while (true) {
                System.out.print("\n"+"Seleziona una libreria inserendo il numero corrispondente: ");
                String scelta = scanner.nextLine();
        
                try {
                    int sceltaInt = Integer.parseInt(scelta);
                    if (librerieUtente.containsKey(sceltaInt)) {
                        return librerieUtente.get(sceltaInt);
                    } else {
                        System.out.print(ROSSO+X+"\nSelezione non valida. Riprova\n"+RESET);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\n"+ROSSO+X+"Selezione non valida. Riprova"+RESET);
                }
            }
        }
        
        /**
         * Verifica se un libro è già presente nella libreria di un utente.
         *
         * @param userid       L'ID dell'utente.
         * @param nomeLibreria Il nome della libreria.
         * @param libro        Il titolo del libro da cercare.
         * @return true se il libro è già presente, false altrimenti.
         */
        public static boolean libroGiaPresenteNellaLibreria(String userid, String nomeLibreria, String libro) {
            List<String[]> librerie = leggiFileCsv2(Main.librerie_path);
            for (String[] libreria : librerie) {
                if (libreria.length >= 3 && libreria[0].equals(userid) && libreria[1].equals(nomeLibreria)) {
                    String[] libri = libreria[2].split("\t");
                    for (String titolo : libri) {
                        if (titolo.trim().equalsIgnoreCase(libro.trim())) {
                            return true; // Il libro è già presente nella libreria
                        }
                    }
                }
            }
            return false; // Il libro non è presente nella libreria
        }
        
        /**
         * Ordina la lista dei libri utilizzando il Bucket Sort.
         *
         * @param listaLibri La lista dei libri da ordinare.
         */
        private static void bucketSort(List<String> listaLibri) {
            if(listaLibri.isEmpty())
            return;    // Step 1: Crea i bucket
            int numBuckets = 26; // uno per ogni lettera dell'alfabeto
            List<List<String>> buckets = new ArrayList<>(numBuckets);
            for (int i = 0; i < numBuckets; i++) {
                buckets.add(new ArrayList<>());
            }
        
            // Step 2: Distribuisci i libri nei bucket in base alla prima lettera
            for (String libro : listaLibri) {
                char firstChar = libro.toLowerCase().charAt(0);
                int bucketIndex = firstChar - 'a';
                buckets.get(bucketIndex).add(libro);
            }
        
            // Step 3: Ordina ciascun bucket
            for (List<String> bucket : buckets) {
                Collections.sort(bucket);
            }
        
            // Step 4: Concatenare tutti i bucket nella lista originale
            listaLibri.clear();
            for (List<String> bucket : buckets) {
                listaLibri.addAll(bucket);
            }
        }
        
        /**
         * Stampa i dettagli di un libro.
         *
         * @param datiLibro L'array di stringhe contenente i dati del libro.
         */
        public static void stampaDettagliLibro(String[] datiLibro) {
            if (datiLibro == null) {
                System.out.println("\n" + ROSSO + X + "Nessun libro trovato." + RESET);
                return;
            }
        
            System.out.println(VERDE + "\nTitolo: " + datiLibro[0]);
            System.out.println("\nAutori: " + datiLibro[1]);
            System.out.println("\nCategoria: " + datiLibro[2]);
            System.out.println("\nEditore: " + datiLibro[3]);
            System.out.println("\nAnno di pubblicazione: " + datiLibro[4]);
            System.out.println("\nMedia voti stile: " + datiLibro[5]);
            System.out.println("\nCommenti stile: " + getCommentiPerCaratteristica(datiLibro[0], "stile"));
            System.out.println("\nMedia voti contenuto: " + datiLibro[6]);
            System.out.println("\nCommenti contenuto: " + getCommentiPerCaratteristica(datiLibro[0], "contenuto"));
            System.out.println("\nMedia voti gradevolezza: " + datiLibro[7]);
            System.out.println("\nCommenti gradevolezza: " + getCommentiPerCaratteristica(datiLibro[0], "gradevolezza"));
            System.out.println("\nMedia voti originalità: " + datiLibro[8]);
            System.out.println("\nCommenti originalità: " + getCommentiPerCaratteristica(datiLibro[0], "originalita"));
            System.out.println("\nMedia voti edizione: " + datiLibro[9]);
            System.out.println("\nCommenti edizione: " + getCommentiPerCaratteristica(datiLibro[0], "edizione"));
            System.out.println("\nMedia totale voti: " + datiLibro[10]);
            System.out.println("\nNumero di utenti che hanno votato: " + datiLibro[11]);
            System.out.println("\nCommento generale: " + getCommentoGenerale(datiLibro[0]));
            System.out.println("\nLibri consigliati: " + datiLibro[12]);
            System.out.println("\n" + RESET);
       
        }
        
        private static String getCommentiPerCaratteristica(String titoloLibro, String caratteristica) {
            List<String> commenti = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(Main.VALUTAZIONI_FILE_PATH))) {
                String line;
                br.readLine(); // Salta l'intestazione
                while ((line = br.readLine()) != null) {
                    String[] campi = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // Gestisce le virgole all'interno delle virgolette
                    if (campi[1].equalsIgnoreCase(titoloLibro)) {
                        switch (caratteristica) {
                            case "stile":
                                commenti.add(campi[9]);
                                break;
                            case "contenuto":
                                commenti.add(campi[10]);
                                break;
                            case "gradevolezza":
                                commenti.add(campi[11]);
                                break;
                            case "originalita":
                                commenti.add(campi[12]);
                                break;
                            case "edizione":
                                commenti.add(campi[13]);
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return commenti.isEmpty() ? "nessun commento presente" : String.join("; ", commenti);
        }
        
        private static String getCommentoGenerale(String titoloLibro) {
            List<String> commentiGenerali = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(Main.VALUTAZIONI_FILE_PATH))) {
                String line;
                br.readLine(); // Salta l'intestazione
                while ((line = br.readLine()) != null) {
                    String[] campi = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // Gestisce le virgole all'interno delle virgolette
                    if (campi[1].equalsIgnoreCase(titoloLibro)) {
                        commentiGenerali.add(campi[8]); // Il campo 8 contiene il commento generale
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return commentiGenerali.isEmpty() ? "nessun commento generale presente" : String.join("; ", commentiGenerali);
        }
        
        /**
         * Cerca un libro per autore e anno.
         */
        public static void cercaLibroPerAutoreeanno() {
            try {
                // Imposta la codifica dell'output della console su UTF-8
                System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            System.out.print("\nDimmi autore: ");
            String autore = scanner.nextLine().trim();
        
            if (autore.length() < 3) {
                System.out.println("\n"+ROSSO+X+"L'autore non può essere inferiore a 3 lettere."+RESET);
                ricercalibronologin(); // Torna al menu utente non registrato
                return;
            }
        
            if (!esisteAutore(autore)) {
                System.out.println("\n"+ROSSO+X+"Non esiste nessun autore chiamato così in Bibloteca."+RESET);
               ricercalibronologin(); // Torna al menu utente non registrato
                return;
            }
        
            System.out.print("\nDimmi l'anno di pubblicazione di quel libro: ");
            int anno = scanner.nextInt();
            scanner.nextLine(); // Consuma la newline
        
            if (!esisteAnnoPerAutore(autore, anno)) {
                System.out.println("\n"+ROSSO+X+"Non esiste nessun anno associato a quel autore."+RESET);
               ricercalibronologin(); // Torna al menu utente non registrato
                return;
            }
        
            stampaDettagliLibro(recuperaDatiLibroPerAutoreEAnno(autore, anno));
        }
        
        /**
         * Cerca un libro per autore.
         *
         * @param autore Il nome dell'autore.
         * @return 
         */
        public static String[] cercaLibroPerAutore(String autore) {
            try {
                // Imposta la codifica dell'output della console su UTF-8
                System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (autore.isEmpty()) {
                System.out.println("\n"+ROSSO+X+"L'autore non può essere vuoto."+RESET);
                return null;
            }
            if (autore.length() <= 2) {
                System.out.println("\n"+ROSSO+X+"Nessun libro trovato con quel autore in Bibloteca."+RESET);
                return null;
            }
            List<String[]> libri = leggiFileCsv(Main.LIBRI_FILE_PATH);
            System.out.println("\nRisultati della ricerca per autore \"" + autore + "\":");
            for (String[] datiLibro : libri) {
                if (datiLibro.length >= 9 && datiLibro[1].toLowerCase().contains(autore.toLowerCase())) {
                    Libreria.stampaDettagliLibro(datiLibro);
                }
            }
            return null;
        }
        
        /**
         * Cerca un libro per titolo.
         *
         * @param titolo Il titolo del libro.
         */
        public static void cercaLibroPerTitolo(String titolo) {
            try {
                // Imposta la codifica dell'output della console su UTF-8
                System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (titolo.isEmpty()) {
                System.out.println("\n"+ROSSO+X+"Il titolo non può essere vuoto.");
                return;
            }
            if (titolo.length() <= 2) {
                System.out.println("\n"+ROSSO+X+"Nessun libro trovato con quel titolo in Bibloteca."+ RESET);
                return;
            }
            Libreria.stampaDettagliLibro(recuperaDatiLibro(titolo));
        }
        
        /**
         * Recupera i dati di un libro per titolo.
         *
         * @param titolo Il titolo del libro.
         * @return Un array di stringhe contenente i dati del libro, o null se non trovato.
         */
        public static String[] recuperaDatiLibro(String titolo) {
            List<String[]> libri = leggiFileCsv(Main.LIBRI_FILE_PATH);
            for (String[] datiLibro : libri) {
                if (datiLibro.length >= 9 && datiLibro[0].toLowerCase().contains(titolo.toLowerCase())) {
                    return datiLibro;
                }
            }
            return null;
        }
        
        /**
         * Verifica se esiste un autore nel file CSV.
         *
         * @param autore Il nome dell'autore.
         * @return true se l'autore esiste, false altrimenti.
         */
        public static boolean esisteAutore(String autore) {
            List<String[]> libri = leggiFileCsv(Main.LIBRI_FILE_PATH);
            for (String[] datiLibro : libri) {
                if (datiLibro.length >= 2 && datiLibro[1].toLowerCase().contains(autore.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Verifica se esiste un anno per un dato autore nel file CSV.
         *
         * @param autore Il nome dell'autore.
         * @param anno   L'anno di pubblicazione.
         * @return true se l'anno esiste per l'autore, false altrimenti.
         */
        public static boolean esisteAnnoPerAutore(String autore, int anno) {
            List<String[]> libri = leggiFileCsv(Main.LIBRI_FILE_PATH);
            for (String[] datiLibro : libri) {
                if (datiLibro.length >= 5 && datiLibro[1].toLowerCase().contains(autore.toLowerCase())) {
                    try {
                        String annoString = datiLibro[4].replace("\"", "").trim();
                        int annoLibro = Integer.parseInt(annoString);
                        if (annoLibro == anno) {
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }
        
        /**
         * Recupera i dati di un libro per autore e anno.
         *
         * @param autore Il nome dell'autore.
         * @param anno   L'anno di pubblicazione.
         * @return Un array di stringhe contenente i dati del libro, o null se non trovato.
         */
        public static String[] recuperaDatiLibroPerAutoreEAnno(String autore, int anno) {
            List<String[]> libri = leggiFileCsv(Main.LIBRI_FILE_PATH);
            for (String[] datiLibro : libri) {
                if (datiLibro.length >= 5 && datiLibro[1].toLowerCase().contains(autore.toLowerCase())) {
                    try {
                        String annoString = datiLibro[4].replace("\"", "").trim();
                        int annoLibro = Integer.parseInt(annoString);
                        if (annoLibro == anno) {
                            return datiLibro;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        public static void ricercalibronologin() {
            if (!new File(Main.LIBRI_FILE_PATH).exists()) {
                Libricsv.generaFileLibri();
            }
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
    
            while (true) {
                System.out.println(Main.VIOLA + "\n\n* Menu di ricerca libri:\n" + Main.RESET);
                System.out.println("1. Cerca per titolo");
                System.out.println("2. Cerca per autore");
                System.out.println("3. Cerca per autore e anno");
                System.out.println(Main.ROSSO + "4. Torna al menu principale" + Main.RESET);
                System.out.print("\nInserisci la tua scelta: ");
    
                int scelta;
                try {
                    scelta = scanner.nextInt();
                    scanner.nextLine(); // Consuma la newline
                } catch (InputMismatchException e) {
                    System.out.println("\n" + Main.ROSSO + Main.X + "Inserisci un numero valido. Riprova." + Main.RESET);
                    scanner.nextLine(); // Consuma l'input non valido
                    continue;
                }
    
                switch (scelta) {
                    case 1:
                        while (true) {
                            System.out.print("\nInserisci il titolo da cercare (digita 'back' per tornare al menu ricerca): ");
                            String titolo = scanner.nextLine();
                            if (titolo.equalsIgnoreCase("back")) {
                                break;
                            }
                            Libreria.cercaLibroPerTitolo(titolo);
                        }
                        break;
                    case 2:
                        while (true) {
                            System.out.print("\nInserisci l'autore da cercare (digita 'back' per tornare al menu ricerca): ");
                            String autore = scanner.nextLine();
                            if (autore.equalsIgnoreCase("back")) {
                                break;
                            }
                            Libreria.cercaLibroPerAutore(autore);
                        }
                        break;
                    case 3:
                        Libreria.cercaLibroPerAutoreeanno();
                        break;
                    case 4:
                        System.out.println("\n" + Main.ROSSO + "Tornando al menù principale..." + Main.RESET);
                        Main.menu(); // Torna al menu principale
                        return; // Esci dal metodo
                    default:
                        System.out.println("\n" + Main.ROSSO + Main.X + "Scelta non valida. Riprova." + Main.RESET);
                }
            }
        }
    }