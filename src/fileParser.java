import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

/*
Helper class created in the animation class as a filter between the raw file path and the information in the file
it takes the information, filters it and stores the information in the appropriate data structures, parses Drawfiles specifically
 */

public class fileParser {

    private HashMap<String, ArrayList<Point2D>> OGcirc;
    private HashMap<String, ArrayList<Point2D>> circ;
    private ArrayList<String> timeStamps;
    private ArrayList<String> LOL;
    private String currKey;
    private boolean recordTimes;

    //data structures are passed in from the animation class, and populated by the fileParser
    public fileParser(HashMap<String, ArrayList<Point2D>> circles, HashMap<String, ArrayList<Point2D>> OGcircles, ArrayList<String> stamps, ArrayList<String> listOfLines){

        this.OGcirc = OGcircles;
        this.circ = circles;
        this.timeStamps = stamps;
        this.LOL = listOfLines;
        this.currKey = Constants.DEF_KEY; //default key -- if everything works it will never be used, just put in place so it would not be null
        this.recordTimes = false;

    }

    public void fillStructures(){
        for(int i = 0; i < LOL.size(); i++){
            parse(LOL.get(i));
        }
    }

    public String makeWord(String[] w){
        String result = "";
        for(int i = 0; i < w.length; i++){
            result = result + w[i];
        }
        return result;
    }

    //helper method to delete the last character of the string
    public String deleteLast(String coordinate){

        String[] letters = coordinate.split("");
        String[] copy = new String[letters.length - 1];

        for(int i = 0; i < copy.length; i++){
            copy[i] = letters[i];
        }
        return makeWord(copy);
    }

    public void storePoint(String x, String y){

        //delete the last character
        String xMod = deleteLast(x);
        String yMod = deleteLast(y);

        double xCoord = Double.parseDouble(xMod);
        double yCoord = Double.parseDouble(yMod);

        Point2D circle = new Point2D(xCoord, yCoord);

        ArrayList<Point2D> list;
        if(!this.circ.containsKey(this.currKey)){
            //if the current key does not exist a new circle array list needs to be made under that key
            list = new ArrayList<>();
        } else {
            //the current key already has a list
            list = this.circ.get(this.currKey);
        }

        list.add(circle);
        this.circ.put(this.currKey, list);
        this.OGcirc.put(this.currKey, list);
    }

    public void printA(String[] arr){
        for(int w = 0; w < arr.length; w++){
            System.out.println(arr[w] + " ");
        }
    }

    public void parse(String line){

        String[] words = line.split(" "); //splits the line into an array of words by spaces

        if(recordTimes){ //we must be in the last part of the file
            this.timeStamps.add(line); //ways the file are formatted all time stamps get their own line
        } else {
            for(int i = 0; i < words.length; i++){
                String currW = words[i];
                if(currW.equals("Point2D")){
                    //case - point
                    String x = words[i+3];
                    String y = words[i+6];
                    i = i + 6; //skip over the rest in between
                    storePoint(x, y);
                } else if (currW.equals("TIMESTAMPS")){
                    //case - time stamp
                    recordTimes = true;
                    break;
                } else if(currW.length() > 2){
                    //case - color - update global var currkey
                    String[] spl = currW.split("");
                    String l1 = spl[0];
                    String l2 = spl[1];
                    if((l1.equals("0")) && (l2.equals("x"))){
                        this.currKey = currW;
                    }
                }
            }
        }

    }


}
