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

//pixelization in
public class pixInOut {

    private Timeline clock;
    private Rectangle backDrop;
    private Pane board;
    private ArrayList<ArrayList<Circle>> circles;
    private ArrayList<Circle> original;
    private double sp;
    private double time;
    private int inOut;
    private Color dotCol;
    private int tracker;
    private int index;
    //if in is 1 == pixelate in, if in is 0 == pixelate out

    public pixInOut(ArrayList<Circle> dots, int speed, Color background, Color dotColor, int in){

        this.tracker = 2;
        this.index = 0;
        this.dotCol = dotColor;
        this.time = 0;
        this.inOut = in;

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
        this.original = dots;
        this.circles = new ArrayList<>(); //should be an arraylist of ever decreasing length
        //to pix out loop through and add in order, to pix in loop through and add in REVERSE order

        while(!(this.original.size() == 0 || this.original.size() == 1)){
            circles.add(populate(this.original));
        }

        if(inOut == 1){ //if it is pix in the order of the array needs to be reversed
            ArrayList<ArrayList<Circle>> reversed = new ArrayList<>();
            for(int j = circles.size() - 1; j >= 0; j--) {
                ArrayList<Circle> l = circles.get(j);
                reversed.add(l);
            }
            this.circles = reversed;
        }

        this.setUpClock();
    }

    //takes every other circle in the list, from og list to the returned list
    public ArrayList<Circle> populate(ArrayList<Circle> list){
        ArrayList<Circle> newList = new ArrayList<>();
        ArrayList<Circle> oldList = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            Circle dot = list.get(i);
            dot.setFill(this.dotCol);
            if(i%2 == 0){
               newList.add(dot);
               if(inOut == 0){
                   this.board.getChildren().add(dot);
               }
            } else {
                oldList.add(dot);
            }
        }
        this.original = oldList;
        return newList;
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
        this.time = time + this.sp;
        if(inOut == 0){
            if(((int)this.time == tracker) && (index != circles.size() - 1)){
                tracker = tracker + 2;
                ArrayList<Circle> list = circles.get(index);
                for(int i = 0; i < list.size(); i++){
                    Circle circ = list.get(i);
                    circ.setOpacity(0);
                }
                index++;
            }
        } else {
            if(((int)this.time == tracker) && (index != circles.size() - 1)){
                tracker = tracker + 2;
                ArrayList<Circle> list = circles.get(index);
                for(int i = 0; i < list.size(); i++){
                    Circle circ = list.get(i);
                    this.board.getChildren().add(circ);
                }
                index++;
            }
        }
    }

}
