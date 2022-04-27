module com.example.queue_demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.queue_demo to javafx.fxml;
    exports com.example.queue_demo;
}