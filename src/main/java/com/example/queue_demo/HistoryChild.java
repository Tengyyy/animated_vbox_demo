package com.example.queue_demo;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public class HistoryChild extends Region {

    MenuController menuController;
    HistoryBox historyBox;

    int red = 0;
    int green = 0;
    int blue = 0;

    static double height = 50.0;

    HistoryChild(HistoryBox parent, MenuController menuController){

        this.menuController = menuController;
        this.historyBox = parent;

        this.setMinSize(150, 50);
        this.setPrefSize(150, 50);
        this.setMaxSize(150, 50);

        this.setOpacity(0);
        this.setCursor(Cursor.HAND);
        this.setBackground(createBackground());

        this.setOnMouseClicked(e -> {
            if(!menuController.animationsInProgress.isEmpty()) return;
            play();
        });
    }

    public Background createBackground(){
        Random random = new Random();
        red = random.nextInt(256);
        green = random.nextInt(256);
        blue = random.nextInt(256);

        return new Background(new BackgroundFill(Color.rgb(red, green, blue), CornerRadii.EMPTY, Insets.EMPTY));
    }

    public Background createBackground(int red1, int green1, int blue1){
        red = red1;
        green = green1;
        blue = blue1;
        return new Background(new BackgroundFill(Color.rgb(red1, green1, blue1), CornerRadii.EMPTY, Insets.EMPTY));
    }

    public void setActive(){
        if(historyBox.index != -1){
            HistoryChild historyChild = (HistoryChild) historyBox.getChildren().get(historyBox.index);
            historyChild.setMinWidth(150);
            historyChild.setPrefWidth(150);
            historyChild.setMaxWidth(150);
        }

        historyBox.index = historyBox.getChildren().indexOf(this);

        this.setMinWidth(200);
        this.setPrefWidth(200);
        this.setMaxWidth(200);
    }

    public void setInactive(){
        this.setMinWidth(150);
        this.setPrefWidth(150);
        this.setMaxWidth(150);

        historyBox.index = -1;

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

    public void play(){

        if(historyBox.index == -1 && !menuController.activeItem.getChildren().isEmpty()){
            // add active item to history

            ActiveChild activeChild = (ActiveChild) menuController.activeItem.getChildren().get(0);

            HistoryChild historyChild = new HistoryChild(menuController.historyBox, menuController);
            historyChild.setBackground(historyChild.createBackground(activeChild.getRed(), activeChild.getGreen(), activeChild.getBlue()));

            menuController.historyBox.add(historyChild);

            ActiveChild newActive = new ActiveChild(menuController.activeItem, menuController);
            newActive.setBackground(newActive.createBackground(getRed(), getGreen(), getBlue()));
            menuController.activeItem.set(newActive, true);
        }
        else {
            ActiveChild newActive = new ActiveChild(menuController.activeItem, menuController);
            newActive.setBackground(newActive.createBackground(getRed(), getGreen(), getBlue()));
            menuController.activeItem.set(newActive, false);
        }

        this.setActive();
    }
}
