package com.example.queue_demo;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class AnimatedSwitcher extends StackPane {

    double animationSpeed = 200;
    QueueController queueController;

    AnimatedSwitcher(QueueController queueController){
        this.queueController = queueController;
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: white");
        this.setMinSize(150, 50);
        this.setPrefSize(150, 50);
        this.setMaxSize(150, 50);
    }

    public void clear(){
        if(this.getChildren().isEmpty()) return;

        Node child = this.getChildren().get(0);
        FadeTransition fadeTransition = fadeOut(child);
        fadeTransition.setOnFinished(e -> {
            this.getChildren().clear();
            queueController.animationsInProgress.remove(fadeTransition);
        });

        queueController.animationsInProgress.add(fadeTransition);
        fadeTransition.playFromStart();
    }

    public void set(Node node){

        if(this.getChildren().isEmpty()){
            // add new item

            this.getChildren().add(node);
            FadeTransition fadeTransition = fadeIn(node);
            fadeTransition.setOnFinished(e -> {
                queueController.animationsInProgress.remove(fadeTransition);
            });

            queueController.animationsInProgress.add(fadeTransition);
            fadeTransition.playFromStart();
        }
        else {
            // remove old, add new item

            Node oldChild = this.getChildren().get(0);
            FadeTransition fadeTransition = fadeOut(oldChild);
            fadeTransition.setOnFinished(e -> {
                this.getChildren().set(0, node);
                FadeTransition fadeTransition1 = fadeIn(node);
                fadeTransition1.setOnFinished(j -> {
                    queueController.animationsInProgress.remove(fadeTransition1);
                });

                queueController.animationsInProgress.remove(fadeTransition);
                queueController.animationsInProgress.add(fadeTransition1);
                fadeTransition1.playFromStart();
            });

            queueController.animationsInProgress.add(fadeTransition);
            fadeTransition.playFromStart();
        }
    }


    public FadeTransition fadeIn(Node child){
        Duration animationDuration = Duration.millis(animationSpeed);
        FadeTransition fadeTransition = new FadeTransition(animationDuration, child);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        return fadeTransition;
    }

    public FadeTransition fadeOut(Node child){
        Duration animationDuration = Duration.millis(animationSpeed);
        FadeTransition fadeTransition = new FadeTransition(animationDuration, child);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        return fadeTransition;
    }

}
