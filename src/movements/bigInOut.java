package movements;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

public class bigInOut {

    private Timeline clock;
    private Rectangle backDrop;
    private Pane board;
    private boolean in;
    private double sp;
    private int index; //index of selected dot
    private ArrayList<Circle> shape;

    public bigInOut(ArrayList<Circle> dots, int speed, Color background, Color dotCol, int inOut){

        this.index = (int)(dots.size()/2);
        this.shape = dots;
        //sets the speed
        if (speed == 2) {
            this.sp = 0.1;
        } else if (speed == 3) {
            this.sp = 0.07;
        } else { //speed = 1 or slow is the default
            this.sp = 0.4;
        }

        if(inOut == 0){ //if in out is 0 then it is a big out transition, dot gets bigger
            in = false;
        } else { //else , probaly set to one or default is the big in where it starts off big then gets smaller
            in = true;
        }

        this.board = new Pane();
        backDrop = new Rectangle(0, 0, 1500, 800);
        backDrop.setFill(background);
        board.getChildren().add(this.backDrop);

        if(in){
            for(int i = 0; i < shape.size(); i++){
                Circle dot = this.shape.get(i);
                dot.setFill(dotCol);
                if(i == index){
                    dot.setRadius(1500);
                }
                this.board.getChildren().add(dot);
            }
        } else {
            for(int i = 0; i < shape.size(); i++){
                Circle dot = this.shape.get(i);
                dot.setFill(dotCol);
                this.board.getChildren().add(dot);
            }
        }

        this.setUpClock();
    }

    public void setUpClock() {
        KeyFrame kf = new KeyFrame(Duration.seconds(this.sp), (ActionEvent e) -> detectTimeStamp());
        this.clock = new Timeline(kf);
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public Pane getPane(){
        return this.board;
    }

    public void detectTimeStamp(){
        if(in){ //starts off big and gets smaller
            Circle circ = this.shape.get(index);
            if(circ.getRadius() > shape.get(0).getRadius()){
                circ.setRadius(circ.getRadius() - 10); //decrease radius by 10 pixels per frame
            } else {
                clock.stop();
            }
        } else { //starts off small and gets bigger
            Circle circ = this.shape.get(index);
            if(circ.getRadius() < 1500){
                circ.setRadius(circ.getRadius() + 10); //increase radius by 10 pixels per frame
            } else {
                clock.stop();
            }
        }
    }
}
