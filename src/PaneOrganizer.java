import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.scene.paint.Color;
//TODO: remember if depixelated, before you play check for any invisible circles and if they still exist, delete them so that the program can run faster
import java.util.ArrayList;
import java.util.HashMap;

/*
The pane organizer class contains an instance of animate and draw which are set to the left and right of the root
borderpane and the button box with screen toggle pair of radio buttons set at the bottom and a quit button to exit
the program
 */

public class PaneOrganizer {

    private BorderPane root;
    private String selectedRBT;
    private HBox buttonBox;
    private Timeline time;
    private Draw d;
    private Animate a;
    private String status; //keeps track about which screen the focus of the program is on -- draw or animate
    private HashMap<Point2D, Color> depixelatedPoints; //hashmap to store the point location and previous color to restore
    //if the user wishes to re-pixelate

    public PaneOrganizer() {

        this.depixelatedPoints = new HashMap<>();
        this.root = new BorderPane();
        this.time = new Timeline();
        this.buttonBox = new HBox(Constants.BBOX_SPACING);
        this.status = Constants.DEFAULT_STATUS; //originally the status of the screen is split

        //two signal which screen it is on - it starts out on the draw screen
        this.selectedRBT = Constants.NONE;
        this.setUpTimeline();
        this.setUpButtons();

        //creates the draw and animation screens
        this.d = new Draw();
        this.a = new Animate(this.d);

        //sets the dimensions of the button box
        setBBoxDim(Constants.BBOX_WIDTH, Constants.BBOX_HEIGHT);

        //sets the objects in the root pane
        this.root.setBottom(this.buttonBox);
        this.root.setLeft(this.d.getDrawBoard());
        this.root.setRight(this.a.getanimationBoard());
        this.root.addEventHandler(KeyEvent.KEY_PRESSED, new KeyHandler());

    }

    /*
    helper method which adjusts the dimensions of the button box
     */
    public void setBBoxDim(int w, int h){
        //helper method to adjust the size of the button box
        this.buttonBox.setMaxHeight(w);
        this.buttonBox.setMaxHeight(h);
    }

    /*
    sets up the timeline to record timestamps
     */
    public void setUpTimeline() {
        KeyFrame kf = new KeyFrame(Duration.seconds(Constants.KF_SECONDS), (ActionEvent e) -> detectSwitch());
        this.time = new Timeline(kf);
        time.setCycleCount(Animation.INDEFINITE);
        time.play();
    }

    /*
    method used as a helper in the timeline called at each keyframe to control the switch screen toggle buttons
     */
    public void detectSwitch(){
        int maxW = Constants.DSW_MAXW;
        int maxH = Constants.APP_HEIGHT - Constants.BBOX_HEIGHT;
        int minW = Constants.DSW_MIN;
        int minH = Constants.DSW_MIN;
        int offset = Constants.DSW_OFFSET;

        if((this.selectedRBT).equals(Constants.DRAW_LABEL_UPPER) && !((this.status).equals(Constants.DRAW_LABEL_LOWER))){
            this.d.setFrameDimensions(maxW + offset,maxH);
            this.a.adjustBoardSize(minW + offset,minH);
            this.status = Constants.DRAW_LABEL_LOWER;
        } else if ((this.selectedRBT).equals(Constants.ANIMATE_LABEL_UPPER) && !((this.status).equals(Constants.ANIMATE_LABEL_LOWER))){
            this.d.setFrameDimensions(minW,minH);
            this.a.adjustBoardSize(maxW + offset,maxH);
            this.status = Constants.ANIMATE_LABEL_LOWER;
        }
    }

    /*
    used when setting up the radio button, sets the global variable to update which radio button is selected
     */
    public void selectButton(RadioButton button) {
        this.selectedRBT = button.getText();
    }

    /*
    used to set the radio buttons up
     */
    private void setUpRadioButton(RadioButton b, ToggleGroup group, String label) {
        b.setText(label);
        b.setToggleGroup(group);
        b.setOnAction((ActionEvent e) -> selectButton(b));
        b.setFocusTraversable(false);
    }

