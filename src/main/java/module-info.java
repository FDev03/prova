module book_recommender.lab_b {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;

    opens book_recommender.lab_b to javafx.fxml;
    exports book_recommender.lab_b;
    exports book_recommender.lab_b.nonservira;
    opens book_recommender.lab_b.nonservira to javafx.fxml;
    exports book_recommender.lab_b.base;
    opens book_recommender.lab_b.base to javafx.fxml;
}