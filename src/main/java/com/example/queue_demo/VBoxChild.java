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

public class VBoxChild extends Region {

    QueueController queueController;

    int red = 0;
    int green = 0;
    int blue = 0;

    VBoxChild(AnimatedVBox parent, QueueController queueController){

        this.queueController = queueController;

        this.setMinSize(150, 50);
        this.setPrefSize(150, 50);
        this.setMaxSize(150, 50);

        this.setOpacity(0);
        this.setCursor(Cursor.HAND);
        this.setBackground(createBackground());
        this.setOnMouseClicked((e) -> {
            if(!queueController.animationsInProgress.isEmpty()) return;

            if(queueController.moveToggle.isSelected()) {
                queueController.animationWrapper.play(this);
            }
            else if(queueController.randomToggle.isSelected()){
                queueController.animationWrapper.playRandom(this);
            }
            else parent.remove(this);
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

}
