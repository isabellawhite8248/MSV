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

//rotates counter clockwise around a given point
public class Rotate {

    private Timeline clock;
    private Rectangle backDrop;
    private Pane board;
    private ArrayList<Circle> shape;
    private double sp;
    private boolean clockwise;
    private double AOR;
    private Point2D PointOfRot;
    //iof right is 1 the direction of rotation is clockwise, otherwise go counter clockwise

    public Rotate(int right, ArrayList<Circle> dots, int speed, Color background, Color dotColor, Point2D POR){

        PointOfRot = POR;
        if(right == 1){
            AOR = 0.1;
        } else {
            AOR = -0.1;
        }
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
        this.shape = new ArrayList<>();

        for(int i = 0; i < dots.size(); i++){
            Circle dot = dots.get(i);
            dot.setFill(dotColor);
            this.shape.add(dot);
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

    public Point2D rotate(Point2D anchor, Point2D point, double dAngle) {
        double newX = anchor.getX() + Math.cos(dAngle) * (point.getX() - anchor.getX()) - Math.sin(dAngle) * (point.getY() - anchor.getY());
        double newY = anchor.getY() + Math.sin(dAngle) * (point.getX() - anchor.getX()) + Math.cos(dAngle) * (point.getY() - anchor.getY());
        return new Point2D(newX, newY);
    }

    public void detectTimeStamp(){
        for(int i = 0; i < this.shape.size(); i++){
            Circle c = this.shape.get(i);
            Point2D newCords = rotate(PointOfRot, new Point2D(c.getCenterX(), c.getCenterY()), AOR);
            c.setCenterX(newCords.getX());
            c.setCenterY(newCords.getY());
        }
    }
}
