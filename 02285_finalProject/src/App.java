
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;


//Read level from server
//Start search

public class App {

    public State initialState;

    public App(BufferedReader serverMessages) throws Exception {
        
        // Send client name to server via stdout stream
        System.out.println("Client");

        // Reads lines of the level file provided as argument when running the server
        String line = serverMessages.readLine();

        if (!line.startsWith("#")){
            System.err.println("Error, does not start with #");
            System.exit(1);
        }
        
        this.initialState = new State();
        
        while(!line.equals("")){
            switch (line) {
                case "#domain": 
                    State.domain = serverMessages.readLine();
                    line = serverMessages.readLine();
                break;
                case "#levelname":
                    State.levelName = serverMessages.readLine();
                    line = serverMessages.readLine();
                break;
                case "#colors":
                line = serverMessages.readLine();    
                do {
                        String[] colors = line.split(": |\\, ");
                        for (int j = 1; j < colors.length; j++) {
                            char col = colors[j].charAt(0);
                            if('A' <= col && col <= 'Z'){
                                initialState.addBox(colors[0], colors[j].charAt(0));
                            }
                            else{
                                initialState.addAgent(colors[0], colors[j].charAt(0));
                                
                            }
                        }
                        line = serverMessages.readLine();
                    } while (!line.startsWith(("#")));
                break;
                case "#initial":
                int row = 0;
                line = serverMessages.readLine();
                initialState.MAX_COL = 0;
                do {
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
                            System.err.println(line);
                            
                            System.err.println("Error, read invalid level character: " + (int) chr);
                            System.exit(1);
                        }
                    }
                    row++;
                    if(initialState.MAX_COL < line.length()){
                        initialState.MAX_COL = line.length();
                    }
                    line = serverMessages.readLine();
                } while (!line.startsWith("#"));          
                    initialState.MAX_ROW = row;
                break;
                case "#goal":
                line = serverMessages.readLine();
                row = 0;
                do {                   
                    for (int col = 0; col < line.length(); col++) {
                        char chr = line.charAt(col);
                        if ('A' <= chr && chr <= 'Z') { // Goal.
                            this.initialState.goals[row][col] = chr;
                        }
                    }
                    row++;
                    line = serverMessages.readLine();
                } while (!line.startsWith("#"));
                break;
                case "#end":
                    initialState.cleanLevel();
                return;
                default:
                
                    break;
            }
        }

       
    }

    public ArrayList<State> Search(Strategy strategy) {
        System.err.format("Search starting with strategy %s.\n", strategy.toString());
        strategy.addToFrontier(this.initialState);

        int iterations = 0;
        while (true) {
            if (iterations == 1000) {
                System.err.println(strategy.searchStatus());
                iterations = 0;
            }

            if (strategy.frontierIsEmpty()) {
                return null;
            }

            State leafState = strategy.getAndRemoveLeaf();

            if (leafState.isGoalState()) {
                return leafState.extractPlan();
            }

            
            strategy.addToExplored(leafState);
            for (State n : leafState.getExpandedStates()) { // The list of expanded states is shuffled randomly; see State.java.
                if (!strategy.isExplored(n) && !strategy.inFrontier(n)) {
                    strategy.addToFrontier(n);
                }
            }
            iterations++;
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));

        // Use stderr to print to console
        System.err.println("SearchClient initializing. I am sending this using the error output stream.");

        // Read level and create the initial state of the problem
        App app = new App(serverMessages);

        Strategy strategy = new Strategy.StrategyBFS();

        ArrayList<State> solution;
        try {
            solution = app.Search(strategy);
        } catch (OutOfMemoryError ex) {
            System.err.println("Maximum memory usage exceeded.");
            solution = null;
        }

        if (solution == null) {
            System.err.println(strategy.searchStatus());
            System.err.println("Unable to solve level.");
            System.exit(0);
        } else {
            System.err.println("\nSummary for " + strategy.toString());
            System.err.println("Found solution of length " + solution.size());
            System.err.println(strategy.searchStatus());

            for (State n : solution) {
                String act = n.action.toString();
                System.out.println(act);
                
                String response = serverMessages.readLine();
                if (response.contains("false")) {
                    System.err.format("Server responsed with %s to the inapplicable action: %s\n", response, act);
                    System.err.format("%s was attempted in \n%s\n", act, n.toString());
                    break;
                }
            }
        }

    }
}