package book_recommender.lab_b;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la funzionalità di consiglio libri.
 * Questo controller gestisce l'interfaccia utente per consigliare libri in base a un libro selezionato.
 */
public class RecommendBookController {

    @FXML
    private Label userIdLabel;

    @FXML
    private Label selectedBookLabel;

    @FXML
    private ListView<String> recommendedBooksListView;

    @FXML
    private Label recommendationsCountLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button removeSelectedButton;

    @FXML
    private Button clearAllButton;

    @FXML
    private TextField titleSearchField;

    @FXML
    private TextField authorSearchField;

    @FXML
    private TextField authorYearSearchField;

    @FXML
    private TextField yearSearchField;

    @FXML
    private VBox titleResultsContainer;

    @FXML
    private VBox authorResultsContainer;

    @FXML
    private VBox authorYearResultsContainer;

    private String userId;
    private String selectedBook;
    private String libraryName;
    private List<String> recommendedBooks = new ArrayList<>();
    private List<Book> allBooks = new ArrayList<>();

    // Classe interna per rappresentare un libro
    private static class Book {
        private String title;
        private String author;
        private int year;

        public Book(String title, String author, int year) {
            this.title = title;
            this.author = author;
            this.year = year;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public int getYear() {
            return year;
        }

        @Override
        public String toString() {
            return title + " - " + author + " (" + year + ")";
        }

        public boolean matchesTitle(String searchTerm) {
            return title.toLowerCase().contains(searchTerm.toLowerCase());
        }

        public boolean matchesAuthor(String searchTerm) {
            return author.toLowerCase().contains(searchTerm.toLowerCase());
        }

        public boolean matchesAuthorAndYear(String authorTerm, int year) {
            return author.toLowerCase().contains(authorTerm.toLowerCase()) && this.year == year;
        }
    }

    /**
     * Inizializza il controller.
     * Questo metodo viene chiamato automaticamente da JavaFX dopo che l'FXML è stato caricato.
     */
    public void initialize() {
        // Nasconde i messaggi di errore all'inizio
        errorLabel.setVisible(false);

        // Carica tutti i libri disponibili dal file CSV
        loadAllBooks();

        // Inizializza il conteggio delle raccomandazioni
        updateRecommendationsCount();

        // Abilita il pulsante di salvataggio solo quando ci sono raccomandazioni
        saveButton.setVisible(false);

        // Imposta gli eventi per i listener del pulsante Enter nei campi di ricerca
        titleSearchField.setOnAction(event -> handleTitleSearch(event));
        authorSearchField.setOnAction(event -> handleAuthorSearch(event));
        yearSearchField.setOnAction(event -> handleAuthorYearSearch(event));
        authorYearSearchField.setOnAction(event -> handleAuthorYearSearch(event));
    }

    /**
     * Imposta i dati necessari (userId, libro selezionato e libreria).
     * Questo metodo deve essere chiamato dopo il caricamento del controller.
     *
     * @param userId ID dell'utente
     * @param selectedBook libro selezionato per cui si stanno facendo raccomandazioni
     * @param libraryName nome della libreria
     */
    public void setData(String userId, String selectedBook, String libraryName) {
        this.userId = userId;
        this.selectedBook = selectedBook;
        this.libraryName = libraryName;

        userIdLabel.setText(userId);
        selectedBookLabel.setText("Libro selezionato: " + selectedBook);

        // Controlla se ci sono già raccomandazioni per questo libro
        loadExistingRecommendations();
    }

