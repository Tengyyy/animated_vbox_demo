package com.example.queue_demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

public class QueueController {

    @FXML
    StackPane stackPane;

    @FXML
    ScrollPane scrollPane;

    @FXML
    Button addButton;

    AnimatedVBox animatedVBox = new AnimatedVBox();

    @FXML
    public void initialize(){
        scrollPane.setContent(animatedVBox);
    }



    public void add(){
        animatedVBox.add(new Child(animatedVBox));
    }




}