import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class progExecuter {
        //a new instance is created when play is pressed which parses the selected prog file -- if one isn't selected
    //TODO: if a prog file is not selected, create an error message saying a prog file needs to be created or selected before playing
        //arraylist for time stamps -- have the indices match up with the movement selected per time stamp
    //TODO: handle the case where there are multiple groups of dots executing multiple movements -- competing panes -- how will that look --
    //MAKE IT A PRE-REC TO THE PROGRAM THAT WITHIN EACH FRAME FOR NOW BACKGROUND COLORS AND DOT COLORS MUST BE CONSISTENT
    //TODO; create a red recorder dot appear for when it is playing and a timeline which shows how much is left  --> lock all button controls when the program is playing
    //TODO: loading program previously if selected and button pressed with a preexisting p file.

    private String fName;
    private Timeline time;
    private Pane mainPane;
    private Button st;
    private Button star;
    private String executer;
    private ArrayList<String> circleCoords;
    private ArrayList<String> progPrint;

    //will have to read the draw file for the time stamps then match them up with the screens from the progFile
    public progExecuter(String nameOfFile, Pane animationPane, Button startButton, Button stopButton){
        this.star = startButton;
        this.executer = " --- ";
        this.st = stopButton;
        this.mainPane = animationPane;
        this.fName = nameOfFile;
        this.time = new Timeline();
        this.setUpTimeline();
        this.readInInformation();
    }

    public void parseCircleList(){

    }

    public void parseProgInstructions(){

    }

    public void readInInformation(){
        this.circleCoords = new ArrayList<>();
        this.progPrint = new ArrayList<>();
        boolean readProg = false;
//        try {
//            File myObj = new File("/Users/isabellawhite/MSV/src/ProgramFiles.txt");
//            Scanner myReader = new Scanner(myObj);
//            while (myReader.hasNextLine()) {
//                String data = myReader.nextLine();
//                while (!readProg){
////                    circleCoords.add(data);
//                    if(data.equals(this.executer)){
//                        readProg = true;
//                    }
//                }
////                progPrint.add(myReader.nextLine());
//            }
//            myReader.close();
//        } catch (FileNotFoundException e) {
//            System.out.println(Constants.IO_EX_ERR);
//            e.printStackTrace();
//        }
        System.out.println("CIRCLE COORDS");
        System.out.println(this.circleCoords.toString());
        System.out.println("PROGRAM PRINT");
        System.out.println(this.progPrint.toString());
    }

    public void detectTime(){
        //method used to detect if a specific time stamp has passed
        if(st.isPressed()){
            this.time.stop(); //animation is stopped - prematurely? red recording and time
        }
    }

    public void setUpTimeline() {
        KeyFrame kf = new KeyFrame(Duration.seconds(0.1), (ActionEvent e) -> detectTime());
        this.time = new Timeline(kf);
        this.time.setCycleCount(Animation.INDEFINITE);
        //TODO: program in red circle, time label that changes/is updated in the detect Time function
        this.time.play();
    }
}
