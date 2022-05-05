package com.example.queue_demo;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
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

public class ActiveChild extends Region {

    MenuController menuController;
    ActiveItem activeItem;

    int red = 0;
    int green = 0;
    int blue = 0;

    ActiveChild(ActiveItem activeItem, MenuController menuController){

        this.menuController = menuController;
        this.activeItem = activeItem;

        this.setMinSize(150, 50);
        this.setPrefSize(150, 50);
        this.setMaxSize(150, 50);

        this.setOpacity(0);
        this.setCursor(Cursor.HAND);
        this.setBackground(createBackground());
        this.setOnMouseClicked((e) -> {
            if(!menuController.animationsInProgress.isEmpty()) return;

            this.remove();
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

    public int getRed(){
        return red;
    }

    public int getGreen(){
        return green;
    }

    public int getBlue(){
        return blue;
    }

    public void remove() {
        if (menuController.historyBox.index == -1 && !menuController.queueBox.getChildren().isEmpty()) {
            // play next item from queue
            QueueChild queueChild = (QueueChild) menuController.queueBox.getChildren().get(0);
            queueChild.play(false);
        } else if (menuController.historyBox.index != -1 && menuController.historyBox.index < menuController.historyBox.getChildren().size() - 1) {
            // play next item from history
            HistoryChild historyChild = (HistoryChild) menuController.historyBox.getChildren().get(menuController.historyBox.index + 1);
            historyChild.play();
        } else if (menuController.historyBox.index == menuController.historyBox.getChildren().size() - 1 && !menuController.queueBox.getChildren().isEmpty()) {
            // play next item from queue, set last item in history inactive
            QueueChild queueChild = (QueueChild) menuController.queueBox.getChildren().get(0);
            queueChild.play(true);
        } else {
            activeItem.clear();
        }

    }

}
