package com.example.queue_demo;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.Random;

public class Child extends Region {

    boolean animating = false;

    Child(AnimatedVBox parent){
        this.setMinSize(150, 50);
        this.setPrefSize(150, 50);
        this.setMaxSize(150, 50);

        this.setCursor(Cursor.HAND);
        this.setBackground(createBackground());
        this.setOnMouseClicked((e) -> {
            if(!animating) parent.remove(this);
        });

    }

    public Background createBackground(){
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        return new Background(new BackgroundFill(Color.rgb(red, green, blue), CornerRadii.EMPTY, Insets.EMPTY));
    }

    public void setAnimating(boolean value){
        this.animating = value;
    }
}
