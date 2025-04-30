package book_recommender.lab_b;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller per la visualizzazione dei dettagli di un libro.
 */
public class BookDetailsController implements Initializable {

    @FXML
    private Label bookTitleLabel;

    @FXML
    private Label authorsLabel;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label publisherLabel;

    @FXML
    private Label yearLabel;

    @FXML
    private Label styleRatingLabel;

    @FXML
    private Label contentRatingLabel;

    @FXML
    private Label pleasantnessRatingLabel;

    @FXML
    private Label originalityRatingLabel;

    @FXML
    private Label editionRatingLabel;

    @FXML
    private Label totalRatingLabel;

    @FXML
    private Label usersCountLabel;

    @FXML
    private Label generalCommentsLabel;

    @FXML
    private Label recommendedBooksLabel;


    @FXML
    private Button backButton;

    // Container per le recensioni per ogni categoria
    @FXML
    private VBox styleReviewsBox;

    @FXML
    private VBox contentReviewsBox;

    @FXML
    private VBox pleasantnessReviewsBox;

    @FXML
    private VBox originalityReviewsBox;

    @FXML
    private VBox editionReviewsBox;

    // Riferimenti alle stelle per le valutazioni
    @FXML private Text styleStar1, styleStar2, styleStar3, styleStar4, styleStar5;
    @FXML private Text contentStar1, contentStar2, contentStar3, contentStar4, contentStar5;
    @FXML private Text pleasantnessStar1, pleasantnessStar2, pleasantnessStar3, pleasantnessStar4, pleasantnessStar5;
    @FXML private Text originalityStar1, originalityStar2, originalityStar3, originalityStar4, originalityStar5;
    @FXML private Text editionStar1, editionStar2, editionStar3, editionStar4, editionStar5;
    @FXML private Text totalStar1, totalStar2, totalStar3, totalStar4, totalStar5;

    // File CSV contenente i libri
    private static final String BOOKS_FILE_PATH = "data/Libri.csv";
    // File CSV alternativo contenente i libri (da usare se il primo non contiene il libro)
    private static final String DATA_FILE_PATH = "data/Data.csv";
    // File CSV contenente le valutazioni
    private static final String RATINGS_FILE_PATH = "data/ValutazioniLibri.csv";
    // File CSV contenente i consigli personalizzati di libri
    private static final String RECOMMENDATIONS_FILE_PATH = "data/ConsigliLibri.dati.csv";

    // Mappa dei colori assegnati a ciascun utente
    private Map<String, String> userColors = new HashMap<>();
    private String[] colorPalette = {
            "#e74c3c", // Rosso
            "#3498db", // Blu
            "#2ecc71", // Verde
            "#f39c12", // Arancione
            "#9b59b6", // Viola
            "#1abc9c", // Turchese
            "#d35400", // Arancione scuro
            "#27ae60", // Verde scuro
            "#2980b9", // Blu scuro
            "#8e44ad", // Viola scuro
            "#f1c40f", // Giallo
            "#16a085", // Turchese scuro
            "#2c3e50", // Blu notte
            "#f39c12", // Arancione chiaro
            "#e67e22", // Arancione chiaro scuro
            "#95a5a6", // Grigio chiaro
            "#bdc3c7", // Grigio
            "#7f8c8d", // Grigio scuro
            "#34495e", // Blu acciaio
            "#d5dbdb", // Grigio chiaro pastello
            "#9a59b6", // Viola chiaro
            "#f5b041", // Giallo aranciato
            "#58d68d", // Verde chiaro
            "#5dade2", // Blu cielo
            "#f1948a", // Rosa chiaro
            "#d6eaf8", // Blu pallido
            "#f7b7a3", // Rosa tenue
            "#e8daef", // Lilla chiaro
            "#c39bd3"  // Lilla
    };


    // Struttura per memorizzare recensioni
    private class Review {
        public String userId;
        public int rating;
        public String comment;

        public Review(String userId, int rating, String comment) {
            this.userId = userId;
            this.rating = rating;
            this.comment = comment;
        }
    }