    /*
    sets up the quit buttons and toggle radio buttons to switch between screens
     */
    public void setUpButtons(){

        Button quitButton = new Button(Constants.QUIT_LABEL);
        quitButton.setOnAction((ActionEvent e) -> System.exit(0));
        quitButton.setFocusTraversable(false);

        //sets up radio buttons
        ToggleGroup group = new ToggleGroup();
        RadioButton r1 = new RadioButton();
        this.setUpRadioButton(r1, group, Constants.DRAW_LABEL_UPPER);

        RadioButton r2 = new RadioButton();
        this.setUpRadioButton(r2, group, Constants.ANIMATE_LABEL_UPPER);

        this.buttonBox.getChildren().addAll(quitButton, r1, r2);
    }

    /*
    gets the root borderpane
     */
    public Pane getRoot() {
        return this.root;
    }

    /*
    used when the user presses 2 to depixelate the density of the dots by eliminating each filled dot which is
    a multiple of two
     */
    public void dePixelate(ArrayList<Circle> circles){
        //step one -- if the circle is a multiple of 2 -- store color in hashmap point 2D (loc) to color
        // change the color to invisible
        int c = 0;
        for (int i = 0; i < circles.size(); i++){
            Circle circ = circles.get(i);
            if(circ.getFill() == Constants.INVISIBLE){
                continue;
            } else {
                if((c%2) == 0){
                    Color oldCol = (Color) circ.getFill();
                    Point2D loc = new Point2D(circ.getCenterX(), circ.getCenterY());
                    this.depixelatedPoints.put(loc, oldCol);
                    circ.setFill(Constants.INVISIBLE);
                }
                c++;
            }
        }
    }

    /*
    restores the original density of dots in the image
     */
    public void rePixelate(ArrayList<Circle> circles){
        //if the color is invisible then look up the location in the hashmap - if it doesn't exist throw necessary error message
        //if it does exist then find the color and restore the color of the dot
        for(int i = 0; i < circles.size(); i++){
            Circle circ = circles.get(i);
            if(circ.getFill() == Constants.INVISIBLE){
                Point2D loc = new Point2D(circ.getCenterX(), circ.getCenterY()); //sketch may not work because it may make a unique object
                if(this.depixelatedPoints.containsKey(loc)){
                    Color col = this.depixelatedPoints.get(loc);
                    circ.setFill(col);
                } else {
                    System.out.println("ERROR, IMPROPERLY ADDED CIRCLE FILLS TO THE HASHMAP \n");
                }
            }
        }
    }

    /*
    a keyhandler class that handles adjusting the frame of the draw instance which manipulates the dimensions of the image stencil
     */
    private class KeyHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent e){

            //the controls will alter the dimensions of the image by this offset
            int offset = Constants.OFFSET;
            String command = e.getText();

            if(status.equals(Constants.DRAW_LABEL_LOWER)){ //key commands for adjusting the dimensions of the image in the draw pane
                switch (command) {
                    case "d": //increase width by offset number of pixels
                        d.setFrameDimensions((int)d.getFrameWidth() + offset, (int)d.getFrameHeight());
                        break;
                    case "a":  //decrease width by offset number of pixels
                        d.setFrameDimensions((int)d.getFrameWidth() - offset, (int)d.getFrameHeight());
                        break;
                    case "w": //increase height by offset number of pixels
                        d.setFrameDimensions((int)d.getFrameWidth(), (int)d.getFrameHeight() + offset);
                        break;
                    case "z":  //decrease height by offset number of pixels
                        d.setFrameDimensions((int)d.getFrameWidth(), (int)d.getFrameHeight() - offset);
                        break;
                    default:
                        System.out.println(Constants.ERROR_NOT_COMMAND);
                        break;
                }
            } else { //must be on the animation screen -- de pixelator functionality

                ArrayList<Circle> cList = a.getLoadedDots();
                int keyPressed = 0;
                switch (command) {
                    case "1": //resets to original pixelation
                        keyPressed = 1;
                        break;
                    case "2": //depixelates by a factor of two
                        keyPressed = 2;
                        break;
                    default:
                        System.out.println(Constants.ERROR_NOT_COMMAND);
                        break;
                }
                if(keyPressed == 1){
                    dePixelate(cList);
                }
                if(keyPressed == 2){
                    rePixelate(cList);
                }
            }

        }
    }

}

