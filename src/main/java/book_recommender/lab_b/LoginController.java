package book_recommender.lab_b;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField userIdField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessage;

    /**
     * Gestisce l'evento di pressione tasto nella finestra di login
     * Utile per attivare il login quando si preme Invio
     */
    @FXML
    public void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin(new ActionEvent());
        }
    }

    /**
     * Gestisce il click sul pulsante "Login" o la pressione del tasto Invio
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        String userId = userIdField.getText();
        String password = passwordField.getText();

        // Verifica credenziali
        if (isValidLogin(userId, password)) {
            // Login riuscito - vai alla pagina del menu utente
            errorMessage.setVisible(false);
            navigateToUserMenu(event, userId);
        } else {
            // Login fallito - mostra messaggio di errore
            errorMessage.setVisible(true);
        }
    }

    // Metodo per verificare le credenziali
    private boolean isValidLogin(String userId, String password) {
        // Credenziali valide per il test
        final String VALID_USER_ID = "admin";
        final String VALID_PASSWORD = "password123";

        // Verifica se le credenziali corrispondono
        return userId.equals(VALID_USER_ID) && password.equals(VALID_PASSWORD);

        // Nota: in una applicazione reale, dovresti verificare le credenziali
        // in un database o file, e la password dovrebbe essere criptata
    }

    // Metodo per navigare al menu utente
    private void navigateToUserMenu(ActionEvent event, String userId) {
        try {
            // Carica il file FXML del menu utente
            String fxmlFile = "/book_recommender/lab_b/userMenu.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            // Debug: stampa i dettagli del percorso
            System.out.println("Tentativo di caricare: " + fxmlFile);
            System.out.println("URL risolto: " + getClass().getResource(fxmlFile));

            if (getClass().getResource(fxmlFile) == null) {
                throw new IOException("File FXML non trovato: " + fxmlFile);
            }

            Parent root = loader.load();

            // Ottieni il controller e passa il nome utente
            UserMenuController controller = loader.getController();
            controller.setUserData(userId);

            // Ottieni lo stage corrente
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Imposta la nuova scena mantenendo le dimensioni
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Book Recommender - Menu Utente");
            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del menu utente: " + e.getMessage());
            e.printStackTrace();

            // Mostra un messaggio di errore all'utente
            errorMessage.setText("Errore: " + e.getMessage());
            errorMessage.setVisible(true);
        }
    }

    /**
     * Gestisce il click sul pulsante "Torna al menu principale"
     */
    @FXML
    public void handleBack(ActionEvent event) {
        try {
            // Torna alla pagina principale
            String fxmlFile = "/book_recommender/lab_b/homepage.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            if (getClass().getResource(fxmlFile) == null) {
                throw new IOException("File FXML non trovato: " + fxmlFile);
            }

            Parent root = loader.load();

            // Ottieni lo stage corrente
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Imposta la nuova scena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Book Recommender");
            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della homepage: " + e.getMessage());
            e.printStackTrace();

            // Mostra un messaggio di errore all'utente
            errorMessage.setText("Errore: " + e.getMessage());
            errorMessage.setVisible(true);
        }
    }
}