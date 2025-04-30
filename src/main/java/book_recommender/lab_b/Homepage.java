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

    // Dimensioni iniziali per la finestra dell'applicazione
    public static final double INITIAL_WIDTH = 800.0;
    public static final double INITIAL_HEIGHT = 600.0;

    // Dimensioni minime per la finestra dell'applicazione
    public static final double MIN_WIDTH = 600.0;
    public static final double MIN_HEIGHT = 400.0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carica la pagina principale
        Parent root = FXMLLoader.load(getClass().getResource("/book_recommender/lab_b/homepage.fxml"));

        // Imposta titolo e scena con dimensioni iniziali
        primaryStage.setTitle("Book Recommender");
        Scene scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT);
        primaryStage.setScene(scene);

        // Imposta le dimensioni minime della finestra
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);

        // Permette il ridimensionamento della finestra
        primaryStage.setResizable(true);

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