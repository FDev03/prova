package book_recommender.lab_b;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationController {

    @FXML
    private TextField nomeCognomeField;

    @FXML
    private TextField userIdField;

    @FXML
    private TextField codiceFiscaleField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label nomeCognomeError;

    @FXML
    private Label userIdError;

    @FXML
    private Label userIdExistsError;

    @FXML
    private Label codiceFiscaleError;

    @FXML
    private Label emailError;

    @FXML
    private Label passwordError;

    @FXML
    private Label successMessage;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button registerButton;

    @FXML
    private Button backButton;

    // Pattern per le validazioni
    private final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private final Pattern CF_PATTERN = Pattern.compile("^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$");
    private final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    // Percorso relativo al file CSV degli utenti
    private final String USERS_FILE_PATH = "data/UtentiRegistrati.csv";

    @FXML
    public void handleRegistration(ActionEvent event) {
        // Nascondi tutti i messaggi di errore precedenti
        hideAllErrors();

        // Flag per tenere traccia delle validazioni
        boolean isValid = true;

        // Validazione nome e cognome
        if (nomeCognomeField.getText().trim().isEmpty()) {
            nomeCognomeError.setVisible(true);
            isValid = false;
        }

        // Validazione UserID
        String userId = userIdField.getText().trim();
        if (userId.length() > 8) {
            userIdError.setVisible(true);
            isValid = false;
        } else if (isUserIdExists(userId)) {
            userIdExistsError.setVisible(true);
            isValid = false;
        }

        // Validazione codice fiscale
        if (!isValidCodiceFiscale(codiceFiscaleField.getText().trim())) {
            codiceFiscaleError.setVisible(true);
            isValid = false;
        }

        // Validazione email
        if (!isValidEmail(emailField.getText().trim())) {
            emailError.setVisible(true);
            isValid = false;
        }

        // Validazione password
        if (!isValidPassword(passwordField.getText())) {
            passwordError.setVisible(true);
            isValid = false;
        }

        // Se tutti i campi sono validi, procedi con la registrazione
        if (isValid) {
            // Salva l'utente nel file CSV
            if (saveUserToFile()) {
                // Registrazione completata, naviga al menu utente
                navigateToUserMenu(event, userIdField.getText().trim());
            } else {
                // Mostra un messaggio di errore generale
                successMessage.setText("Errore durante la registrazione. Riprova.");
                successMessage.setVisible(true);
            }
        }
    }

    /**
     * Verifica se l'UserID esiste già nel file CSV
     */
    private boolean isUserIdExists(String userId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE_PATH))) {
            String line;
            // Salta l'intestazione
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 4 && fields[3].trim().equals(userId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file degli utenti: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Salva i dati dell'utente nel file CSV
     */
    private boolean saveUserToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE_PATH, true))) {
            // Prepara una nuova riga CSV con i dati dell'utente
            String userData = String.format("%s,%s,%s,%s,%s",
                    nomeCognomeField.getText().trim(),
                    codiceFiscaleField.getText().trim(),
                    emailField.getText().trim(),
                    userIdField.getText().trim(),
                    passwordField.getText());

            // Scrive la nuova riga
            writer.write(userData);
            writer.newLine();


            return true;
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file degli utenti: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gestisce il click sul pulsante "Torna al menu principale"
     */
    @FXML
    public void handleBack(ActionEvent event) {
        try {
            // Torna alla pagina principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/homepage.fxml"));
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
        }
    }

    /**
     * Naviga al menu utente.
     *
     * @param event L'evento che ha scatenato la navigazione
     * @param userId L'ID dell'utente registrato
     */
    private void navigateToUserMenu(ActionEvent event, String userId) {
        try {
            // Carica il file FXML del menu utente
            String fxmlFile = "/book_recommender/lab_b/userMenu.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            if (getClass().getResource(fxmlFile) == null) {
                throw new IOException("File FXML non trovato: " + fxmlFile);
            }

            Parent root = loader.load();

            // Ottieni il controller e passa il nome utente
            UserMenuController controller = loader.getController();
            controller.setUserData(userId);
            controller.setStatusMessage("Registrazione completata con successo! Benvenuto/a " + userId);

            // Ottieni lo stage corrente
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Imposta la nuova scena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Book Recommender - Menu Utente");
            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento del menu utente: " + e.getMessage());
            e.printStackTrace();

            // Mostra un messaggio di errore all'utente
            successMessage.setText("Errore: " + e.getMessage());
            successMessage.setVisible(true);
        }
    }

    // Metodi di utilità per la validazione
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    private boolean isValidCodiceFiscale(String cf) {
        if (cf == null || cf.isEmpty()) {
            return false;
        }
        Matcher matcher = CF_PATTERN.matcher(cf);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }

    private void hideAllErrors() {
        nomeCognomeError.setVisible(false);
        userIdError.setVisible(false);
        userIdExistsError.setVisible(false);
        codiceFiscaleError.setVisible(false);
        emailError.setVisible(false);
        passwordError.setVisible(false);
        successMessage.setVisible(false);
    }

    private void disableFields() {
        nomeCognomeField.setDisable(true);
        userIdField.setDisable(true);
        codiceFiscaleField.setDisable(true);
        emailField.setDisable(true);
        passwordField.setDisable(true);
        registerButton.setDisable(true);
    }
}