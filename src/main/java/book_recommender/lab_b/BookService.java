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
     * Ottiene una lista dei libri con la valutazione media più alta.
     * Include solo libri con valutazione media totale > 0.
     *
     * @param limit il numero massimo di libri da ritornare
     * @return lista dei libri con la valutazione media totale più alta
     */
    public static List<Book> getTopRatedBooks(int limit) {
        // Assicurati che i libri siano caricati
        if (!booksLoaded && !loadBooks()) {
            return new ArrayList<>(); // Restituisci una lista vuota in caso di errore
        }

        // Percorso al file delle valutazioni dei libri
        final String RATINGS_FILE_PATH = "data/ValutazioniLibri.csv";

        // Mappa per memorizzare le valutazioni medie totali dei libri
        java.util.Map<String, Double> bookRatings = new java.util.HashMap<>();

        // Mappa per contare il numero di valutazioni per libro
        java.util.Map<String, Integer> ratingCounts = new java.util.HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(RATINGS_FILE_PATH))) {
            String line;
            // Salta l'intestazione
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] fields = parseCsvLine(line);

                // Verifica che ci siano tutti i campi necessari per le valutazioni
                if (fields.length >= 7) {
                    String bookTitle = fields[1].trim();

                    try {
                        // Estrai le 5 valutazioni
                        double styleRating = Double.parseDouble(fields[2].trim());
                        double contentRating = Double.parseDouble(fields[3].trim());
                        double pleasantnessRating = Double.parseDouble(fields[4].trim());
                        double originalityRating = Double.parseDouble(fields[5].trim());
                        double editionRating = Double.parseDouble(fields[6].trim());

                        // Calcola la media totale per questa valutazione
                        double avgRating = (styleRating + contentRating + pleasantnessRating +
                                originalityRating + editionRating) / 5.0;

                        // Aggiorna la somma delle valutazioni
                        if (!bookRatings.containsKey(bookTitle)) {
                            bookRatings.put(bookTitle, avgRating);
                            ratingCounts.put(bookTitle, 1);
                        } else {
                            double currentSum = bookRatings.get(bookTitle) * ratingCounts.get(bookTitle);
                            int currentCount = ratingCounts.get(bookTitle);

                            currentSum += avgRating;
                            currentCount++;

                            // Aggiorna con la nuova media
                            bookRatings.put(bookTitle, currentSum / currentCount);
                            ratingCounts.put(bookTitle, currentCount);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Errore nel formato delle valutazioni per il libro: " + bookTitle);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file delle valutazioni: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Restituisci una lista vuota in caso di errore
        }

        // Filtra i libri con valutazione media totale > 0
        java.util.Map<String, Double> positiveRatings = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, Double> entry : bookRatings.entrySet()) {
            if (entry.getValue() > 0) {
                // Arrotonda a 1 decimale
                double roundedRating = Math.round(entry.getValue() * 10) / 10.0;
                positiveRatings.put(entry.getKey(), roundedRating);
            }
        }

        // Se non ci sono libri con valutazione positiva, restituisci una lista vuota
        if (positiveRatings.isEmpty()) {
            return new ArrayList<>();
        }

        // Ottiene i libri corrispondenti alle valutazioni positive
        List<Book> topRatedBooks = new ArrayList<>();

        // Classe interna per abbinare un libro con la sua valutazione
        class BookWithRating implements Comparable<BookWithRating> {
            Book book;
            double rating;

            public BookWithRating(Book book, double rating) {
                this.book = book;
                this.rating = rating;
            }

            @Override
            public int compareTo(BookWithRating other) {
                // Ordine decrescente basato sul rating
                return Double.compare(other.rating, this.rating);
            }
        }

        List<BookWithRating> booksWithRatings = new ArrayList<>();

        // Trova i libri corrispondenti
        for (Book book : allBooks) {
            String bookTitle = book.getTitle().trim();

            // Cerca una corrispondenza esatta o parziale
            for (java.util.Map.Entry<String, Double> entry : positiveRatings.entrySet()) {
                String ratedTitle = entry.getKey();

                // Confronto flessibile per gestire differenze minori nei titoli
                if (bookTitle.equalsIgnoreCase(ratedTitle) ||
                        bookTitle.contains(ratedTitle) ||
                        ratedTitle.contains(bookTitle)) {

                    booksWithRatings.add(new BookWithRating(book, entry.getValue()));
                    break;
                }
            }
        }

        // Ordina in base alla valutazione (ordine decrescente)
        Collections.sort(booksWithRatings);

        // Prendi al massimo 'limit' libri
        for (int i = 0; i < Math.min(limit, booksWithRatings.size()); i++) {
            topRatedBooks.add(booksWithRatings.get(i).book);
        }

        return topRatedBooks;
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
    }
}