package book_recommender.lab_b;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la selezione dei libri.
 * Questo controller gestisce l'interfaccia utente per selezionare un libro dalla libreria.
 */
public class BookSelectionController {

    @FXML
    private Label userIdLabel;

    @FXML
    private Label libraryNameLabel;

    @FXML
    private ListView<String> booksListView;

    @FXML
    private Label noBooksLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Button selectButton;

    @FXML
    private Button backButton;

    @FXML
    private Button cancelButton;

    private String userId;
    private String libraryName;
    private List<String> books = new ArrayList<>();

    /**
     * Inizializza il controller.
     * Questo metodo viene chiamato automaticamente da JavaFX dopo che l'FXML è stato caricato.
     */
    public void initialize() {
        // Nasconde i messaggi di errore all'inizio
        errorLabel.setVisible(false);
        noBooksLabel.setVisible(false);

        // Aggiungi event listener per il tasto Enter nella ListView
        booksListView.setOnKeyPressed(this::handleKeyPress);
    }

    /**
     * Gestisce gli eventi di pressione tasti nella ListView.
     * Se viene premuto Enter, attiva la selezione come se fosse stato premuto il pulsante Seleziona.
     *
     * @param event l'evento di pressione del tasto
     */
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String selectedBook = booksListView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                // Simula la pressione del pulsante seleziona
                handleSelect(new ActionEvent(event.getSource(), null));
            }
        }
    }

    /**
     * Imposta i dati necessari (userId e libreria selezionata).
     * Questo metodo deve essere chiamato dopo il caricamento del controller.
     *
     * @param userId ID dell'utente
     * @param libraryName nome della libreria selezionata
     */
    public void setData(String userId, String libraryName) {
        this.userId = userId;
        this.libraryName = libraryName;

        userIdLabel.setText(userId);
        libraryNameLabel.setText(libraryName);

        // Carica i libri dell'utente per la libreria selezionata
        loadUserBooks(userId, libraryName);

        // Verifica se ci sono libri disponibili
        if (books.isEmpty()) {
            noBooksLabel.setVisible(true);
            selectButton.setDisable(true);
        } else {
            noBooksLabel.setVisible(false);
            selectButton.setDisable(false);
        }
    }

    /**
     * Carica i libri dell'utente per la libreria selezionata dal file CSV.
     *
     * @param userId ID dell'utente
     * @param libraryName nome della libreria selezionata
     */
    private void loadUserBooks(String userId, String libraryName) {
        String csvFilePath = "data/Librerie.dati.csv"; // Il percorso del file CSV
        books.clear(); // Pulisce eventuali libri precedenti
        booksListView.getItems().clear(); // Pulisce la ListView

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Leggi l'intestazione per identificare gli indici delle colonne
            String headerLine = br.readLine();

            if (headerLine == null) {
                throw new IOException("Il file CSV non contiene dati");
            }

            String[] headers = headerLine.trim().split("\\s+");

            // Cerca la riga dell'utente con la libreria specificata
            while ((line = br.readLine()) != null) {
                String[] data = line.trim().split("\\s+");

                // Verifica che ci siano abbastanza dati e che l'ID utente e la libreria corrispondano
                if (data.length >= 3 && data[0].trim().equals(userId.trim()) && data[1].trim().equals(libraryName.trim())) {
                    // Aggiungi tutti i libri non nulli dalla riga
                    for (int i = 2; i < data.length; i++) {
                        if (!data[i].equals("null")) {
                            // Sostituisci gli underscore con spazi per i titoli dei libri
                            String bookTitle = data[i].replace("_", " ");
                            books.add(bookTitle);
                        }
                    }
                    break; // Abbiamo trovato la riga giusta, possiamo uscire dal ciclo
                }
            }

            // Popola la ListView con i libri trovati
            if (!books.isEmpty()) {
                booksListView.getItems().addAll(books);
            }

        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file Librerie.dati.csv: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Errore: Impossibile caricare i libri. " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    /**
     * Gestisce il click sul pulsante "Seleziona".
     * Seleziona il libro scelto e procede all'operazione successiva.
     */
    @FXML
    public void handleSelect(ActionEvent event) {
        String selectedBook = booksListView.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            // Nessun libro selezionato, mostra errore
            errorLabel.setText("Errore: Seleziona un libro prima di procedere.");
            errorLabel.setVisible(true);
            return;
        }

        // Nascondi eventuali messaggi di errore
        errorLabel.setVisible(false);

        // Qui puoi aggiungere il codice per gestire l'operazione successiva
        // Per esempio, mostrare i dettagli del libro o richiedere consigli

        // Per ora, mostreremo un messaggio di successo
        errorLabel.setText("Libro '" + selectedBook + "' selezionato con successo!");
        errorLabel.setStyle("-fx-text-fill: #75B965;"); // Verde per il successo
        errorLabel.setVisible(true);
    }

    /**
     * Gestisce il click sul pulsante "Torna alla libreria".
     * Torna alla schermata di selezione della libreria.
     */
    @FXML
    public void handleBack(ActionEvent event) {
        navigateToLibrarySelection(event);
    }

    /**
     * Gestisce il click sul pulsante "Annulla".
     * Torna alla schermata di selezione della libreria.
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
            controller.setUserId(userId);

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
}