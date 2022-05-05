package com.example.queue_demo;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class QueueBox extends VBox {

    double animationSpeed = 200;
    double height = 0;
    MenuController menuController;


    QueueBox(MenuController menuController){
        this.menuController = menuController;
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: white;");
        this.setMinWidth(150);
        this.setPrefWidth(150);
        this.setMaxWidth(150);
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
        Timeline heightAnimation = Animations.animateMinHeight(height, this);

        ArrayList<Node> childrenToBeMoved = new ArrayList<>();
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().add(heightAnimation);

        for(int i = index; i < this.getChildren().size(); i++){
            childrenToBeMoved.add(this.getChildren().get(i));
            parallelTransition.getChildren().add(Animations.animateDown(this.getChildren().get(i), 50));
        }

        parallelTransition.setOnFinished((ev) -> {
            this.getChildren().add(index, child);
            for(Node node : childrenToBeMoved){
                node.setTranslateY(0);
            }

            FadeTransition fadeTransition = Animations.fadeIn(child);
            menuController.animationsInProgress.remove(parallelTransition);
            fadeTransition.playFromStart();
        });


        menuController.animationsInProgress.add(parallelTransition);
        parallelTransition.playFromStart();

    }

    public void add(Node child){

        height+= 50;
        this.setMaxHeight(height);
        Timeline heightAnimation = Animations.animateMinHeight(height, this);
        heightAnimation.setOnFinished((e) -> {
            // add item with opacity 0, then fade it in
            this.getChildren().add(child);
            FadeTransition fadeTransition = Animations.fadeIn(child);
            menuController.animationsInProgress.remove(heightAnimation);
            fadeTransition.playFromStart();
        });
        menuController.animationsInProgress.add(heightAnimation);
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

            FadeTransition fadeTransition = Animations.fadeOut(this.getChildren().get(index));

            SequentialTransition sequentialTransition = new SequentialTransition();
            sequentialTransition.getChildren().add(fadeTransition);

            Timeline timeline = Animations.animateMaxHeight(height, this);
            ArrayList<Node> childrenToBeMoved = new ArrayList<>();

            ParallelTransition parallelTransition = new ParallelTransition();
            parallelTransition.getChildren().add(timeline);

                if (index < this.getChildren().size() - 1) {
                    // removed child was not the last inside the vbox, have to translate upwards all nodes that were below
                    for (int i = index + 1; i < this.getChildren().size(); i++) {
                        childrenToBeMoved.add(this.getChildren().get(i));
                        parallelTransition.getChildren().add(Animations.animateUp(this.getChildren().get(i), 50));
                    }
                }

                sequentialTransition.getChildren().add(parallelTransition);

                sequentialTransition.setOnFinished((ev) -> {
                    this.getChildren().remove(index);
                    for (Node node : childrenToBeMoved) {
                        node.setTranslateY(0);
                    }
                    menuController.animationsInProgress.remove(sequentialTransition);
                });

                menuController.animationsInProgress.add(sequentialTransition);
                sequentialTransition.playFromStart();

                // decrease max height by 50, apply translate of -50 to all nodes below the one that will be removed and on end actually remove the node and reset translate
        }
    }
    public void removeAndMove(int index){
        // removes item at index from the queuebox, moves all previous items to the bottom
        if(index < 0 || index >= getChildren().size()) return;
        if(index ==0){
            remove(index);
            return;
        }

        height -= 50;
        this.setMinHeight(height);

        ParallelTransition parallelFadeOut = new ParallelTransition();
        ParallelTransition parallelTranslate = new ParallelTransition();
        ParallelTransition parallelFadeIn = new ParallelTransition();
        ArrayList<Node> itemsToBeTranslated = new ArrayList<>();
        ArrayList<Node> itemsToBeMoved = new ArrayList<>();

        FadeTransition fade = Animations.fadeOut(this.getChildren().get(index));
        parallelFadeOut.getChildren().add(fade);

        for(int i = 0; i < index; i++){
            FadeTransition fadeTransition = Animations.fadeOut(getChildren().get(i));
            parallelFadeOut.getChildren().add(fadeTransition);
            itemsToBeMoved.add(getChildren().get(i));
        }

        parallelFadeOut.setOnFinished(e -> {

            parallelTranslate.getChildren().add(Animations.animateMaxHeight(height, this));
            for(int i = index + 1; i < getChildren().size(); i++){
                itemsToBeTranslated.add(getChildren().get(i));
                parallelTranslate.getChildren().add(Animations.animateUp(getChildren().get(i), itemsToBeMoved.size() * 50 + 50));
            }

            parallelTranslate.setOnFinished(k -> {

                getChildren().remove(index);
                getChildren().removeAll(itemsToBeMoved);

                for(Node node: itemsToBeTranslated){
                    node.setTranslateY(0);
                }

                getChildren().addAll(itemsToBeMoved);
                for(Node node : itemsToBeMoved){
                    parallelFadeIn.getChildren().add(Animations.fadeIn(node));
                }

                menuController.animationsInProgress.remove(parallelTranslate);
                parallelFadeIn.playFromStart();
            });


            menuController.animationsInProgress.remove(parallelFadeOut);
            menuController.animationsInProgress.add(parallelTranslate);
            parallelTranslate.playFromStart();
        });

        menuController.animationsInProgress.add(parallelFadeOut);
        parallelFadeOut.playFromStart();

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
            FadeTransition fadeTransition = Animations.fadeOut(this.getChildren().get(i));
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
                    parallelTranslateTransition.getChildren().add(Animations.animateUp(this.getChildren().get(i), 50 * (secondBound - firstBound + 1)));
                }
            }
            else {
                // move items up
                for(int i = firstBound - 1; i>= newIndex; i--){
                    childrenToBeMoved.add(this.getChildren().get(i));
                    parallelTranslateTransition.getChildren().add(Animations.animateDown(this.getChildren().get(i), 50 * (secondBound - firstBound + 1)));
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
                    parallelFadeTransition.getChildren().add(Animations.fadeIn(node));
                }
                menuController.animationsInProgress.remove(parallelTranslateTransition);
                parallelFadeTransition.playFromStart();

            });
            menuController.animationsInProgress.remove(parallelTransition);
            menuController.animationsInProgress.add(parallelTranslateTransition);
            parallelTranslateTransition.playFromStart();
        });

        menuController.animationsInProgress.add(parallelTransition);
        parallelTransition.playFromStart();

    }

    public void addAll(Collection<? extends Node> collection){
        height += (collection.size() * 50);
        this.setMaxHeight(height);

        Timeline heightAnimation = Animations.animateMinHeight(height, this);
        heightAnimation.setOnFinished(e -> {
            this.getChildren().addAll(collection);
            ParallelTransition parallelTransition = new ParallelTransition();
            for(Node node : collection){
                parallelTransition.getChildren().add(Animations.fadeIn(node));
            }

            parallelTransition.playFromStart();
            menuController.animationsInProgress.remove(heightAnimation);
        });

        menuController.animationsInProgress.add(heightAnimation);
        heightAnimation.playFromStart();

    }

    public void addAll(int index, Collection<? extends Node> collection) {
        if (index < 0) return;
        else if(index >= this.getChildren().size()){
            addAll(collection);
            return;
        }
        height += (collection.size() * 50);
        this.setMaxHeight(height);

        ParallelTransition parallelTransition = new ParallelTransition();
        Timeline heightAnimation = Animations.animateMinHeight(height, this);
        parallelTransition.getChildren().add(heightAnimation);

        ArrayList<Node> itemsToBeMoved = new ArrayList<>();

        if(index < this.getChildren().size() -1){
            // items won't be added to the last slot, have to translate items below index
            for(int i = index; i < this.getChildren().size(); i++){
                TranslateTransition translateTransition = Animations.animateDown(this.getChildren().get(i), collection.size() * 50);
                parallelTransition.getChildren().add(translateTransition);
                itemsToBeMoved.add(this.getChildren().get(i));
            }
        }

        parallelTransition.setOnFinished(e -> {
            this.getChildren().addAll(index, collection);
            for(Node node : itemsToBeMoved){
                node.setTranslateY(0);
            }

            ParallelTransition parallelFadeIn = new ParallelTransition();
            for(Node node : collection){
                parallelFadeIn.getChildren().add(Animations.fadeIn(node));
            }

            parallelFadeIn.playFromStart();
            menuController.animationsInProgress.remove(parallelTransition);
        });

        menuController.animationsInProgress.add(parallelTransition);
        parallelTransition.playFromStart();
    }

    public void clear(){
        if(!this.getChildren().isEmpty()){
            height = 0;
            this.setMinHeight(height);

            ParallelTransition parallelFadeOut = new ParallelTransition();
            for(Node node : this.getChildren()){
                FadeTransition fadeTransition = Animations.fadeOut(node);
                parallelFadeOut.getChildren().add(fadeTransition);
            }

            Timeline timeline = Animations.animateMaxHeight(height, this);

            SequentialTransition sequentialTransition = new SequentialTransition();
            sequentialTransition.getChildren().addAll(parallelFadeOut, timeline);

            sequentialTransition.setOnFinished(e -> {
                this.getChildren().clear();
                menuController.animationsInProgress.remove(sequentialTransition);
            });

            menuController.animationsInProgress.add(sequentialTransition);
            sequentialTransition.playFromStart();

        }
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

            FadeTransition fadeTransition = Animations.fadeOut(child);
            fadeTransition.setOnFinished((e) -> {
                ArrayList<Node> childrenToBeMoved = new ArrayList<>();
                ParallelTransition parallelTransition = new ParallelTransition();

                int loopEndIndex = newIndex == -1 ? this.getChildren().size() : newIndex + 1;

                for(int i = oldIndex + 1; i < loopEndIndex; i++){
                    childrenToBeMoved.add(this.getChildren().get(i));
                    parallelTransition.getChildren().add(Animations.animateUp(this.getChildren().get(i), 50));
                }

                parallelTransition.setOnFinished((ev) -> {
                    this.getChildren().remove(oldIndex);
                    for (Node child1 : childrenToBeMoved) {
                        child1.setTranslateY(0);
                    }
                    if(newIndex == -1) this.getChildren().add(child);
                    else this.getChildren().add(newIndex, child);
                    FadeTransition fadeTransition1 = Animations.fadeIn(child);

                    menuController.animationsInProgress.remove(parallelTransition);
                    fadeTransition1.playFromStart();
                });

                menuController.animationsInProgress.remove(fadeTransition);
                menuController.animationsInProgress.add(parallelTransition);
                parallelTransition.playFromStart();
            });

            menuController.animationsInProgress.add(fadeTransition);
            fadeTransition.playFromStart();

        }

        else {
            // move item up


            Node child = this.getChildren().get(oldIndex);

            FadeTransition fadeTransition = Animations.fadeOut(child);
            fadeTransition.setOnFinished((e) -> {
                ArrayList<Node> childrenToBeMoved = new ArrayList<>();
                ParallelTransition parallelTransition = new ParallelTransition();


                for(int i = newIndex; i < oldIndex; i++){
                    childrenToBeMoved.add(this.getChildren().get(i));
                    parallelTransition.getChildren().add(Animations.animateDown(this.getChildren().get(i), 50));
                }

                parallelTransition.setOnFinished((ev) -> {
                    this.getChildren().remove(oldIndex);
                    for (Node child1 : childrenToBeMoved) {
                        child1.setTranslateY(0);
                    }

                    this.getChildren().add(newIndex, child);
                    FadeTransition fadeTransition1 = Animations.fadeIn(child);

                    menuController.animationsInProgress.remove(parallelTransition);

                    fadeTransition1.playFromStart();
                });

                menuController.animationsInProgress.remove(fadeTransition);
                menuController.animationsInProgress.add(parallelTransition);
                parallelTransition.playFromStart();
            });

            menuController.animationsInProgress.add(fadeTransition);
            fadeTransition.playFromStart();
        }

    }


    public void shuffle(){
        // fade out, shuffle, fade in

        ParallelTransition parallelFadeOut = new ParallelTransition();

        for(Node node : this.getChildren()){
            FadeTransition fadeTransition = Animations.fadeOut(node);
            parallelFadeOut.getChildren().add(fadeTransition);
        }

        parallelFadeOut.setOnFinished(e -> {
            ObservableList<Node> workingCollection = FXCollections.observableArrayList(this.getChildren());
            Collections.shuffle(workingCollection);
            this.getChildren().setAll(workingCollection);

            ParallelTransition parallelFadeIn = new ParallelTransition();
            for(Node node : this.getChildren()){
                FadeTransition fadeTransition = Animations.fadeIn(node);
                parallelFadeIn.getChildren().add(fadeTransition);
            }

            parallelFadeIn.setOnFinished(k -> {
                menuController.animationsInProgress.remove(parallelFadeIn);
            });

            menuController.animationsInProgress.add(parallelFadeIn);
            parallelFadeIn.playFromStart();

            menuController.animationsInProgress.remove(parallelFadeOut);
        });

        menuController.animationsInProgress.add(parallelFadeOut);
        parallelFadeOut.playFromStart();

    }


}
