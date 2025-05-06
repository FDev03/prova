module book_recommender.lab_b {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.net.http;
    requires javafx.graphics;

    opens book_recommender.lab_b to javafx.fxml;
    exports book_recommender.lab_b;
}