    // Liste per memorizzare le recensioni per ogni caratteristica
    private List<Review> styleReviews = new ArrayList<>();
    private List<Review> contentReviews = new ArrayList<>();
    private List<Review> pleasantnessReviews = new ArrayList<>();
    private List<Review> originalityReviews = new ArrayList<>();
    private List<Review> editionReviews = new ArrayList<>();

    // Dati del libro corrente
    private Book currentBook;

    // Mappa per memorizzare le valutazioni del libro
    private Map<String, Double> ratings = new HashMap<>();

    // Numero di utenti che hanno valutato il libro
    private int numRaters = 0;

    /**
     * Inizializza il controller.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inizializzazione degli elementi dell'interfaccia

        // Inizializza i container delle recensioni
        styleReviewsBox.setSpacing(10);
        contentReviewsBox.setSpacing(10);
        pleasantnessReviewsBox.setSpacing(10);
        originalityReviewsBox.setSpacing(10);
        editionReviewsBox.setSpacing(10);
    }

    /**
     * Imposta i dati del libro da visualizzare.
     *
     * @param bookTitle il titolo del libro da visualizzare
     */
    public void setBookData(String bookTitle) {
        // Cerca il libro per titolo nel file CSV
        this.currentBook = findBookByTitle(bookTitle);

        if (currentBook != null) {
            // Carica le valutazioni e le recensioni del libro
            loadRatingsAndReviews(currentBook.getTitle());

            // Popola l'interfaccia con i dati del libro
            updateUI();
        } else {
            // Libro non trovato, prova con il file Data.csv
            this.currentBook = findBookInDataFile(bookTitle);

            if (currentBook != null) {
                // Carica le valutazioni e le recensioni del libro
                loadRatingsAndReviews(currentBook.getTitle());

                // Popola l'interfaccia con i dati del libro
                updateUI();
            } else {
                // Crea un libro di esempio se non trovato
                this.currentBook = createExampleBook(bookTitle);

                // Utilizza valutazioni di esempio
                setupExampleRatings();

                // Popola l'interfaccia con i dati del libro di esempio
                updateUI();

                // Libro non trovato
                System.err.println("Libro non trovato in nessun file: " + bookTitle);
            }
        }
    }

    /**
     * Crea un libro di esempio quando non è possibile trovarlo nei file.
     */
    private Book createExampleBook(String title) {
        return new Book(
                title,
                "Autore sconosciuto",
                "Non categorizzato",
                "Editore sconosciuto",
                2000
        );
    }

    /**
     * Imposta valutazioni a zero quando non è possibile trovare quelle reali.
     */
    private void setupExampleRatings() {
        ratings.put("style", 0.0);
        ratings.put("content", 0.0);
        ratings.put("pleasantness", 0.0);
        ratings.put("originality", 0.0);
        ratings.put("edition", 0.0);
        ratings.put("total", 0.0);
        numRaters = 0;

        // Pulisci le liste di recensioni
        styleReviews.clear();
        contentReviews.clear();
        pleasantnessReviews.clear();
        originalityReviews.clear();
        editionReviews.clear();
    }

