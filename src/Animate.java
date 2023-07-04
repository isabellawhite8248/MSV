import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
has an animate class where you can program a specific animation based off of a timeline which can be
stopped and started with a play button, to make music videos program with the song, after obtaining the list of time
stamps then play it and screen record.
 */

//TODO: create a manipulateable backdrop
public class Animate {
    //TODO: make a checkbox called program -> more details in the read me

    private Pane animationBoard;
    private int boardWidth;
    private int boardHeight;
    private Draw drawing;
    private HBox buttonBox;
    private boolean programming;
    private Timeline detection;
    private ChoiceBox<String> pFiles;
    private Button stop;
    private Button play;

    private HashMap<String, ArrayList<Point2D>> OGcircles;
    private HashMap<String, ArrayList<Point2D>> circles;
    private Timeline time;
    private Color background;
    private ArrayList<String> stamps;
    private ArrayList<String> listOfLines;
    private Rectangle backdrop;
    private ArrayList<Circle> loadedDots;
    private String existingFile;
    private Program prog;
    private String selectedProg;

    public Animate(Draw stuff) { //needs to pass in a draw object to get the file name to load from

        //initialization of integral data structures
        this.programming = false; //necessary because we need to know when the program window is quit that way we can remove it from the pane
        this.detection = new Timeline();
        this.selectedProg = null; //if the selected program is null, program should not be able to play
        this.prog = null;

        this.OGcircles = new HashMap<>();
        this.circles = new HashMap<>();
        this.setTime();

        this.stamps = new ArrayList<>();
        this.listOfLines = new ArrayList<>();
        this.loadedDots = new ArrayList<>();
        this.existingFile = "none";

        this.drawing = stuff;
        this.boardWidth = Constants.APP_WIDTH/2 - 1;
        this.boardHeight = Constants.APP_HEIGHT - Constants.BBOX_HEIGHT;
        this.animationBoard = new Pane();

        //set the background
        this.background = Color.BLACK;
        this.backdrop = new Rectangle(0, 0, this.boardWidth, this.boardHeight);
        this.backdrop.setFill(this.background);
        this.animationBoard.getChildren().add(this.backdrop);

        this.adjustBoardSize(this.boardWidth, this.boardHeight);

        this.buttonBox = new HBox();
        this.animationBoard.getChildren().add(this.buttonBox); //sets the buttons to the top of the animation board
        this.setUpButtons();

    }

