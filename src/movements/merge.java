package movements;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/*
contains three timelines, one ghost timeline to dictate the stop and start of the other two, phase one
causes dots to roam around in total entropy then phase two causes it to merge to their invisible respective
bound boxes forming an image
 */

public class merge { //TODO: ACCOUNT FOR SPEED AND DOT COLORS FIX THE CONSTANT ISSUES AND PARAMETERS PASSED IN THE DEMO CLASS
    //potentially make this a parent class or folder and have it branch out into the two seperate timelines

    private Color col;
    private Rectangle backDrop;
    private ArrayList<Circle> dots; //arraylist associated from the paneOrganizer class
    private Rectangle boundBox; //invisible rectangle that all the circles must stay inside
    private BorderPane root; //root which is associated from the paneOrganizer class
    private HashMap<Circle, Point2D> dir; //assigns an arbitrary intial direction for the circle to migrate in
    private HashMap<Circle, Point2D> OGLocs; //the dot positions of the original loaded picture from the txt file
    private HashMap<Point2D, Rectangle> bounds; //bound boxes -- MAY BE UNECESSARY, BUT LOOK COOL WHEN NOT INVISIBLE
    private Timeline time1; //first timeline - necessary to make it global so we can stop it fore timeline 2 to begin
    private HashMap<Circle, Point2D[]> paths; // maps the dot to it's path back from the OG position
    private double sp;
    private double rad;

    public merge(ArrayList<Circle> dots, int speed, Color background, Color dotColor){

        rad = dots.get(0).getRadius();
        backDrop = new Rectangle(0, 0, 1500, 800);
        backDrop.setFill(background);
        col = dotColor;

        this.sp = speed;
        if (speed == 2) {
            this.sp = 0.1;
        } else if (speed == 3) {
            this.sp = 0.07;
        } else { //speed = 1 or slow is the default
            this.sp = 0.4;
        }

        this.paths = null; //intialized in the setup timeline 2 method because
        // curr locs are not yet set -- they're still floating around, need time1 to elapse before intialization

        //setting up the bound box
        this.boundBox = new Rectangle(0, 0, 1437, 807);
        this.boundBox.setStroke(new Color(1, 1, 1, 0));
        this.boundBox.setFill(new Color(1, 1, 1, 0));

        this.time1 = null; //assigned in the setuptimeline method for the first phase animation, same for ghost

        //adding the root association from the pane organizer and boundbox to the root borderpane
        this.root = new BorderPane();
        this.root.getChildren().addAll(backDrop, this.boundBox);

        this.dots = dots; //same dot from the paneOrganizer class

        for(int k = 0; k < dots.size(); k++){
            this.dots.get(k).setFill(dotColor);
        }

        //populates the OG dot locations in a hashmap of dot to point2D
        this.OGLocs = new HashMap<Circle, Point2D>();

        for (int i = 0; i < dots.size(); i++){
            Circle dot = dots.get(i);
            OGLocs.put(dot, new Point2D(dot.getCenterX(), dot.getCenterY()));
        }

        //adds the invisible bound circles around the OG locations to the pane
        //maps the 2D point or the OG loc to the circle which is the bound box
        this.bounds = new HashMap<Point2D, Rectangle>();

        for (int i = 0; i < dots.size(); i++){
            Circle dot = dots.get(i);
            Point2D loc = OGLocs.get(dot);
            Rectangle rect = new Rectangle(loc.getX() - 2.5, loc.getY() - 2.5, 5, 5); //TODO: replace with constants
            this.bounds.put(loc, rect);
            rect.setStroke(new Color(1, 1, 1, 0)); //looks wicked cool if you set it to red just saying
            rect.setFill(new Color(1, 1, 1, 0));
            this.root.getChildren().add(rect); //kept invisible for now so if we need to we can change it and
            //visually test whether the circles are returning to their bounds
        }

        //populates the dictionary using the associated arraylist of dots, direction assigns a random direction
        //for the dots to randomly migrate during the first phase of the animation
        this.dir = new HashMap<Circle, Point2D>();
        for (int i = 0; i < dots.size(); i++){
            dir.put(dots.get(i), genRand(0, 0, 5, 5));
        }

        this.startPos(this.dots); //assigns dots to a random start position
        this.setupTimeline2();
        this.setupTimeline();

    }

    public Pane getPane(){
        return root;
    }

    public void startPos(ArrayList<Circle> listOfCircles){
        //sets the dots to a random position of the screen before the timeline1 starts
        for (int i = 0; i < listOfCircles.size(); i++){

            //x max = 1437 (width of the bound box)
            //y max = 807 (height of the bound box)

            Point2D c = genRand(0, 0, 1437,
                    807);

            listOfCircles.get(i).setCenterX(c.getX());
            listOfCircles.get(i).setCenterY(c.getY());
            listOfCircles.get(i).setRadius(rad);
            listOfCircles.get(i).setFill(col);
            root.getChildren().add(listOfCircles.get(i));
        }
    }

    public void setupTimeline(){
        KeyFrame kf = new KeyFrame(Duration.seconds(this.sp), new HopHandler());
        time1 = new Timeline(kf);
        time1.setCycleCount(50); //causes it to play for 5 seconds max
        time1.play();
    }

