import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

//TODO: think about editing past programs, right now you have to start from scratch -- maybe something to revisit after full functionality is established
//TODO: if a user finishes a program pre maturely, default information should be loaded, all information must be present before a draw file is written to
/*
Program class creates the program pane which allows the user to program the animation based off of the information
from the draw file, to an animation, the pane should disapear after finish is clicked, when the user hovers over
the finish button then
 */

public class Program {

    //TODO: make a thing before the program window is opened to figure out whether there is an existing prog file on hand, if there is reload the previous set of preferences

    private Timeline clock;
    private Pane programPane;
    private HBox timeSects;
    private Rectangle finishBounds;
    private Rectangle msg;
    private Label txt;
    private boolean finish;
    private ArrayList<Color> numGroups;
    private Rectangle board;
    private int numStamps;
    private ArrayList<String> stamps;
    private ArrayList<VBox> screens;
    private int currBox;
    private Button sw;
    private HBox bBox;
    private HashMap<Color, ArrayList<Circle>> dotGroups;
    private ArrayList<Circle> circ;
    private Color BGcurrColor;
    private Color currColor;
    private HashMap<String, VBox> attrBox;
    private Button exit;
    private String dFile;

    public Program(ArrayList<Circle> dots, ArrayList<String> timeStamps, String dfileName){

        this.dFile = dfileName;
        this.setUpClock();
        this.attrBox = new HashMap();
        this.currColor = Color.WHITE;
        this.BGcurrColor = Color.BLACK;
        this.currBox = 0;
        this.circ = dots;
        this.screens = new ArrayList<>();
        this.stamps = timeStamps;
        this.numStamps = timeStamps.size();
        this.numGroups = new ArrayList<>();
        this.populateDotGroups();

        this.finish = false;
        this.programPane = new Pane();
        this.programPane.addEventHandler(MouseEvent.MOUSE_MOVED, new detectHover());

        this.board = new Rectangle(64, 0, Constants.APP_WIDTH - 100, Constants.APP_HEIGHT - 60);
        this.board.setFill(Color.LIGHTGRAY);
        this.board.setOpacity(0.6);
        this.programPane.getChildren().add(this.board);

        this.msg = new Rectangle(0, 0, 177, 150);
        msg.setFill(Constants.INVISIBLE);
        this.txt = new Label("\n \n  Finish the program, \n program will be saved \n in a new programmable file \n to play select the \nfile from the drop down \n and click play!");
        this.txt.setTextFill(Constants.INVISIBLE);
        programPane.getChildren().addAll(msg, txt);

        this.timeSects = new HBox();
        this.programPane.getChildren().add(this.timeSects);
        this.setButtons();
    }

    public void detectFin(){
        if(this.getFinStat()){
            HashMap<String, ArrayList<String>> af = getPrint();
            ProgParser p = new ProgParser(this.dFile, af, this.circ); //creates a file out of the program or overwrites the old one
            clock.stop();
        }
    }