    /**
     * Carica tutti i libri disponibili dal file CSV.
     */
    private void loadAllBooks() {
        String csvFilePath = "data/libri.csv";
        allBooks.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Salta la prima riga che contiene intestazioni
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 3) {
                    String title = data[0].trim();
                    String author = data[1].trim();
                    int year = Integer.parseInt(data[2].trim());

                    allBooks.add(new Book(title, author, year));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Errore durante la lettura del file libri.csv: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Errore: Impossibile caricare i libri. " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    /**
     * Carica le raccomandazioni esistenti dal file CSV.
     */
    private void loadExistingRecommendations() {
        String csvFilePath = "data/consiglilibri.csv";
        recommendedBooks.clear();
        recommendedBooksListView.getItems().clear();

        File file = new File(csvFilePath);
        if (!file.exists()) {
            try {
                // Crea il file se non esiste
                file.createNewFile();
                // Aggiungi l'intestazione
                FileWriter fw = new FileWriter(file);
                fw.write("utente,libro,consigliato1,consigliato2,consigliato3\n");
                fw.close();
            } catch (IOException e) {
                System.err.println("Errore durante la creazione del file consiglilibri.csv: " + e.getMessage());
                e.printStackTrace();
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Salta la prima riga che contiene intestazioni
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 5 && data[0].trim().equals(userId.trim()) && data[1].trim().equals(selectedBook.trim())) {
                    // Aggiungi i libri consigliati alla lista
                    for (int i = 2; i < 5; i++) {
                        if (data[i] != null && !data[i].trim().isEmpty() && !data[i].equals("null")) {
                            recommendedBooks.add(data[i].trim());
                        }
                    }
                    break;
                }
            }

            // Popola la ListView con i libri consigliati trovati
            if (!recommendedBooks.isEmpty()) {
                recommendedBooksListView.getItems().addAll(recommendedBooks);
                saveButton.setVisible(true);
            }

            // Aggiorna il conteggio
            updateRecommendationsCount();

        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file consiglilibri.csv: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Errore: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    /**
     * Gestisce la ricerca per titolo.
     */
    @FXML
    public void handleTitleSearch(ActionEvent event) {
        String searchTerm = titleSearchField.getText().trim();
        if (searchTerm.isEmpty()) {
            errorLabel.setText("Inserisci un termine di ricerca.");
            errorLabel.setVisible(true);
            return;
        }

        errorLabel.setVisible(false);
        titleResultsContainer.getChildren().clear();

        List<Book> results = new ArrayList<>();
        for (Book book : allBooks) {
            if (book.matchesTitle(searchTerm) && !book.getTitle().equals(selectedBook)) {
                results.add(book);
            }
        }

        if (results.isEmpty()) {
            Label noResultsLabel = new Label("Nessun risultato trovato.");
            noResultsLabel.setStyle("-fx-text-fill: #555555;");
            titleResultsContainer.getChildren().add(noResultsLabel);
        } else {
            displaySearchResults(titleResultsContainer, results);
        }
    }

    /**
     * Gestisce la ricerca per autore.
     */
    @FXML
    public void handleAuthorSearch(ActionEvent event) {
        String searchTerm = authorSearchField.getText().trim();
        if (searchTerm.isEmpty()) {
            errorLabel.setText("Inserisci un termine di ricerca.");
            errorLabel.setVisible(true);
            return;
        }

        errorLabel.setVisible(false);
        authorResultsContainer.getChildren().clear();

        List<Book> results = new ArrayList<>();
        for (Book book : allBooks) {
            if (book.matchesAuthor(searchTerm) && !book.getTitle().equals(selectedBook)) {
                results.add(book);
            }
        }

        if (results.isEmpty()) {
            Label noResultsLabel = new Label("Nessun risultato trovato.");
            noResultsLabel.setStyle("-fx-text-fill: #555555;");
            authorResultsContainer.getChildren().add(noResultsLabel);
        } else {
            displaySearchResults(authorResultsContainer, results);
        }
    }

    /**
     * Gestisce la ricerca per autore e anno.
     */
    @FXML
    public void handleAuthorYearSearch(ActionEvent event) {
        String authorTerm = authorYearSearchField.getText().trim();
        String yearTerm = yearSearchField.getText().trim();

        if (authorTerm.isEmpty() || yearTerm.isEmpty()) {
            errorLabel.setText("Inserisci sia l'autore che l'anno.");
            errorLabel.setVisible(true);
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearTerm);
        } catch (NumberFormatException e) {
            errorLabel.setText("L'anno deve essere un numero.");
            errorLabel.setVisible(true);
            return;
        }

        errorLabel.setVisible(false);
        authorYearResultsContainer.getChildren().clear();

        List<Book> results = new ArrayList<>();
        for (Book book : allBooks) {
            if (book.matchesAuthorAndYear(authorTerm, year) && !book.getTitle().equals(selectedBook)) {
                results.add(book);
            }
        }

        if (results.isEmpty()) {
            Label noResultsLabel = new Label("Nessun risultato trovato.");
            noResultsLabel.setStyle("-fx-text-fill: #555555;");
            authorYearResultsContainer.getChildren().add(noResultsLabel);
        } else {
            displaySearchResults(authorYearResultsContainer, results);
        }
    }

    /**
     * Visualizza i risultati della ricerca in un container.
     */
    private void displaySearchResults(VBox container, List<Book> books) {
        for (Book book : books) {
            HBox resultBox = createResultBox(book);
            container.getChildren().add(resultBox);
        }
    }

    /**
     * Crea un box per visualizzare un risultato di ricerca con pulsante per aggiungere ai consigli.
     */
    private HBox createResultBox(Book book) {
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(5));
        hbox.setStyle("-fx-background-color: white; -fx-border-color: #DDDDDD; -fx-border-radius: 5;");

        Label titleLabel = new Label(book.toString());
        titleLabel.setWrapText(true);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        Button addButton = new Button("Aggiungi");
        addButton.setStyle("-fx-background-color: #4054B2; -fx-text-fill: white; -fx-cursor: hand;");

        // Se il libro è già nei consigliati, disabilita il pulsante
        if (recommendedBooks.contains(book.getTitle()) || recommendedBooks.size() >= 3) {
            addButton.setDisable(true);
        }

        addButton.setOnAction(event -> {
            if (recommendedBooks.size() < 3 && !recommendedBooks.contains(book.getTitle())) {
                recommendedBooks.add(book.getTitle());
                recommendedBooksListView.getItems().add(book.getTitle());
                updateRecommendationsCount();
                addButton.setDisable(true);

                // Abilita il pulsante di salvataggio se ci sono raccomandazioni
                if (!recommendedBooks.isEmpty()) {
                    saveButton.setVisible(true);
                }
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        hbox.getChildren().addAll(titleLabel, spacer, addButton);
        return hbox;
    }

    /**
     * Aggiorna il conteggio delle raccomandazioni.
     */
    private void updateRecommendationsCount() {
        recommendationsCountLabel.setText("Selezionati: " + recommendedBooks.size() + "/3");
    }

    /**
     * Gestisce la rimozione di un libro selezionato dalla lista dei consigli.
     */
    @FXML
    public void handleRemoveSelected(ActionEvent event) {
        String selectedItem = recommendedBooksListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            recommendedBooks.remove(selectedItem);
            recommendedBooksListView.getItems().remove(selectedItem);
            updateRecommendationsCount();

            // Disabilita il pulsante di salvataggio se non ci sono raccomandazioni
            if (recommendedBooks.isEmpty()) {
                saveButton.setVisible(false);
            }
        } else {
            errorLabel.setText("Seleziona un libro da rimuovere.");
            errorLabel.setVisible(true);
        }
    }

    /**
     * Gestisce la cancellazione di tutti i libri consigliati.
     */
    @FXML
    public void handleClearAll(ActionEvent event) {
        recommendedBooks.clear();
        recommendedBooksListView.getItems().clear();
        updateRecommendationsCount();
        saveButton.setVisible(false);
    }

    /**
     * Gestisce il salvataggio dei consigli.
     */
    @FXML
    public void handleSave(ActionEvent event) {
        if (recommendedBooks.isEmpty()) {
            errorLabel.setText("Devi aggiungere almeno un libro consigliato.");
            errorLabel.setVisible(true);
            return;
        }

        // Salva i consigli nel file CSV
        if (saveRecommendations()) {
            // Torna alla schermata precedente
            navigateToLibrarySelection(event);
        }
    }

    /**
     * Salva le raccomandazioni nel file CSV.
     * @return true se il salvataggio è avvenuto con successo, false altrimenti
     */
    private boolean saveRecommendations() {
        String csvFilePath = "data/consiglilibri.csv";
        File file = new File(csvFilePath);

        try {
            // Leggi il file per vedere se esiste già una riga per questo utente e libro
            List<String> lines = new ArrayList<>();
            boolean found = false;

            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] data = line.split(",");
                        if (data.length >= 2 && data[0].trim().equals(userId.trim()) && data[1].trim().equals(selectedBook.trim())) {
                            // Sostituisci questa riga con i nuovi dati
                            StringBuilder sb = new StringBuilder();
                            sb.append(userId).append(",").append(selectedBook);

                            // Aggiungi i libri consigliati (massimo 3)
                            for (int i = 0; i < 3; i++) {
                                sb.append(",");
                                if (i < recommendedBooks.size()) {
                                    sb.append(recommendedBooks.get(i));
                                } else {
                                    sb.append("null");
                                }
                            }

                            lines.add(sb.toString());
                            found = true;
                        } else {
                            lines.add(line);
                        }
                    }
                }
            } else {
                // Il file non esiste, aggiungi l'intestazione
                lines.add("utente,libro,consigliato1,consigliato2,consigliato3");
            }

