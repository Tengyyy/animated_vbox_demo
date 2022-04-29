package com.example.queue_demo;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.ArrayList;

public class AnimatedVBox extends VBox {

    double animationSpeed = 200;
    double height = 0;

    ArrayList<Animation> animationsInProgress = new ArrayList<>();

    AnimatedVBox(){
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: rgb(45,45,45);");
        this.setMinWidth(150);
        this.setFillWidth(true);
    }

    public void add(int index, Node child){
        if(index < 0) return;
        else if(index >= this.getChildren().size()){
            this.add(child);
            return;
        }
        height+= 50;
        this.setMaxHeight(height);
        Timeline heightAnimation = animateMinHeight(height);

        ArrayList<Node> childrenToBeMoved = new ArrayList<>();
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().add(heightAnimation);

        for(int i = index; i < this.getChildren().size(); i++){
            childrenToBeMoved.add(this.getChildren().get(i));
            parallelTransition.getChildren().add(animateDown(this.getChildren().get(i), 50));
        }

        parallelTransition.setOnFinished((ev) -> {
            this.getChildren().add(index, child);
            for(Node node : childrenToBeMoved){
                node.setTranslateY(0);
            }

            FadeTransition fadeTransition = fadeIn(child);
            animationsInProgress.remove(parallelTransition);
            fadeTransition.playFromStart();
        });


        animationsInProgress.add(parallelTransition);
        parallelTransition.playFromStart();

    }

    public void add(Node child){

        height+= 50;
        this.setMaxHeight(height);
        Timeline heightAnimation = animateMinHeight(height);
        heightAnimation.setOnFinished((e) -> {
            // add item with opacity 0, then fade it in
            this.getChildren().add(child);
            FadeTransition fadeTransition = fadeIn(child);
            animationsInProgress.remove(heightAnimation);
            fadeTransition.playFromStart();
        });
        animationsInProgress.add(heightAnimation);
        heightAnimation.playFromStart();
    }


    public void remove(Node child){
        if(this.getChildren().contains(child)){
            this.remove(this.getChildren().indexOf(child));
        }
    }

    public void remove(int index){
        if(index >= 0 && !this.getChildren().isEmpty() && index < this.getChildren().size()) {
            height -= 50;
            this.setMinHeight(height);

            FadeTransition fadeTransition = fadeOut(this.getChildren().get(index));

            SequentialTransition sequentialTransition = new SequentialTransition();
            sequentialTransition.getChildren().add(fadeTransition);

            Timeline timeline = animateMaxHeight(height);
            ArrayList<Node> childrenToBeMoved = new ArrayList<>();

            ParallelTransition parallelTransition = new ParallelTransition();
            parallelTransition.getChildren().addAll(timeline);


                if (index < this.getChildren().size() - 1) {
                    // removed child was not the last inside the vbox, have to translate upwards all nodes that were below
                    for (int i = index + 1; i < this.getChildren().size(); i++) {
                        childrenToBeMoved.add(this.getChildren().get(i));
                        parallelTransition.getChildren().add(animateUp(this.getChildren().get(i), 50));
                    }
                }

                sequentialTransition.getChildren().add(parallelTransition);

                sequentialTransition.setOnFinished((ev) -> {
                    this.getChildren().remove(index);
                    for (Node node : childrenToBeMoved) {
                        node.setTranslateY(0);
                    }
                    animationsInProgress.remove(sequentialTransition);
                });

                animationsInProgress.add(sequentialTransition);
                sequentialTransition.playFromStart();

                // decrease max height by 50, apply translate of -50 to all nodes below the one that will be removed and on end actually remove the node and reset translate
        }
    }

    public void moveAll(int firstBound, int secondBound, int newIndex){
        if(this.getChildren().size() < 3 ||
                firstBound == newIndex ||
                firstBound < 0 ||
                firstBound >= this.getChildren().size() - 1 ||
                newIndex < -1 ||
                newIndex >= this.getChildren().size() - 1 ||
                secondBound < 0 ||
                secondBound >= this.getChildren().size() ||
                secondBound - firstBound + newIndex >= this.getChildren().size() ||
                firstBound >= secondBound) return;

        ParallelTransition parallelTransition = new ParallelTransition();

        ArrayList<Node> nodesInRange = new ArrayList<>();

        for(int i = firstBound; i <= secondBound; i++){
            // all the children that will be moved
            nodesInRange.add(this.getChildren().get(i));
            FadeTransition fadeTransition = fadeOut(this.getChildren().get(i));
            parallelTransition.getChildren().add(fadeTransition);

        }

        parallelTransition.setOnFinished((e) -> {
            ArrayList<Node> childrenToBeMoved = new ArrayList<>();
            ParallelTransition parallelTranslateTransition = new ParallelTransition();

            if(newIndex > firstBound || newIndex == -1){
                // move items down
                int loopTo = newIndex == -1 ? this.getChildren().size() : secondBound - firstBound + newIndex + 1;
                for(int i = secondBound + 1; i < loopTo; i++){
                    childrenToBeMoved.add(this.getChildren().get(i));
                    parallelTranslateTransition.getChildren().add(animateUp(this.getChildren().get(i), 50 * (secondBound - firstBound + 1)));
                }
            }
            else {
                // move items up
                for(int i = firstBound - 1; i>= newIndex; i--){
                    childrenToBeMoved.add(this.getChildren().get(i));
                    parallelTranslateTransition.getChildren().add(animateDown(this.getChildren().get(i), 50 * (secondBound - firstBound + 1)));
                }
            }

            parallelTranslateTransition.setOnFinished((ev) -> {
                this.getChildren().removeAll(nodesInRange);
                for (Node child : childrenToBeMoved) {
                    child.setTranslateY(0);
                }
                if(newIndex == -1){
                    this.getChildren().addAll(nodesInRange);
                }
                else {
                    for(int i = 0; i < nodesInRange.size(); i++){
                        this.getChildren().add(newIndex + i, nodesInRange.get(i));
                    }
                }

                ParallelTransition parallelFadeTransition = new ParallelTransition();
                for(Node node : nodesInRange){
                    parallelFadeTransition.getChildren().add(fadeIn(node));
                }
                animationsInProgress.remove(parallelTranslateTransition);
                parallelFadeTransition.playFromStart();

            });
            animationsInProgress.remove(parallelTransition);
            animationsInProgress.add(parallelTranslateTransition);
            parallelTranslateTransition.playFromStart();
        });

        animationsInProgress.add(parallelTransition);
        parallelTransition.playFromStart();

    }

