package book_recommender.lab_b;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller per la pagina di creazione della libreria.
 */
public class CreateLibraryController implements Initializable {

    @FXML
    private Label userIdLabel;

    @FXML
    private TextField libraryNameField;

    @FXML
    private Label infoLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Button createButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button backButton;

    private String userId;

    /**
     * Inizializza il controller.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inizializzazione del controller
        System.out.println("CreateLibraryController inizializzato");
    }

    /**
     * Imposta l'ID dell'utente corrente.
     *
     * @param userId l'ID dell'utente
     */
    public void setUserId(String userId) {
        this.userId = userId;
        userIdLabel.setText(userId);
        System.out.println("UserId impostato a: " + userId);
    }

    /**
     * Gestisce il click sul pulsante "Crea e aggiungi libri".
     */
    @FXML
    public void handleCreate(ActionEvent event) {
        String libraryName = libraryNameField.getText().trim();

        if (libraryName.isEmpty()) {
            // Mostra il messaggio di errore se il nome della libreria è vuoto
            errorLabel.setVisible(true);
            return;
        }

        // Nascondi eventuali messaggi di errore precedenti
        errorLabel.setVisible(false);

        // Qui andrebbe la logica per creare la libreria nel database/file
        System.out.println("Creazione libreria: " + libraryName + " per l'utente: " + userId);

        try {
            // Dopo aver creato la libreria, passa alla pagina di aggiunta libri
            // o potrebbe essere la stessa schermata di ricerca ma con un flag che indica
            // che si sta aggiungendo a una libreria specifica

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/homepage.fxml"));
            Parent root = loader.load();

            // Potresti voler passare il nome della libreria e l'ID utente al controller di ricerca/aggiunta libri
            // SearchBooksController controller = loader.getController();
            // controller.setLibraryInfo(userId, libraryName);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Book Recommender - Aggiungi Libri");
            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della pagina di aggiunta libri: " + e.getMessage());
            e.printStackTrace();

            // Mostra un messaggio di errore
            errorLabel.setText("Errore: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    /**
     * Gestisce il click sul pulsante "Annulla".
     */
    @FXML
    public void handleCancel(ActionEvent event) {
        try {
            // Torna al menu utente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/userMenu.fxml"));
            Parent root = loader.load();

            // Passa l'ID utente al controller del menu utente
            UserMenuController controller = loader.getController();
            controller.setUserData(userId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Book Recommender - Menu Utente");
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
     * Gestisce il click sul pulsante "Logout".
     */
    @FXML
    public void handleBack(ActionEvent event) {
        try {
            // Torna alla homepage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/homepage.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Book Recommender");
            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della homepage: " + e.getMessage());
            e.printStackTrace();

            // Mostra un messaggio di errore
            errorLabel.setText("Errore: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }
}