    /**
     * Cerca un libro per titolo nel file Libri.csv.
     *
     * @param title il titolo del libro da cercare
     * @return il libro trovato o null se non trovato
     */
    private Book findBookByTitle(String title) {
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE_PATH))) {
            String line;
            // Salta l'intestazione (prima riga)
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] fields = BookService.parseCsvLine(line);

                if (fields.length >= 5) {
                    Book book = new Book(fields);

                    // Confronta ignorando maiuscole/minuscole
                    if (book.getTitle().trim().equalsIgnoreCase(title.trim())) {
                        return book;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file Libri.csv: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Cerca un libro per titolo nel file Data.csv (backup).
     *
     * @param title il titolo del libro da cercare
     * @return il libro trovato o null se non trovato
     */
    private Book findBookInDataFile(String title) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE_PATH))) {
            String line;
            // Salta l'intestazione (prima riga)
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] fields = BookService.parseCsvLine(line);

                if (fields.length >= 5) {
                    Book book = new Book(fields);

                    // Confronto esatto per titoli (solo case insensitive)
                    if (book.getTitle().trim().equalsIgnoreCase(title.trim())) {
                        return book;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file Data.csv: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Carica le valutazioni e le recensioni per il libro specifico.
     *
     * @param bookTitle il titolo del libro di cui caricare le valutazioni
     */
    private void loadRatingsAndReviews(String bookTitle) {
        // Inizializza le valutazioni con valori predefiniti
        ratings.put("style", 0.0);
        ratings.put("content", 0.0);
        ratings.put("pleasantness", 0.0);
        ratings.put("originality", 0.0);
        ratings.put("edition", 0.0);
        ratings.put("total", 0.0);

        // Pulisci le liste di recensioni
        styleReviews.clear();
        contentReviews.clear();
        pleasantnessReviews.clear();
        originalityReviews.clear();
        editionReviews.clear();

        try {
            // Controlla se esiste il file delle valutazioni
            BufferedReader reader = new BufferedReader(new FileReader(RATINGS_FILE_PATH));
            String line;
            int count = 0;
            double styleSum = 0, contentSum = 0, pleasantnessSum = 0, originalitySum = 0, editionSum = 0;
            int colorIndex = 0;

            // Salta l'intestazione
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] fields = BookService.parseCsvLine(line);

                // Verifica che ci siano abbastanza campi e che il titolo corrisponda esattamente
                if (fields.length >= 8 && fields[1].trim().equalsIgnoreCase(bookTitle.trim())) {
                    try {
                        // Estrai informazioni dell'utente e valutazioni
                        String userId = fields[0].trim();
                        int style = Integer.parseInt(fields[2].trim());
                        int content = Integer.parseInt(fields[3].trim());
                        int pleasantness = Integer.parseInt(fields[4].trim());
                        int originality = Integer.parseInt(fields[5].trim());
                        int edition = Integer.parseInt(fields[6].trim());

                        // Assegna un colore all'utente se non ne ha già uno
                        if (!userColors.containsKey(userId)) {
                            userColors.put(userId, colorPalette[colorIndex % colorPalette.length]);
                            colorIndex++;
                        }

                        // Somma le valutazioni per il calcolo della media
                        styleSum += style;
                        contentSum += content;
                        pleasantnessSum += pleasantness;
                        originalitySum += originality;
                        editionSum += edition;
                        count++;

                        // Aggiungi recensioni specifiche se disponibili (campi 9-13)
                        if (fields.length >= 9) {
                            String generalReview = fields[8].trim();

                            if (fields.length >= 10 && !fields[9].trim().isEmpty()) {
                                styleReviews.add(new Review(userId, style, fields[9].trim()));
                            } else if (!generalReview.isEmpty()) {
                                styleReviews.add(new Review(userId, style, generalReview));
                            }

                            if (fields.length >= 11 && !fields[10].trim().isEmpty()) {
                                contentReviews.add(new Review(userId, content, fields[10].trim()));
                            } else if (!generalReview.isEmpty()) {
                                contentReviews.add(new Review(userId, content, generalReview));
                            }

                            if (fields.length >= 12 && !fields[11].trim().isEmpty()) {
                                pleasantnessReviews.add(new Review(userId, pleasantness, fields[11].trim()));
                            } else if (!generalReview.isEmpty()) {
                                pleasantnessReviews.add(new Review(userId, pleasantness, generalReview));
                            }

                            if (fields.length >= 13 && !fields[12].trim().isEmpty()) {
                                originalityReviews.add(new Review(userId, originality, fields[12].trim()));
                            } else if (!generalReview.isEmpty()) {
                                originalityReviews.add(new Review(userId, originality, generalReview));
                            }

                            if (fields.length >= 14 && !fields[13].trim().isEmpty()) {
                                editionReviews.add(new Review(userId, edition, fields[13].trim()));
                            } else if (!generalReview.isEmpty()) {
                                editionReviews.add(new Review(userId, edition, generalReview));
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Errore nel formato dei dati di valutazione: " + e.getMessage());
                    }
                }
            }

            reader.close();

            // Se ci sono valutazioni, calcola la media
            if (count > 0) {
                ratings.put("style", Math.round(styleSum / count * 10) / 10.0);
                ratings.put("content", Math.round(contentSum / count * 10) / 10.0);
                ratings.put("pleasantness", Math.round(pleasantnessSum / count * 10) / 10.0);
                ratings.put("originality", Math.round(originalitySum / count * 10) / 10.0);
                ratings.put("edition", Math.round(editionSum / count * 10) / 10.0);

                // Calcola la media totale
                double total = (styleSum + contentSum + pleasantnessSum + originalitySum + editionSum) / (5 * count);
                ratings.put("total", Math.round(total * 10) / 10.0);

                // Aggiorna il numero di valutatori
                numRaters = count;
            } else {
                // Se non ci sono valutazioni, imposta tutto a 0
                setupExampleRatings();
            }

        } catch (IOException e) {
            System.err.println("Errore nella lettura del file delle valutazioni: " + e.getMessage());
            e.printStackTrace();

            // Se c'è un errore, imposta tutte le stelle a 0
            setupExampleRatings();
        }
    }

    /**
     * Aggiorna l'interfaccia utente con i dati del libro corrente.
     */
    private void updateUI() {
        // Imposta i dati del libro
        bookTitleLabel.setText(currentBook.getTitle());
        authorsLabel.setText(currentBook.getAuthors());
        categoryLabel.setText(currentBook.getCategory());
        publisherLabel.setText(currentBook.getPublisher());
        yearLabel.setText(String.valueOf(currentBook.getPublishYear()));

        // Imposta le valutazioni
        styleRatingLabel.setText(String.valueOf(ratings.get("style")));
        contentRatingLabel.setText(String.valueOf(ratings.get("content")));
        pleasantnessRatingLabel.setText(String.valueOf(ratings.get("pleasantness")));
        originalityRatingLabel.setText(String.valueOf(ratings.get("originality")));
        editionRatingLabel.setText(String.valueOf(ratings.get("edition")));
        totalRatingLabel.setText(String.valueOf(ratings.get("total")));

        // Aggiorna il numero di valutatori
        usersCountLabel.setText(String.valueOf(numRaters));

        // Colora le stelle in base alle valutazioni
        updateStars(styleStar1, styleStar2, styleStar3, styleStar4, styleStar5, ratings.get("style"));
        updateStars(contentStar1, contentStar2, contentStar3, contentStar4, contentStar5, ratings.get("content"));
        updateStars(pleasantnessStar1, pleasantnessStar2, pleasantnessStar3, pleasantnessStar4, pleasantnessStar5, ratings.get("pleasantness"));
        updateStars(originalityStar1, originalityStar2, originalityStar3, originalityStar4, originalityStar5, ratings.get("originality"));
        updateStars(editionStar1, editionStar2, editionStar3, editionStar4, editionStar5, ratings.get("edition"));
        updateStars(totalStar1, totalStar2, totalStar3, totalStar4, totalStar5, ratings.get("total"));

        // Genera un commento generale basato sulle valutazioni
        generateGeneralComment();

        // Aggiorna i container delle recensioni
        updateReviewContainers();

        // Genera libri consigliati basati sulla categoria del libro attuale
        generateRecommendedBooks();
    }

    /**
     * Aggiorna i container delle recensioni con i dati caricati.
     */
    private void updateReviewContainers() {
        // Pulisci i container
        styleReviewsBox.getChildren().clear();
        styleReviewsBox.getChildren().add(new Label("Recensioni sullo Stile"));
        ((Label) styleReviewsBox.getChildren().get(0)).setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        contentReviewsBox.getChildren().clear();
        contentReviewsBox.getChildren().add(new Label("Recensioni sul Contenuto"));
        ((Label) contentReviewsBox.getChildren().get(0)).setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        pleasantnessReviewsBox.getChildren().clear();
        pleasantnessReviewsBox.getChildren().add(new Label("Recensioni sulla Gradevolezza"));
        ((Label) pleasantnessReviewsBox.getChildren().get(0)).setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        originalityReviewsBox.getChildren().clear();
        originalityReviewsBox.getChildren().add(new Label("Recensioni sull'Originalità"));
        ((Label) originalityReviewsBox.getChildren().get(0)).setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        editionReviewsBox.getChildren().clear();
        editionReviewsBox.getChildren().add(new Label("Recensioni sull'Edizione"));
        ((Label) editionReviewsBox.getChildren().get(0)).setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Aggiungi le recensioni per ogni categoria
        addReviewsToContainer(styleReviews, styleReviewsBox);
        addReviewsToContainer(contentReviews, contentReviewsBox);
        addReviewsToContainer(pleasantnessReviews, pleasantnessReviewsBox);
        addReviewsToContainer(originalityReviews, originalityReviewsBox);
        addReviewsToContainer(editionReviews, editionReviewsBox);
    }

    /**
     * Aggiunge recensioni ad un container specifico.
     *
     * @param reviews Lista di recensioni da aggiungere
     * @param container Container in cui aggiungere le recensioni
     */
    private void addReviewsToContainer(List<Review> reviews, VBox container) {
        if (reviews.isEmpty()) {
            Label noReviewsLabel = new Label("Nessuna recensione disponibile.");
            noReviewsLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #777777;");
            container.getChildren().add(noReviewsLabel);
            return;
        }

        for (Review review : reviews) {
            // Crea un box per la recensione
            VBox reviewBox = new VBox(5);
            reviewBox.setPadding(new Insets(10));
            reviewBox.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #e0e0e0; -fx-border-radius: 5px;");

            // Intestazione con utente e valutazione
            HBox headerBox = new HBox(10);
            headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // Usa il colore assegnato all'utente
            String userColor = userColors.getOrDefault(review.userId, "#333333");

            Label userLabel = new Label(review.userId);
            userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + userColor + ";");

            // Aggiungi stelle per la valutazione
            HBox starsBox = new HBox(2);
            for (int i = 1; i <= 5; i++) {
                Text star = new Text("★");
                star.setStyle("-fx-font-size: 14px;");
                star.setFill(i <= review.rating ? Color.web("#ffc107") : Color.web("#dddddd"));
                starsBox.getChildren().add(star);
            }

            headerBox.getChildren().addAll(userLabel, starsBox);

            // Corpo della recensione
            Label commentLabel = new Label(review.comment);
            commentLabel.setWrapText(true);
            commentLabel.setStyle("-fx-padding: 5 0 0 0;");

            reviewBox.getChildren().addAll(headerBox, commentLabel);
            container.getChildren().add(reviewBox);
        }
    }

    /**
     * Colora le stelle in base alla valutazione.
     *
     * @param star1 prima stella
     * @param star2 seconda stella
     * @param star3 terza stella
     * @param star4 quarta stella
     * @param star5 quinta stella
     * @param rating valutazione (da 0 a 5)
     */
    private void updateStars(Text star1, Text star2, Text star3, Text star4, Text star5, double rating) {
        Color goldColor = Color.web("#ffc107");
        Color grayColor = Color.web("#dddddd");

        star1.setFill(rating >= 1 ? goldColor : grayColor);
        star2.setFill(rating >= 2 ? goldColor : grayColor);
        star3.setFill(rating >= 3 ? goldColor : grayColor);
        star4.setFill(rating >= 4 ? goldColor : grayColor);
        star5.setFill(rating >= 5 ? goldColor : grayColor);
    }

    /**
     * Genera un commento generale basato sulle recensioni degli utenti.
     */
    private void generateGeneralComment() {
        // Pulisci prima il container dei commenti generali
        generalCommentsLabel.setText("");

        // Crea un container per i commenti se non esiste già
        VBox commentsContainer;
        if (generalCommentsLabel.getParent() instanceof VBox) {
            commentsContainer = (VBox) generalCommentsLabel.getParent();
            commentsContainer.getChildren().clear();
        } else {
            // Crea un nuovo container e sostituisci l'etichetta originale
            commentsContainer = new VBox(10);
            commentsContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // Sostituisci l'etichetta originale con il container nel layout
            VBox parentContainer = (VBox) generalCommentsLabel.getParent();
            int index = parentContainer.getChildren().indexOf(generalCommentsLabel);
            if (index >= 0) {
                parentContainer.getChildren().set(index, commentsContainer);
            }
        }

        // Aggiungi l'intestazione
        Label headerLabel = new Label();
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        headerLabel.setText("Commento generale");
        commentsContainer.getChildren().add(headerLabel);

        // Aggiungi il sommario delle valutazioni
        Label summaryLabel = new Label();
        if (numRaters > 0) {
            double totalRating = ratings.get("total");

        } else {
            summaryLabel.setText("Questo libro non ha ancora ricevuto .");
        }
        summaryLabel.setWrapText(true);
        commentsContainer.getChildren().add(summaryLabel);

        // Aggiungi i commenti degli utenti
        if (numRaters > 0) {
            // Recupera i commenti dal file ValutazioniLibri.csv
            List<Comment> userComments = getUserComments(currentBook.getTitle());

            if (!userComments.isEmpty()) {
                Label reviewsHeader = new Label("Recensioni degli utenti:");
                reviewsHeader.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0;");
                commentsContainer.getChildren().add(reviewsHeader);

                // Aggiungi ogni commento colorato al container
                for (Comment comment : userComments) {
                    // Crea un box per ogni commento
                    VBox commentBox = new VBox(5);
                    commentBox.setStyle("-fx-padding: 5; -fx-background-color: #f8f8f8; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

                    // Usa il colore assegnato all'utente
                    String userColor = userColors.getOrDefault(comment.userId, "#333333");

                    // Crea etichette separate per utente e commento
                    Label userLabel = new Label(comment.userId + ":");
                    userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + userColor + ";");

                    Label textLabel = new Label(comment.text);
                    textLabel.setWrapText(true);
                    textLabel.setStyle("-fx-font-style: italic;");

                    commentBox.getChildren().addAll(userLabel, textLabel);
                    commentsContainer.getChildren().add(commentBox);
                }
            }
        }
    }

    // Classe per rappresentare un commento generale
    private class Comment {
        public String userId;
        public String text;

        public Comment(String userId, String text) {
            this.userId = userId;
            this.text = text;
        }
    }

    // Classe ausiliaria per memorizzare i consigli personalizzati
    private class RecommendedBook {
        public String userId;
        public String bookTitle;

        public RecommendedBook(String userId, String bookTitle) {
            this.userId = userId;
            this.bookTitle = bookTitle;
        }
    }

    /**
     * Recupera i commenti generali degli utenti per un libro specifico.
     *
     * @param bookTitle il titolo del libro
     * @return lista di commenti
     */
    private List<Comment> getUserComments(String bookTitle) {
        List<Comment> comments = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(RATINGS_FILE_PATH))) {
            String line;
            // Salta l'intestazione
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] fields = BookService.parseCsvLine(line);

                // Verifica che ci siano abbastanza campi e che il titolo corrisponda esattamente
                if (fields.length >= 9 && fields[1].trim().equalsIgnoreCase(bookTitle.trim())) {

                    // Estrai ID utente e recensione generale
                    String userId = fields[0].trim();
                    String generalReview = fields[8].trim();

                    if (!generalReview.isEmpty()) {
                        comments.add(new Comment(userId, generalReview));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file delle valutazioni: " + e.getMessage());
            e.printStackTrace();
        }

        return comments;
    }

    /**
     * Genera suggerimenti di libri basati sulla categoria del libro corrente.
     */
    private void generateRecommendedBooks() {
        // Prima cerca se esistono consigli personalizzati nel file ConsigliLibri.dati.csv
        boolean foundCustomRecommendations = findCustomRecommendations();

        // Se non sono stati trovati consigli personalizzati, genera consigli basati sulla categoria
        if (!foundCustomRecommendations) {
            // Ottieni alcuni libri della stessa categoria
            boolean foundRecommendations = false;

            // Prima prova con Libri.csv
            foundRecommendations = findSimilarBooks(BOOKS_FILE_PATH);

            // Se non sono state trovate raccomandazioni, prova con Data.csv
            if (!foundRecommendations) {
                foundRecommendations = findSimilarBooks(DATA_FILE_PATH);
            }

            // Se non sono state trovate raccomandazioni, nascondi l'etichetta
            if (!foundRecommendations) {
                recommendedBooksLabel.setText("");
            }
        }
    }

    /**
     * Cerca consigli personalizzati nel file ConsigliLibri.dati.csv
     * e li visualizza in box come recensioni con ID utente colorato.
     *
     * @return true se è stato trovato anche un solo consiglio personalizzato, false altrimenti
     */
    /**
     * Cerca consigli personalizzati nel file ConsigliLibri.dati.csv
     * e li visualizza in box come recensioni con ID utente colorato.
     * Ora include anche l'autore del libro consigliato.
     *
     * @return true se è stato trovato anche un solo consiglio personalizzato, false altrimenti
     */
    private boolean findCustomRecommendations() {
        List<RecommendedBook> customRecommendations = new ArrayList<>();
        int colorIndex = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(RECOMMENDATIONS_FILE_PATH))) {
            String line;
            String firstLine = reader.readLine();
            if (firstLine == null) {
                return false;
            }

            // Leggi tutte le righe cercando corrispondenze con il titolo esatto
            BufferedReader dataReader = new BufferedReader(new FileReader(RECOMMENDATIONS_FILE_PATH));
            while ((line = dataReader.readLine()) != null) {
                String[] fields = BookService.parseCsvLine(line);
                if (fields.length >= 3) { // Deve avere almeno utente, libro corrente, libro consigliato
                    String sourceBookTitle = fields[1].trim();
                    String currentBookTitle = currentBook.getTitle().trim();

                    // Confronta solo con corrispondenza esatta (case insensitive)
                    if (sourceBookTitle.equalsIgnoreCase(currentBookTitle)) {
                        String userId = fields[0].trim();
                        String recommendedBookTitle = fields[2].trim();

                        // Assegna un colore all'utente se non ne ha già uno
                        if (!userColors.containsKey(userId)) {
                            userColors.put(userId, colorPalette[colorIndex % colorPalette.length]);
                            colorIndex++;
                        }

                        customRecommendations.add(new RecommendedBook(userId, recommendedBookTitle));
                    }
                }
            }
            dataReader.close();
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file dei consigli: " + e.getMessage());
            e.printStackTrace();
        }

        // Se ci sono consigli personalizzati, crea box visivi per ognuno
        if (!customRecommendations.isEmpty()) {
            VBox recommendationsContainer = new VBox(10);
            recommendationsContainer.setPadding(new Insets(10));

            Label headerLabel = new Label("Libri consigliati specificamente per questo libro:");
            headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            recommendationsContainer.getChildren().add(headerLabel);

            for (RecommendedBook recommendation : customRecommendations) {
                // Crea un box per ogni consiglio, simile alle recensioni
                VBox recommendationBox = new VBox(5);
                recommendationBox.setPadding(new Insets(10));
                recommendationBox.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #e0e0e0; -fx-border-radius: 5px;");

                // Usa il colore assegnato all'utente
                String userColor = userColors.getOrDefault(recommendation.userId, "#333333");

                Label userLabel = new Label(recommendation.userId);
                userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + userColor + ";");

                // Cerca il libro nei file per ottenere l'autore
                Book recommendedBook = findBookByTitle(recommendation.bookTitle);
                if (recommendedBook == null) {
                    recommendedBook = findBookInDataFile(recommendation.bookTitle);
                }

                Label bookLabel = new Label("\"" + recommendation.bookTitle + "\"");
                bookLabel.setStyle("-fx-font-style: italic;");
                bookLabel.setWrapText(true);

                // Aggiungi l'autore se il libro è stato trovato
                if (recommendedBook != null) {
                    Label authorLabel = new Label(recommendedBook.getAuthors());
                    authorLabel.setWrapText(true);
                    recommendationBox.getChildren().addAll(userLabel, bookLabel, authorLabel);
                } else {
                    recommendationBox.getChildren().addAll(userLabel, bookLabel);
                }

                recommendationsContainer.getChildren().add(recommendationBox);
            }

            // Sostituisci l'etichetta delle raccomandazioni con il container
            if (recommendedBooksLabel.getParent() instanceof VBox) {
                VBox parent = (VBox) recommendedBooksLabel.getParent();
                int index = parent.getChildren().indexOf(recommendedBooksLabel);
                if (index >= 0) {
                    parent.getChildren().set(index, recommendationsContainer);
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Cerca libri simili in un file CSV specifico e li visualizza in box colorati.
     *
     * @param filePath percorso del file CSV da cui cercare i libri
     * @return true se sono stati trovati libri simili, false altrimenti
     */
    private boolean findSimilarBooks(String filePath) {
        // Lista per raccogliere i libri simili
        List<Book> similarBooks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Salta l'intestazione
            reader.readLine();

            // Raccogli tutti i libri della stessa categoria
            while ((line = reader.readLine()) != null) {
                String[] fields = BookService.parseCsvLine(line);

                if (fields.length >= 5) {
                    Book book = new Book(fields);

                    // Se è della stessa categoria ma non è lo stesso libro
                    if (book.getCategory().equalsIgnoreCase(currentBook.getCategory()) &&
                            !book.getTitle().equalsIgnoreCase(currentBook.getTitle())) {
                        similarBooks.add(book);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file per le raccomandazioni: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        // Se non ci sono libri simili, ritorna false
        if (similarBooks.isEmpty()) {
            return false;
        }

        // Prendi massimo 3 libri casuali dalla lista (o tutti se ce ne sono meno di 3)
        int maxBooksToShow = Math.min(3, similarBooks.size());
        Collections.shuffle(similarBooks); // Mescola la lista per prendere 3 libri casuali

        // Crea il container per i libri consigliati
        VBox recommendationsContainer = new VBox(10);
        recommendationsContainer.setPadding(new Insets(10));

        // Intestazione
        Label headerLabel = new Label("Libri della stessa categoria:");
        headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        recommendationsContainer.getChildren().add(headerLabel);

        // Aggiungi i libri al container con box colorati
        for (int i = 0; i < maxBooksToShow; i++) {
            Book book = similarBooks.get(i);

            // Crea un box per ogni libro consigliato
            VBox bookBox = new VBox(5);
            bookBox.setPadding(new Insets(10));
            bookBox.setStyle("-fx-background-color:white "  +
                    "; -fx-border-color: #e0e0e0; -fx-border-radius: 5px;");

            Label titleLabel = new Label("\"" + book.getTitle() + "\"");
            titleLabel.setStyle("-fx-font-weight: bold;");
            titleLabel.setWrapText(true);

            Label authorLabel = new Label( book.getAuthors());
            authorLabel.setWrapText(true);

            bookBox.getChildren().addAll(titleLabel, authorLabel);
            recommendationsContainer.getChildren().add(bookBox);
        }

        // Sostituisci l'etichetta delle raccomandazioni con il container
        if (recommendedBooksLabel.getParent() instanceof VBox) {
            VBox parent = (VBox) recommendedBooksLabel.getParent();
            int index = parent.getChildren().indexOf(recommendedBooksLabel);
            if (index >= 0) {
                parent.getChildren().set(index, recommendationsContainer);
            }
        }

        return true;
    }

// Rimuovere questo metodo completo
    /**
     * Gestisce il click sul pulsante "Chiudi".
     */
    @FXML
    public void handleClose(ActionEvent event) {
        // Torna alla homepage
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/homepage.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.show();
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della homepage: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Gestisce il click sul pulsante "Torna indietro".
     */
    @FXML
    public void handleBack(ActionEvent event) {
        // Torna alla homepage
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/homepage.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.show();
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della homepage: " + e.getMessage());
            e.printStackTrace();
        }
    }}