    public void setupTimeline2(){

        KeyFrame kf = new KeyFrame(Duration.seconds(this.sp), new mergeHandler());
        Timeline timeline = new Timeline(kf);
        timeline.setDelay(Duration.seconds(5)); //delays it for time1 to play out -- 5 seconds
        //TODO: check the transition, may be a gap becuase you put that extra thing that stops time1 at 4.5 seconds, accessing it as a global variable

        timeline.setCycleCount(Animation.INDEFINITE); //will stop motion when all circles migrate to bounds -- outlined by boundboxes initialized in the constructor

        //before we play we need to setUp the paths -- so the dots will know where to go
//        this.paths = makePath(this.OGLocs, this.dots);
        System.out.println("timeline 2 set up, locations stored");
        timeline.play();
    }

    private class HopHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e){

            for (int i = 0; i < dots.size(); i++){
                //moves the dots in random ways
                Circle dot = dots.get(i);
                Point2D trans = dir.get(dot);
                double xMove = trans.getX();
                double yMove = trans.getY();

                if (boundBox.contains(dot.getCenterX(), dot.getCenterY())){ //move in arbitrary dir if in boundbox
                    // -- stored in the hashmap

                    Double newCenterX = dot.getCenterX() + xMove;
                    dot.setCenterX(newCenterX);
                    Double newCenterY = dot.getCenterY() + yMove;
                    dot.setCenterY(newCenterY);
                } else { //move in the opposite direction -- because it is no longer in the bound box

                    Double newCenterX = dot.getCenterX() - xMove;

                    dot.setCenterX(newCenterX);
                    Double newCenterY = dot.getCenterY() - yMove;
                    dot.setCenterY(newCenterY);
                }

            }
        }
    }

    private class mergeHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent e){

            int counter = 0;
            if (counter == 0) {
                paths = makePath(OGLocs, dots);
            }

            for (int i = 0; i < dots.size(); i++){
                if (counter < 50){
                    Circle dot = dots.get(i);
                    Point2D[] path = paths.get(dot);
                    Point2D point = path[counter];
                    setLoc(dot, point.getX(), point.getY());
                }
            }

            counter = counter + 1;
        }
    }

    public Point2D getEquation(Point2D start, Point2D end){
        //helper method which is used in makePath, takes in two points, the start and the end of the path
        //calculates a line with the two points in the form y = mx + b, then spits out the equation in the
        //form of a tuple, Point2D version in java with the x being the slope and the y being the y intercept
        Double x1 = start.getX();
        Double x2 = end.getX();
        Double y1 = start.getY();
        Double y2 = end.getY();

        Double slope = (y1 - y2) / (x1 - x2);

        //y = mx + b --> get the b, using the starting point, x1 and y1
        Double yIntercept = y1 - ((slope)*(x1));

        return new Point2D(slope, yIntercept);
    }

    public Double distance(Double x1, Double x2, Double y1, Double y2){ //distance helper function
        Double sq1 = (x2 - x1)*(x2 - x1); //x distance squared
        Double sq2 = (y2 - y1)*(y2 - y1); //y distance squared
        return Math.sqrt(sq1 + sq2);
    }

    public HashMap<Circle, Point2D[]> makePath(HashMap<Circle, Point2D> OGLocs, ArrayList<Circle> shapes){
        // hashmap nested mapping dots to mini hashmap maping an equation
        // (tuple) to a array of 50 points, constants path size, each keyframe it will loop through a point until it
        // gets to the end, then it will stay stationary, we can do this only this method is called after OGlocs hashmap
        //is populated in the constructor
        HashMap<Circle, Point2D[]> map = new HashMap<>();

        for (int i = 0; i < dots.size(); i++){

            //initalizes all necessary components
            Circle dot = shapes.get(i);
            Point2D curr = new Point2D(dot.getCenterX(),dot.getCenterY());
            Point2D origin = this.OGLocs.get(dot);

            //uses the helper method to calculate an equation between the curr and origin point
            Point2D path = getEquation(curr, origin);
            Double pathDist = distance(curr.getX(), origin.getX(), curr.getY(), origin.getY());
            Double interval = pathDist/ 50;
            Point2D[] route = new Point2D[50]; //sets up the array of points that make up the path

            Double x = curr.getX(); //not really needed mostly added for readability
            Double y = curr.getY();

            Double m = path.getX();
            Double b = path.getY();

            //uses the equation to calculate the corresponding path of 50 evenly spaced points with the start being the
            //0th index of the array and the OG loc or target point being the 49th or last index

            for(int k = 0; k < route.length; k++){ //populates the path of points

                x = x + interval; //increments the x up by the interval
                y = (m)*x + b; //calculates the y based off of the new x

                Point2D p = new Point2D(x, y);
                route[k] = p;
            }

            Point2D[] value = route;
            Circle key = dot; //extraneous, already cited above -- just used so I can keep things straight -- organized
            map.put(key, value);
            //we do this by taking the distance between the target and current point and dividing by 50 then starting
            // at that number and increment through the indexes by that much
        }
        return map;
    }

    public Point2D genRand(double minX, double minY, double maxX, double maxY){
        //generates a number between an x range and a y range and stores it in a tuple -- point 2D to access later on
        double rangeMinX = minX;
        double rangeMaxX = maxX;

        double rangeMinY = minY;
        double rangeMaxY = maxY;

        Random x = new Random();
        double xRand = rangeMinX + (rangeMaxX - rangeMinX)* x.nextDouble();

        Random y = new Random();
        double yRand = rangeMinY + (rangeMaxY - rangeMinY)* y.nextDouble();

        Point2D tup = new Point2D(xRand, yRand);

        return tup;
    }

    private void setLoc(Circle dot, Double x, Double y){ //helper method to set the location of the dot
        // in the event handler, when merging them into the final picture
        dot.setCenterX(x);
        dot.setCenterY(y);
    }
}
