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
//causes it to float randomly, either bounces off the sides of the screen when it reaches the border or wraps, cannot set the wrap and the bounce boolean at the same time
//for direction could potentially use the unit circle? could write some sort of visualization for this but I'm not sure
public class translate {

    private Timeline clock;
    private Rectangle backDrop;
    private Pane board;
    private ArrayList<Circle> shape;
    private double sp;
    private double maxY;
    private double minY;
    private double maxX;
    private double minX;
    private Point2D dirs;
    private double lastIndex;


    public translate(ArrayList<Circle> dots, double dirX, double dirY, int speed, Color dotColor, Color background){

        dirs = new Point2D(dirX, dirY);
        lastIndex = dots.size() - 1;
        if (speed == 2) {
            this.sp = 0.1;
        } else if (speed == 3) {
            this.sp = 0.07;
        } else { //speed = 1 or slow is the default
            this.sp = 0.4;
        }

        this.board = new Pane();
        backDrop = new Rectangle(0, 0, 1500, 800);

        maxY = 800;
        minY = 0;
        maxX = 1500;
        minX = 0;

        backDrop.setFill(background);
        board.getChildren().add(this.backDrop);
        this.shape = dots;

        for(int i = 0; i < shape.size(); i++){
            Circle circ = this.shape.get(i);
            circ.setFill(dotColor);
            this.board.getChildren().add(circ);
        }

        this.setUpClock();
    }

    public void setUpClock() {
        KeyFrame kf = new KeyFrame(Duration.seconds(0.1), (ActionEvent e) -> detectTimeStamp());
        this.clock = new Timeline(kf);
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public boolean inBounds(Circle dot){
        double x = dot.getCenterX();
        double y = dot.getCenterY();
        if((x < maxX) && (x> minX)){
            if((y < maxY) && ( y > minY)){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Pane getPane(){
        return this.board;
    }

    public void detectTimeStamp(){
        double x = dirs.getX();
        double y = dirs.getY();
        double vel = 5;
        for(int i = 0; i < shape.size(); i++){
            Circle circ = this.shape.get(i);
            circ.setCenterX(circ.getCenterX() + vel*x);
            circ.setCenterY(circ.getCenterY() + vel*y);
            if(i == lastIndex){
                if(!inBounds(circ)){
                    Point2D switchDir = new Point2D((-1)*x, (-1*y));
                    this.dirs = switchDir;
                }
            }
        }
    }
}
