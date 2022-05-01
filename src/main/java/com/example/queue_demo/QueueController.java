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
    Button addButton, addRandButton, switcherAddButton;

    @FXML
    CheckBox moveToggle;

    @FXML
    VBox scrollContent;

    AnimatedSwitcher animatedSwitcher = new AnimatedSwitcher(this);
    AnimatedVBox animatedVBox = new AnimatedVBox(this);

    @FXML
    public void initialize(){
        scrollContent.setSpacing(30);
        scrollContent.getChildren().addAll(animatedSwitcher, animatedVBox);
    }



    public void add(){
        animatedVBox.add(new VBoxChild(animatedVBox, this));
    }

    public void addToSwitcher(){
        animatedSwitcher.set(new SwitcherChild(animatedSwitcher, this));
    }

    public void addRand(){
        Random random = new Random();
        animatedVBox.add(random.nextInt(Math.max(animatedVBox.getChildren().size(), 1)), new VBoxChild(animatedVBox, this));
    }

    public void clear(){
        animatedVBox.clear();
    }


}