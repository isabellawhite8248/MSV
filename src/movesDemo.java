//testing class for new moves -- test them on a generic shape of dots
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import movements.*;
import java.io.*;
import java.util.ArrayList;

public class movesDemo {

    private Pane root;
    private ArrayList<Point2D> shape;
    private ArrayList<Circle> testArr;

    public movesDemo() {

        this.testArr = new ArrayList<Circle>();
        this.root = new Pane();
        this.shape = fileToPoints();
        for (int i = 0; i < this.shape.size(); i++) {
            Circle circ = new Circle(2, Color.DARKORANGE); //TODO: CHANGE
            Point2D currPoint = shape.get(i);
            circ.setCenterX(currPoint.getX());
            circ.setCenterY(currPoint.getY());
            this.testArr.add(circ);
        }
        //insert demo code here to play (copy and pasted sections at the bottom)
//        translate trans = new translate(this.testArr, 1, 1, 2, Color.BLUE, Color.BLACK);
//        this.root.getChildren().add(trans.getPane());
        bounce b = new bounce(this.testArr, 2, Color.BLACK, Color.RED);
        this.root.getChildren().add(b.getPane());
//        opacityFlicker oP = new opacityFlicker(this.testArr, 2, Color.DARKGREY, Color.WHITE);
//        this.root.getChildren().add(oP.getPane());
        //code to play the merge demo - code taken from the hopper animation class that's why it's messy but whatevsies
//          merge dem = new merge(testArr, 2, Color.DARKGREY, Color.WHITE);
//          this.root.getChildren().add(dem.getPane());
    }

    public Point2D lineToPoint(String line){

        //helper method which converts the line like: Point2D [x = 1163.0, y = 550.0] to a point (1163.0, 550.0)
        String[] chain = line.split(",");

        //should split it into two indexes of the array with no comma, indice 0 = array 1, indice 1 = array 2
        //need to split the first indices into array 1 by " " then take the last element for the x coord
        //need to split array 2 by " ", take the last one then split by ] take the last one for the y coord
        //use the two coordinates to make a 2D point

        String arr1 = chain[0];
        String[] xc = arr1.split(" ");
        String x = xc[xc.length - 1];
        Double xCoord = Double.parseDouble(x);

        String arr2 = chain[1];
        String[] yc = arr2.split(" ");
        String mid = yc[yc.length - 1];
        String[] ar = mid.split("]");
        String y = ar[0];
        Double yCoord = Double.parseDouble(y);

        Point2D point = new Point2D(xCoord, yCoord);
        return point;
    }

    public ArrayList<Point2D> fileToPoints(){
        try {

            BufferedReader bufReader = new BufferedReader(new FileReader("/Users/isabellawhite/MSV/src/images/521.txt"));
            ArrayList<String> listOfLines = new ArrayList<>();
            String line = bufReader.readLine();
            while (line != null) {
                listOfLines.add(line);
                line = bufReader.readLine();
            }
            bufReader.close();
            ArrayList<Point2D> circ = new ArrayList<>();
            for (int i = 0; i < listOfLines.size(); i++){
                //we need to convert this line in the form of a string to a 2Dpoint
                circ.add(lineToPoint(listOfLines.get(i)));
            }
            return circ;

            //throws a file not found error
        } catch (FileNotFoundException e){
            System.out.println("THE FILE WAS NOT FOUND");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Pane getRoot(){
        return this.root;
    }
}

//code to play the merge demo - code taken from the hopper animation class that's why it's messy but whatevsies
//          merge dem = new merge(testArr, 2, Color.BLACK, Color.WHITE);
//          this.root.getChildren().add(dem.getPane());

//code to play the demerge demo
//        deMerge dem = new deMerge(testArr, 3, Color.BLACK, Color.LIME);
//        this.root.getChildren().add(dem.getPane());

//        code to play the bounce
//        bounce b = new bounce(this.testArr, 2, Color.BLACK, Color.RED);
//        this.root.getChildren().add(b.getPane());

//code to play the opacityFlicker
//        opacityFlicker oP = new opacityFlicker(this.testArr, 2, Color.BLACK, Color.YELLOW);
//        this.root.getChildren().add(oP.getPane());

//code to play the pixBlink demo
//        pixBlink pix = new pixBlink(this.testArr, 2, Color.BLACK, Color.HOTPINK);
//        this.root.getChildren().add(pix.getPane());

//code for pixIn demo
//        pixInOut p = new pixInOut(this.testArr, 2, Color.BLACK, Color.DARKORANGE, 0);
//        this.root.getChildren().add(p.getPane());

//code for bigInOut demo
//        bigInOut bigIn = new bigInOut(this.testArr, 2, Color.BLUE, Color.LIGHTBLUE, 0);
//        this.root.getChildren().add(bigIn.getPane());

    //        rotation demo, 1 for right 0 for left
//    Rotate oP = new Rotate(1, this.testArr, 3, Color.WHITE, Color.BLACK, new Point2D(550, 550));
//        this.root.getChildren().add(oP.getPane());