    public void setUpClock() {
        KeyFrame kf = new KeyFrame(Duration.seconds(0.1), (ActionEvent e) -> detectFin());
        this.clock = new Timeline(kf);
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public void populateDotGroups(){

        this.dotGroups = new HashMap<>();
        for(int i = 0; i < circ.size(); i++){
            Circle c = this.circ.get(i);
            Color col = (Color)c.getFill();
            if(!(dotGroups.containsKey(col))){
                dotGroups.put(col, new ArrayList<Circle>());
                numGroups.add(col);
            }
            dotGroups.get(col).add(c);
        }

    }

    public boolean getFinStat(){
        return this.finish;
    }

    public HashMap<String, ArrayList<String>> getPrint(){
        //gets the screen information once finish is called
        HashMap<String, ArrayList<String>> map = new HashMap();
        ArrayList<String> lines = new ArrayList<>();
        for(int i = 0; i < this.screens.size(); i++){
            String screenKey = "\n SCREEN VBOX NUMBER " + i + "\n";
            VBox box = this.screens.get(i); //each box represents a new color group
            ObservableList allChildren = box.getChildren(); //each vBox will have a list of hBox children, each with it's own individual dot color coded group
            Object[] hboxes = allChildren.toArray();
            for(int j = 0; j < hboxes.length; j++){
                HBox b = (HBox) hboxes[j];
                ObservableList hChildren = b.getChildren();

                for(int k = 2; k < hChildren.size(); k++){ //for all the children inside the h box -- last five children must be of type VBox
                    lines.add(screenKey);
                    VBox v = (VBox) hChildren.get(k);
                    Object[] obj = v.getChildren().toArray();

                    if(obj.length < 18){
                        System.out.println("ERROR: incorrect VBOX pane value of children entered, check what is being stored in the pane");
                    }

                    ArrayList<String> sliderVals = new ArrayList<>();
                    ArrayList<String> dub = new ArrayList<>();
                    for(int y = 0; y < obj.length; y++){
                        Object o = obj[y];
                        if (y == 0){ //label, want to grab the text from the label
                           Label l = (Label)o;
                           dub.add(l.getText());}
                        else if (y == 3){//speed Choicebox selection
                           ChoiceBox c1 = (ChoiceBox)o;
                           if(c1.getValue() != null){
                               dub.add(c1.getValue().toString());
                           } else {
                               dub.add("c1 NULL");
                           }
                        } else if (y == 5){ //movement Choicebox selection
                            ChoiceBox c2 = (ChoiceBox)o;
                            if(c2.getValue() != null){
                                dub.add(c2.getValue().toString());
                            } else {
                                dub.add("c2 NULL");
                            }
                        }
                        else if (y == 7){//Color Picker, get the color for the dot color
                            ColorPicker cp1 = (ColorPicker)o;
                            if(cp1.getValue() != null){
                                dub.add(cp1.getValue().toString());
                            } else {
                                dub.add("no dot color picked");
                            }
                        }
                        else if (y == 9){ //Color Picker, get the color for the background color
                            ColorPicker cp2 = (ColorPicker)o;
                            dub.add(cp2.getValue().toString());
                            if(cp2.getValue() != null){
                                dub.add(cp2.getValue().toString());
                            } else {
                                dub.add("no background color picked");
                            }
                        }
                        else if ( y == 11){ //slider, get the val for PoRotX
                            Slider s1 = (Slider)o;
                            double s1Val = s1.getValue();
                            String ad1 = Double.toString(s1Val);
                            sliderVals.add(ad1); }
                        else if (y == 13) { //slider, get the val for POROTY
                            Slider s2 = (Slider) o;
                            double s2Val = s2.getValue();
                            String ad2 = Double.toString(s2Val);
                            sliderVals.add(ad2);}
                        else if (y == 15){ //slider, get the value for dirX
                            Slider s3 = (Slider)o;
                            double s3Val = s3.getValue();
                            String ad3 = Double.toString(s3Val);
                            sliderVals.add(ad3);}
                        else if (y == 17){ //slider, get the value for diry
                            Slider s4 = (Slider)o;
                            double s4Val = s4.getValue();
                            String ad4 = Double.toString(s4Val);
                            sliderVals.add(ad4);
                        }
                    }
                    lines.add(sliderVals.toString());
                    for(int x = 0; x < dub.size(); x++){
                        lines.add(dub.get(x));
                    }
                }
            }
            map.put(screenKey, lines);
        }
        return map;
    }

    public Pane getProgramPane(){
        return this.programPane;
    }

    public void quit(){
        getPrint();
        this.finish = true;
    }

    public void switchScreen(){

        this.programPane.getChildren().removeAll(this.bBox, this.finishBounds);
        VBox currentScreen = screens.get(currBox);
        this.programPane.getChildren().remove(currentScreen);

        if(this.currBox == (this.numStamps - 3)){
            this.currBox = 0;
        } else {
            this.currBox++;
        }

        VBox newScreen = screens.get(currBox);
        this.programPane.getChildren().add(newScreen);
        this.programPane.getChildren().addAll(this.finishBounds, this.bBox);

    }

    public VBox setAttributes(Color key, String time){

        Label dotG = new Label("DOT GROUP: " + key.toString());

        Label BgCol = new Label("MOVEMENT");
        //color picker for the background
        ColorPicker BGcolorPicker = new ColorPicker();
        BGcolorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                Color c = BGcolorPicker.getValue();
                //updates the current color with a global variable
                BGcurrColor = c;
            }
        });
        BGcolorPicker.getStyleClass().add(Constants.COL_BUT_STYLE);

        Label Col = new Label("SPEED");
        //colorpicker for the dots
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                Color c = colorPicker.getValue();
                //updates the current color with a global variable
                currColor = c;
            }
        });
        colorPicker.getStyleClass().add(Constants.COL_BUT_STYLE);

        Label l1 = new Label("DOT COLOR");

        Label l2 = new Label("BACKGROUND COLOR");

        //need the speed of motion - drop down menu with numbers
        ChoiceBox<String> speed = new ChoiceBox<>(FXCollections.observableArrayList("Slow", "Medium", "Fast"));

        //the actual movement from a dropdown menu
        ChoiceBox<String> moves = new ChoiceBox<>(FXCollections.observableArrayList("bounce", "de-merge", "merge", "opacityflicker", "pixblink", "pixIn", "pixOut", "rotateLeft", "rotateRight", "translate"));

        Slider PoRX = new Slider();
        PoRX.setMax(1500);
        PoRX.setMin(0);
        Label l3 = new Label("PoRotX " + "from 0  to 1500");

        Slider PoRY = new Slider();
        PoRY.setMax(800);
        PoRY.setMin(0);
        Label l4 = new Label("PoRotY " + "from 0  to 800");

        Slider dirX = new Slider();
        PoRX.setMax(10);
        PoRX.setMin(-10);
        Label l5 = new Label("dirX " + "from 10 to -10");

        Slider dirY = new Slider();
        PoRY.setMax(10);
        PoRY.setMin(-10);
        Label l6 = new Label("dirY " + "from 10 to -10");

        Circle cir = new Circle(5);
        cir.setStroke(Color.BLACK);
        cir.setFill(key);

        VBox organizer = new VBox();
        organizer.getChildren().addAll(dotG, cir, Col, speed, BgCol, moves, l1, colorPicker, l2, BGcolorPicker, l3, PoRX, l4, PoRY, l5, dirX, l6, dirY);

        attrBox.put(time, organizer);

        return organizer;
    }

    //shortens string to 5 characters
    public String shorten(String s){
        char[] l = s.toCharArray();
        String nStr = "";
        for(int i = 0; i < 5; i++){
            nStr = nStr + l[i];
        }
        return nStr;
    }

    public void setButtons(){

        this.bBox = new HBox();

        this.exit = new Button("Finish");
        this.finishBounds = new Rectangle(0, 0, 66, 30);
        this.finishBounds.setFill(Constants.INVISIBLE);

        exit.setOnAction((ActionEvent e) -> quit());
        exit.setFocusTraversable(false);

        this.sw = new Button("Next TStamp");
        sw.setOnAction((ActionEvent e) -> switchScreen());
        sw.setFocusTraversable(false);

        if(this.numStamps < 2){
            System.out.println("Error, there must be at least two timestamps in the draw file to enable programming functionality, please check the txt file.");
            System.exit(0);
        }

        for(int i = 1; i < (this.numStamps - 1); i++){

            VBox sets = new VBox();

            Pane labelPane = new Pane();

            Rectangle labelBg = new Rectangle(0,0, 95, 30);
            labelBg.setFill(Color.BLACK);

            Label msg = new Label(shorten(this.stamps.get(i)) + " to " + shorten(this.stamps.get(i + 1)));
            msg.setTextFill(Color.RED);

            labelPane.getChildren().addAll(labelBg, msg);

            VBox title = new VBox();
            title.getChildren().add(labelPane);

            Rectangle spacer = new Rectangle(0,0,140,100);
            spacer.setFill(Constants.INVISIBLE);

            //TODO: measure elements in the hbox, make a new hbox below -- grid do this by making an arraylist of vbox with a list of hboxes per pane then add the panes to the vbox and the vbox is the actaul screen will need to do some switching around to accomodate this
            //6 boxes per row I think -- first row have the label msg and that's it then carry on from there
            //per each dot group use the groupedDots hashmap and make a programmable box for it with the set attribute helper method

            int boxCount = 0;
            HBox row = new HBox();
            row.setSpacing(20);
            row.getChildren().addAll(spacer, labelPane);

            boolean first = true;

            for(int k = 0; k < numGroups.size(); k++){
                if(boxCount > 6){
                    boxCount = 0;
                } else if(boxCount == 0 && (!first)){
                    Color key = numGroups.get(k);
                    VBox attributeSetBox = setAttributes(key, msg.getText());
                    row.getChildren().add(attributeSetBox);
                    sets.getChildren().add(row); //row added
                    row = new HBox();
                    row.setSpacing(20);
                } else {
                    Color key = numGroups.get(k);
                    VBox attributeSetBox = setAttributes(key, msg.getText());
                    row.getChildren().add(attributeSetBox);
                }
                first = false;
                boxCount++;

                if(k == (numGroups.size() - 1)){
                    sets.getChildren().add(row);
                }
            }
            this.screens.add(sets);
        }

        this.programPane.getChildren().add(screens.get(0));

        //time stamp buttons from this time to this time
        bBox.getChildren().addAll(this.exit, sw);
        this.programPane.getChildren().addAll(this.bBox, this.finishBounds);

    }

    private void showFinishInstructions(){
        this.msg.setFill(Color.BLUE);
        this.txt.setTextFill(Color.WHITE);
    }

    private void hideFinishInstructions(){
        if(!(this.msg.getFill() == Constants.INVISIBLE)){
            this.msg.setFill(Constants.INVISIBLE);
            this.txt.setTextFill(Constants.INVISIBLE);
        }
    }

    //when we hover over the finish we need to say that it will save the program
    private class detectHover implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent e) {

            if(finishBounds.contains(e.getX(), e.getY())){
                showFinishInstructions();
            } else {
                hideFinishInstructions();
            }

        }
    }

}
