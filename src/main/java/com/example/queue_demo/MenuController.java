package com.example.queue_demo;

import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class MenuController {

    @FXML
    StackPane stackPane, historyWrapper;

    @FXML
    ScrollPane scrollPane;

    @FXML
    Button addButton, addRandButton, switcherAddButton, shuffleButton, playPreviousButton, playNextButton, historyButton;

    @FXML
    CheckBox playToggle, shufflePlayToggle;

    @FXML
    VBox scrollContent;

    HistoryBox historyBox;
    ActiveItem activeItem;
    QueueBox queueBox;

    ArrayList<Animation> animationsInProgress = new ArrayList<>();


    @FXML
    public void initialize(){

        historyBox = new HistoryBox(this, historyWrapper);
        activeItem = new ActiveItem(this);
        queueBox = new QueueBox(this);

        scrollContent.setSpacing(30);
        historyWrapper.getChildren().add(historyBox);
        scrollContent.getChildren().addAll(activeItem, queueBox);

        historyWrapper.setMinHeight(0);
        historyWrapper.setMaxHeight(0);

        Rectangle rectangle = new Rectangle();
        rectangle.heightProperty().bind(historyWrapper.heightProperty());
        rectangle.widthProperty().bind(historyWrapper.widthProperty());
        historyWrapper.setClip(rectangle);
    }

    public void add(){
        queueBox.add(new QueueChild(queueBox, this));
    }

    public void addToSwitcher(){
        activeItem.set(new ActiveChild(activeItem, this), false);
    }

    public void addRand(){
        Random random = new Random();
        queueBox.add(random.nextInt(Math.max(queueBox.getChildren().size(), 1)), new QueueChild(queueBox, this));
    }

    public void clear(){
        queueBox.clear();
    }

    public void shuffle(){
        queueBox.shuffle();
    }

    public void playPrevious(){

        if(!animationsInProgress.isEmpty()) return;

        if(!historyBox.getChildren().isEmpty() && historyBox.index == -1){
            // play most recent item in history
            HistoryChild historyChild = (HistoryChild) historyBox.getChildren().get(historyBox.getChildren().size() -1);
            historyChild.play();
        }
        else if(historyBox.index > 0){

            // play previous item
            HistoryChild historyChild = (HistoryChild) historyBox.getChildren().get(historyBox.index -1);
            historyChild.play();
        }
    }

    public void historyClick(){
        if(historyBox.open){
            historyBox.close();
        }
        else {
            historyBox.open();
        }
    }

    public void playNext(){
        if(!animationsInProgress.isEmpty()) return;

        if(historyBox.index != -1 && historyBox.index < historyBox.getChildren().size() -1){
            // play next video inside history
            HistoryChild historyChild = (HistoryChild) historyBox.getChildren().get(historyBox.index + 1);
            historyChild.play();

        }
        else if((historyBox.index == historyBox.getChildren().size() -1 || historyBox.index == -1) && !queueBox.getChildren().isEmpty()) {
            // play first item in queue

            QueueChild queueChild = (QueueChild) queueBox.getChildren().get(0);
            queueChild.play(true);

        }
    }

}