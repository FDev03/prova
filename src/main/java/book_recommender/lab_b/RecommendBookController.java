package book_recommender.lab_b;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

        // Rendi visibile il pulsante di salvataggio
        saveButton.setVisible(true);

        // Imposta gli eventi per i listener del pulsante Enter nei campi di ricerca
        titleSearchField.setOnAction(event -> handleTitleSearch(event));
        authorSearchField.setOnAction(event -> handleAuthorSearch(event));
        yearSearchField.setOnAction(event -> handleAuthorYearSearch(event));
        authorYearSearchField.setOnAction(event -> handleAuthorYearSearch(event));

        // Personalizza la ListView per i libri consigliati
        setupRecommendedBooksListView();
    }

    /**
     * Configura la ListView per mostrare i libri consigliati con un pulsante X per rimuoverli,
     * con un aspetto simile a quello dell'aggiunta libri a libreria
     */
    private void setupRecommendedBooksListView() {
        recommendedBooksListView.setCellFactory(listView -> new ListCell<String>() {
            private final Button deleteButton = new Button("X");
            private final HBox hbox = new HBox();
            private final Label label = new Label("");

            {
                hbox.setAlignment(Pos.CENTER_LEFT);
                hbox.setSpacing(10);
                hbox.setPadding(new Insets(5));

                // Stilizza il pulsante X con lo stile dell'altra schermata
                deleteButton.setStyle("-fx-background-color: #ff4136; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 25px; -fx-min-height: 25px; -fx-max-width: 25px; -fx-max-height: 25px; -fx-background-radius: 50%;");

                // Configura il layout
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                label.setWrapText(true);
                label.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(label, Priority.ALWAYS);

                hbox.getChildren().addAll(label, spacer, deleteButton);

                // Aggiungi l'azione al pulsante di rimozione
                deleteButton.setOnAction(event -> {
                    String item = getItem();
                    recommendedBooks.remove(item);
                    recommendedBooksListView.getItems().remove(item);
                    updateRecommendationsCount();
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    setGraphic(hbox);
                }
            }
        });
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
     * Carica tutti i libri disponibili.
     * Utilizza il metodo diretto di lettura del CSV con parsing più robusto.
     */
    private void loadAllBooks() {
        allBooks.clear();
        errorLabel.setVisible(false);

        // Modifica il percorso del file CSV
        String csvFilePath = "data/Data.csv";
        File csvFile = new File(csvFilePath);

        if (!csvFile.exists()) {
            // Prova a creare la directory se non esiste
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            // Se il file non esiste, aggiungi direttamente alcuni libri di esempio

            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Salta la prima riga che contiene intestazioni
            br.readLine();

            while ((line = br.readLine()) != null) {
                try {
                    // Utilizziamo un parsing più robusto per gestire le virgolette e le virgole
                    String[] parts = parseCsvLine(line);

                    if (parts.length >= 3) {
                        String title = parts[0].trim();
                        String author = parts[1].trim();

                        // Parsing dell'anno con gestione degli errori
                        int year = 0;
                        try {
                            // Estrai solo la parte numerica dell'anno
                            String yearStr = parts[2].trim().replaceAll("[^0-9]", "");
                            if (!yearStr.isEmpty()) {
                                year = Integer.parseInt(yearStr);
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Impossibile convertire l'anno per il libro: " + title);
                            // Se non riusciamo a convertire l'anno, impostiamo un valore di default
                            year = 0;
                        }

                        // Aggiungi il libro alla lista
                        allBooks.add(new Book(title, author, year));
                    }
                } catch (Exception e) {
                    // Ignora le righe che causano errori e continua con la prossima
                    System.err.println("Errore durante il parsing della riga: " + line);
                    System.err.println("Dettaglio errore: " + e.getMessage());
                }
            }


            // Se non ci sono libri, aggiungi alcuni esempi di libri predefiniti
            if (allBooks.isEmpty()) {

            }

        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file CSV: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Errore: Impossibile caricare i libri. " + e.getMessage());
            errorLabel.setVisible(true);


        }
    }

    /**
     * Aggiunge libri di esempio alla lista
     */


    /**
     * Effettua il parsing di una riga CSV tenendo conto delle virgolette.
     * @param line La riga da analizzare
     * @return Array di stringhe con i valori
     */
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder currentStr = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Inverti lo stato delle virgolette
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                // Se troviamo una virgola e non siamo tra virgolette, è un separatore
                result.add(currentStr.toString());
                currentStr = new StringBuilder();
            } else {
                // Altrimenti aggiungiamo il carattere corrente
                currentStr.append(c);
            }
        }

        // Aggiungi l'ultimo campo
        result.add(currentStr.toString());

        return result.toArray(new String[0]);
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
                // Crea la directory se non esiste
                File dataDir = new File("data");
                if (!dataDir.exists()) {
                    dataDir.mkdirs();
                }

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
            HBox bookRow = createSearchResultRow(book);
            container.getChildren().add(bookRow);
        }
    }

    /**
     * Crea una riga di risultato ricerca simile all'interfaccia di AddBooksToLibraryController.
     */
    private HBox createSearchResultRow(Book book) {
        HBox rowContainer = new HBox();
        rowContainer.setPadding(new Insets(10));
        rowContainer.setStyle("-fx-background-color: white; -fx-border-color: #DDDDDD; -fx-border-radius: 5;");
        rowContainer.setAlignment(Pos.CENTER_LEFT);

        // Contenitore principale per le informazioni del libro (occupa tutto lo spazio disponibile)
        VBox bookInfoContainer = new VBox();
        bookInfoContainer.setSpacing(5);
        HBox.setHgrow(bookInfoContainer, Priority.ALWAYS);

        // Titolo del libro
        Label titleLabel = new Label(book.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        titleLabel.setWrapText(true);

        // Informazioni sulla categoria, editore e anno
        String categoriaInfo = "Categoria: Fiction | Editore: " + book.getAuthor() + " | Anno: " + book.getYear();
        Label infoLabel = new Label(categoriaInfo);
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
        infoLabel.setWrapText(true);

        bookInfoContainer.getChildren().addAll(titleLabel, infoLabel);

        // Pulsante di aggiunta sul lato destro
        Button actionButton = new Button();
        actionButton.setPrefHeight(30);

        // Se il libro è già nei consigliati o abbiamo raggiunto il limite, mostra "Rimuovi" (disabilitato)
        if (recommendedBooks.contains(book.getTitle())) {
            actionButton.setText("Rimuovi");
            actionButton.setStyle("-fx-background-color: #FF4136; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 15;");
            actionButton.setDisable(true);
        } else if (recommendedBooks.size() >= 3) {
            actionButton.setText("Aggiungi");
            actionButton.setStyle("-fx-background-color: #75B965; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 15;");
            actionButton.setDisable(true);
        } else {
            actionButton.setText("Aggiungi");
            actionButton.setStyle("-fx-background-color: #75B965; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 15;");

            actionButton.setOnAction(event -> {
                if (recommendedBooks.size() < 3 && !recommendedBooks.contains(book.getTitle())) {
                    recommendedBooks.add(book.getTitle());
                    recommendedBooksListView.getItems().add(book.getTitle());
                    updateRecommendationsCount();

                    // Cambia il pulsante dopo l'aggiunta
                    actionButton.setText("Rimuovi");
                    actionButton.setStyle("-fx-background-color: #FF4136; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 15;");
                    actionButton.setDisable(true);
                }
            });
        }

        // Aggiungi spazio flessibile tra le informazioni e il pulsante
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        rowContainer.getChildren().addAll(bookInfoContainer, spacer, actionButton);
        return rowContainer;
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
    }

    /**
     * Gestisce il salvataggio dei consigli.
     */
    @FXML
    public void handleSave(ActionEvent event) {
        // Controlla se ci sono libri consigliati
        if (recommendedBooks.isEmpty()) {
            // Crea un alert dialog per avvisare l'utente
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Nessun libro consigliato");
            alert.setHeaderText("Nessun libro consigliato");
            alert.setContentText("Vuoi tornare indietro?");

            // Personalizza i pulsanti
            ButtonType buttonTypeSi = new ButtonType("Sì");
            ButtonType buttonTypeNo = new ButtonType("No");

            alert.getButtonTypes().setAll(buttonTypeSi, buttonTypeNo);

            // Mostra l'alert e attendi la risposta
            alert.showAndWait().ifPresent(result -> {
                if (result == buttonTypeSi) {
                    // Se l'utente sceglie "Sì", torna al menu
                    navigateToUserMenu(event);
                }
                // Se l'utente sceglie "No", non fare nulla e rimani nella schermata
            });

            return; // Esci dalla funzione senza salvare
        }

        // Se ci sono libri consigliati, continua con il salvataggio
        if (saveRecommendations()) {
            // Mostra un messaggio di successo
            errorLabel.setText("Consigli salvati con successo!");
            errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 18px;");
            errorLabel.setVisible(true);
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
            // Crea la directory se non esiste
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

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