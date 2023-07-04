import javafx.scene.paint.Color;

/**
 * The constants class contains all the constants necessary for the program to run
 */

public class Constants {

    //dimensions of the app
    public static int APP_HEIGHT = 800;
    public static int APP_WIDTH = 1500;

    //rest of the Constants are organized by object type
    public static int BBOX_HEIGHT = 50;
    public static int BBOX_WIDTH = APP_WIDTH;
    public static int BBOX_SPACING = 50;
    public static int BBOX_DRAW_SPACING = 20;
    public static int MAX_BUTTON_WIDTH = 300;

    //detect switch variables from the paneOrganizer
    public static int DSW_MAXW = 500;
    public static int DSW_MIN = 20;
    public static int DSW_OFFSET = 800;
    public static int OFFSET = 23; //offset for image manipulation controls

    public static double CIRC_RADIUS = 1;
    public static double KF_SECONDS = 0.2;
    public static double KF_LENGTH_DRAW = 0.1;

    public static String DEFAULT_STATUS = "split-both";
    public static String DRAW_LABEL_UPPER = "Draw";
    public static String DRAW_LABEL_LOWER = "draw";
    public static String ANIMATE_LABEL_UPPER = "Animate";
    public static String ANIMATE_LABEL_LOWER = "animate";
    public static String QUIT_LABEL = "QUIT";
    public static String NONE = "none";
    public static String ERROR_NOT_COMMAND = "error: not a command";

    //image stencil for the draw class
    public static String STENCIL_IMAGE_PATH = "images/interview.png"; //WHERE YOU CAN ALTER THE STENCIL IMAGE -- MAKE IT ALTERABLE FROM PROGRAM?? TO IMPROVE USER EXPERIENCe
    public static String DEFAULT_FILE_PATH = "/Users/isabellawhite/MSV/src/images/"; //TODO: eliminate one of these -- duplicate constants
    public static String FILE_SUFFIX = ".txt";
    public static String FILE_PREFIX = "/Users/isabellawhite/MSV/src/images/";
    public static String FILE_CREATED_MSG = "File created: ";
    public static String LOAD_FILE_ERROR_MSG = "File already exists.";
    public static String IO_EX_ERR = "An IO Exception error occurred.";
    public static String FILE_TITLE = "File Title = ";
    public static String NEW_LINE = "\n";
    public static String SEPARATOR = " / ";
    public static String TS_INDICATOR = "TIMESTAMPS";
    public static String FILE_WRITE_SUCCESS = "Successfully wrote to the file.";
    public static String LAUNCH_TL = "Launch Timeline";
    public static String TL_IN_PROG = "Timeline in Progress";
    public static String ADD_ST = "Add Stamp";
    public static String REM_ST = "Remove Stencil";
    public static String SHOW_ST = "Show Stencil";
    public static String LOAD_F = "Load to File";
    public static String COL_BUT_STYLE = "menu-button";

    //fileParsing Constants
    public static String DEF_KEY = "white";
    public static String NOT_A_POINT_ERR = "\nERROR" + " : String passed in was not a point \n";
    public static String COL = "color";
    public static String POINT = "point";
    public static String NA = "none";
    public static String MS = "ms";

    //animation Constants
    public static String LD = "LOAD";
    public static String PL = "PLAY";
    public static String STP = "STOP";

    public static Color INVISIBLE = new Color(1, 1, 1, 0);
}