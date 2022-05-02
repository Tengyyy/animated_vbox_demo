package com.example.queue_demo;

import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Random;

public class QueueController {

    @FXML
    StackPane stackPane;

    @FXML
    ScrollPane scrollPane;

    @FXML
    Button addButton, addRandButton, switcherAddButton, shuffleButton;

    @FXML
    CheckBox moveToggle, randomToggle;

    @FXML
    VBox scrollContent;

    AnimatedVBox historyBox = new AnimatedVBox(this);
    AnimatedSwitcher activeItem = new AnimatedSwitcher(this);
    AnimatedVBox queueBox = new AnimatedVBox(this);
    AnimationWrapper animationWrapper = new AnimationWrapper(this, activeItem, queueBox);

    ArrayList<Animation> animationsInProgress = new ArrayList<>();


    @FXML
    public void initialize(){
        scrollContent.setSpacing(30);
        scrollContent.getChildren().addAll(historyBox, activeItem, queueBox);
    }

    public void add(){
        queueBox.add(new VBoxChild(queueBox, this));
    }

    public void addToSwitcher(){
        activeItem.set(new SwitcherChild(activeItem, this));
    }

    public void addRand(){
        Random random = new Random();
        queueBox.add(random.nextInt(Math.max(queueBox.getChildren().size(), 1)), new VBoxChild(queueBox, this));
    }

    public void clear(){
        queueBox.clear();
    }

    public void shuffle(){
        queueBox.shuffle();
    }

}