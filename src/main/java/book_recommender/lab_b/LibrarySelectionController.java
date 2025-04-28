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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la selezione delle librerie.
 * Questo controller gestisce l'interfaccia utente per selezionare una libreria.
 */
public class LibrarySelectionController {

    @FXML
    private Label userIdLabel;

    @FXML
    private ListView<String> librariesListView;

    @FXML
    private Label noLibrariesLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Button selectButton;

    @FXML
    private Button backButton;

    @FXML
    private Button cancelButton;

    private String userId;
    private List<String> libraries = new ArrayList<>();

    /**
     * Inizializza il controller.
     * Questo metodo viene chiamato automaticamente da JavaFX dopo che l'FXML è stato caricato.
     */
    public void initialize() {
        // Nasconde i messaggi di errore all'inizio
        errorLabel.setVisible(false);
        noLibrariesLabel.setVisible(false);

        // Aggiungi event listener per il tasto Enter nella ListView
        librariesListView.setOnKeyPressed(this::handleKeyPress);
    }

    /**
     * Gestisce gli eventi di pressione tasti nella ListView.
     * Se viene premuto Enter, attiva la selezione come se fosse stato premuto il pulsante Seleziona.
     *
     * @param event l'evento di pressione del tasto
     */
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String selectedLibrary = librariesListView.getSelectionModel().getSelectedItem();
            if (selectedLibrary != null) {
                // Simula la pressione del pulsante seleziona
                handleSelect(new ActionEvent(event.getSource(), null));
            }
        }
    }

    /**
     * Imposta i dati dell'utente.
     * Questo metodo deve essere chiamato dopo il caricamento del controller.
     *
     * @param userId l'ID dell'utente che ha effettuato l'accesso
     */
    public void setUserId(String userId) {
        this.userId = userId;
        userIdLabel.setText(userId);

        // Carica le librerie dell'utente dal file CSV
        loadUserLibraries(userId);

        // Verifica se ci sono librerie disponibili
        if (libraries.isEmpty()) {
            noLibrariesLabel.setVisible(true);
            selectButton.setDisable(true);
        } else {
            noLibrariesLabel.setVisible(false);
            selectButton.setDisable(false);
        }
    }

    /**
     * Carica le librerie dell'utente dal file CSV.
     * @param userId ID dell'utente di cui caricare le librerie
     */
    private void loadUserLibraries(String userId) {
        String csvFilePath = "data/Librerie.dati.csv"; // Il percorso del file CSV
        libraries.clear(); // Pulisce eventuali librerie precedenti
        librariesListView.getItems().clear(); // Pulisce la ListView

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Salta la prima riga che contiene intestazioni
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // La prima riga contiene sempre le intestazioni
                }

                // Divide la riga usando gli spazi come separatori, poiché il file usa spazi anziché virgole
                String[] data = line.trim().split("\\s+");

                // Verifica che ci siano almeno due campi e che l'ID utente corrisponda
                if (data.length >= 2 && data[0].trim().equals(userId.trim())) {
                    // Aggiunge il nome della libreria alla lista
                    String libraryName = data[1].trim();
                    // Evita duplicati
                    if (!libraries.contains(libraryName)) {
                        libraries.add(libraryName);
                    }
                }
            }

            // Popola la ListView con le librerie trovate
            if (!libraries.isEmpty()) {
                librariesListView.getItems().addAll(libraries);
            }

        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file Librerie.dati.csv: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Errore: Impossibile caricare le librerie. " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    /**
     * Gestisce il click sul pulsante "Seleziona".
     * Seleziona la libreria scelta e naviga alla schermata di selezione del libro.
     */
    @FXML
    public void handleSelect(ActionEvent event) {
        String selectedLibrary = librariesListView.getSelectionModel().getSelectedItem();

        if (selectedLibrary == null) {
            // Nessuna libreria selezionata, mostra errore
            errorLabel.setText("Errore: Seleziona una libreria prima di procedere.");
            errorLabel.setVisible(true);
            return;
        }

        // Nascondi eventuali messaggi di errore
        errorLabel.setVisible(false);

        // Naviga alla schermata di selezione del libro
        try {
            // Carica la pagina di selezione del libro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/selezionaLibro.fxml"));
            Parent root = loader.load();

            // Passa i dati al controller
            BookSelectionController controller = loader.getController();
            controller.setData(userId, selectedLibrary);

            // Imposta la nuova scena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Book Recommender - Seleziona Libro");
            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della pagina di selezione libro: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Errore: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    /**
     * Gestisce il click sul pulsante "Torna al menu".
     * Torna al menu utente senza selezionare alcuna libreria.
     */
    @FXML
    public void handleBack(ActionEvent event) {
        navigateToUserMenu(event);
    }

    /**
     * Gestisce il click sul pulsante "Annulla".
     * Torna al menu utente senza selezionare alcuna libreria.
     */
    @FXML
    public void handleCancel(ActionEvent event) {
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