    public void addAll(){

    }

    public void clear(){

    }

    public void move(int oldIndex, int newIndex){
        // move to bottom if newIndex = -1

        // massive guard clause
        if(this.getChildren().size() < 2 ||
                oldIndex == newIndex ||
                oldIndex < 0 ||
                oldIndex >= this.getChildren().size() ||
                newIndex < -1 ||
                newIndex >= this.getChildren().size() ||
                (oldIndex == this.getChildren().size() -1 && newIndex == -1)) return;

        if(newIndex == -1 || newIndex > oldIndex){

            Node child = this.getChildren().get(oldIndex);

            FadeTransition fadeTransition = fadeOut(child);
            fadeTransition.setOnFinished((e) -> {
                ArrayList<Node> childrenToBeMoved = new ArrayList<>();
                ParallelTransition parallelTransition = new ParallelTransition();

                int loopEndIndex = newIndex == -1 ? this.getChildren().size() : newIndex + 1;

                for(int i = oldIndex + 1; i < loopEndIndex; i++){
                    childrenToBeMoved.add(this.getChildren().get(i));
                    parallelTransition.getChildren().add(animateUp(this.getChildren().get(i), 50));
                }

                parallelTransition.setOnFinished((ev) -> {
                    this.getChildren().remove(oldIndex);
                    for (Node child1 : childrenToBeMoved) {
                        child1.setTranslateY(0);
                    }
                    if(newIndex == -1) this.getChildren().add(child);
                    else this.getChildren().add(newIndex, child);
                    FadeTransition fadeTransition1 = fadeIn(child);

                    animationsInProgress.remove(parallelTransition);
                    fadeTransition1.playFromStart();
                });

                animationsInProgress.remove(fadeTransition);
                animationsInProgress.add(parallelTransition);
                parallelTransition.playFromStart();
            });

            animationsInProgress.add(fadeTransition);
            fadeTransition.playFromStart();

        }

        else {
            // move item up


            Node child = this.getChildren().get(oldIndex);

            FadeTransition fadeTransition = fadeOut(child);
            fadeTransition.setOnFinished((e) -> {
                ArrayList<Node> childrenToBeMoved = new ArrayList<>();
                ParallelTransition parallelTransition = new ParallelTransition();


                for(int i = newIndex; i < oldIndex; i++){
                    childrenToBeMoved.add(this.getChildren().get(i));
                    parallelTransition.getChildren().add(animateDown(this.getChildren().get(i), 50));
                }

                parallelTransition.setOnFinished((ev) -> {
                    this.getChildren().remove(oldIndex);
                    for (Node child1 : childrenToBeMoved) {
                        child1.setTranslateY(0);
                    }

                    this.getChildren().add(newIndex, child);
                    FadeTransition fadeTransition1 = fadeIn(child);

                    animationsInProgress.remove(parallelTransition);

                    fadeTransition1.playFromStart();
                });

                animationsInProgress.remove(fadeTransition);
                animationsInProgress.add(parallelTransition);
                parallelTransition.playFromStart();
            });

            animationsInProgress.add(fadeTransition);
            fadeTransition.playFromStart();
        }

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

    private FadeTransition fadeIn(Node child){
        Duration animationDuration = Duration.millis(animationSpeed);
        FadeTransition fadeTransition = new FadeTransition(animationDuration, child);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        return fadeTransition;
    }

    private FadeTransition fadeOut(Node child){
        Duration animationDuration = Duration.millis(animationSpeed);
        FadeTransition fadeTransition = new FadeTransition(animationDuration, child);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        return fadeTransition;
    }

    private TranslateTransition animateUp(Node child, int translate){
        Duration animationDuration = Duration.millis(animationSpeed);
        TranslateTransition translateTransition = new TranslateTransition(animationDuration, child);
        translateTransition.setFromY(0);
        translateTransition.setToY(-translate);
        return translateTransition;
    }

    private TranslateTransition animateDown(Node child, int translate){
        Duration animationDuration = Duration.millis(animationSpeed);
        TranslateTransition translateTransition = new TranslateTransition(animationDuration, child);
        translateTransition.setFromY(0);
        translateTransition.setToY(translate);
        return translateTransition;
    }
}
