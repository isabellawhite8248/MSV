package movements;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.HashMap;

//starts in the bound box then demerges and floats randomly
public class deMerge {

    private Timeline clock;
    private double sp;
    private Pane board;
    private ArrayList<Circle> OGlocs;
    private Rectangle backDrop;
    private ArrayList<Rectangle> bounds;
    private HashMap<Circle, String> targetMet; //records whether the circles have met their target -- if they are they will
    private ArrayList<Point2D> mb; //contains the slope, then y intercept for that index of path between the point and the bound box
    //have to be set to a state of entropy for the remainder of the timeline, string will be initialized to "no"

    //each circles matches up with a square in the bounds array, this will dictate the path it will go to, and once
    // it reaches its random location it will float in a complete state of entropy

    public deMerge(ArrayList<Circle> dots, int speed, Color background, Color dotCol){

        this.board = new Pane();
        this.OGlocs = dots;
        this.bounds = new ArrayList<>();
        this.mb = new ArrayList<>();

        if (speed == 2) {
            this.sp = 0.1;
        } else if (speed == 3) {
            this.sp = 0.07;
        } else { //speed = 1 or slow is the default
            this.sp = 0.4;
        }

        backDrop = new Rectangle(0, 0, 1500, 800);
        backDrop.setFill(background);
        board.getChildren().add(this.backDrop);

        this.targetMet = new HashMap<>();

        for(int i = 0; i < this.OGlocs.size(); i++){

            int y = getRandomNumber(0, 800);
            int x = getRandomNumber(0, 1500);

            Rectangle point = new Rectangle(x, y, 8, 8);
            point.setFill(new Color(1, 1, 1, 0));

            this.bounds.add(point);
            this.board.getChildren().add(point);

            Circle p = this.OGlocs.get(i);
            p.setFill(dotCol);
            board.getChildren().add(p);
            targetMet.put(p, "no");

            double y1 = p.getCenterY();
            double x1 = p.getCenterX();

            //calculate the equation to populate the path arraylist
            double m = (y - y1)/(x - x1);
            double b = y - m*x;
            Point2D path = new Point2D(m, b);
            mb.add(path);
        }

        assert(mb.size() == bounds.size());
        assert(OGlocs.size() == mb.size());
        assert(bounds.size() == OGlocs.size());

        this.setUpClock();
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public double getRandomDouble(double min, double max) {
        return ((Math.random() * (max - min)) + min);
    }

    public Pane getPane(){
        return board;
    }

    public void setUpClock() {
        KeyFrame kf = new KeyFrame(Duration.seconds(this.sp), (ActionEvent e) -> detectTimeStamp());
        this.clock = new Timeline(kf);
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public double getYCoor(Point2D equation, double x){
        double slope = equation.getX();
        double yInter = equation.getY();
        double y = slope*x + yInter;
        return y;
    }

    public void move(Circle c, Point2D newLoc){
        c.setCenterX(newLoc.getX());
        c.setCenterY(newLoc.getY());
    }

    public void detectTimeStamp(){
        for(int i = 0; i < OGlocs.size(); i++){
            Circle circ = this.OGlocs.get(i);
            String targetReached = this.targetMet.get(circ);

            if(targetReached.equals("no")){
                //calculate in the direction of the target, if the point lies in the bound box change the value to "yes"
                double newX = circ.getCenterX() + 2;
                double newY = getYCoor(mb.get(i), newX);
                Point2D n = new Point2D(newX, newY);

                if(bounds.get(i).contains(n)){ //check if the current point lies in the target bound box
                    targetMet.put(circ, "yes"); //the target has been met if it is inside
                }

                move(circ, n); //move it closer to the target

            } else { //the target has been reaches so let it just bounce around in entropy -- won't go off the screen because each
                //dot has a 40 pixel radius and we already calculated the targets to be within the bounds of the app dimensions
                Point2D newLoc = entropy(circ.getCenterX(), circ.getCenterY());
                circ.setCenterX(newLoc.getX());
                circ.setCenterY(newLoc.getY());
            }
        }
    }

    //takes in a current set of coordinates and returns a new point in space randomly within a 3 pixel radius
    public Point2D entropy(double currx, double curry){
        double newX = currx + getRandomDouble(-40, 40);
        double newY = curry + getRandomDouble(-40, 40);
        Point2D p = new Point2D(newX, newY);
        return p;
    }
}
