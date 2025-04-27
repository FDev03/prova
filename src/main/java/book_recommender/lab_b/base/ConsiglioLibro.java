/**
 * La classe ConsiglioLibro rappresenta un consiglio di libri per un utente.
 * 
 * @author Alessio     	Gervasini 		Mat. 756181
 * @author Francesco 	Orsini Pio		Mat. 756954
 * @author Luca      	Borin        	Mat. 756563

 
 */

package book_recommender.lab_b.base;
import book_recommender.lab_b.nonservira.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ConsiglioLibro {
    private String titoloLibro;
    private List<String> libriConsigliati;
    private Utente utente;

    /**
     * Costruttore per creare un'istanza di ConsiglioLibro.
     * 
     * @param titoloLibro il titolo del libro selezionato.
     * @param libriConsigliati una lista di libri consigliati.
     * @param utente l'utente che ha selezionato il libro.
     */
    public ConsiglioLibro(String titoloLibro, List<String> libriConsigliati, Utente utente) {
        this.titoloLibro = titoloLibro;
        this.libriConsigliati = libriConsigliati;
        this.utente = utente;
    }

    /**
     * Ottiene il titolo del libro selezionato.
     * 
     * @return il titolo del libro selezionato.
     */
    public String getTitoloLibro() {
        return titoloLibro;
    }

    /**
     * Ottiene la lista dei libri consigliati.
     * 
     * @return una lista di libri consigliati.
     */
    public List<String> getLibriConsigliati() {
        return libriConsigliati;
    }

    /**
     * Ottiene l'utente che ha selezionato il libro.
     * 
     * @return l'utente che ha selezionato il libro.
     */
    public Utente getUtente() {
        return utente;
    }

    /**
     * Scrive i dati del consiglio libro su un file CSV.
     * 
     * @param userId l'ID dell'utente che ha selezionato il libro.
     * @param selectedBook il titolo del libro selezionato.
     * @param recommendedBooks una lista di libri consigliati.
     * @throws IOException se si verifica un errore di I/O durante la scrittura del file.
     */
    public static void writeToFile(String userId, String selectedBook, List<String> recommendedBooks)
            throws IOException {
        String filename = Main.CONSIGLI_FILE_PATH; // Nome del file CSV
        File file = new File(filename);
        boolean fileExists = file.exists(); // Verifica se il file esiste

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            // Scrivere l'intestazione se il file non esiste o è vuoto
            if (!fileExists || file.length() == 0) {
                bw.write("UserID,Libro selezionato,Libro Consigliato 1, Libro Consigliato 2, Libro Consigliato 3\n");
            }

            // Costruire la stringa di record
            StringBuilder record = new StringBuilder();
            record.append(userId).append(",").append(selectedBook).append(",");
            for (int i = 0; i < recommendedBooks.size(); i++) {
                record.append(recommendedBooks.get(i));
                if (i < recommendedBooks.size() - 1) {
                    record.append(", ");
                }
            }
            record.append("\n");
            bw.write(record.toString()); // Scrive il record nel file
        }
    }

    public void printDetails(double nuovaMediaStile, int numUtenti) {
        System.out.println("Titolo: " + titoloLibro);
        System.out.println("Nuova media stile: " + nuovaMediaStile);
        System.out.println("Numero utenti: " + numUtenti);
    }

    public void updateValues(String[] values) {
        if (values[0].trim().equalsIgnoreCase(titoloLibro.trim())) {
            // Aggiorna i valori
        }
    }
}