package com.example.queue_demo;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class Animations {

    static final double ANIMATION_SPEED = 200;

    public static FadeTransition fadeIn(Node child){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        FadeTransition fadeTransition = new FadeTransition(animationDuration, child);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        return fadeTransition;
    }

    public static FadeTransition fadeOut(Node child){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        FadeTransition fadeTransition = new FadeTransition(animationDuration, child);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        return fadeTransition;
    }

    public static TranslateTransition animateUp(Node child, double translate){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        TranslateTransition translateTransition = new TranslateTransition(animationDuration, child);
        translateTransition.setFromY(0);
        translateTransition.setToY(-translate);
        return translateTransition;
    }

    public static TranslateTransition animateDown(Node child, double translate){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        TranslateTransition translateTransition = new TranslateTransition(animationDuration, child);
        translateTransition.setFromY(0);
        translateTransition.setToY(translate);
        return translateTransition;
    }


    public static Timeline animateMinHeight(double newHeight, Region region){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        Timeline minTimeline = new Timeline(new KeyFrame(animationDuration,
                new KeyValue(region.minHeightProperty(),newHeight, Interpolator.EASE_BOTH)));
        return minTimeline;
    }

    public static Timeline animateMaxHeight(double newHeight, Region region){
        Duration animationDuration = Duration.millis(ANIMATION_SPEED);
        Timeline maxTimeline = new Timeline(new KeyFrame(animationDuration,
                new KeyValue(region.maxHeightProperty(),newHeight, Interpolator.EASE_BOTH)));
        return maxTimeline;
    }
}
