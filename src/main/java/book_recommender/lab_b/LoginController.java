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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class LoginController {

    @FXML
    private TextField userIdField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessage;

    // Percorso relativo al file CSV degli utenti
    private final String USERS_FILE_PATH = "data/UtentiRegistrati.csv";

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

    // Metodo per verificare le credenziali usando il file CSV
    private boolean isValidLogin(String userId, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE_PATH))) {
            String line;
            // Salta l'intestazione (prima riga)
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                // Verifica che l'array abbia almeno 5 elementi
                if (fields.length >= 5) {
                    // Nome e Cognome,Codice Fiscale,Email,UserID,Password
                    String storedUserId = fields[3].trim();
                    String storedPassword = fields[4].trim();

                    if (userId.equals(storedUserId) && password.equals(storedPassword)) {

                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file degli utenti: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Metodo per navigare al menu utente
    private void navigateToUserMenu(ActionEvent event, String userId) {
        try {
            // Carica il file FXML del menu utente
            String fxmlFile = "/book_recommender/lab_b/userMenu.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            // Debug: stampa i dettagli del percorso


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