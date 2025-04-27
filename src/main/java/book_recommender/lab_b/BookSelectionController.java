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
 * Controller per la selezione dei libri da una libreria.
 * Questo controller gestisce l'interfaccia utente per selezionare un libro da aggiungere.
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

        // Inizializza la lista dei libri con dati di esempio
        // In un'applicazione reale, questi dati verrebbero caricati da un database
        loadSampleBooks();
    }

    /**
     * Imposta i dati dell'utente e della libreria.
     * Questo metodo deve essere chiamato dopo il caricamento del controller.
     *
     * @param userId l'ID dell'utente che ha effettuato l'accesso
     * @param libraryName il nome della libreria selezionata
     */
    public void setData(String userId, String libraryName) {
        this.userId = userId;
        this.libraryName = libraryName;

        userIdLabel.setText(userId);
        libraryNameLabel.setText("Libreria: " + libraryName);

        // Verifica se ci sono libri nella libreria
        if (books.isEmpty()) {
            noBooksLabel.setVisible(true);
            selectButton.setDisable(true);
        } else {
            noBooksLabel.setVisible(false);
            selectButton.setDisable(false);
        }
    }

    /**
     * Carica libri di esempio nella lista.
     * In un'applicazione reale, questi dati verrebbero caricati da un database.
     */
    private void loadSampleBooks() {
        books.add("Il nome della rosa - Umberto Eco");
        books.add("1984 - George Orwell");
        books.add("Il Signore degli Anelli - J.R.R. Tolkien");
        books.add("Harry Potter e la pietra filosofale - J.K. Rowling");
        books.add("Orgoglio e pregiudizio - Jane Austen");

        // Popola la ListView con i libri
        booksListView.getItems().addAll(books);
    }

    /**
     * Gestisce il click sul pulsante "Seleziona".
     * Seleziona il libro scelto e procede all'azione successiva (es. aggiunta a una libreria).
     */
    @FXML
    public void handleSelect(ActionEvent event) {
        String selectedBook = booksListView.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            // Nessun libro selezionato, mostra errore
            errorLabel.setVisible(true);
            return;
        }

        // Nascondi eventuali messaggi di errore
        errorLabel.setVisible(false);

        // Qui andrà la logica per gestire la selezione del libro
        System.out.println("Libro selezionato: " + selectedBook + " dalla libreria: " + libraryName);

        // Per esempio, potresti voler tornare al menu utente con un messaggio di successo
        try {
            // Carica il menu utente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/book_recommender/lab_b/userMenu.fxml"));
            Parent root = loader.load();

            // Ottieni il controller e passa i dati dell'utente
            UserMenuController controller = loader.getController();
            controller.setUserData(userId);
            controller.setStatusMessage("Libro '" + selectedBook + "' aggiunto con successo alla libreria!");

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

    /**
     * Gestisce il click sul pulsante "Torna al menu".
     * Torna al menu utente senza selezionare alcun libro.
     */
    @FXML
    public void handleBack(ActionEvent event) {
        navigateToUserMenu(event);
    }

    /**
     * Gestisce il click sul pulsante "Annulla".
     * Torna al menu utente senza selezionare alcun libro.
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