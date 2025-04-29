package book_recommender.lab_b;

import java.io.*;
import java.net.URL;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Controller per la gestione dell'aggiunta di libri ad una libreria.
 * Permette di cercare libri per titolo, autore o autore+anno e aggiungerli alla libreria selezionata.
 */
public class AddBooksToLibraryController implements Initializable {

    // Componenti dell'interfaccia utente
    @FXML private Label userIdLabel;
    @FXML private Label libraryNameLabel;
    @FXML private Label selectedBooksCountLabel;
    @FXML private Label errorLabel;

    // Campi di ricerca
    @FXML private TextField titleSearchField;
    @FXML private TextField authorSearchField;
    @FXML private TextField authorYearSearchField;
    @FXML private TextField yearSearchField;

    // Pulsanti
    @FXML private Button titleSearchButton;
    @FXML private Button authorSearchButton;
    @FXML private Button authorYearSearchButton;
    @FXML private Button clearAllButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button backButton;

    // Contenitori per i risultati della ricerca
    @FXML private VBox titleResultsContainer;
    @FXML private VBox authorResultsContainer;
    @FXML private VBox authorYearResultsContainer;

    // Lista dei libri selezionati
    @FXML private ListView<String> selectedBooksListView;

    // Variabili di stato
    private String userId;
    private String libraryName;
    private Set<String> selectedBooks = new HashSet<>();
    private List<Book> searchResults = new ArrayList<>();

    // Percorso del file CSV per le librerie
    private static final String LIBRARIES_FILE_PATH = "data/Librerie.dati.csv";

    /**
     * Inizializza il controller.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Nascondi l'etichetta di errore all'avvio
        errorLabel.setVisible(false);

        // Imposta handler per la pressione del tasto Invio nei campi di ricerca
        setupEnterKeyHandlers();

        // Assicura che il servizio libri sia inizializzato
        BookService.loadBooks();

        // Configura la ListView con i libri selezionati
        setupSelectedBooksListView();

        // Il bottone saveButton è sempre attivo
        saveButton.setDisable(false);

        // Aggiorna lo stato del bottone clearAllButton
        updateClearAllButtonState();
    }

    /**
     * Aggiorna lo stato del pulsante ClearAll in base alla presenza di libri selezionati.
     */
    private void updateClearAllButtonState() {
        boolean hasSelectedBooks = !selectedBooks.isEmpty();
        clearAllButton.setDisable(!hasSelectedBooks);
    }

