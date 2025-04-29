package book_recommender.lab_b;

import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller per la gestione della valutazione di un libro.
 * Permette all'utente di valutare un libro secondo vari criteri (stile, contenuto,
 * gradevolezza, originalità, edizione) e lasciare commenti.
 */
public class RateBookController implements Initializable {

    // Componenti dell'interfaccia utente dichiarati nel file FXML
    @FXML private Label userIdLabel;
    @FXML private Label bookTitleLabel;
    @FXML private Label averageRatingLabel;
    @FXML private Label errorLabel;

    // Aree di testo per i commenti
    @FXML private TextArea styleCommentArea;
    @FXML private TextArea contentCommentArea;
    @FXML private TextArea pleasantnessCommentArea;
    @FXML private TextArea originalityCommentArea;
    @FXML private TextArea editionCommentArea;
    @FXML private TextArea finalCommentArea;

    // Pulsanti
    @FXML private Button submitButton;
    @FXML private Button cancelButton;
    @FXML private Button backButton;

    // Contenitori per le stelle di valutazione
    @FXML private HBox styleStarsContainer;
    @FXML private HBox contentStarsContainer;
    @FXML private HBox pleasantnessStarsContainer;
    @FXML private HBox originalityStarsContainer;
    @FXML private HBox editionStarsContainer;
    @FXML private HBox averageStarsContainer;

    // StackPane per gestire il click sulle stelle
    @FXML private StackPane styleStar1Container, styleStar2Container, styleStar3Container,
            styleStar4Container, styleStar5Container;
    @FXML private StackPane contentStar1Container, contentStar2Container, contentStar3Container,
            contentStar4Container, contentStar5Container;
    @FXML private StackPane pleasantnessStar1Container, pleasantnessStar2Container, pleasantnessStar3Container,
            pleasantnessStar4Container, pleasantnessStar5Container;
    @FXML private StackPane originalityStar1Container, originalityStar2Container, originalityStar3Container,
            originalityStar4Container, originalityStar5Container;
    @FXML private StackPane editionStar1Container, editionStar2Container, editionStar3Container,
            editionStar4Container, editionStar5Container;

    // Stelle di valutazione (rappresentate come Text)
    @FXML private Text styleStar1, styleStar2, styleStar3, styleStar4, styleStar5;
    @FXML private Text contentStar1, contentStar2, contentStar3, contentStar4, contentStar5;
    @FXML private Text pleasantnessStar1, pleasantnessStar2, pleasantnessStar3, pleasantnessStar4, pleasantnessStar5;
    @FXML private Text originalityStar1, originalityStar2, originalityStar3, originalityStar4, originalityStar5;
    @FXML private Text editionStar1, editionStar2, editionStar3, editionStar4, editionStar5;
    @FXML private Text averageStar1, averageStar2, averageStar3, averageStar4, averageStar5;

    // Dati dell'utente e del libro
    private String userId;
    private String bookTitle;
    private String libraryName;

    // Valutazioni assegnate (da 1 a 5)
    private int styleRating = 0;
    private int contentRating = 0;
    private int pleasantnessRating = 0;
    private int originalityRating = 0;
    private int editionRating = 0;

    // Colori per le stelle
    private static final String STAR_ACTIVE_COLOR = "#f2e485";   // Colore giallo per le stelle attive
    private static final String STAR_INACTIVE_COLOR = "#dddddd"; // Colore grigio per le stelle inattive

    // Percorsi dei file CSV
    private static final String RATINGS_FILE_PATH = "data/ValutazioniLibri.csv";
    private static final String BOOKS_FILE_PATH = "data/Libri.csv";

