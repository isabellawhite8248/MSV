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

//depixelization of random dots blink in and out in the primary colors

//seperates the dots into 3 groups == multiples of 3 -- uses a counter and a for loop then puts only 3 on for 2 seconds, then next group + rotates
public class pixBlink {

    private Timeline clock;
    private Rectangle backDrop;
    private Pane board;
    private double sp;
    private ArrayList<Circle> OGdots;
    private ArrayList<Circle> g1;
    private ArrayList<Circle> g2;
    private ArrayList<Circle> g3;
    private double time;
    private int selectedGroup;

    public pixBlink(ArrayList<Circle> dots, int speed, Color background, Color dotColor){

        this.time = 0;
        this.selectedGroup = 1;
        if (speed == 2) {
            this.sp = 0.1;
        } else if (speed == 3) {
            this.sp = 0.07;
        } else { //speed = 1 or slow is the default
            this.sp = 0.4;
        }

        this.board = new Pane();
        backDrop = new Rectangle(0, 0, 1500, 800);
        backDrop.setFill(background);
        board.getChildren().add(this.backDrop);

        this.g1 = new ArrayList<Circle>();
        this.g2 = new ArrayList<Circle>();
        this.g3 = new ArrayList<Circle>();

        this.OGdots = dots;
        //seperate original dots into 3 groups and add each one to the pane
        for(int i = 0; i < this.OGdots.size(); i++){
            Circle dot = this.OGdots.get(i);
            dot.setFill(dotColor);
            if(i%3 == 0){
                this.g1.add(dot);
                this.board.getChildren().add(dot);
            } else if (i%2 == 0){
                this.g2.add(dot);
                this.board.getChildren().add(dot);
            } else {
                this.g3.add(dot);
                this.board.getChildren().add(dot);
            }
        }
        this.setUpClock();
    }

    public void setVisibility(ArrayList<Circle> group, int opacity){
        for(int y = 0; y < group.size(); y++){
            group.get(y).setOpacity(opacity);
        }
    }
    public void setUpClock() {
        KeyFrame kf = new KeyFrame(Duration.seconds(0.1), (ActionEvent e) -> detectTimeStamp());
        this.clock = new Timeline(kf);
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public int swGroup(int group){
        if(group == 1){
            return 2;
        } else if (group == 2){
            return 3;
        } else {
            return 1;
        }
    }

    public Pane getPane(){
        return this.board;
    }

    public void detectTimeStamp(){

        this.time = time + sp;
        int t = (int)this.time;
        if(t%2 == 0){ //every 6 seconds change the selected group
            selectedGroup = swGroup(selectedGroup);
        }
        if(selectedGroup == 1){
            setVisibility(g1, 1);
            setVisibility(g2, 0);
            setVisibility(g3, 0);
        } else if (selectedGroup == 2){
            setVisibility(g1, 0);
            setVisibility(g2, 1);
            setVisibility(g3, 0);
        } else {
            setVisibility(g1, 0);
            setVisibility(g2, 0);
            setVisibility(g3, 1);
        }
    }
}
