import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

//TODO: currently when no radio button is not selected, it tries to load a filepath that is non existent and crashes -- maybe link
//a default file path with a drawing saying no file is loaded this is the default drawing -- could manipulate the default radio
//buttons and just make a seperate text file dedicated to the pre existing files

//TODO: some sort of filtering method using the mod function to de-pixelate, could use same technique for the elise app cartooon pixelator
//but maybe that should be an animation func? idk
//TODO: no eraser/back method, if you mess up you have to restart the whole program
//TODO: the two default files are static, make it dynamic by having an arraylist of default files and
//if the program exits record it and update the list to include previously stored files that are not deleted in
//a designated text file? Would make funcionality a little better, but not absolutely necessary.

/*
This class takes in user mouse input to trace the stencil of a selected image, then loads the infomation into
a txt file, the dot locations stored are stored under a color key, therefore, the user should fill in each section
they want to animate with a separate color - the colors can be manipulated later, but this is to differentiate
which dots will be animated which way -- for the programming portion of the animation section to the left of the screen
 */

public class Draw {

    private BorderPane drawBoard;
    private VBox vert;
    private Boolean timeRunning;
    private VBox buttons;
    private ScrollPane box;
    private Timeline clock;
    private ImageView frame;
    private Image pic;
    private int frameWidth;
    private int frameHeight;
    private Color currColor;
    private HashMap<String, ArrayList<Point2D>> dots; //hashmap to store string color to an arraylist of corresponding dots
    private ArrayList<String> keys;
    private Button launch;
    private ArrayList<String> timeStamps;
    private ArrayList<String> files;
    private ToggleGroup fileGroup;

    public Draw(){ //TODO: functionality for the stencil works, clean up and get the switch screen buttons at the bottom working

        this.dots = new HashMap<>();
        this.timeStamps = new ArrayList<>();
        this.frameWidth = (Constants.APP_WIDTH/2);
        this.frameHeight = (Constants.APP_HEIGHT - Constants.BBOX_HEIGHT);
        this.files = new ArrayList<>();

        this.setUpFileToggles(); //must come before we setup buttons, need the information of pre-saved files

        this.currColor = Color.WHITE; //color picker button starts off picking white as the color

        this.drawBoard = new BorderPane();
        this.drawBoard.addEventHandler(MouseEvent.MOUSE_DRAGGED, new drawLine());
        this.timeRunning = false; //used to signal if the timeline is running and if it is this will switch to true

        this.buttons = new VBox(Constants.BBOX_DRAW_SPACING);
        this.buttons.setMaxWidth(Constants.MAX_BUTTON_WIDTH);
        this.clock = new Timeline();
        this.keys = new ArrayList<>();

        this.frame = new ImageView();
        this.pic = new Image(Constants.STENCIL_IMAGE_PATH);
        this.frame.setImage(this.pic);
        this.setFrameDimensions(this.frameWidth, this.frameHeight);

        this.drawBoard.setRight(this.frame);
        this.drawBoard.setLeft(buttons);

        this.setUpClock();
        this.setUpControls();
    }

