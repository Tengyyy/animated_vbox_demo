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

public class SwitcherChild extends Region {

    QueueController queueController;

    SwitcherChild(AnimatedSwitcher parent, QueueController queueController){

        this.queueController = queueController;

        this.setMinSize(150, 50);
        this.setPrefSize(150, 50);
        this.setMaxSize(150, 50);

        this.setOpacity(0);
        this.setCursor(Cursor.HAND);
        this.setBackground(createBackground());
        this.setOnMouseClicked((e) -> {
            if(queueController.moveToggle.isSelected()) {
                parent.set(new SwitcherChild(parent, queueController));
            }
            else parent.clear();
        });

    }

    public Background createBackground(){
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        return new Background(new BackgroundFill(Color.rgb(red, green, blue), CornerRadii.EMPTY, Insets.EMPTY));
    }

}
