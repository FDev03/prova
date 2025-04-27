package book_recommender.lab_b;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale dell'applicazione Book Recommender.
 */
public class Homepage extends Application {

    // Dimensioni standard per tutte le pagine dell'applicazione
    public static final double APP_WIDTH = 800.0;
    public static final double APP_HEIGHT = 600.0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carica la pagina principale
        Parent root = FXMLLoader.load(getClass().getResource("/book_recommender/lab_b/homepage.fxml"));

        // Imposta titolo e scena con dimensioni fisse
        primaryStage.setTitle("Book Recommender");
        Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
        primaryStage.setScene(scene);

        // Impedisce il ridimensionamento manuale della finestra
        primaryStage.setResizable(false);

        // Mostra la finestra
        primaryStage.show();
    }

    /**
     * Metodo main dell'applicazione.
     * @param args argomenti da linea di comando
     */
    public static void main(String[] args) {
        launch(args);
    }
}