    public void setUpFileToggles(){

        int lineNum = 0;
        try {
            File myObj = new File("/Users/isabellawhite/MSV/src/savedDrawFiles.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                lineNum++;
                String fileName = myReader.nextLine();
                files.add(fileName);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(Constants.IO_EX_ERR);
            e.printStackTrace();
        }

    }

    //helper function to parse the file path
    public String reverseArray(char[] arr){
        //reverses the character array passed in then converts to a string
        String forwards = "";
        for (int i = arr.length - 2; i >= 0; i--){
            forwards = forwards + arr[i]; //concat the character to the end of the string
        }
        return forwards;
    }

    //takes in the path and parses the appropriate text to pass to the animation class
    //that way it can read it into the buffer and parse the contents of the file written in
    //the draw class
    public String parseFilePath(String path){

        char[] test = path.toCharArray();
        int titleLen = 0;

        //get the length of the file title
        for (int i = test.length - 2; i >= 0; i--){ //start at the second to last element
            titleLen++;
            if(test[i] == '\''){
                break;
            }
        }

        //store the letters of the title file
        char[] backwards = new char[titleLen];
        int c = 0;
        for (int i = test.length - 2; i >= 0; i--){
            backwards[c] = test[i];
            c++;
            if(test[i] == '\''){
                break;
            }
        }

        //reverse the backwards array into a new forwards array and return
        String forwards = reverseArray(backwards);
        return forwards;
    }

    //sends the file path to the animation path -- contingent on which radio button the
    //user selects
    public String getFileName(){
        if(!(fileGroup.getSelectedToggle() == null)){
            String fileName = parseFilePath((fileGroup.getSelectedToggle()).toString());
            return Constants.FILE_PREFIX + fileName;
        } else {
            return "none";
        }
    }

    //helper method to manipulate the frame of the stencil, can do so with key input
    public void setFrameDimensions(int width, int height){
        this.frame.setFitWidth(width);
        this.frame.setFitHeight(height);
        this.drawBoard.setMaxHeight(height);
        this.drawBoard.setMaxWidth(width);
    }

    public double getFrameWidth(){
        return this.frame.getFitWidth();
    }

    public double getFrameHeight(){
        return this.frame.getFitHeight();
    }

    //returns the current time of the timeline that was set up previously
    public String detectTimeStamp(){
        String c = (clock.getCurrentTime().toString());
        return c;
    }

    //sets up the timeline to record the time stamps into the file
    public void setUpClock() {
        KeyFrame kf = new KeyFrame(Duration.seconds(Constants.KF_LENGTH_DRAW), (ActionEvent e) -> detectTimeStamp());
        this.clock = new Timeline(kf);
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public BorderPane getDrawBoard(){
        return this.drawBoard;
    }

    //helper method action set for the radio buttons to change the stencil to a blank screen -- lets
    //the user more clearly see what they have drawn
    public void changeImage(RadioButton button){
        //uses information from the radio button to either show the image or not show the image
        if((button.getText()).equals("Show Stencil")){
            this.pic = new Image(Constants.STENCIL_IMAGE_PATH);
            this.frame.setImage(this.pic);
        } else {
            this.pic = new Image("images/blank.jpeg");
            this.frame.setImage(this.pic);
        }
    }

    //helper method to assign the radio button a label, group and action method
    private void setUpRadioButton(RadioButton b, ToggleGroup group, String label) {
        b.setText(label);
        b.setToggleGroup(group);
        b.setOnAction((ActionEvent e) -> changeImage(b));
    }

    //helper method for updateFileList, when a new draw file is added, the draw files list in the
    //scroll pane to the right is updated
    public void addToggle(String label){
        RadioButton r = new RadioButton();
        this.setUpRadioButton(r, fileGroup, label);
        this.vert.getChildren().add(r);
        box.setContent(vert);
    }

    //updates the scrollpane to the right with files, triggered when load is pressed by the user, creating a
    //new draw file
    public void updateFileList(String fileName){

        try {
            FileWriter myWriter = new FileWriter("/Users/isabellawhite/MSV/src/savedDrawFiles.txt", true);
            myWriter.write("\n" + fileName);
            myWriter.close();
            System.out.println(Constants.FILE_WRITE_SUCCESS);
            addToggle(fileName);
        } catch (IOException e) {
            System.out.println(Constants.IO_EX_ERR);
            e.printStackTrace();
        }
    }

    /*
    loads the location of the circles and other necessary information into a draw file.
     */
    public void loadDrawing(){

        //creates a new file to write to
        String fileName = "/Users/isabellawhite/MSV/src/images/blank.txt"; //default is it writes to the blank txt file in the images folder
        String dub = "dub";
        String title = "ERNO";

        try {
            Random rand = new Random(); //instance of random class
            int upperbound = 1400;
            //generate random values from 0-1400
            int int_random = rand.nextInt(upperbound);
            title = int_random + Constants.FILE_SUFFIX;
            fileName = Constants.DEFAULT_FILE_PATH + int_random + Constants.FILE_SUFFIX;
            dub = int_random + Constants.FILE_SUFFIX;
            File myObj = new File(Constants.FILE_PREFIX + int_random + Constants.FILE_SUFFIX); //names the file a random string of nums (unique)
            if (myObj.createNewFile()) {
                System.out.println(Constants.FILE_CREATED_MSG + myObj.getName());
            } else {
                System.out.println(Constants.LOAD_FILE_ERROR_MSG);
            }
        } catch (IOException e) {
            System.out.println(Constants.IO_EX_ERR);
            e.printStackTrace();
        }

        //writes the file in the format: key: arraylist of points (for all dots) + time stamps
        try {
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write(Constants.FILE_TITLE + fileName + "\n");
            int count = 0; //specifies the points printed per line in the file, 5 then writes to a new line

            for(int i = 0; i < keys.size(); i++){

                String key = keys.get(i);
                //take the key and extract the list of point 2Ds from the hashmap
                ArrayList<Point2D> points = dots.get(key);
                //print the list of points after printing the key
                myWriter.write(key + Constants.NEW_LINE);

                for(int j = 0; j < points.size(); j++){
                    count++;
                    Point2D dot = points.get(j);
                    myWriter.write(dot.toString()); //print out the dot locations with separators
                    if (count > 0){
                        myWriter.write(Constants.NEW_LINE);
                        count = 0;
                    }
                }
            }

            //write the time stamps
            myWriter.write(Constants.NEW_LINE + Constants.TS_INDICATOR + Constants.NEW_LINE);
            if(timeStamps.size() > 1){
                for(int y = 0; y < timeStamps.size(); y++){
                    myWriter.write(timeStamps.get(y) + Constants.NEW_LINE);
                }
            }

            myWriter.close();
            System.out.println(Constants.FILE_WRITE_SUCCESS);

        } catch (IOException e) {
            System.out.println(Constants.IO_EX_ERR);
            e.printStackTrace();
        }

        // write the file name in the list of files that way it is saved once the program exits
        updateFileList(title);
    }

    public void launch(){
        if((this.launch.getText()).equals(Constants.LAUNCH_TL)){
            //clear the previous arraylist of time stamps
            this.timeStamps = new ArrayList<>();
            this.timeStamps.add(0, "0.00 ms"); //beginning of time so to speak
            this.clock.playFromStart(); //restarts the time
            this.launch.setText(Constants.TL_IN_PROG);
            timeRunning = true;
        } else {
            this.timeStamps.add(this.clock.getCurrentTime().toString()); //ending time stamp, will be saved along with the
            //beginning time stamp by default
            this.launch.setText(Constants.LAUNCH_TL);
            timeRunning = false;
        }
    }

    public void addStamp(){
        if(timeRunning){
            timeStamps.add(detectTimeStamp()); //add the time stamp to the list
        }
    }

    public void setUpControls(){

        //sets up a button to launch the timeline
        this.launch = new Button(Constants.LAUNCH_TL);
        launch.setOnAction((ActionEvent e) -> launch());
        launch.setFocusTraversable(false);

        //sets up a button to create a new time stamp (if timeline has been launched
        Button addStamp = new Button(Constants.ADD_ST);
        addStamp.setOnAction((ActionEvent e) -> addStamp());
        addStamp.setFocusTraversable(false);

        //sets up the load button, makes a new text file and writes to it
        Button loadButton = new Button(Constants.LOAD_F);
        loadButton.setOnAction((ActionEvent e) -> loadDrawing());
        loadButton.setFocusTraversable(false);

        //sets up radio buttons
        ToggleGroup group = new ToggleGroup();
        RadioButton r1 = new RadioButton();
        this.setUpRadioButton(r1, group, Constants.SHOW_ST);

        RadioButton r2 = new RadioButton();
        this.setUpRadioButton(r2, group, Constants.REM_ST);

        //sets up the color picker
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                Color c = colorPicker.getValue();
                //updates the current color with a global variable
                currColor = c;
            }
        });

