package com.example.queue_demo;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class AnimatedVBox extends VBox {

    double animationSpeed = 200;
    double height = 0;

    AnimatedVBox(){
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: rgb(45,45,45);");
        this.setMinWidth(150);
        this.setFillWidth(true);
    }


    public void add(int index, Object object){

    }

    public void add(Child child){

        height+= 50;
        this.setMaxHeight(height);
        Timeline heightAnimation = animateMinHeight(height);
        heightAnimation.setOnFinished((e) -> {
            // add item with opacity 0, then fade it in
            this.getChildren().add(child);
            FadeTransition fadeTransition = fadeIn(child);
            fadeTransition.setOnFinished((k) -> {
                child.setAnimating(false);
            });
            fadeTransition.play();
        });
        child.setAnimating(true);
        heightAnimation.play();
    }


    public void remove(Child child){
        if(this.getChildren().contains(child)){
            height -= 50;
            this.setMinHeight(height);

            FadeTransition fadeTransition = fadeOut(child);
            fadeTransition.setOnFinished((e) -> {
                Timeline timeline = animateMaxHeight(height);
                int index = this.getChildren().indexOf(child);

                ArrayList<Child> childrenToBeMoved = new ArrayList<>();

                ParallelTransition parallelTransition = new ParallelTransition();
                parallelTransition.getChildren().add(timeline);

                if(index < this.getChildren().size() - 1){
                    // removed child was not the last inside the vbox, have to translate upwards all nodes that were below
                    for(int i = index; i < this.getChildren().size(); i++){
                        childrenToBeMoved.add((Child) this.getChildren().get(i));
                        parallelTransition.getChildren().add(animateUp((Child) this.getChildren().get(i)));
                        ((Child) this.getChildren().get(i)).setAnimating(true);
                    }
                }
                parallelTransition.setOnFinished((ev) -> {
                    this.getChildren().remove(child);
                    for(Child child1 : childrenToBeMoved){
                        child1.setAnimating(false);
                        child1.setTranslateY(0);
                    }
                });

                parallelTransition.playFromStart();

                // decrease max height by 50, apply translate of -50 to all nodes below the one that will be removed and on end actually remove the node and reset translate
            });

            child.setAnimating(true);
            fadeTransition.playFromStart();
        }
    }

    public void move(int oldIndex, int newIndex){
        // move to bottom if newIndex = -1
    }

    private Timeline animateMinHeight(double newHeight){
        Duration animationDuration = Duration.millis(animationSpeed);
        Timeline minTimeline = new Timeline(new KeyFrame(animationDuration,
                new KeyValue(this.minHeightProperty(),newHeight, Interpolator.EASE_BOTH)));
        return minTimeline;
    }

    private Timeline animateMaxHeight(double newHeight){
        Duration animationDuration = Duration.millis(animationSpeed);
        Timeline maxTimeline = new Timeline(new KeyFrame(animationDuration,
                new KeyValue(this.maxHeightProperty(),newHeight, Interpolator.EASE_BOTH)));
        return maxTimeline;
    }

    private FadeTransition fadeIn(Child child){
        Duration animationDuration = Duration.millis(animationSpeed);
        FadeTransition fadeTransition = new FadeTransition(animationDuration, child);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        return fadeTransition;
    }

    private FadeTransition fadeOut(Child child){
        Duration animationDuration = Duration.millis(animationSpeed);
        FadeTransition fadeTransition = new FadeTransition(animationDuration, child);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        return fadeTransition;
    }

    private TranslateTransition animateUp(Child child){
        Duration animationDuration = Duration.millis(animationSpeed);
        TranslateTransition translateTransition = new TranslateTransition(animationDuration, child);
        translateTransition.setFromY(0);
        translateTransition.setToY(-50);
        return translateTransition;
    }
}