    /**
     * Inizializza il controller.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Nasconde l'etichetta di errore all'avvio
        errorLabel.setVisible(false);

        // Imposta il controllo di lunghezza massima per i commenti
        setupTextAreaLimits();

        // Inizializza le stelle con colore inattivo
        resetAllStars();

        // Aggiorna la media delle valutazioni
        updateAverageRating();
    }

    /**
     * Imposta il limite di caratteri per le aree di testo.
     */
    private void setupTextAreaLimits() {
        // Imposta un limite di 256 caratteri per ogni area di testo
        int maxChars = 256;

        styleCommentArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxChars) {
                styleCommentArea.setText(oldValue);
            }
        });

        contentCommentArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxChars) {
                contentCommentArea.setText(oldValue);
            }
        });

        pleasantnessCommentArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxChars) {
                pleasantnessCommentArea.setText(oldValue);
            }
        });

        originalityCommentArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxChars) {
                originalityCommentArea.setText(oldValue);
            }
        });

        editionCommentArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxChars) {
                editionCommentArea.setText(oldValue);
            }
        });

        finalCommentArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxChars) {
                finalCommentArea.setText(oldValue);
            }
        });
    }

    /**
     * Imposta i dati dell'utente e del libro.
     *
     * @param userId ID dell'utente
     * @param bookTitle Titolo del libro
     * @param libraryName Nome della libreria
     */
    public void setData(String userId, String bookTitle, String libraryName) {
        this.userId = userId;
        this.bookTitle = bookTitle;
        this.libraryName = libraryName;

        // Aggiorna le etichette nell'interfaccia
        userIdLabel.setText(userId);
        bookTitleLabel.setText(bookTitle);

        // Controlla se l'utente ha già valutato questo libro
        checkExistingRating();
    }

    /**
     * Verifica se l'utente ha già valutato questo libro e, in caso affermativo, carica i dati.
     */
    private void checkExistingRating() {
        try (BufferedReader reader = new BufferedReader(new FileReader(RATINGS_FILE_PATH))) {
            String line;
            // Salta l'intestazione
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] fields = parseCsvLine(line);

                // Se l'utente ha già valutato questo libro, carica i dati
                if (fields.length >= 7 &&
                        fields[0].trim().equals(userId) &&
                        fields[1].trim().equalsIgnoreCase(bookTitle.trim())) {

                    // Carica le valutazioni
                    styleRating = Integer.parseInt(fields[2].trim());
                    contentRating = Integer.parseInt(fields[3].trim());
                    pleasantnessRating = Integer.parseInt(fields[4].trim());
                    originalityRating = Integer.parseInt(fields[5].trim());
                    editionRating = Integer.parseInt(fields[6].trim());

                    // Aggiorna le stelle
                    updateStars(styleStar1, styleStar2, styleStar3, styleStar4, styleStar5, styleRating);
                    updateStars(contentStar1, contentStar2, contentStar3, contentStar4, contentStar5, contentRating);
                    updateStars(pleasantnessStar1, pleasantnessStar2, pleasantnessStar3, pleasantnessStar4, pleasantnessStar5, pleasantnessRating);
                    updateStars(originalityStar1, originalityStar2, originalityStar3, originalityStar4, originalityStar5, originalityRating);
                    updateStars(editionStar1, editionStar2, editionStar3, editionStar4, editionStar5, editionRating);

                    // Aggiorna la media
                    updateAverageRating();

                    // Carica i commenti se presenti
                    if (fields.length >= 8 && !fields[7].trim().isEmpty()) {
                        finalCommentArea.setText(fields[7].trim());
                    }

                    if (fields.length >= 9 && !fields[8].trim().isEmpty()) {
                        styleCommentArea.setText(fields[8].trim());
                    }

                    if (fields.length >= 10 && !fields[9].trim().isEmpty()) {
                        contentCommentArea.setText(fields[9].trim());
                    }

                    if (fields.length >= 11 && !fields[10].trim().isEmpty()) {
                        pleasantnessCommentArea.setText(fields[10].trim());
                    }

                    if (fields.length >= 12 && !fields[11].trim().isEmpty()) {
                        originalityCommentArea.setText(fields[11].trim());
                    }

                    if (fields.length >= 13 && !fields[12].trim().isEmpty()) {
                        editionCommentArea.setText(fields[12].trim());
                    }

                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file delle valutazioni: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gestisce il click sulle stelle per lo stile.
     */
    @FXML
    private void handleStyleStarClick(MouseEvent event) {
        StackPane source = (StackPane) event.getSource();

        if (source.equals(styleStar1Container)) {
            styleRating = 1;
        } else if (source.equals(styleStar2Container)) {
            styleRating = 2;
        } else if (source.equals(styleStar3Container)) {
            styleRating = 3;
        } else if (source.equals(styleStar4Container)) {
            styleRating = 4;
        } else if (source.equals(styleStar5Container)) {
            styleRating = 5;
        }

        // Aggiorna le stelle
        updateStars(styleStar1, styleStar2, styleStar3, styleStar4, styleStar5, styleRating);

        // Aggiorna la media
        updateAverageRating();
    }

    /**
     * Gestisce il click sulle stelle per il contenuto.
     */
    @FXML
    private void handleContentStarClick(MouseEvent event) {
        StackPane source = (StackPane) event.getSource();

        if (source.equals(contentStar1Container)) {
            contentRating = 1;
        } else if (source.equals(contentStar2Container)) {
            contentRating = 2;
        } else if (source.equals(contentStar3Container)) {
            contentRating = 3;
        } else if (source.equals(contentStar4Container)) {
            contentRating = 4;
        } else if (source.equals(contentStar5Container)) {
            contentRating = 5;
        }

        // Aggiorna le stelle
        updateStars(contentStar1, contentStar2, contentStar3, contentStar4, contentStar5, contentRating);

        // Aggiorna la media
        updateAverageRating();
    }

    /**
     * Gestisce il click sulle stelle per la gradevolezza.
     */
    @FXML
    private void handlePleasantnessStarClick(MouseEvent event) {
        StackPane source = (StackPane) event.getSource();

        if (source.equals(pleasantnessStar1Container)) {
            pleasantnessRating = 1;
        } else if (source.equals(pleasantnessStar2Container)) {
            pleasantnessRating = 2;
        } else if (source.equals(pleasantnessStar3Container)) {
            pleasantnessRating = 3;
        } else if (source.equals(pleasantnessStar4Container)) {
            pleasantnessRating = 4;
        } else if (source.equals(pleasantnessStar5Container)) {
            pleasantnessRating = 5;
        }

        // Aggiorna le stelle
        updateStars(pleasantnessStar1, pleasantnessStar2, pleasantnessStar3, pleasantnessStar4, pleasantnessStar5, pleasantnessRating);

        // Aggiorna la media
        updateAverageRating();
    }

    /**
     * Gestisce il click sulle stelle per l'originalità.
     */
    @FXML
    private void handleOriginalityStarClick(MouseEvent event) {
        StackPane source = (StackPane) event.getSource();

        if (source.equals(originalityStar1Container)) {
            originalityRating = 1;
        } else if (source.equals(originalityStar2Container)) {
            originalityRating = 2;
        } else if (source.equals(originalityStar3Container)) {
            originalityRating = 3;
        } else if (source.equals(originalityStar4Container)) {
            originalityRating = 4;
        } else if (source.equals(originalityStar5Container)) {
            originalityRating = 5;
        }

        // Aggiorna le stelle
        updateStars(originalityStar1, originalityStar2, originalityStar3, originalityStar4, originalityStar5, originalityRating);

        // Aggiorna la media
        updateAverageRating();
    }

    /**
     * Gestisce il click sulle stelle per l'edizione.
     */
    @FXML
    private void handleEditionStarClick(MouseEvent event) {
        StackPane source = (StackPane) event.getSource();

        if (source.equals(editionStar1Container)) {
            editionRating = 1;
        } else if (source.equals(editionStar2Container)) {
            editionRating = 2;
        } else if (source.equals(editionStar3Container)) {
            editionRating = 3;
        } else if (source.equals(editionStar4Container)) {
            editionRating = 4;
        } else if (source.equals(editionStar5Container)) {
            editionRating = 5;
        }

        // Aggiorna le stelle
        updateStars(editionStar1, editionStar2, editionStar3, editionStar4, editionStar5, editionRating);

        // Aggiorna la media
        updateAverageRating();
    }

    /**
     * Aggiorna l'aspetto delle stelle in base alla valutazione.
     */
    private void updateStars(Text star1, Text star2, Text star3, Text star4, Text star5, int rating) {
        star1.setFill(javafx.scene.paint.Color.web(rating >= 1 ? STAR_ACTIVE_COLOR : STAR_INACTIVE_COLOR));
        star2.setFill(javafx.scene.paint.Color.web(rating >= 2 ? STAR_ACTIVE_COLOR : STAR_INACTIVE_COLOR));
        star3.setFill(javafx.scene.paint.Color.web(rating >= 3 ? STAR_ACTIVE_COLOR : STAR_INACTIVE_COLOR));
        star4.setFill(javafx.scene.paint.Color.web(rating >= 4 ? STAR_ACTIVE_COLOR : STAR_INACTIVE_COLOR));
        star5.setFill(javafx.scene.paint.Color.web(rating >= 5 ? STAR_ACTIVE_COLOR : STAR_INACTIVE_COLOR));
    }

    /**
     * Reimposta tutte le stelle al colore inattivo.
     */
    private void resetAllStars() {
        // Reimposta tutte le stelle al colore inattivo
        updateStars(styleStar1, styleStar2, styleStar3, styleStar4, styleStar5, 0);
        updateStars(contentStar1, contentStar2, contentStar3, contentStar4, contentStar5, 0);
        updateStars(pleasantnessStar1, pleasantnessStar2, pleasantnessStar3, pleasantnessStar4, pleasantnessStar5, 0);
        updateStars(originalityStar1, originalityStar2, originalityStar3, originalityStar4, originalityStar5, 0);
        updateStars(editionStar1, editionStar2, editionStar3, editionStar4, editionStar5, 0);
        updateStars(averageStar1, averageStar2, averageStar3, averageStar4, averageStar5, 0);
    }

    /**
     * Aggiorna la valutazione media e le stelle corrispondenti.
     */
    private void updateAverageRating() {
        // Calcola la media solo se è stata assegnata almeno una valutazione
        if (styleRating > 0 || contentRating > 0 || pleasantnessRating > 0 ||
                originalityRating > 0 || editionRating > 0) {

            // Conta quante valutazioni sono state assegnate
            int count = 0;
            int sum = 0;

            if (styleRating > 0) {
                sum += styleRating;
                count++;
            }

            if (contentRating > 0) {
                sum += contentRating;
                count++;
            }

            if (pleasantnessRating > 0) {
                sum += pleasantnessRating;
                count++;
            }

            if (originalityRating > 0) {
                sum += originalityRating;
                count++;
            }

            if (editionRating > 0) {
                sum += editionRating;
                count++;
            }

            if (count > 0) {
                double average = (double) sum / count;

                // Formatta la media a una cifra decimale
                DecimalFormat df = new DecimalFormat("#.#");
                averageRatingLabel.setText(df.format(average));

                // Aggiorna le stelle della media (arrotondando all'intero più vicino)
                int roundedAverage = (int) Math.round(average);
                updateStars(averageStar1, averageStar2, averageStar3, averageStar4, averageStar5, roundedAverage);
            } else {
                averageRatingLabel.setText("0.0");
                updateStars(averageStar1, averageStar2, averageStar3, averageStar4, averageStar5, 0);
            }
        } else {
            // Se non è stata assegnata alcuna valutazione, mostra 0.0
            averageRatingLabel.setText("0.0");
            updateStars(averageStar1, averageStar2, averageStar3, averageStar4, averageStar5, 0);
        }
    }


/**
 * Gestisce il click sul pulsante "Invia Valutazione".
 */
    @FXML
    private void handleSubmit(ActionEvent event) {
        // Verifica che tutte le caratteristiche siano state valutate
        if (styleRating == 0 || contentRating == 0 || pleasantnessRating == 0 ||
                originalityRating == 0 || editionRating == 0) {

            // Mostra il messaggio di errore
            errorLabel.setText("Errore: Devi assegnare un voto a tutte le caratteristiche prima di inviare.");
            errorLabel.setVisible(true);
            return;
        }

        // Nascondi eventuali messaggi di errore
        errorLabel.setVisible(false);

        // Salva la valutazione nei file CSV
        saveRating();

        // Torna al menu utente
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/userMenu.fxml"));
            Parent root = loader.load();

            // Passa l'ID utente al controller del menu utente
            UserMenuController controller = loader.getController();
            controller.setUserData(userId);
            controller.setStatusMessage("Valutazione inviata con successo!");

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del menu utente: " + e.getMessage());
            e.printStackTrace();

            // Mostra un messaggio di errore
            errorLabel.setText("Errore: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    /**
     * Salva la valutazione nei file CSV.
     */
    private void saveRating() {
        try {
            boolean ratingExists = false;
            StringBuilder newContent = new StringBuilder();

            // Leggi il file delle valutazioni
            try (BufferedReader reader = new BufferedReader(new FileReader(RATINGS_FILE_PATH))) {
                String line;
                // Aggiungi l'intestazione
                String header = reader.readLine();
                newContent.append(header).append("\n");

                // Leggi tutte le righe
                while ((line = reader.readLine()) != null) {
                    String[] fields = parseCsvLine(line);

                    // Se l'utente ha già valutato questo libro, sostituisci la valutazione
                    if (fields.length >= 2 &&
                            fields[0].trim().equals(userId) &&
                            fields[1].trim().equalsIgnoreCase(bookTitle.trim())) {

                        // Sostituisci con la nuova valutazione
                        newContent.append(formatRatingLine());
                        ratingExists = true;
                    } else {
                        // Mantieni la riga invariata
                        newContent.append(line).append("\n");
                    }
                }

                // Se l'utente non ha ancora valutato questo libro, aggiungi una nuova riga
                if (!ratingExists) {
                    newContent.append(formatRatingLine());
                }
            }

            // Scrivi il nuovo contenuto nel file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(RATINGS_FILE_PATH))) {
                writer.write(newContent.toString());
            }

            // Aggiorna anche il file Libri.csv con la media delle valutazioni (opzionale)
            updateBookRating();

        } catch (IOException e) {
            System.err.println("Errore nel salvataggio della valutazione: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Aggiorna la valutazione media nel file Libri.csv.
     */
    private void updateBookRating() {
        try {
            // Implementazione per aggiornare il file Libri.csv
            // Questa è una semplificazione, potrebbe essere necessario adattarla
            // alla struttura specifica del file Libri.csv

            // Questa parte è opzionale e dipende dalla struttura del file Libri.csv
            // Se il file non contiene valutazioni medie, questa parte può essere omessa
        } catch (Exception e) {
            System.err.println("Errore nell'aggiornamento del file Libri.csv: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Formatta una riga di valutazione per il salvataggio nel file CSV.
     */
    private String formatRatingLine() {
        // Formato: userId,bookTitle,styleRating,contentRating,pleasantnessRating,originalityRating,editionRating,finalComment,styleComment,contentComment,pleasantnessComment,originalityComment,editionComment
        StringBuilder sb = new StringBuilder();

        // Aggiungi userId e bookTitle
        sb.append(escapeForCsv(userId)).append(",");
        sb.append(escapeForCsv(bookTitle)).append(",");

        // Aggiungi le valutazioni
        sb.append(styleRating).append(",");
        sb.append(contentRating).append(",");
        sb.append(pleasantnessRating).append(",");
        sb.append(originalityRating).append(",");
        sb.append(editionRating).append(",");

        // Aggiungi i commenti
        sb.append(escapeForCsv(finalCommentArea.getText())).append(",");
        sb.append(escapeForCsv(styleCommentArea.getText())).append(",");
        sb.append(escapeForCsv(contentCommentArea.getText())).append(",");
        sb.append(escapeForCsv(pleasantnessCommentArea.getText())).append(",");
        sb.append(escapeForCsv(originalityCommentArea.getText())).append(",");
        sb.append(escapeForCsv(editionCommentArea.getText()));

        // Aggiungi la newline
        sb.append("\n");

        return sb.toString();
    }


    /**
     * Gestisce il click sul pulsante "Annulla".
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        // Torna alla selezione del libro
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/selezionaLibro.fxml"));
            Parent root = loader.load();

            // Passa i dati al controller
            BookSelectionController controller = loader.getController();
            controller.setData(userId, libraryName);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della pagina di selezione libro: " + e.getMessage());
            e.printStackTrace();

            // Mostra un messaggio di errore
            errorLabel.setText("Errore: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    /**
     * Gestisce il click sul pulsante "Torna al menu".
     */
    @FXML
    private void handleBack(ActionEvent event) {
        // Torna al menu utente
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/userMenu.fxml"));
            Parent root = loader.load();

            // Passa l'ID utente al controller del menu utente
            UserMenuController controller = loader.getController();
            controller.setUserData(userId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del menu utente: " + e.getMessage());
            e.printStackTrace();

            // Mostra un messaggio di errore
            errorLabel.setText("Errore: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    /**
     * Analizza una riga CSV gestendo correttamente le virgolette.
     * Questo è un metodo semplificato che potrebbe non gestire tutti i casi possibili.
     *
     * @param line La riga CSV da analizzare
     * @return Un array di campi analizzati
     */
    private String[] parseCsvLine(String line) {
        // Per una gestione completa del CSV sarebbe meglio usare una libreria come Apache Commons CSV
        // Ma per semplicità implementiamo una soluzione base
        java.util.List<String> fields = new java.util.ArrayList<>();
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

    /**
     * Prepara una stringa per l'inclusione in un file CSV.
     * Se la stringa contiene virgole o virgolette, la racchiude tra virgolette
     * e raddoppia le virgolette interne.
     *
     * @param field Il campo da preparare
     * @return Il campo pronto per l'inclusione in un file CSV
     */
    private String escapeForCsv(String field) {
        if (field == null) {
            return "";
        }

        // Se il campo contiene virgole o virgolette, racchiudilo tra virgolette
        // e raddoppia le virgolette interne
        if (field.contains(",") || field.contains("\"")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }

        return field;
    }}