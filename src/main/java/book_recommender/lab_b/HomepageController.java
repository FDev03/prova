package book_recommender.lab_b;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller per la pagina principale (homepage) dell'applicazione Book Recommender.
 * Gestisce la navigazione alle pagine di login e registrazione.
 */
public class HomepageController implements Initializable {

    @FXML
    private Button titleTabButton;

    @FXML
    private Button authorTabButton;

    @FXML
    private Button authorYearTabButton;

    @FXML
    private VBox titlePage;

    @FXML
    private VBox authorPage;

    @FXML
    private VBox authorYearPage;

    /**
     * Metodo chiamato automaticamente dopo che FXML è stato caricato
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Verifico che i riferimenti ai VBox siano corretti
        System.out.println("initialize chiamato");
        System.out.println("titlePage: " + (titlePage != null ? "trovato" : "non trovato"));
        System.out.println("authorPage: " + (authorPage != null ? "trovato" : "non trovato"));
        System.out.println("authorYearPage: " + (authorYearPage != null ? "trovato" : "non trovato"));

        // Imposta la pagina titolo come visibile di default
        if (titlePage != null) titlePage.setVisible(true);
        if (authorPage != null) authorPage.setVisible(false);
        if (authorYearPage != null) authorYearPage.setVisible(false);
    }

    /**
     * Gestisce il click sul pulsante "Login"
     * Naviga alla pagina di login
     */
    @FXML
    public void entrainlogin(ActionEvent event) {
        try {
            // Carica la pagina di login
            String fxmlFile = "/book_recommender/lab_b/login.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            // Debug: stampa i dettagli del percorso
            System.out.println("Tentativo di caricare: " + fxmlFile);
            System.out.println("URL risolto: " + getClass().getResource(fxmlFile));

            if (getClass().getResource(fxmlFile) == null) {
                throw new IOException("File FXML non trovato: " + fxmlFile);
            }

            Parent root = loader.load();

            // Ottieni lo stage corrente
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Imposta la nuova scena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Book Recommender - Login");
            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della pagina di login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gestisce il click sul pulsante "Registrazione"
     * Naviga alla pagina di registrazione
     */
    @FXML
    public void entrainregistrazione(ActionEvent event) {
        try {
            // Carica la pagina di registrazione
            String fxmlFile = "/book_recommender/lab_b/registrazione.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            // Debug: stampa i dettagli del percorso
            System.out.println("Tentativo di caricare: " + fxmlFile);
            System.out.println("URL risolto: " + getClass().getResource(fxmlFile));

            if (getClass().getResource(fxmlFile) == null) {
                throw new IOException("File FXML non trovato: " + fxmlFile);
            }

            Parent root = loader.load();

            // Ottieni lo stage corrente
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Imposta la nuova scena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Book Recommender - Registrazione");
            stage.show();

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della pagina di registrazione: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gestisce il click sui tab di ricerca
     */
    @FXML
    public void onTitleTabClicked(ActionEvent event) {
        // Implementazione per la visualizzazione della ricerca per titolo
        System.out.println("Tab Titolo selezionato");

        // Debug delle variabili
        System.out.println("titlePage: " + (titlePage != null ? "trovato" : "non trovato"));
        System.out.println("authorPage: " + (authorPage != null ? "trovato" : "non trovato"));
        System.out.println("authorYearPage: " + (authorYearPage != null ? "trovato" : "non trovato"));

        try {
            // Aggiorna la visibilità delle pagine
            titlePage.setVisible(true);
            authorPage.setVisible(false);
            authorYearPage.setVisible(false);

            // Aggiorna lo stile dei pulsanti
            titleTabButton.setStyle("-fx-background-color: #4E90E2; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 0;");
            authorTabButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px; -fx-background-radius: 0;");
            authorYearTabButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px; -fx-background-radius: 0;");

            System.out.println("Visibilità pagine aggiornata: titlePage=" + titlePage.isVisible() +
                    ", authorPage=" + authorPage.isVisible() +
                    ", authorYearPage=" + authorYearPage.isVisible());
        } catch (Exception e) {
            System.err.println("Errore durante il cambio tab: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onAuthorTabClicked(ActionEvent event) {
        // Implementazione per la visualizzazione della ricerca per autore
        System.out.println("Tab Autore selezionato");

        // Debug delle variabili
        System.out.println("titlePage: " + (titlePage != null ? "trovato" : "non trovato"));
        System.out.println("authorPage: " + (authorPage != null ? "trovato" : "non trovato"));
        System.out.println("authorYearPage: " + (authorYearPage != null ? "trovato" : "non trovato"));

        try {
            // Aggiorna la visibilità delle pagine
            titlePage.setVisible(false);
            authorPage.setVisible(true);
            authorYearPage.setVisible(false);

            // Aggiorna lo stile dei pulsanti
            titleTabButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px; -fx-background-radius: 0;");
            authorTabButton.setStyle("-fx-background-color: #4E90E2; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 0;");
            authorYearTabButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px; -fx-background-radius: 0;");

            System.out.println("Visibilità pagine aggiornata: titlePage=" + titlePage.isVisible() +
                    ", authorPage=" + authorPage.isVisible() +
                    ", authorYearPage=" + authorYearPage.isVisible());
        } catch (Exception e) {
            System.err.println("Errore durante il cambio tab: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onAuthorYearTabClicked(ActionEvent event) {
        // Implementazione per la visualizzazione della ricerca per autore e anno
        System.out.println("Tab Autore e Anno selezionato");

        // Debug delle variabili
        System.out.println("titlePage: " + (titlePage != null ? "trovato" : "non trovato"));
        System.out.println("authorPage: " + (authorPage != null ? "trovato" : "non trovato"));
        System.out.println("authorYearPage: " + (authorYearPage != null ? "trovato" : "non trovato"));

        try {
            // Aggiorna la visibilità delle pagine
            titlePage.setVisible(false);
            authorPage.setVisible(false);
            authorYearPage.setVisible(true);

            // Aggiorna lo stile dei pulsanti
            titleTabButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px; -fx-background-radius: 0;");
            authorTabButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px; -fx-background-radius: 0;");
            authorYearTabButton.setStyle("-fx-background-color: #4E90E2; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 0;");

            System.out.println("Visibilità pagine aggiornata: titlePage=" + titlePage.isVisible() +
                    ", authorPage=" + authorPage.isVisible() +
                    ", authorYearPage=" + authorYearPage.isVisible());
        } catch (Exception e) {
            System.err.println("Errore durante il cambio tab: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gestisce la ricerca dei libri
     */
    @FXML
    public void cercalibro(ActionEvent event) {
        // Implementazione per la ricerca dei libri
        System.out.println("Ricerca libro");
        // Qui andrà la logica di ricerca dei libri
    }

    /**
     * Gestisce la visualizzazione dei dettagli del libro
     */
    @FXML
    public void visualizzalibro(ActionEvent event) {
        // Implementazione per la visualizzazione dei dettagli del libro
        System.out.println("Visualizza dettagli libro");
        // Qui andrà la logica di visualizzazione dei dettagli del libro
    }
}