    public void addFile(){
        ArrayList<String> files = new ArrayList<>();
        try {
            File myObj = new File("/Users/isabellawhite/MSV/src/ProgramFiles.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                files.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(Constants.IO_EX_ERR);
            e.printStackTrace();
        }

        for(int i = 0; i < files.size(); i++){
            if(!this.pFiles.getItems().contains(files.get(i))){
                this.pFiles.getItems().add(files.get(i));
            } else {
                continue;
            }
        }
    }

    public void detectProgram(){

        //if there is a selected store it in the selected file name - otherwise it should store null
        this.selectedProg = this.pFiles.getValue();

        //detect if the user closes the program pane
        if(this.prog != null){
            boolean go = this.prog.getFinStat();
            if(go){
                Pane progPane = this.prog.getProgramPane();
                addFile(); //adds the file to the prog pane drop down menu in the animation pane if it does not
                //already exist
                this.animationBoard.getChildren().remove(progPane);
            }
        }
    }

    public void setTime(){
        KeyFrame kf = new KeyFrame(Duration.seconds(Constants.KF_LENGTH_DRAW), (ActionEvent e) -> detectProgram());
        this.time = new Timeline(kf);
        time.setCycleCount(Animation.INDEFINITE);
        time.play();
    }

    //helper method to get the animation board, the pane organizer uses this method to set it in the root pane
    public Pane getanimationBoard(){
        return this.animationBoard;
    }

    //helper method which reads in the loaded file of the selected radio button
    //contains an instance of fileParser
    public void populate(String filepath){
        //the populate function code was deallocated to the fileParse class
        int lineNum = 0;
        try {
            File myObj = new File(filepath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                lineNum++;
                String data = myReader.nextLine();
                if(lineNum > 1){ //skip the first line
                    this.listOfLines.add(data);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(Constants.IO_EX_ERR);
            e.printStackTrace();
        }

        fileParser parse = new fileParser(this.circles, this.OGcircles, this.stamps, this.listOfLines);
        parse.fillStructures(); //takes the data and passes it into the appropriate data structures for later

    }

    public Color getRandCol(){
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        Color randomColor = new Color(r, g, b, 1);
        return randomColor;
    }

    public void clearBoard(){
        for(int i = 0; i < this.loadedDots.size(); i++){
            this.animationBoard.getChildren().remove(this.loadedDots.get(i)); //take off all previous loaded dots
        }
        this.loadedDots = new ArrayList<>(); //clear the loaded dots variable for re-use
    }

    public void loadGUI(){
        //helper function which is called after data structures are populated which loads the circles onto the screen
        Set<String> allKeys = this.OGcircles.keySet();
        Object[] keys = allKeys.toArray();
        if(this.loadedDots.size() > 1){
            clearBoard(); //if there are already dots we want to remove it
        }
        for(int i = 0; i < keys.length; i++){
            Color keyColor = getRandCol();
            ArrayList<Point2D> list = this.OGcircles.get(keys[i]);
            for(int j = 0; j < list.size(); j++){
                Point2D circle = list.get(j);
                Circle dot = new Circle(circle.getX(), circle.getY(), Constants.CIRC_RADIUS, keyColor);
                this.loadedDots.add(dot);
                this.animationBoard.getChildren().add(dot);
            }
        }
    }

    //takes the drawing passed into the animation instance parameter and uses this to obtain the file
    //name for the helper function populate
    public void loadImage(){
        if((this.drawing.getFileName()).equals("none")){
            System.out.println("\n <<- ERROR! SELECT A FILE TOGGLE BUTTON ON THE RIGHT TO LOAD  \n");
        } else {
            String file = this.drawing.getFileName();
            if(file.equals(this.existingFile)){ //we don't do anything if the file has already been loaded
            } else {
                this.existingFile = file;
                this.populate(file); //which file is passed in
                this.loadGUI();
            }
        }
    }

    public void play(){
        //plays the programmed animation in accordance with the timeline which starts
        if(this.selectedProg != null){
            progExecuter pExecute = new progExecuter(this.selectedProg, animationBoard, this.play, this.stop);
            System.out.println("Execution success!");
        } else {
            System.out.println("Cannot play the program because no program was selected from the drop down menu");
            System.out.println("Please select a pfile from the drop down menu :)");
        }
        //TODO: setup a timeline, wait to play until this method is triggerex and when playing have a red recording symbol in the HBOx
    }

    public void stop(){
        System.out.println("stop pressed");
       // stops the animating image, stops all movements - can be used for the screen recording, resets the image to the OG locations stored in the hashmap
    }

    public boolean pFileExists(String drawFile){
        //takes in a drawFile then spits out if there is an existing pFile
        // /Users/isabellawhite/MSV/src/images/847.txt
        String[] parse = drawFile.split("/");
        String file = parse[parse.length - 1];
        file = "p" + file;
        try {
            File myObj = new File("/Users/isabellawhite/MSV/src/ProgramFiles.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if(data.equals(file)){
                    return true;
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(Constants.IO_EX_ERR);
            e.printStackTrace();
        }
        return false;
    }

    public void loadPFile(String drawFile, Pane progScreen){
        //takes the draw file, finds the corresponding p file and loads in the necessary information
        String[] parse = drawFile.split("/");
        String file = parse[parse.length - 1];
        file = "p" + file;
        boolean hitProg = false;
        //TODO: finish this -- should go in and alter the contents of the prog screen in accordance to the files

        try {
            File myObj = new File("/Users/isabellawhite/MSV/src/animeFiles/" + file);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if(data.equals(" --- ")){
                    hitProg = true;
                }
                if(hitProg){
                    //load program

                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(Constants.IO_EX_ERR);
            e.printStackTrace();
        }
    }


    public void program(){

        if(this.loadedDots.size() < 1){
            System.out.println("ERROR - CANNOT PROGRAM BECAUSE NO FILE WAS SPECIFIED, CHOOSE AND LOAD A DRAWFILE");
        } else {
            this.prog = new Program(this.loadedDots, this.stamps, this.existingFile);
            Pane progWindow = prog.getProgramPane();
            System.out.println("THE EXISTING FILE " + this.existingFile);
            if(pFileExists(this.existingFile)){
                //if the selected program exists already load the existing program into the prog pane
                loadPFile(this.existingFile, progWindow);
            }
            this.animationBoard.getChildren().add(progWindow);
            this.programming = true; //signals the programming window is open
        }

    }

    public ArrayList<String> getProgFiles(){
        ArrayList<String> aS = new ArrayList<>();
        try {
            File myObj = new File("/Users/isabellawhite/MSV/src/ProgramFiles.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                aS.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(Constants.IO_EX_ERR);
            e.printStackTrace();
        }
        return aS;
    }

    public void setUpButtons(){
        //load button - > loads the image
        Button load = new Button(Constants.LD);
        load.setOnAction((ActionEvent e) -> loadImage());
        load.setFocusTraversable(false);

        //play button -> plays the timeline to make the animation start
        this.play = new Button(Constants.PL);
        play.setOnAction((ActionEvent e) -> play());
        play.setFocusTraversable(false);

        //stop button - > stops the timeline for the animation to stop
        this.stop = new Button(Constants.STP);
        stop.setOnAction((ActionEvent e) -> stop());
        stop.setFocusTraversable(false);

        //program button -- pulls up an instance of program to program animations
        Button program = new Button("PROGRAM");
        program.setOnAction((ActionEvent e) -> program());
        program.setFocusTraversable(false);

        //todo: either implement a listener for whenever a file is added to update the dropdon box or everytime you read the file update it that way
        ArrayList<String> progFiles = this.getProgFiles();
        System.out.println(progFiles.toString());
        this.pFiles = new ChoiceBox<>();
        for(int g = 0; g < progFiles.size(); g++){
            String fileName = progFiles.get(g);
            pFiles.getItems().add(fileName);
        }

        this.buttonBox.getChildren().addAll(load, play, stop, program, pFiles);
    }

    public ArrayList<Circle> getLoadedDots(){
        return this.loadedDots;
    }

    public void adjustBoardSize(int width, int height){
        this.animationBoard.setMaxWidth(width);
        this.animationBoard.setMaxHeight(height);
        this.backdrop.setWidth(width);
        this.backdrop.setHeight(height);
    }



}
