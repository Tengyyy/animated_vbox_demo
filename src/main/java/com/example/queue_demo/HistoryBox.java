package com.example.queue_demo;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

public class HistoryBox extends VBox {

    double animationSpeed = 200;
    double height = 0;
    MenuController menuController;

    boolean open = true;
    int index = -1;
    final int CAPACITY = 20;

    StackPane historyWrapper;

    Timeline openHistory, closeHistory;


    HistoryBox(MenuController menuController, StackPane historyWrapper){

        this.historyWrapper = historyWrapper;
        this.menuController = menuController;
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: white;");
        this.setMinWidth(200);
        this.setPrefWidth(200);
        this.setMaxWidth(200);
        this.setFillWidth(true);

        this.setAlignment(Pos.TOP_LEFT);
    }

    public void add(Region child){

        if(!open){

            child.setOpacity(1);

            if(getChildren().size() < CAPACITY){
                height += HistoryChild.height;

                if(getChildren().isEmpty()) {
                    this.getChildren().add(child);
                    Platform.runLater(() -> {
                        HistoryChild.height = child.getHeight();
                        height = HistoryChild.height * getChildren().size();
                    });
                }
                else this.getChildren().add(child);

            }
            else {
                getChildren().remove(0);
                getChildren().add(child);
            }
        }
        else {

            if(getChildren().size() < CAPACITY){

                PauseTransition pauseTransition = new PauseTransition(Duration.millis(animationSpeed));

                pauseTransition.setOnFinished(k -> {
                    height+= HistoryChild.height;
                    historyWrapper.setMaxHeight(height);
                    Timeline heightAnimation = Animations.animateMinHeight(height, historyWrapper);
                    heightAnimation.setOnFinished((e) -> {
                        // add item with opacity 0, then fade it in
                        if(getChildren().isEmpty()) {
                            this.getChildren().add(child);
                            Platform.runLater(() -> {
                                HistoryChild.height = child.getHeight();
                                height = HistoryChild.height * getChildren().size();
                                historyWrapper.setMaxHeight(height);
                            });
                        }
                        else this.getChildren().add(child);
                        FadeTransition fadeTransition = Animations.fadeIn(child);
                        menuController.animationsInProgress.remove(heightAnimation);
                        fadeTransition.playFromStart();
                    });
                    menuController.animationsInProgress.remove(pauseTransition);
                    menuController.animationsInProgress.add(heightAnimation);
                    heightAnimation.playFromStart();
                });

                menuController.animationsInProgress.add(pauseTransition);
                pauseTransition.playFromStart();
            }
            else {
                ParallelTransition parallelTranslate = new ParallelTransition();
                ArrayList<Node> itemsToBeTranslated = new ArrayList<>();
                FadeTransition fadeOut = Animations.fadeOut(getChildren().get(0));
                FadeTransition fadeIn = Animations.fadeIn(child);
                fadeOut.setOnFinished(e -> {
                    for(int i = 1; i < CAPACITY; i++){
                        itemsToBeTranslated.add(getChildren().get(i));
                        parallelTranslate.getChildren().add(Animations.animateUp(getChildren().get(i), HistoryChild.height));
                    }

                    parallelTranslate.setOnFinished(g -> {

                        getChildren().remove(0);
                        for(Node node : itemsToBeTranslated){
                            node.setTranslateY(0);
                        }
                        getChildren().add(child);

                        menuController.animationsInProgress.remove(parallelTranslate);
                        fadeIn.playFromStart();


                    });

                    menuController.animationsInProgress.remove(fadeOut);
                    menuController.animationsInProgress.add(parallelTranslate);
                    parallelTranslate.playFromStart();
                });

                menuController.animationsInProgress.add(fadeOut);
                fadeOut.playFromStart();
            }
        }

    }

    public void open(){

        if(closeHistory != null && closeHistory.getStatus() == Animation.Status.RUNNING) return;

        openHistory = new Timeline(new KeyFrame(Duration.millis(300),
                new KeyValue(historyWrapper.minHeightProperty(), height, Interpolator.EASE_BOTH)));

        openHistory.setOnFinished(e -> {
            historyWrapper.setMinHeight(height);
            historyWrapper.setMaxHeight(height);
            open = true;
        });

        openHistory.playFromStart();
    }

    public void close(){

        if(openHistory != null && openHistory.getStatus() == Animation.Status.RUNNING) return;

        open = false;
        System.out.println("Closing history");

        historyWrapper.setMinHeight(0);

        closeHistory = new Timeline(new KeyFrame(Duration.millis(300),
                new KeyValue(historyWrapper.maxHeightProperty(), 0, Interpolator.EASE_BOTH)));

        closeHistory.setOnFinished(e -> {
            historyWrapper.setMaxHeight(0);
            historyWrapper.setMinHeight(0);
        });

        closeHistory.playFromStart();
    }

}
