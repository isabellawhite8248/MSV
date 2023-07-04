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

//opacity of the dots flickers in and out
public class opacityFlicker {

    private Timeline clock;
    private Rectangle backDrop;
    private Pane board;
    private ArrayList<Circle> shape;
    private double sp;
    private double time;

    public opacityFlicker(ArrayList<Circle> dots, int speed, Color background, Color dotColor){

        if (speed == 2) {
            this.sp = 0.1;
        } else if (speed == 3) {
            this.sp = 0.07;
        } else { //speed = 1 or slow is the default
            this.sp = 0.4;
        }

        this.shape = dots;
        this.board = new Pane();
        backDrop = new Rectangle(0, 0, 1500, 800);
        backDrop.setFill(background);
        board.getChildren().add(this.backDrop);

        for(int i = 0; i < this.shape.size(); i++){
            Circle dot = this.shape.get(i);
            dot.setFill(dotColor);
            board.getChildren().add(dot);
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
        time = time + this.sp; //keeps track of the time in seconds
        double opacity = Math.sin(time);
        for(int i = 0; i < shape.size(); i++){
            Circle c = shape.get(i);
            c.setOpacity(opacity);
        }
    }
}

//idea timeline use a sine wave -- flickers on and off based off the equation that is entered -- ex cos or log function -- look into how to do this
