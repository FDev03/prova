package book_recommender.lab_b;

/**
 * Classe che rappresenta un libro nell'applicazione Book Recommender.
 */
public class Book {
    private String title;
    private String authors;
    private String category;
    private String publisher;
    private int publishYear;

    /**
     * Costruttore della classe Book.
     *
     * @param title Il titolo del libro
     * @param authors Gli autori del libro
     * @param category La categoria del libro
     * @param publisher L'editore del libro
     * @param publishYear L'anno di pubblicazione
     */
    public Book(String title, String authors, String category, String publisher, int publishYear) {
        this.title = title;
        this.authors = authors;
        this.category = category;
        this.publisher = publisher;
        this.publishYear = publishYear;
    }

    /**
     * Costruttore che accetta un array di valori CSV.
     *
     * @param csvValues Array di valori CSV [titolo, autori, categoria, editore, anno]
     */
    public Book(String[] csvValues) {
        if (csvValues.length >= 5) {
            this.title = csvValues[0].trim();
            this.authors = csvValues[1].trim();
            this.category = csvValues[2].trim();
            this.publisher = csvValues[3].trim();

            try {
                // Gestisce la possibilità che l'anno non sia un numero valido
                this.publishYear = Integer.parseInt(csvValues[4].trim());
            } catch (NumberFormatException e) {
                this.publishYear = 0; // Valore predefinito se l'anno non è valido
            }
        }
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getCategory() {
        return category;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getPublishYear() {
        return publishYear;
    }

    /**
     * Restituisce una rappresentazione testuale del libro.
     */
    @Override
    public String toString() {
        return String.format("%s - %s (%d)", title, authors, publishYear);
    }
}