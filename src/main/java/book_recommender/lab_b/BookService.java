package book_recommender.lab_b;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Servizio per la gestione dei libri nell'applicazione Book Recommender.
 */
public class BookService {
    // Percorso relativo al file CSV dei libri
    private static final String BOOKS_FILE_PATH = "data/Data.csv";

    // Lista di tutti i libri caricati dal file CSV
    private static List<Book> allBooks = new ArrayList<>();

    // Flag per indicare se i libri sono già stati caricati
    private static boolean booksLoaded = false;

    /**
     * Carica tutti i libri dal file CSV.
     *
     * @return true se il caricamento è avvenuto con successo, false altrimenti
     */
    public static boolean loadBooks() {
        if (booksLoaded) {
            return true; // I libri sono già stati caricati
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE_PATH))) {
            String line;
            // Salta l'intestazione (prima riga)
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                // Gestisci correttamente le virgolette nei campi CSV
                String[] fields = parseCsvLine(line);

                if (fields.length >= 5) {
                    Book book = new Book(fields);
                    allBooks.add(book);
                }
            }

            booksLoaded = true;

            return true;
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file dei libri: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ottiene una lista di libri casuali.
     *
     * @param count Il numero di libri casuali da ottenere
     * @return Una lista di libri casuali
     */
    public static List<Book> getRandomBooks(int count) {
        // Assicurati che i libri siano caricati
        if (!booksLoaded && !loadBooks()) {
            return new ArrayList<>(); // Restituisci una lista vuota in caso di errore
        }

        // Se ci sono meno libri di quanti richiesti, restituisci tutti i libri disponibili
        if (allBooks.size() <= count) {
            return new ArrayList<>(allBooks);
        }

        // Crea una copia della lista di tutti i libri
        List<Book> shuffledBooks = new ArrayList<>(allBooks);
        // Mescola la lista
        Collections.shuffle(shuffledBooks, new Random());

        // Restituisci i primi 'count' libri
        return shuffledBooks.subList(0, count);
    }

    /**
     * Cerca libri per titolo.
     *
     * @param title Il titolo da cercare
     * @return Una lista di libri che contengono il titolo specificato
     */
    public static List<Book> searchBooksByTitle(String title) {
        if (!booksLoaded && !loadBooks()) {
            return new ArrayList<>();
        }

        List<Book> result = new ArrayList<>();
        String searchTerm = title.toLowerCase();

        for (Book book : allBooks) {
            if (book.getTitle().toLowerCase().contains(searchTerm)) {
                result.add(book);
            }
        }

        return result;
    }

    /**
     * Cerca libri per autore.
     *
     * @param author L'autore da cercare
     * @return Una lista di libri scritti dall'autore specificato
     */
    public static List<Book> searchBooksByAuthor(String author) {
        if (!booksLoaded && !loadBooks()) {
            return new ArrayList<>();
        }

        List<Book> result = new ArrayList<>();
        String searchTerm = author.toLowerCase();

        for (Book book : allBooks) {
            if (book.getAuthors().toLowerCase().contains(searchTerm)) {
                result.add(book);
            }
        }

        return result;
    }

    /**
     * Cerca libri per autore e anno.
     *
     * @param author L'autore da cercare
     * @param year L'anno di pubblicazione
     * @return Una lista di libri scritti dall'autore nell'anno specificato
     */
    public static List<Book> searchBooksByAuthorAndYear(String author, int year) {
        if (!booksLoaded && !loadBooks()) {
            return new ArrayList<>();
        }

        List<Book> result = new ArrayList<>();
        String searchTerm = author.toLowerCase();

        for (Book book : allBooks) {
            if (book.getAuthors().toLowerCase().contains(searchTerm) && book.getPublishYear() == year) {
                result.add(book);
            }
        }

        return result;
    }

    /**
     * Analizza una riga CSV gestendo correttamente le virgolette.
     * Questo è un metodo semplificato che potrebbe non gestire tutti i casi possibili.
     * Ora reso pubblico per essere utilizzato da altre classi.
     *
     * @param line La riga CSV da analizzare
     * @return Un array di campi analizzati
     */
    public static String[] parseCsvLine(String line) {
        // Per una gestione completa del CSV sarebbe meglio usare una libreria come Apache Commons CSV
        // Ma per semplicità implementiamo una soluzione base
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        // Aggiungi l'ultimo campo
        fields.add(currentField.toString());

        return fields.toArray(new String[0]);
    }}