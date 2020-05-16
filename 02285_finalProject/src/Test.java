import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
    
        Test test = new Test();
    
        test.testMerger();
    
    
    }

    public void testMerger(){

        System.out.println("Running Merge test");

        Merger merger = new Merger(new State());
        
        State stateAll = initializeState("MATest_merge1");
        

        //state1.action = new Command(Command.Dir.S);
        //state2.action = new Command(Command.Dir.W);

        //ArrayList<State> states = new ArrayList<>();
        //states.add(state1);
        //states.add(state2);

        //ArrayList<Command> actions = merger.merge(stateAll, states);

        // for (Command command : actions) {
        //     System.out.println(command);
        // }
    }

    public State initializeState(String level){

        State initialState = new State();

        try {  
            //the file to be opened for reading  
            FileInputStream fis=new FileInputStream("..\\level\\" + level + ".lvl");       
            Scanner sc=new Scanner(fis);    //file to be scanned  
            //returns true if there is another line to read  



            String line = "";
            line = sc.nextLine();      //returns the line that was skipped  

            while(true){  
                
                switch (line) {
                    case "#domain":
                        State.domain = sc.nextLine();

                        line = sc.nextLine();
                        break;
                    case "#levelname":
                        State.levelName = sc.nextLine();
                        if (State.levelName.toLowerCase().startsWith("sa")) {
                            State.isMultiLevel = false;
                        }
                        line = sc.nextLine();
                        break;
                    case "#colors":
                        line = sc.nextLine();
                        do {
                            String[] colors = line.split(": |\\, ");
                            for (int j = 1; j < colors.length; j++) {
                                char col = colors[j].charAt(0);
                                if ('A' <= col && col <= 'Z') {
                                    initialState.addBox(colors[0], colors[j].charAt(0));
                                } else {
                                    initialState.addAgent(colors[0], colors[j].charAt(0));
    
                                }
                            }
                            line = sc.nextLine();
                        } while (!line.startsWith(("#")));
                        break;
                    case "#initial":
                        int row = 0;
                        line = sc.nextLine();
                        initialState.MAX_COL = 0;
                        do {
                            for (int col = 0; col < line.length(); col++) {
                                char chr = line.charAt(col);
    
                                if (chr == '+') { // Wall.
                                    initialState.walls[row][col] = true;
                                } else if ('0' <= chr && chr <= '9') { // Agent.
                                    initialState.updateAgent(chr, row, col);
                                } else if ('A' <= chr && chr <= 'Z') { // Box.
                                    initialState.updateBox(chr, row, col);
                                } else if (chr == ' ') {
                                    // Free space.
                                } else {
                                    System.err.println(line);
    
                                    System.err.println("Error, read invalid level character: " + (int) chr);
                                    System.exit(1);
                                }
                            }
                            row++;
                            if (initialState.MAX_COL < line.length()) {
                                initialState.MAX_COL = line.length();
                            }
                            line = sc.nextLine();
                        } while (!line.startsWith("#"));
                        initialState.MAX_ROW = row;
                        break;
                    case "#goal":
                        line = sc.nextLine();
                        row = 0;
                        do {
                            for (int col = 0; col < line.length(); col++) {
                                char chr = line.charAt(col);
                                if ('A' <= chr && chr <= 'Z') { // Goal.
                                    initialState.goals[row][col] = chr;
                                }
                            }
                            row++;
                            line = sc.nextLine();
                        } while (!line.startsWith("#"));
                        break;
                    case "#end":
                        initialState.cleanLevel();
                        sc.close();     //closes the scanner  
                        System.out.println("Created Level");
                        System.out.println(initialState.toString()); 
                        return initialState;
                    default:
                        sc.close();     //closes the scanner  
                        return null;
                }
            }  
        }  
        catch(IOException e){  
            e.printStackTrace();  
            return null;
        }
          
    }
}