    /**
     * Configura la ListView per i libri selezionati.
     */
    private void setupSelectedBooksListView() {
        selectedBooksListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Aggiungi un listener per aggiornare lo stato dei pulsanti quando cambia la selezione
        selectedBooksListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateClearAllButtonState();
        });

        // Modifica la visualizzazione delle celle per mostrare meglio i titoli dei libri
        selectedBooksListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> cell = new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            // Creiamo un contenitore orizzontale per l'elemento e il pulsante
                            HBox container = new HBox(10);
                            container.setAlignment(Pos.CENTER_LEFT);

                            // Etichetta per il titolo del libro con wrapping
                            Label titleLabel = new Label(item);
                            titleLabel.setWrapText(true);
                            titleLabel.setMaxWidth(280);
                            HBox.setHgrow(titleLabel, Priority.ALWAYS);

                            // Pulsante ❌ per rimuovere l'elemento
                            Button deleteButton = new Button("❌");
                            deleteButton.setStyle(
                                    "-fx-background-color: transparent; " +  // Sfondo trasparente
                                            "-fx-text-fill: #FF0000; " +     // Testo (❌) rosso
                                            "-fx-font-weight: bold; " +
                                            "-fx-cursor: hand;"
                            );
                            deleteButton.setOnAction(event -> {
                                selectedBooks.remove(item);
                                updateSelectedBooksList();
                                updateSelectedBooksCount();
                                updateClearAllButtonState();
                                updateSearchResults();
                            });

                            container.getChildren().addAll(titleLabel, deleteButton);
                            setGraphic(container);
                            setText(null);
                        }
                    }
                };

                // Aggiungiamo un po' di padding alla cella
                cell.setPadding(new Insets(5, 0, 5, 5));
                return cell;
            }
        });
    }

    /**
     * Gestisce la cancellazione di tutti i libri selezionati.
     */
    @FXML
    public void handleClearAll(ActionEvent event) {
        // Se non ci sono libri selezionati, non fare nulla
        if (selectedBooks.isEmpty()) {
            return;
        }

        // Cancella tutti i libri selezionati senza mostrare alcun avviso
        selectedBooks.clear();

        // Aggiorna la lista dei libri selezionati
        updateSelectedBooksList();

        // Aggiorna il contatore dei libri selezionati
        updateSelectedBooksCount();

        // Aggiorna lo stato dei pulsanti
        updateClearAllButtonState();

        // Aggiorna i pulsanti nei risultati di ricerca
        updateSearchResults();
    }

    /**
     * Gestisce il salvataggio della libreria con i libri selezionati.
     */
    @FXML
    public void handleSave(ActionEvent event) {
        // Se non ci sono libri selezionati, mostra un avviso per confermare l'eliminazione della libreria
        if (selectedBooks.isEmpty()) {
            // Mostra un dialogo di conferma
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Conferma cancellazione");
            alert.setHeaderText("Attenzione");
            alert.setContentText("Non ci sono libri selezionati. La libreria verrà eliminata. Vuoi procedere?");

            ButtonType buttonTypeYes = new ButtonType("Sì");
            ButtonType buttonTypeNo = new ButtonType("No");

            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeYes) {
                // Elimina la libreria e i dati correlati
                deleteLibraryAndRelatedData();

                // Torna al menu utente con un messaggio di successo
                navigateToUserMenuWithMessage(event, "Libreria '" + libraryName + "' eliminata con successo!");
            }
            return;
        }

        // Nascondi eventuali messaggi di errore
        errorLabel.setVisible(false);

        // Salva la libreria nel file CSV
        saveLibraryToFile();

        // Torna al menu utente con un messaggio di successo
        navigateToUserMenuWithMessage(event, "");  }

    /**
     * Elimina la libreria e tutti i dati correlati (valutazioni, voti, consigli).
     */
    private void deleteLibraryAndRelatedData() {
        try {
            // 1. Rimuovi la libreria da Librerie.dati.csv
            deleteLibraryFromFile();

            // 2. Rimuovi le valutazioni dell'utente per questa libreria da valutazioni.csv
            deleteUserRatingsForLibrary();

            // 3. Rimuovi i voti dell'utente per questa libreria da libri.csv
            deleteUserVotesForLibrary();

            // 4. Rimuovi i consigli degli altri utenti per questa libreria
            deleteUserRecommendationsForLibrary();

        } catch (IOException e) {
            System.err.println("Errore durante l'eliminazione della libreria e dei dati correlati: " + e.getMessage());
            e.printStackTrace();

            // Mostra un messaggio di errore
            errorLabel.setText("Errore: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    /**
     * Elimina la libreria dal file Librerie.dati.csv.
     */
    private void deleteLibraryFromFile() throws IOException {
        // Legge tutto il file in memoria
        List<String> allLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(LIBRARIES_FILE_PATH))) {
            String line;
            String header = reader.readLine();
            allLines.add(header); // Aggiungi l'intestazione

            while ((line = reader.readLine()) != null) {
                String[] fields = line.trim().split("\\s+", 3);

                // Se trova la libreria dell'utente, saltala (non aggiungerla alle righe da scrivere)
                if (fields.length >= 2 && fields[0].trim().equals(userId) && fields[1].trim().equals(libraryName)) {
                    // Non aggiungere questa riga
                } else {
                    // Mantieni le altre righe invariate
                    allLines.add(line);
                }
            }
        }

        // Riscrive il file completo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LIBRARIES_FILE_PATH))) {
            for (String line : allLines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Elimina le valutazioni dell'utente per la libreria da valutazioni.csv.
     */
    private void deleteUserRatingsForLibrary() throws IOException {
        final String RATINGS_FILE_PATH = "data/Valutazioni.csv";
        List<String> allLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(RATINGS_FILE_PATH))) {
            String line;
            String header = reader.readLine();
            allLines.add(header); // Aggiungi l'intestazione

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                // Se la valutazione è dell'utente e appartiene alla libreria corrente, saltala
                if (fields.length >= 3 && fields[0].trim().equals(userId) && fields[1].trim().equals(libraryName)) {
                    // Non aggiungere questa riga
                } else {
                    // Mantieni le altre righe invariate
                    allLines.add(line);
                }
            }
        }

        // Riscrive il file completo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RATINGS_FILE_PATH))) {
            for (String line : allLines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Elimina i voti dell'utente per la libreria da libri.csv.
     */
    private void deleteUserVotesForLibrary() throws IOException {
        final String BOOKS_FILE_PATH = "data/Data.csv";
        List<String> allLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE_PATH))) {
            String line;
            String header = reader.readLine();
            allLines.add(header); // Aggiungi l'intestazione

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                // Se il voto è dell'utente e appartiene alla libreria corrente, saltalo
                if (fields.length >= 3 && fields[0].trim().equals(userId) && fields[1].trim().equals(libraryName)) {
                    // Non aggiungere questa riga
                } else {
                    // Mantieni le altre righe invariate
                    allLines.add(line);
                }
            }
        }

        // Riscrive il file completo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE_PATH))) {
            for (String line : allLines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Elimina i consigli degli altri utenti per questa libreria.
     */
    private void deleteUserRecommendationsForLibrary() throws IOException {
        // Assumo che i consigli siano memorizzati in un file chiamato Consigli.csv
        final String RECOMMENDATIONS_FILE_PATH = "data/Consigli.csv";
        List<String> allLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(RECOMMENDATIONS_FILE_PATH))) {
            String line;
            String header = reader.readLine();
            allLines.add(header); // Aggiungi l'intestazione

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                // Se il consiglio è per l'utente e la libreria corrente, saltalo
                if (fields.length >= 3 && fields[0].trim().equals(userId) && fields[1].trim().equals(libraryName)) {
                    // Non aggiungere questa riga
                } else {
                    // Mantieni le altre righe invariate
                    allLines.add(line);
                }
            }
        }

        // Riscrive il file completo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RECOMMENDATIONS_FILE_PATH))) {
            for (String line : allLines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Naviga al menu utente con un messaggio specifico.
     */
    private void navigateToUserMenuWithMessage(ActionEvent event, String message) {
        try {
            // Carica il menu utente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/userMenu.fxml"));
            Parent root = loader.load();

            // Ottieni il controller e passa l'ID utente e il messaggio
            UserMenuController controller = loader.getController();
            controller.setUserData(userId);
            controller.setStatusMessage(message);

            // Imposta la nuova scena
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
     * Configura gli handler per la pressione del tasto Invio nei campi di ricerca.
     */
    private void setupEnterKeyHandlers() {
        titleSearchField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleTitleSearch(new ActionEvent());
            }
        });

        authorSearchField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleAuthorSearch(new ActionEvent());
            }
        });

        authorYearSearchField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleAuthorYearSearch(new ActionEvent());
            }
        });

        yearSearchField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleAuthorYearSearch(new ActionEvent());
            }
        });
    }

    /**
     * Imposta i dati dell'utente e della libreria.
     *
     * @param userId ID dell'utente
     * @param libraryName Nome della libreria
     */
    public void setData(String userId, String libraryName) {
        this.userId = userId;
        this.libraryName = libraryName;

        // Aggiorna le etichette nell'interfaccia
        userIdLabel.setText(userId);
        libraryNameLabel.setText("Libreria: " + libraryName);

        // Carica i libri già presenti nella libreria (se esistenti)
        loadExistingBooks();

        // Aggiorna il contatore dei libri selezionati
        updateSelectedBooksCount();
    }

    /**
     * Carica i libri già presenti nella libreria dal file CSV.
     */
    private void loadExistingBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LIBRARIES_FILE_PATH))) {
            String line;
            // Salta l'intestazione
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] fields = line.trim().split("\\s+");

                // Controlla se la riga corrisponde all'utente e alla libreria
                if (fields.length >= 3 && fields[0].trim().equals(userId) && fields[1].trim().equals(libraryName)) {
                    // Aggiungi tutti i libri non nulli
                    for (int i = 2; i < fields.length; i++) {
                        if (!fields[i].equals("null")) {
                            // Sostituisci underscore con spazi per i titoli dei libri
                            String bookTitle = fields[i].replace("_", " ");
                            selectedBooks.add(bookTitle);
                        }
                    }
                    break;
                }
            }

            // Aggiorna la ListView con i libri caricati
            updateSelectedBooksList();
        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file Librerie.dati.csv: " + e.getMessage());
            e.printStackTrace();

            // Mostra un messaggio di errore
            errorLabel.setText("Errore nel caricamento dei libri esistenti: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    /**
     * Gestisce la ricerca per titolo.
     */
    @FXML
    public void handleTitleSearch(ActionEvent event) {
        String title = titleSearchField.getText().trim();

        if (title.isEmpty()) {
            // Nascondi eventuali messaggi di errore precedenti
            errorLabel.setVisible(false);
            return;
        }

        // Cerca i libri per titolo
        searchResults = BookService.searchBooksByTitle(title);

        // Aggiorna il container dei risultati
        updateSearchResultsContainer(titleResultsContainer, searchResults);
    }

    /**
     * Gestisce la ricerca per autore.
     */
    @FXML
    public void handleAuthorSearch(ActionEvent event) {
        String author = authorSearchField.getText().trim();

        if (author.isEmpty()) {
            // Nascondi eventuali messaggi di errore precedenti
            errorLabel.setVisible(false);
            return;
        }

        // Cerca i libri per autore
        searchResults = BookService.searchBooksByAuthor(author);

        // Aggiorna il container dei risultati
        updateSearchResultsContainer(authorResultsContainer, searchResults);
    }

    /**
     * Gestisce la ricerca per autore e anno.
     */
    @FXML
    public void handleAuthorYearSearch(ActionEvent event) {
        String author = authorYearSearchField.getText().trim();
        String yearText = yearSearchField.getText().trim();

        if (author.isEmpty() || yearText.isEmpty()) {
            // Nascondi eventuali messaggi di errore precedenti
            errorLabel.setVisible(false);
            return;
        }

        try {
            int year = Integer.parseInt(yearText);

            // Cerca i libri per autore e anno
            searchResults = BookService.searchBooksByAuthorAndYear(author, year);

            // Aggiorna il container dei risultati
            updateSearchResultsContainer(authorYearResultsContainer, searchResults);
        } catch (NumberFormatException e) {
            // Mostra un messaggio di errore se l'anno non è un numero valido
            errorLabel.setText("Errore: L'anno deve essere un numero.");
            errorLabel.setVisible(true);
        }
    }

    /**
     * Aggiorna il container dei risultati di ricerca.
     *
     * @param container Il container da aggiornare
     * @param books La lista di libri da visualizzare
     */
    private void updateSearchResultsContainer(VBox container, List<Book> books) {
        // Pulisci il container
        container.getChildren().clear();

        if (books.isEmpty()) {
            // Nessun risultato
            Label noResultsLabel = new Label("Nessun libro trovato.");
            noResultsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777777; -fx-padding: 20px;");
            container.getChildren().add(noResultsLabel);
            return;
        }

        // Aggiungi ogni libro al container
        for (Book book : books) {
            // Crea un HBox per ogni libro
            HBox bookBox = createBookResultBox(book);
            container.getChildren().add(bookBox);
        }
    }

    /**
     * Crea un box per visualizzare un libro nei risultati di ricerca.
     *
     * @param book Il libro da visualizzare
     * @return Un HBox contenente le informazioni del libro e un pulsante per aggiungerlo
     */
    private HBox createBookResultBox(Book book) {
        HBox bookBox = new HBox();
        bookBox.setAlignment(Pos.CENTER_LEFT);
        bookBox.setSpacing(10);
        bookBox.setPadding(new Insets(10));
        bookBox.setStyle("-fx-background-color: white; -fx-border-color: #EEEEEE; -fx-border-radius: 5px;");

        // Informazioni del libro in un contenitore
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // Titolo del libro più grande e in evidenza
        Label titleLabel = new Label(book.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(480); // Imposta una larghezza massima per evitare troncamenti

        // Autore del libro
        Label authorLabel = new Label("Autore: " + book.getAuthors());
        authorLabel.setStyle("-fx-font-size: 14px;");
        authorLabel.setWrapText(true);

        // Dettagli del libro su una riga
        Label detailsLabel = new Label("Categoria: " + book.getCategory() + " | Editore: " + book.getPublisher() + " | Anno: " + book.getPublishYear());
        detailsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777;");
        detailsLabel.setWrapText(true);

        // Aggiungi le etichette al contenitore delle informazioni
        infoBox.getChildren().addAll(titleLabel, authorLabel, detailsLabel);

        // Pulsante per aggiungere o rimuovere il libro
        Button actionButton = new Button();

        // Controlla se il libro è già selezionato
        boolean isSelected = selectedBooks.contains(book.getTitle());

        if (isSelected) {
            actionButton.setText("Rimuovi");
            actionButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
        } else {
            actionButton.setText("Aggiungi");
            actionButton.setStyle("-fx-background-color: #75B965; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
        }

        // Imposta l'evento per il pulsante
        actionButton.setOnAction(e -> toggleBookSelection(book.getTitle(), actionButton));

        // Aggiungi i componenti al box
        bookBox.getChildren().addAll(infoBox, actionButton);

        return bookBox;
    }

    /**
     * Gestisce la selezione/deselezione di un libro.
     *
     * @param bookTitle Il titolo del libro
     * @param button Il pulsante associato al libro
     */
    private void toggleBookSelection(String bookTitle, Button button) {
        // Controlla se il libro è già selezionato
        if (selectedBooks.contains(bookTitle)) {
            // Rimuovi il libro dalla selezione
            selectedBooks.remove(bookTitle);
            button.setText("Aggiungi");
            button.setStyle("-fx-background-color: #75B965; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
        } else {
            // Aggiungi il libro alla selezione
            selectedBooks.add(bookTitle);
            button.setText("Rimuovi");
            button.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
        }

        // Aggiorna la lista dei libri selezionati
        updateSelectedBooksList();

        // Aggiorna il contatore dei libri selezionati
        updateSelectedBooksCount();

        // Aggiorna lo stato del pulsante ClearAll
        updateClearAllButtonState();

        // Nascondi eventuali messaggi di errore
        errorLabel.setVisible(false);
    }

    /**
     * Aggiorna la ListView con i libri selezionati.
     */
    private void updateSelectedBooksList() {
        // Pulisci la ListView
        selectedBooksListView.getItems().clear();

        // Aggiungi i libri selezionati
        List<String> sortedBooks = new ArrayList<>(selectedBooks);
        Collections.sort(sortedBooks);
        selectedBooksListView.getItems().addAll(sortedBooks);
    }

    /**
     * Aggiorna il contatore dei libri selezionati.
     */
    private void updateSelectedBooksCount() {
        selectedBooksCountLabel.setText("Libri totali: " + selectedBooks.size());
    }

    /**
     * Aggiorna i risultati di ricerca in base ai libri selezionati.
     */
    private void updateSearchResults() {
        if (!searchResults.isEmpty()) {
            // Aggiorna i pulsanti nei risultati di ricerca
            // Determina quale tab è attualmente visibile e aggiorna il contenitore corrispondente
            if (!titleResultsContainer.getChildren().isEmpty()) {
                updateSearchResultsContainer(titleResultsContainer, searchResults);
            } else if (!authorResultsContainer.getChildren().isEmpty()) {
                updateSearchResultsContainer(authorResultsContainer, searchResults);
            } else if (!authorYearResultsContainer.getChildren().isEmpty()) {
                updateSearchResultsContainer(authorYearResultsContainer, searchResults);
            }
        }
    }

    /**
     * Gestisce la rimozione dei libri selezionati dalla ListView.
     */
    @FXML
    public void handleRemoveSelected(ActionEvent event) {
        // Ottieni i libri selezionati nella ListView
        List<String> selectedItems = new ArrayList<>(selectedBooksListView.getSelectionModel().getSelectedItems());

        if (selectedItems.isEmpty()) {
            return;
        }

        // Rimuovi i libri selezionati
        for (String book : selectedItems) {
            selectedBooks.remove(book);
        }

        // Aggiorna la lista dei libri selezionati
        updateSelectedBooksList();

        // Aggiorna il contatore dei libri selezionati
        updateSelectedBooksCount();

        // Aggiorna lo stato dei pulsanti
        updateClearAllButtonState();

        // Aggiorna i pulsanti nei risultati di ricerca
        updateSearchResults();
    }

    /**
     * Salva la libreria con i libri selezionati nel file CSV.
     * Modifica il file esistente senza creare righe duplicate.
     */
    private void saveLibraryToFile() {
        try {
            // Legge tutto il file in memoria
            List<String> allLines = new ArrayList<>();
            boolean libraryFound = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(LIBRARIES_FILE_PATH))) {
                String line;
                String header = reader.readLine();
                allLines.add(header); // Aggiungi l'intestazione

                while ((line = reader.readLine()) != null) {
                    String[] fields = line.trim().split("\\s+", 3); // Divide al massimo in 3 parti: userId, libraryName, restante

                    // Se trova la libreria dell'utente
                    if (fields.length >= 2 && fields[0].trim().equals(userId) && fields[1].trim().equals(libraryName)) {
                        libraryFound = true;

                        // Se ci sono ancora libri, aggiungi la riga modificata
                        if (!selectedBooks.isEmpty()) {
                            allLines.add(formatLibraryLine());
                        }
                        // Se non ci sono libri, la libreria verrà cancellata (non aggiungere la riga)
                    } else {
                        // Mantieni le altre righe invariate
                        allLines.add(line);
                    }
                }
            }

            // Se la libreria non è stata trovata e ci sono libri da aggiungere
            if (!libraryFound && !selectedBooks.isEmpty()) {
                allLines.add(formatLibraryLine());
            }

            // Riscrive il file completo
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LIBRARIES_FILE_PATH))) {
                for (String line : allLines) {
                    writer.write(line);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            System.err.println("Errore nel salvataggio della libreria: " + e.getMessage());
            e.printStackTrace();

            // Mostra un messaggio di errore
            errorLabel.setText("Errore nel salvataggio della libreria: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    /**
     * Formatta una riga per il salvataggio della libreria nel file CSV.
     *
     * @return Una riga formattata con l'utente, la libreria e i libri selezionati
     */
    private String formatLibraryLine() {
        StringBuilder sb = new StringBuilder();

        // Aggiungi userId e libraryName
        sb.append(userId).append(" ").append(libraryName);

        // Aggiungi i libri selezionati
        for (String book : selectedBooks) {
            // Sostituisci gli spazi con underscore per i titoli dei libri
            sb.append(" ").append(book.replace(" ", "_"));
        }

        return sb.toString();
    }

    /**
     * Gestisce il click sul pulsante "Annulla".
     */
    @FXML
    public void handleCancel(ActionEvent event) {
        // Torna al menu utente senza salvare
        navigateToUserMenu(event);
    }

    /**
     * Gestisce il click sul pulsante "Torna al menu".
     */
    @FXML
    public void handleBack(ActionEvent event) {
        // Torna al menu utente senza salvare
        navigateToUserMenu(event);
    }

    /**
     * Naviga al menu utente.
     */
    private void navigateToUserMenu(ActionEvent event) {
        try {
            // Carica il menu utente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/userMenu.fxml"));
            Parent root = loader.load();

            // Ottieni il controller e passa l'ID utente
            UserMenuController controller = loader.getController();
            controller.setUserData(userId);

            // Imposta la nuova scena
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
    }}