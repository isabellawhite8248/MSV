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

//bounces up and down -- ex with the beat
public class bounce {

    private Timeline clock;
    private ArrayList<Circle> shape;
    private boolean goingUp;
    private double sp;
    private Pane animePane;
    private Rectangle backDrop;
    private double initialVelocity;
    private double initPos;
    private double acc;
    private double seconds;

    public bounce(ArrayList<Circle> dots, int speed, Color background, Color dotCol) {

        this.seconds = 0;
        this.acc = -9.8;
        initialVelocity = 15; //initial velocity set to 10 pixels per second
        animePane = new Pane();
        backDrop = new Rectangle(0, 0, 1500, 800);
        backDrop.setFill(background);
        animePane.getChildren().add(this.backDrop);

        for (int y = 0; y < dots.size(); y++) {
            Circle dot = dots.get(y);
            dot.setFill(dotCol);
            animePane.getChildren().add(dot);
        }

        //sets the speed
        if (speed == 2) {
            this.sp = 0.1;
        } else if (speed == 3) {
            this.sp = 0.07;
        } else { //speed = 1 or slow is the default
            this.sp = 0.4;
        }

        this.goingUp = true;

        this.setUpClock();
        this.shape = dots;
        this.initPos = this.shape.get(0).getCenterY();

    }

    public Pane getPane() {
        return this.animePane;
    }

    public void setUpClock() {
        KeyFrame kf = new KeyFrame(Duration.seconds(this.sp), (ActionEvent e) -> detectTimeStamp());
        this.clock = new Timeline(kf);
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public double deltaY(double time) {
        //helper method to help find the change in position - based off of projectile motion kinematics equations
        double delta = (initialVelocity*time + 0.5*(acc)*(time*time))*(-1);
        return delta;
    }

    public void detectTimeStamp() {
        Circle firstDot = this.shape.get(0);
        if(firstDot.getCenterY() > this.initPos){
            seconds = 1; //reset the time -- needs to bounce back up -- simulated invisible floor
        } else {
            seconds = seconds + this.sp; //if not keep it in projectile motion
        }
        for(int i = 0; i < shape.size(); i++){
            Circle s = shape.get(i);
            double newCenter = s.getCenterY() + deltaY(seconds);
            s.setCenterY(newCenter);
        }
    }
}
