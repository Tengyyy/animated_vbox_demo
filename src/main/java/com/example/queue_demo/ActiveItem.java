package com.example.queue_demo;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ActiveItem extends StackPane {

    double animationSpeed = 200;
    MenuController menuController;

    ActiveItem(MenuController queueController){
        this.menuController = queueController;
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: white");
        this.setMinSize(150, 50);
        this.setPrefSize(150, 50);
        this.setMaxSize(150, 50);
    }

    public void clear(){
        if(this.getChildren().isEmpty()) return;

        Node child = this.getChildren().get(0);
        FadeTransition fadeTransition = Animations.fadeOut(child);
        fadeTransition.setOnFinished(e -> {
            this.getChildren().clear();
            menuController.animationsInProgress.remove(fadeTransition);
        });

        menuController.animationsInProgress.add(fadeTransition);
        fadeTransition.playFromStart();
    }

    public void set(Node node, boolean pause){

        if(this.getChildren().isEmpty()){
            // add new item

            // pause transitions are necessary to line up this animation with queue and history animations which are longer
            PauseTransition pause1 = new PauseTransition();
            if(pause) pause1.setDuration(Duration.millis(animationSpeed));
            else pause1.setDuration(Duration.ZERO);

            PauseTransition pause2 = new PauseTransition();
            if(pause) pause2.setDuration(Duration.millis(animationSpeed));
            else pause2.setDuration(Duration.ZERO);


            pause1.setOnFinished(l -> {
                menuController.animationsInProgress.remove(pause1);
                menuController.animationsInProgress.add(pause2);
                pause2.playFromStart();
            });

            pause2.setOnFinished(e -> {
                this.getChildren().add(node);
                FadeTransition fadeTransition = Animations.fadeIn(node);

                menuController.animationsInProgress.remove(pause2);
                fadeTransition.playFromStart();
            });

            menuController.animationsInProgress.add(pause1);
            pause1.playFromStart();
        }
        else {
            // remove old, add new item

            Node oldChild = this.getChildren().get(0);
            FadeTransition fadeOut = Animations.fadeOut(oldChild);
            fadeOut.setOnFinished(e -> {
                PauseTransition pauseTransition = new PauseTransition();
                if(pause) pauseTransition.setDuration(Duration.millis(animationSpeed));
                else pauseTransition.setDuration(Duration.ZERO);

                pauseTransition.setOnFinished(h -> {
                    getChildren().set(0, node);
                    FadeTransition fadeIn = Animations.fadeIn(node);

                    menuController.animationsInProgress.remove(pauseTransition);
                    fadeIn.playFromStart();
                });

                menuController.animationsInProgress.remove(fadeOut);
                menuController.animationsInProgress.add(pauseTransition);
                pauseTransition.playFromStart();
            });

            menuController.animationsInProgress.add(fadeOut);
            fadeOut.playFromStart();
        }
    }

}
