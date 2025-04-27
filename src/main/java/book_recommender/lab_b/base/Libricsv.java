


/**
 * Classe per la gestione dei file CSV relativi ai libri, valutazioni e consigli.
 * 
 * @author Alessio     	Gervasini 		Mat. 756181
 * @author Francesco 	Orsini Pio		Mat. 756954
 * @author Luca      	Borin        	Mat. 756563

 
 */
package book_recommender.lab_b.base;
import book_recommender.lab_b.nonservira.Main;

import java.io.*;
import java.util.*;


public class Libricsv {


    /**
     * Genera il file Libri.csv aggregando le valutazioni e i consigli dai file corrispondenti.
     */
    public static void generaFileLibri() {
        Map<String, List<Integer[]>> valutazioni = new HashMap<>();
        Map<String, Set<String>> consigli = new HashMap<>();
        boolean consigliFileExists = new File(Main.CONSIGLI_FILE_PATH).exists();

        // Leggi il file ValutazioniLibri.csv e aggrega le valutazioni per ogni libro
        try (BufferedReader br = new BufferedReader(new FileReader(Main.VALUTAZIONI_FILE_PATH))) {
            String line;
            br.readLine(); // Salta l'intestazione
            while ((line = br.readLine()) != null) {
                String[] campi = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // Utilizza regex per considerare le virgole all'interno delle virgolette
                String titolo = campi[1].toLowerCase(); // Rende la chiave non sensibile al maiuscolo/minuscolo
                int stile = Integer.parseInt(campi[2]);
                int contenuto = Integer.parseInt(campi[3]);
                int gradevolezza = Integer.parseInt(campi[4]);
                int originalita = Integer.parseInt(campi[5]);
                int edizione = Integer.parseInt(campi[6]);

                valutazioni.putIfAbsent(titolo, new ArrayList<>());
                valutazioni.get(titolo).add(new Integer[]{stile, contenuto, gradevolezza, originalita, edizione});
            }
        } catch (IOException e) {
            // Continua comunque in caso di errore
        }

        // Leggi il file ConsigliLibri.dati.csv e aggrega i consigli per ogni libro
        if (consigliFileExists) {
            try (BufferedReader br = new BufferedReader(new FileReader(Main.CONSIGLI_FILE_PATH))) {
                String line;
                br.readLine(); // Salta l'intestazione
                while ((line = br.readLine()) != null) {
                    String[] campi = line.split(",");
                    String titolo = campi[1].toLowerCase(); // Rende la chiave non sensibile al maiuscolo/minuscolo

                    consigli.putIfAbsent(titolo, new HashSet<>());
                    for (int i = 2; i < campi.length; i++) {
                        if (!campi[i].trim().isEmpty()) {
                            consigli.get(titolo).add(campi[i].trim());
                        }
                    }
                }
            } catch (IOException e) {
                // Continua comunque in caso di errore
            }
        }

        // Leggi il file Data.csv, calcola le medie e aggiungi i consigli
        try (BufferedReader br = new BufferedReader(new FileReader(Main.FILE_PATH));
             BufferedWriter bw = new BufferedWriter(new FileWriter(Main.LIBRI_FILE_PATH))) {

            String header = "Titolo\tAutore\tCategoria\tEditore\tAnno\tMediaStile\tMediaContenuto\tMediaGradevolezza\tMediaOriginalita\tMediaEdizione\tMediaTotale\tNumeroUtenti\tLibriConsigliati";
            bw.write(header + "\n");

            String line;
            br.readLine(); // Salta l'intestazione
            while ((line = br.readLine()) != null) {
                String[] campi = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // Utilizza regex per considerare le virgole all'interno delle virgolette
              

                String titolo = campi[0].replace("\"", "").trim().toLowerCase(); // Rende la chiave non sensibile al maiuscolo/minuscolo

                // Ottiene la lista di valutazioni per un dato titolo dal dizionario 'valutazioni', o crea una nuova lista se il titolo non è presente
                List<Integer[]> listaValutazioni = valutazioni.getOrDefault(titolo, new ArrayList<>());

                // Calcola la media delle valutazioni per lo stile
                double mediaStile = listaValutazioni.stream().mapToInt(v -> v[0]).average().orElse(0);

                // Calcola la media delle valutazioni per il contenuto
                double mediaContenuto = listaValutazioni.stream().mapToInt(v -> v[1]).average().orElse(0);

                // Calcola la media delle valutazioni per la gradevolezza
                double mediaGradevolezza = listaValutazioni.stream().mapToInt(v -> v[2]).average().orElse(0);

                // Calcola la media delle valutazioni per l'originalità
                double mediaOriginalita = listaValutazioni.stream().mapToInt(v -> v[3]).average().orElse(0);

                // Calcola la media delle valutazioni per l'edizione
                double mediaEdizione = listaValutazioni.stream().mapToInt(v -> v[4]).average().orElse(0);

                // Calcola la media totale delle valutazioni
                double mediaTotale = (mediaStile + mediaContenuto + mediaGradevolezza + mediaOriginalita + mediaEdizione) / 5.0;

                // Ottiene il numero di utenti che hanno valutato il libro
                int numeroUtenti = listaValutazioni.size();

                // Ottiene i libri consigliati per il titolo corrente
                String libriConsigliati = String.join(";", consigli.getOrDefault(titolo, Collections.singleton("nessun consiglio")));

                // Crea la nuova riga con i dati aggiornati
                String nuovaRiga = String.format(java.util.Locale.US,
                        "%s\t%s\t%s\t%s\t%s\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%d\t%s",
                        campi[0], campi[1], campi[2], campi[3], campi[4],
                        mediaStile, mediaContenuto, mediaGradevolezza, mediaOriginalita, mediaEdizione, mediaTotale,
                        numeroUtenti, libriConsigliati);

                bw.write(nuovaRiga + "\n");
            }
        } catch (IOException e) {
            // Continua comunque in caso di errore
        }
    }
}