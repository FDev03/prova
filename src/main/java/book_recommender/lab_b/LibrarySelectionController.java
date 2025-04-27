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
import javafx.stage.Stage;

import java.io.IOException;
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

        // Inizializza la lista delle librerie con dati di esempio
        // In un'applicazione reale, questi dati verrebbero caricati da un database
        loadSampleLibraries();
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
     * Carica librerie di esempio nella lista.
     * In un'applicazione reale, questi dati verrebbero caricati da un database.
     */
    private void loadSampleLibraries() {
        libraries.add("Libreria Personale");
        libraries.add("Preferiti");
        libraries.add("Libri da leggere");
        libraries.add("Libri letti");
        libraries.add("Classici");

        // Popola la ListView con le librerie
        librariesListView.getItems().addAll(libraries);
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
            errorLabel.setVisible(true);
            return;
        }

        // Nascondi eventuali messaggi di errore
        errorLabel.setVisible(false);

        // Naviga alla schermata di selezione del libro
        try {
            // Carica la pagina di selezione del libro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/selezionalib.fxml"));
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