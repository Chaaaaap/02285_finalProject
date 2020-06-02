import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Communicator {
    
    //Should return a new state where the conflict is resolves
    //Meaning either an agent or box has been moved so that the desiredState is now possible    
    //Thr return state should have been combined with the desired State                
    public State pleaseMove(State conflictingState, State desiredState, State nextState){

        // System.err.println("COMMUNICATOR");
        // System.err.println("conflictingState");
        // System.err.println(conflictingState.toString());
        // System.err.println("desiredState");
        // System.err.println(desiredState.toString());

        // Find coordinates for desired state agent
        Agent desiredAgent = desiredState.agent.get(0);
        int desiredCol = desiredAgent.col;
        int desiredRow = desiredAgent.row;
    


        // Find the agent blocking those coordinates on the conflicting state

        int desiredColTempAgent = desiredCol;
        int desiredRowTempAgent = desiredRow;
        Agent conflictingAgent = conflictingState.agent.stream().filter(a->a.col == desiredColTempAgent && a.row == desiredRowTempAgent).findFirst().orElse(null);
        // State.Box conflictingBox = conflictingState.boxes[desiredRow][desiredCol];
        State.Box conflictingBox = conflictingState.boxSparse.stream().filter(b -> b.location.row == desiredRowTempAgent && b.location.col == desiredColTempAgent).findFirst().orElse(null);

        if (conflictingAgent != null){
            return fixForAgent(conflictingState, desiredState, conflictingAgent, desiredAgent, nextState, false);
        } else if (conflictingBox.color.equals(desiredAgent.color)) { // It is my own box god damit, imma push it
            if (desiredState.action.dir2 == Command.Dir.E){
                desiredCol = desiredCol + 1;
            } else if (desiredState.action.dir2 == Command.Dir.W){
                desiredCol = desiredCol - 1; 
            } else if (desiredState.action.dir2 == Command.Dir.N){
                desiredRow = desiredRow - 1;
            } else {
                desiredRow = desiredRow + 1;
            }
            int desiredColTempBox = desiredCol;
            int desiredRowTempBox = desiredRow;
            // Find the agent blocking those coordinates on the conflicting state
            conflictingAgent = conflictingState.agent.stream().filter(a->a.col == desiredColTempBox && a.row == desiredRowTempBox).findFirst().orElse(null);
            if(conflictingAgent != null){
                return fixForAgent(conflictingState, desiredState, conflictingAgent, desiredAgent, nextState, true);
            }
            else{
                return makeWayForMyBox(conflictingState, desiredState, desiredRow, desiredCol);
            }
            
        } else {
            return makeWayForMyBox(conflictingState, desiredState, desiredRow, desiredCol);
        }   
    }

    private State makeWayForMyBox (State conflictingState, State desiredState, int row, int col){
        State.Box box = conflictingState.boxSparse.stream().filter(b -> b.location.row == row && b.location.col == col).findFirst().orElse(null);
        Agent[] agents = conflictingState.agent.stream().filter(a->a.color.equals(box.color)).toArray(Agent[]::new);
        Agent agent = null;
        if(agents.length == 0){
            return null;
        }

        for (Agent ag : agents) {
            int distance = Math.abs(ag.col - col) + Math.abs(ag.row - row);
            if (distance == 1) {
                agent = ag;
            }
        }

        if(agent == null){
            return null;
        }
      
        //Find retning aften kan gå
        Command.Dir dir = null;
        Command.Dir dir2 = null;

        State.Box conflictingBox = conflictingState.boxSparse.stream().filter(b -> b.location.row == row && b.location.col == col).findFirst().orElse(null);
        if(agent.row - row == 1){
            //
            dir2 = Command.Dir.N;
        }
        else if(agent.row - row == -1){
            //
            dir2 = Command.Dir.S;
        }
        else if(agent.col - col == 1){
            //
            dir2 = Command.Dir.W;
        }
        else if (agent.col - col == -1) {
            //
            dir2 = Command.Dir.E;
        }
        String s = "";

        if(conflictingState.isCellEmpty(agent.row, agent.col + 1)){
            conflictingState.updateAgent(agent.name, agent.row, agent.col + 1);
            dir = Command.Dir.E;
        }
        else if(conflictingState.isCellEmpty(agent.row, agent.col - 1)){
            conflictingState.updateAgent(agent.name, agent.row, agent.col - 1);
            dir = Command.Dir.W;
        }
        else if(conflictingState.isCellEmpty(agent.row + 1, agent.col)){
            conflictingState.updateAgent(agent.name, agent.row + 1, agent.col);
            dir = Command.Dir.S;
        }
        else if(conflictingState.isCellEmpty(agent.row - 1, agent.col)){
            conflictingState.updateAgent(agent.name, agent.row - 1, agent.col);
            dir = Command.Dir.N;
        }
        else{
            return null;
        }
        //pull box hen hvor agent stod før -> finde den retning
        
        switch (dir2) {
            case N:
            conflictingBox.location.row++;
                break;
            case S:
            conflictingBox.location.row--;
            break;
             case E:
             conflictingBox.location.col--;
             break;
             case W:
             conflictingBox.location.col++;
                break;
            
            default:
                break;
        }

        for (int i = 0 ; i < conflictingState.agent.size() ; i++){
            if (conflictingState.agent.get(i).name == agent.name){  
                s = s + (new Command(Command.Type.Pull, dir, dir2).toString()) + ";";
            } else {
                s = s + "NoOp;";
            }
        }
        // remove last char from s
        s = s.substring(0, s.length() - 1);
        System.out.println(s);


        s = "";
        for (int i = 0 ; i < conflictingState.agent.size() ; i++){
            if (conflictingState.agent.get(i).name == desiredState.agent.get(0).name){  
                s = s + desiredState.action.toString() + ";";
            } else {
                s = s + "NoOp;";
            }
        }
        // remove last char from s
        s = s.substring(0, s.length() - 1);
        System.out.println(s);
        return conflictingState.combineTwoStates(conflictingState, desiredState);
    }

    private State fixForAgent(State conflictingState, State desiredState, Agent conflictingAgent, Agent desiredAgent, State nextState, Boolean fixForBox){

        int conflictingCol = conflictingAgent.col;
        int conflictingRow = conflictingAgent.row;

        // Find a cell around the conflicting agent that is free
        int freeCellCol = -1;
        int freeCellRow = -1;
        Command moveCommandConflict = null;
        Command backupCommand = null;

        if (conflictingState.isCellEmpty(conflictingRow, conflictingCol + 1)){ // Move Right
            freeCellRow = conflictingRow;
            freeCellCol = conflictingCol + 1;
            backupCommand = new Command(Command.Dir.E);
            if (nextState.isCellEmpty(freeCellRow, freeCellCol)){
                moveCommandConflict = backupCommand;
            }

        } if (conflictingState.isCellEmpty(conflictingRow, conflictingCol - 1 ) && moveCommandConflict == null){ // Move Left
            freeCellRow = conflictingRow;
            freeCellCol = conflictingCol - 1;
            backupCommand = new Command(Command.Dir.W);
            if (nextState.isCellEmpty(freeCellRow, freeCellCol) && moveCommandConflict == null){
                moveCommandConflict = backupCommand;
            }

        } if (conflictingState.isCellEmpty(conflictingRow + 1, conflictingCol) && moveCommandConflict == null){ // Move Down
            freeCellRow = conflictingRow + 1;
            freeCellCol = conflictingCol;
            backupCommand = new Command(Command.Dir.S);
            if (nextState.isCellEmpty(freeCellRow, freeCellCol) && moveCommandConflict == null){
                moveCommandConflict = backupCommand;
            }

        } if (conflictingState.isCellEmpty(conflictingRow - 1, conflictingCol) && moveCommandConflict == null){ // Move Up
            freeCellRow = conflictingRow - 1;
            freeCellCol = conflictingCol;
            backupCommand = new Command(Command.Dir.N);
            if (nextState.isCellEmpty(freeCellRow, freeCellCol) && moveCommandConflict == null){
                moveCommandConflict = backupCommand;
            }

        }
        
        if (moveCommandConflict == null){
            moveCommandConflict = backupCommand;
        }
        if (backupCommand == null){
            // System.err.println("I AM STUCK");
            return null;
        }

        State resolvedState = null;

        if (fixForBox){ // Updating the box
            State.Box conflictingBox = conflictingState.boxSparse.stream().filter(b -> b.location.row == desiredAgent.row && b.location.col == desiredAgent.col).findFirst().orElse(null);
            conflictingBox.location = new Pair(conflictingRow, conflictingCol);

        }
        
        // Move the conflicting agent
        conflictingState.updateAgent(conflictingAgent.name, freeCellRow, freeCellCol);

        String s = "";

        for (int i = 0 ; i < conflictingState.agent.size() ; i++){
            if (conflictingState.agent.get(i).name == conflictingAgent.name){  
                s = s + moveCommandConflict.toString() + ";";
            } else {
                s = s + "NoOp;";
            }
        }
        // remove last char from s
        s = s.substring(0, s.length() - 1);
        System.out.println(s);
        System.err.println("Confligting solve: " + s);

        // Move the desired agent
        // conflictingState.updateAgent(desiredAgent.name, desiredAgent.row, desiredAgent.col);
        conflictingState = conflictingState.combineTwoStates(conflictingState, desiredState);

        s = "";
        for (int i = 0 ; i < conflictingState.agent.size() ; i++){
            if (conflictingState.agent.get(i).name == desiredAgent.name){  
                s = s + desiredState.action.toString() + ";";
            } else {
                s = s + "NoOp;";
            }
        }
        // remove last char from s
        s = s.substring(0, s.length() - 1);
        System.out.println(s);  
        System.err.println("Wanted move: " + s);

        //Hvis det her ikke er her så laver MAKaren mærkelig ting med agent 4 og 5, 
        //hvis det er her så gør den som den burde
        try {
            BufferedReader br = 
              new BufferedReader(new InputStreamReader(System.in));
            String st = br.readLine();
            //System.err.println("Response is: " + st);
        } catch (Exception e) {
            System.err.println("Error:" + e.getMessage());
        }


        resolvedState = conflictingState;
        
        //return null;
        // Return state
        //System.err.println("Resolved state");
        return resolvedState;
    }

}