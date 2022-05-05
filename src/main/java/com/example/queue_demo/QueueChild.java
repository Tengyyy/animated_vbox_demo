package com.example.queue_demo;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public class QueueChild extends Region {

    MenuController menuController;
    QueueBox queueBox;

    int red = 0;
    int green = 0;
    int blue = 0;

    QueueChild(QueueBox queueBox, MenuController menuController){

        this.menuController = menuController;
        this.queueBox = queueBox;

        this.setMinSize(150, 50);
        this.setPrefSize(150, 50);
        this.setMaxSize(150, 50);

        this.setOpacity(0);
        this.setCursor(Cursor.HAND);
        this.setBackground(createBackground());
        this.setOnMouseClicked((e) -> {
            if(!menuController.animationsInProgress.isEmpty()) return;
            if(menuController.playToggle.isSelected()) play(true);
            else queueBox.remove(this);
        });

    }

    public Background createBackground(){
        Random random = new Random();
        red = random.nextInt(256);
        green = random.nextInt(256);
        blue = random.nextInt(256);

        return new Background(new BackgroundFill(Color.rgb(red, green, blue), CornerRadii.EMPTY, Insets.EMPTY));
    }

    public int getRed(){
        return red;
    }

    public int getGreen(){
        return green;
    }

    public int getBlue(){
        return blue;
    }

    public void play(boolean addToHistory){

        if(menuController.historyBox.index == -1 && !menuController.activeItem.getChildren().isEmpty() && addToHistory){
            // add active item to history

            ActiveChild activeChild = (ActiveChild) menuController.activeItem.getChildren().get(0);

            HistoryChild historyChild = new HistoryChild(menuController.historyBox, menuController);
            historyChild.setBackground(historyChild.createBackground(activeChild.getRed(), activeChild.getGreen(), activeChild.getBlue()));

            menuController.historyBox.add(historyChild);
        }
        else if(addToHistory){
            HistoryChild historyChild = (HistoryChild) menuController.historyBox.getChildren().get(menuController.historyBox.index);
            historyChild.setInactive();
        }

        ActiveChild activeChild = new ActiveChild(menuController.activeItem, menuController);
        activeChild.setBackground(activeChild.createBackground(getRed(), getGreen(), getBlue()));

        menuController.activeItem.set(activeChild, true);

        if(menuController.shufflePlayToggle.isSelected()){
            queueBox.remove(this);
        }
        else {
            queueBox.removeAndMove(queueBox.getChildren().indexOf(this));
        }
    }

}
