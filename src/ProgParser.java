
import javafx.scene.shape.Circle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class ProgParser {

    private String progFileName;
    private ArrayList<Circle> shape;
    private boolean fileExists;
    private HashMap<String, ArrayList<String>> dotGroupToScreenPrint;
    private HashMap<String, ArrayList<Circle>> groups;

    public ProgParser(String drawFileName, HashMap<String, ArrayList<String>> map, ArrayList<Circle> circles){

        this.dotGroupToScreenPrint = map;
        this.shape = filter(circles);
        this.formGroups();
        //TODO: if the file name does not exist, make sure you create a new file writer to the programs list and update it
        //TODO: also if the file exists already check for that in the program class and load all the contents back into it
        String title = parseName(drawFileName);

        char[] cArr = title.toCharArray();
        this.progFileName = "";
        for(int x = 0; x < title.length(); x++){
            if(x==0){
                this.progFileName = this.progFileName + "p";
            } else {
                this.progFileName = this.progFileName + cArr[x];
            }
        }
//
//        System.out.println("HASHMAP : " + map.toString());
//        System.out.println("PROG FILE : " + this.progFileName);
        //first search all the saved program files for the drawFile name + p
        if(fileExists(this.progFileName)){
            fileExists = true;
        } else {
            fileExists = false;
        }

        //next if there is not one then make a new one, if there is one then open that one and write over that one (DO NOT APPEND)
        if(!fileExists){
            writeNewFile();
        } else {
            writeOverExistingFile();
        }

    }

    public void storeNewFileName(String nm){
        try {
            FileWriter myWriter = new FileWriter("/Users/isabellawhite/MSV/src/ProgramFiles.txt");
            myWriter.write("\n" + nm);
            myWriter.close();
            System.out.println(Constants.FILE_WRITE_SUCCESS);

        } catch (IOException e) {
            System.out.println(Constants.IO_EX_ERR);
            e.printStackTrace();
        }
    }

    public void formGroups(){
        this.groups = new HashMap<>();
        for (int i = 0; i < this.shape.size(); i++) {
            Circle dot = this.shape.get(i);
            String col = dot.getFill().toString();
            ArrayList<Circle> list;
            if(this.groups.containsKey(col)){
                list = this.groups.get(col);
            } else {
                list = new ArrayList<Circle>();
            }
            list.add(dot);
            this.groups.put(col, list);
        }
    }

    public String parseName(String name){
        String[] n = name.split("images");
        System.out.println("N ARR " + n.toString() + "\n");
        return (n[n.length -1]);
    }

    public void write(FileWriter myWriter){

        try{
            //writes the key then the dots in that specific dot group underneath
            Set s = this.groups.keySet();
            Object[] arr = s.toArray();
            for(int i = 0; i < s.size(); i++){
                String obj = (arr[i]).toString(); //to get object i out of the array it is the key and we will write to it
                myWriter.write("KEY : " + obj + "\n");
                ArrayList<Circle> circs = this.groups.get(obj);
                int count = 0;
                for(int h = 0; h < circs.size(); h++){
                    if(count % 3 == 0){
                        count = 0;
                        myWriter.write("\n");
                    }
                    myWriter.write(circs.get(h).toString() + " ");
                }
            }
            myWriter.write("\n --- \n");
            //takes information from the file and writes it in the specified hashmap file format
            myWriter.write(this.dotGroupToScreenPrint.toString());

        } catch (IOException e) {
            System.out.println(Constants.IO_EX_ERR);
            e.printStackTrace();
        }
    }

    public void writeNewFile(){
        try {
            File newFile = new File("/Users/isabellawhite/MSV/src/animeFiles/" + this.progFileName);
            storeNewFileName(this.progFileName);
            FileWriter myWriter = new FileWriter(newFile);

            write(myWriter);
            myWriter.close();
            System.out.println(Constants.FILE_WRITE_SUCCESS);

        } catch (IOException e) {
            System.out.println(Constants.IO_EX_ERR);
            e.printStackTrace();
        }
    }

    public void writeOverExistingFile(){
        try {
            FileWriter myWriter = new FileWriter("/Users/isabellawhite/MSV/src/animeFiles/" + this.progFileName);
            write(myWriter);
            myWriter.close();
            System.out.println(Constants.FILE_WRITE_SUCCESS);

        } catch (IOException e) {
            System.out.println(Constants.IO_EX_ERR);
            e.printStackTrace();
        }
    }

    public boolean fileExists(String fileName){ //searches if the file already exists -- if it is it will be stored in the ProgramFiles.txt file
        ArrayList<String> listOfLines = new ArrayList<>();
        boolean exists = false;
        try {
            File myObj = new File("/Users/isabellawhite/MSV/src/ProgramFiles.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                listOfLines.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(Constants.IO_EX_ERR);
            e.printStackTrace();
        }

        for(int i = 0; i < listOfLines.size(); i++){
            String line = listOfLines.get(i);
            if(line.equals(fileName)){
                exists = true;
            }
        }

        return exists;
    }

    public ArrayList<Circle> filter(ArrayList<Circle> list){
        //helper method to filter out any circles with invisible fill which may have been eliminated by de pixelization of key input
        for(int i = 0; i < list.size(); i++){
            Circle circ = list.get(i);
            if(circ.getFill() == Constants.INVISIBLE){
                list.remove(circ);
            }
        }

        return list;
    }
}
