package com.example.queue_demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.Random;

public class QueueController {

    @FXML
    StackPane stackPane;

    @FXML
    ScrollPane scrollPane;

    @FXML
    Button addButton, addRandButton;

    @FXML
    CheckBox moveToggle;

    AnimatedVBox animatedVBox = new AnimatedVBox();

    @FXML
    public void initialize(){
        scrollPane.setContent(animatedVBox);
    }



    public void add(){
        animatedVBox.add(new Child(animatedVBox, this));
    }

    public void addRand(){
        Random random = new Random();
        animatedVBox.add(random.nextInt(Math.max(animatedVBox.getChildren().size(), 1)), new Child(animatedVBox, this));
    }

    public void clear(){
        animatedVBox.clear();
    }


}