        colorPicker.getStyleClass().add(Constants.COL_BUT_STYLE);

        this.buttons.getChildren().addAll(colorPicker, r1, r2, loadButton, this.launch, addStamp);

        this.box = new ScrollPane(); //make the loaded files radio button a scrollable list
        //potential idea for later, when mouse hovers over a file name -- number giberish give a brief description of the stencil image
        //make a toggle for stencil images too - idk may have to redesign the GUI but for now it is okay

        //sets up initial file buttons
        this.fileGroup = new ToggleGroup();
        this.vert = new VBox();

        for(int i = 0; i < this.files.size(); i++){
            String fileName = this.files.get(i);
            RadioButton r = new RadioButton();
            this.setUpRadioButton(r, fileGroup, fileName);
            this.vert.getChildren().add(r);
        }

        box.setContent(vert);
        this.buttons.getChildren().addAll(box); //TODO: add max dim to the box
    }

    //handles the mouse pressed event and when a color is selected implements a draw functionality
    //drawing where the user clicks the mouse and adding circles to the appropriate data structures
    private class drawLine implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent e) {

            double xCoord = e.getX();
            double yCoord = e.getY();

            Circle dot = new Circle(xCoord, yCoord, Constants.CIRC_RADIUS, currColor);
            drawBoard.getChildren().add(dot);

            String k = currColor.toString(); //variable to search for the key in the hashmap

            if(dots.containsKey(k)){ //if the key already has a list...
                ArrayList<Point2D> list = dots.get(k);
                Point2D point = new Point2D(xCoord, yCoord);
                list.add(point);
                dots.put(k, list);
            } else { //there is not a list under that color so make a new one
                ArrayList<Point2D> list = new ArrayList<>();
                Point2D point = new Point2D(xCoord, yCoord);
                list.add(point);
                dots.put(k, list);
                keys.add(k);
            }
        }
    }

}
