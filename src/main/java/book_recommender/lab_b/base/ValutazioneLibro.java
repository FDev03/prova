/**
 *  Classe per la gestione delle valutazioni dei libri.
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


public class ValutazioneLibro {
        // Definizione dei codici ANSI per i colori
    public static final String RESET = "\033[0m";  // Resetta il colore
    public static final String ROSSO = "\033[0;31m";
    public static final String VERDE = "\033[0;32m";


    public static final String V = "✓"; // Carattere  per il segno di spunta
    public static final String X = "x ";



    /**
     * Inserisce una valutazione per un libro specifico.
     *
     * @param titoloLibro Il titolo del libro da valutare.
     * @param userid      L'ID dell'utente che effettua la valutazione.
     */
    public static void inserisciValutazioneLibro(String titoloLibro, String userid) {
        Scanner scanner = new Scanner(System.in);
       

        // Input delle valutazioni
        System.out.print("\n1. Inserisci la valutazione per lo stile (da 1 a 5): ");
        int stile = inputValutazione(scanner);
        System.out.print("Inserisci un commento per lo stile (max 256 caratteri): ");
        String commentoStile = inputCommento(scanner);

        System.out.print("\n2. Inserisci la valutazione per il contenuto (da 1 a 5): ");
        int contenuto = inputValutazione(scanner);
        System.out.print("Inserisci un commento per il contenuto (max 256 caratteri): ");
        String commentoContenuto = inputCommento(scanner);

        System.out.print("\n3. Inserisci la valutazione per la gradevolezza (da 1 a 5): ");
        int gradevolezza = inputValutazione(scanner);
        System.out.print("Inserisci un commento per la gradevolezza (max 256 caratteri): ");
        String commentoGradevolezza = inputCommento(scanner);

        System.out.print("\n4. Inserisci la valutazione per l'originalità (da 1 a 5): ");
        int originalita = inputValutazione(scanner);
        System.out.print("Inserisci un commento per l'originalità (max 256 caratteri): ");
        String commentoOriginalita = inputCommento(scanner);

        System.out.print("\n5. Inserisci la valutazione per l'edizione (da 1 a 5): ");
        int edizione = inputValutazione(scanner);
        System.out.print("Inserisci un commento per l'edizione (max 256 caratteri): ");
        String commentoEdizione = inputCommento(scanner);
        
        double media = (stile + contenuto + gradevolezza + originalita + edizione) / 5.0;
        System.out.println("\nLa media dei tuoi voti è: " + media);

        System.out.print("\n6. Inserisci un commento finale (max 256 caratteri): ");
        String recensione = inputCommento(scanner);

        // Scrivi nel file CSV delle valutazioni
        scriviValutazioneCsv(userid, titoloLibro, stile, contenuto, gradevolezza, originalita, edizione, media, recensione,
            commentoStile, commentoContenuto, commentoGradevolezza, commentoOriginalita, commentoEdizione);
      

        // Aggiorna il file Libri.csv
        aggiornaLibriCsv(titoloLibro, stile, contenuto, gradevolezza, originalita, edizione, media);

        Main.menuUtenteRegistrato(userid);
    }

    /**
     * Metodo per l'input della valutazione da parte dell'utente.
     *
     * @param scanner Lo scanner per la lettura dell'input dell'utente.
     * @return La valutazione inserita dall'utente (compresa tra 1 e 5).
     */
    public static int inputValutazione(Scanner scanner) {
        while (true) {
            if (!scanner.hasNextInt()) {
                System.out.println(ROSSO + X + "Inserisci un numero tra 1 e 5." + RESET);
                scanner.next(); // Consuma l'input non valido
                continue;
            }

            int valutazione = scanner.nextInt();
            scanner.nextLine(); // Consuma la newline
            if (valutazione < 1 || valutazione > 5) {
                System.out.println(ROSSO + X + "Valutazione non valida. Inserisci un numero tra 1 e 5." + RESET);
                continue;
            }
            return valutazione;
        }
    }

    /**
     * Metodo per l'input del commento da parte dell'utente.
     *
     * @param scanner Lo scanner per la lettura dell'input dell'utente.
     * @return Il commento inserito dall'utente (max 256 caratteri).
     */
    public static String inputCommento(Scanner scanner) {
        while (true) {
            String commento = scanner.nextLine();
            // Check if the comment contains any digits
            if (commento.matches(".*\\d.*")) {
                System.out.println(ROSSO + X + "Il commento non può contenere numeri. Riprova." + RESET);
                continue;
            }
            if (commento.length() > 256) {
                System.out.println(ROSSO + X + "Il commento non può superare i 256 caratteri. Riprova." + RESET);
            } else {
                return commento;
            }
        }
    }

    /**
     * Scrive i dati della valutazione nel file CSV delle valutazioni.
     *
     * @param userid            L'ID dell'utente che ha effettuato la valutazione.
     * @param titoloLibro       Il titolo del libro valutato.
     * @param stile             La valutazione per lo stile.
     * @param contenuto         La valutazione per il contenuto.
     * @param gradevolezza      La valutazione per la gradevolezza.
     * @param originalita       La valutazione per l'originalità.
     * @param edizione          La valutazione per l'edizione.
     * @param mediaValutazioni  La media delle valutazioni.
     * @param recensione        La recensione del libro.
     * @param commentoStile     Il commento per lo stile.
     * @param commentoContenuto Il commento per il contenuto.
     * @param commentoGradevolezza Il commento per la gradevolezza.
     * @param commentoOriginalita Il commento per l'originalità.
     * @param commentoEdizione Il commento per l'edizione.
     */
    public static void scriviValutazioneCsv(String userid, String titoloLibro, int stile, int contenuto,
            int gradevolezza, int originalita, int edizione, double mediaValutazioni, String recensione,
            String commentoStile, String commentoContenuto, String commentoGradevolezza, String commentoOriginalita,
            String commentoEdizione) {
        File file = new File(Main.VALUTAZIONI_FILE_PATH);
        boolean fileExists = file.exists();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Main.VALUTAZIONI_FILE_PATH, true))) {
            // Se il file non esiste o è vuoto, scrivi l'intestazione
            if (!fileExists || file.length() == 0) {
                bw.write("userid,titoloLibro,stile,contenuto,gradevolezza,originalita,edizione,media,recensione,commentoStile,commentoContenuto,commentoGradevolezza,commentoOriginalita,commentoEdizione\n");
            }
            // Scrivi i dati della valutazione
            bw.write(String.format(java.util.Locale.US, "%s,%s,%d,%d,%d,%d,%d,%.1f,%s,%s,%s,%s,%s,%s\n",
                    userid, titoloLibro, stile, contenuto, gradevolezza, originalita, edizione, mediaValutazioni, recensione,
                    commentoStile, commentoContenuto, commentoGradevolezza, commentoOriginalita, commentoEdizione));
            System.out.println("\n" + VERDE + V + "Valutazione aggiunta con successo per il libro: " + titoloLibro + RESET);

        } catch (IOException e) {
            System.err.println("Errore durante la scrittura nel file: " + e.getMessage());
        }
    }

    /**
     * Aggiorna le medie delle valutazioni nel file Libri.csv.
     *
     * @param titoloLibro  Il titolo del libro valutato.
     * @param stile        La valutazione per lo stile.
     * @param contenuto    La valutazione per il contenuto.
     * @param gradevolezza La valutazione per la gradevolezza.
     * @param originalita  La valutazione per l'originalità.
     * @param edizione     La valutazione per l'edizione.
     * @param media        La media delle valutazioni.
     */
    public static void aggiornaLibriCsv(String titoloLibro, int stile, int contenuto, int gradevolezza, int originalita,
            int edizione, double media) {
                try {
                    // Imposta la codifica dell'output della console su UTF-8
                    System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
        File file = new File(Main.LIBRI_FILE_PATH);
        if (!file.exists()) {
            System.err.println("File non trovato: " + Main.LIBRI_FILE_PATH);
            return;
        }

        List<String> lines = new ArrayList<>();
       

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Leggi l'intestazione
            if (line == null) {
                System.err.println("File vuoto: " + Main.LIBRI_FILE_PATH);
                return;
            }

            lines.add(line); // Aggiungi l'intestazione alla lista delle linee

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // Utilizza regex per considerare le virgole all'interno delle virgolette
              

                if (values[0].trim().equalsIgnoreCase(titoloLibro.trim().toLowerCase())) {
           
                    // Calcola le nuove medie
                    int numUtenti = Integer.parseInt(values[11]) + 1;
                    double nuovaMediaStile = (Double.parseDouble(values[5]) * (numUtenti - 1) + stile) / numUtenti;
                    double nuovaMediaContenuto = (Double.parseDouble(values[6]) * (numUtenti - 1) + contenuto) / numUtenti;
                    double nuovaMediaGradevolezza = (Double.parseDouble(values[7]) * (numUtenti - 1) + gradevolezza) / numUtenti;
                    double nuovaMediaOriginalita = (Double.parseDouble(values[8]) * (numUtenti - 1) + originalita) / numUtenti;
                    double nuovaMediaEdizione = (Double.parseDouble(values[9]) * (numUtenti - 1) + edizione) / numUtenti;
                    double nuovaMediaTotale = (nuovaMediaStile + nuovaMediaContenuto + nuovaMediaGradevolezza
                            + nuovaMediaOriginalita + nuovaMediaEdizione) / 5.0;

                    // Aggiorna la riga con le nuove medie e il numero di utenti
                    values[5] = String.format(java.util.Locale.US, "%.2f", nuovaMediaStile);
                    values[6] = String.format(java.util.Locale.US, "%.2f", nuovaMediaContenuto);
                    values[7] = String.format(java.util.Locale.US, "%.2f", nuovaMediaGradevolezza);
                    values[8] = String.format(java.util.Locale.US, "%.2f", nuovaMediaOriginalita);
                    values[9] = String.format(java.util.Locale.US, "%.2f", nuovaMediaEdizione);
                    values[10] = String.format(java.util.Locale.US, "%.2f", nuovaMediaTotale);
                    values[11] = String.valueOf(numUtenti);
                }

                lines.add(String.join(",", values));
            }

         

        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file: " + e.getMessage());
        }

        // Riscrivi il file con le nuove medie
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Main.LIBRI_FILE_PATH))) {
            for (String l : lines) {
                bw.write(l);
                bw.write("\n");
            }
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del file: " + e.getMessage());
        }
    }
}