            // Se non è stata trovata una riga per questo utente e libro, aggiungila
            if (!found) {
                StringBuilder sb = new StringBuilder();
                sb.append(userId).append(",").append(selectedBook);

                // Aggiungi i libri consigliati (massimo 3)
                for (int i = 0; i < 3; i++) {
                    sb.append(",");
                    if (i < recommendedBooks.size()) {
                        sb.append(recommendedBooks.get(i));
                    } else {
                        sb.append("null");
                    }
                }

                lines.add(sb.toString());
            }

            // Scrivi tutte le righe nel file
            Files.write(Paths.get(csvFilePath), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return true;
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio nel file consiglilibri.csv: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Errore: " + e.getMessage());
            errorLabel.setVisible(true);
            return false;
        }
    }

    /**
     * Gestisce il click sul pulsante "Torna al menu".
     */
    @FXML
    public void handleBack(ActionEvent event) {
        navigateToUserMenu(event);
    }

    /**
     * Gestisce il click sul pulsante "Annulla".
     */
    @FXML
    public void handleCancel(ActionEvent event) {
        navigateToLibrarySelection(event);
    }

    /**
     * Naviga alla schermata di selezione della libreria.
     */
    private void navigateToLibrarySelection(ActionEvent event) {
        try {
            // Carica la schermata di selezione libreria
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/selezionalib.fxml"));
            Parent root = loader.load();

            // Ottieni il controller e passa i dati dell'utente
            LibrarySelectionController controller = loader.getController();
            controller.setUserId(userId, "recommend");

            // Imposta la nuova scena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Book Recommender - Seleziona Libreria");
            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della schermata di selezione libreria: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Errore: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    /**
     * Naviga al menu utente.
     */
    private void navigateToUserMenu(ActionEvent event) {
        try {
            // Carica il menu utente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/userMenu.fxml"));
            Parent root = loader.load();

            // Ottieni il controller e passa i dati dell'utente
            UserMenuController controller = loader.getController();
            controller.setUserData(userId);

            // Imposta la nuova scena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Book Recommender - Menu Utente");
            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del menu utente: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Errore: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }
}