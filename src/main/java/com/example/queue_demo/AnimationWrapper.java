package com.example.queue_demo;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;

import java.util.ArrayList;

public class AnimationWrapper {

    AnimatedSwitcher animatedSwitcher;
    AnimatedVBox queueBox;
    QueueController queueController;

    AnimationWrapper(QueueController queueController, AnimatedSwitcher animatedSwitcher, AnimatedVBox queueBox){
        this.animatedSwitcher = animatedSwitcher;
        this.queueBox = queueBox;
        this.queueController = queueController;
    }

    public void play(Node node){
        queueBox.height -= 50;
        queueBox.setMinHeight(queueBox.height);

        ParallelTransition parallelFadeOut = new ParallelTransition();
        if(!animatedSwitcher.getChildren().isEmpty()){
            // switcher is not empty, have to fade out its child
            parallelFadeOut.getChildren().add( animatedSwitcher.fadeOut(animatedSwitcher.getChildren().get(0)));
        }

        ArrayList<Node> childrenToBeMoved = new ArrayList<>();

        if(queueBox.getChildren().indexOf(node) != 0) {
            // child to be played is not first inside queue, have to fade out all previous elements

            for (int i = 0; i < queueBox.getChildren().indexOf(node); i++) {
                Node temp = queueBox.getChildren().get(i);
                childrenToBeMoved.add(temp);
                parallelFadeOut.getChildren().add(queueBox.fadeOut(temp));
            }

        }

        parallelFadeOut.getChildren().add(queueBox.fadeOut(node));

        parallelFadeOut.setOnFinished(e -> {

            ArrayList<Node> childrenToBeTranslated = new ArrayList<>();
            ParallelTransition parallelTranslateTransition = new ParallelTransition();

                Timeline timeline = queueBox.animateMaxHeight(queueBox.height);
                parallelTranslateTransition.getChildren().add(timeline);
                if(queueBox.getChildren().indexOf(node) < queueBox.getChildren().size() -1){
                    // item to be played is not last, have to translate up all following items
                    for(int i =queueBox.getChildren().indexOf(node) + 1; i < queueBox.getChildren().size(); i++){
                        childrenToBeTranslated.add(queueBox.getChildren().get(i));
                        parallelTranslateTransition.getChildren().add(queueBox.animateUp(queueBox.getChildren().get(i), childrenToBeMoved.size() * 50 + 50));
                    }
                }
                parallelTranslateTransition.setOnFinished(j -> {
                    // remove items, add items, fade them in
                    queueBox.getChildren().removeAll(childrenToBeMoved);

                    VBoxChild child = (VBoxChild) node;
                    SwitcherChild switcherChild = new SwitcherChild(animatedSwitcher, queueController);
                    switcherChild.setBackground(switcherChild.createBackground(child.getRed(), child.getGreen(), child.getBlue()));

                    queueBox.getChildren().remove(node);

                    if(!animatedSwitcher.getChildren().isEmpty()) animatedSwitcher.getChildren().set(0, switcherChild);
                    else animatedSwitcher.getChildren().add(switcherChild);
                    queueBox.getChildren().addAll(childrenToBeMoved);

                    for(Node node1 : childrenToBeTranslated){
                        node1.setTranslateY(0);
                    }


                    ParallelTransition parallelFadeIn = new ParallelTransition();
                    parallelFadeIn.getChildren().add(animatedSwitcher.fadeIn(switcherChild));
                    for(Node node1 : childrenToBeMoved){
                        parallelFadeIn.getChildren().add(queueBox.fadeIn(node1));
                    }

                    parallelFadeIn.setOnFinished(i -> queueController.animationsInProgress.remove(parallelFadeIn));

                    queueController.animationsInProgress.remove(parallelTranslateTransition);
                    queueController.animationsInProgress.add(parallelFadeIn);
                    parallelFadeIn.playFromStart();

                });

                queueController.animationsInProgress.remove(parallelFadeOut);
                queueController.animationsInProgress.add(parallelTranslateTransition);
                parallelTranslateTransition.playFromStart();
            });

            queueController.animationsInProgress.add(parallelFadeOut);
            parallelFadeOut.playFromStart();
    }

    public void playRandom(Node node){
        // when playing random item in queue there is no need to move all the previous items to the bottom, because the queue is already shuffled

        queueBox.height -= 50;
        queueBox.setMinHeight(queueBox.height);

        ParallelTransition parallelFadeOut = new ParallelTransition();
        if(!animatedSwitcher.getChildren().isEmpty()){
            // switcher is not empty, have to fade out its child
            parallelFadeOut.getChildren().add( animatedSwitcher.fadeOut(animatedSwitcher.getChildren().get(0)));
        }

        parallelFadeOut.getChildren().add(queueBox.fadeOut(node));

        parallelFadeOut.setOnFinished(e -> {

            ArrayList<Node> childrenToBeTranslated = new ArrayList<>();
            ParallelTransition parallelTranslateTransition = new ParallelTransition();

            Timeline timeline = queueBox.animateMaxHeight(queueBox.height);
            parallelTranslateTransition.getChildren().add(timeline);
            if(queueBox.getChildren().indexOf(node) < queueBox.getChildren().size() -1){
                // item to be played is not last, have to translate up all following items
                for(int i =queueBox.getChildren().indexOf(node) + 1; i < queueBox.getChildren().size(); i++){
                    childrenToBeTranslated.add(queueBox.getChildren().get(i));
                    parallelTranslateTransition.getChildren().add(queueBox.animateUp(queueBox.getChildren().get(i),50));
                }
            }
            parallelTranslateTransition.setOnFinished(j -> {
                // remove items, add items, fade them in

                VBoxChild child = (VBoxChild) node;
                SwitcherChild switcherChild = new SwitcherChild(animatedSwitcher, queueController);
                switcherChild.setBackground(switcherChild.createBackground(child.getRed(), child.getGreen(), child.getBlue()));

                queueBox.getChildren().remove(node);

                if(!animatedSwitcher.getChildren().isEmpty()) animatedSwitcher.getChildren().set(0, switcherChild);
                else animatedSwitcher.getChildren().add(switcherChild);

                for(Node node1 : childrenToBeTranslated){
                    node1.setTranslateY(0);
                }


                FadeTransition switcherFadeIn = animatedSwitcher.fadeIn(switcherChild);

                switcherFadeIn.setOnFinished(i -> queueController.animationsInProgress.remove(switcherFadeIn));

                queueController.animationsInProgress.remove(parallelTranslateTransition);
                queueController.animationsInProgress.add(switcherFadeIn);
                switcherFadeIn.playFromStart();

            });

            queueController.animationsInProgress.remove(parallelFadeOut);
            queueController.animationsInProgress.add(parallelTranslateTransition);
            parallelTranslateTransition.playFromStart();
        });

        queueController.animationsInProgress.add(parallelFadeOut);
        parallelFadeOut.playFromStart();


    }


}

