package app;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//Read level from server
//Start search

public class App {

    public State initialState;

    public App(BufferedReader serverMessages) throws Exception{
        String line = serverMessages.readLine();
        if (!line.startsWith("#")){
            System.err.println("Error, does not start with #");
            System.exit(1);
        }

        
        this.initialState = null;

        while(!line.equals("")){

            switch (line) {
                case "#domain": 
                    State.domain = serverMessages.readLine();
                    
                break;
                case "#levelname":
                    State.levelName = serverMessages.readLine();
                break;
                case "#colors":
                    do {
                        line = serverMessages.readLine();
                        String[] colors = line.split(":|\\,");
                        for (int j = 1; j < colors.length; j++) {
                            if(colors[j].matches("[a-z]")){
                                initialState.addBox(colors[0], colors[j].charAt(0));
                            }
                            else{
                                initialState.addAgent(colors[0], colors[j].charAt(0));
                                
                            }
                        }
                    } while (!line.startsWith(("#")));
                break;
                case "#initial":
                int row = 0;
                do {
                    line = serverMessages.readLine();
                    for (int col = 0; col < line.length(); col++) {
                        char chr = line.charAt(col);
        
                        if (chr == '+') { // Wall.
                            this.initialState.walls[row][col] = true;
                        } else if ('0' <= chr && chr <= '9') { // Agent.
                            initialState.updateAgent(chr, row, col);
                        } else if ('A' <= chr && chr <= 'Z') { // Box.
                            this.initialState.updateBox(chr, row, col);
                        }else if (chr == ' ') {
                            // Free space.
                        } else {
                            System.err.println("Error, read invalid level character: " + (int) chr);
                            System.exit(1);
                        }
                    }
                    row++;
                } while (!line.startsWith("#"));
                           
                break;
                case "#goal":
                do {
                    row = 0;
                    line = serverMessages.readLine();
                    for (int col = 0; col < line.length(); col++) {
                        char chr = line.charAt(col);
                        if ('A' <= chr && chr <= 'Z') { // Goal.
                            this.initialState.goals[row][col] = chr;
                        }
                    }
                    row++;
                } while (!line.startsWith("#"));
                break;
                case "#end":
                    return;
                default:
                    break;
            }
            line = serverMessages.readLine(); 
        }

    }

    public static void main(String[] args) throws Exception {
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));

        // Use stderr to print to console
        System.err.println("SearchClient initializing. I am sending this using the error output stream.");

        // Read level and create the initial state of the problem
        App app = new App(serverMessages);
        System.out.println(app.initialState);

    }
}