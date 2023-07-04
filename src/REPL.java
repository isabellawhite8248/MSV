import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//no longer in use -- may delete later, but useful as a working repl stencil for later projects

public class REPL {
    public REPL(){
            run();
    }
    /**
     * Runs the REPL which executes user-inputted commands
     * This method is called in sol/Main.java to executing the REPL
     */
    public void run() {
        System.out.print(">>> ");
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(System.in))) {
            String line = reader.readLine();
            // While loop continuously reads and executes user input during program execution
            while (line != null) {
                // Formats the user input before running commands
                String response = "";
                String[] args = line.split("");
                if (args.length == 0) {
                    continue;
                }
                String command = args[0];
                // Based on the user-inputted commands, calls the corresponding ITravelController method
                switch (command) {
                    case "load":
                        break;
                    default:
                        response = "Invalid command. Available commands: load, cheap, fast, direct";
                }
                System.out.println(response);
                System.out.print(">>> ");
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("IOException occurred.");
        }
    }
}
