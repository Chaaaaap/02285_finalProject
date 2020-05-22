public class Communicator {
    
    //TODO fix this method
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
        Agent conflictingAgent      = conflictingState.agent.stream().filter(a->a.col == desiredColTempAgent && a.row == desiredRowTempAgent).findFirst().orElse(null);
        State.Box conflictingBox    = conflictingState.boxes[desiredRow][desiredCol];

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
        Agent[] agents = conflictingState.agent.stream().filter(a->a.color.equals(conflictingState.boxes[row][col].color)).toArray(Agent[]::new);
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
        if(agent.row - row == 2){
            conflictingState.boxes[row+1][col] = conflictingState.boxes[row][col];
            conflictingState.boxes[row][col] = null;
            dir2 = Command.Dir.N;
        }
        else if(agent.row - row == -2){
            conflictingState.boxes[row-1][col] = conflictingState.boxes[row][col];
            conflictingState.boxes[row][col] = null;
            dir2 = Command.Dir.S;
        }
        else if(agent.col - col == 2){
            conflictingState.boxes[row][col+1] = conflictingState.boxes[row][col];
            conflictingState.boxes[row][col] = null;
            dir2 = Command.Dir.W;
        }
        else{
            conflictingState.boxes[row][col-1] = conflictingState.boxes[row][col];
            conflictingState.boxes[row][col] = null;
            dir2 = Command.Dir.E;
        }

        String s = "";

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

        } if (conflictingState.isCellEmpty(conflictingRow, conflictingCol - 1)){ // Move Left
            freeCellRow = conflictingRow;
            freeCellCol = conflictingCol - 1;
            backupCommand = new Command(Command.Dir.W);
            if (nextState.isCellEmpty(freeCellRow, freeCellCol)){
                moveCommandConflict = backupCommand;
            }

        } if (conflictingState.isCellEmpty(conflictingRow + 1, conflictingCol)){ // Move Down
            freeCellRow = conflictingRow + 1;
            freeCellCol = conflictingCol;
            backupCommand = new Command(Command.Dir.S);
            if (nextState.isCellEmpty(freeCellRow, freeCellCol)){
                moveCommandConflict = backupCommand;
            }

        } if (conflictingState.isCellEmpty(conflictingRow - 1, conflictingCol)){ // Move Up
            freeCellRow = conflictingRow - 1;
            freeCellCol = conflictingCol;
            backupCommand = new Command(Command.Dir.N);
            if (nextState.isCellEmpty(freeCellRow, freeCellCol)){
                moveCommandConflict = backupCommand;
            }

        }
        
        if (moveCommandConflict == null){
            moveCommandConflict = backupCommand;
        }
        if (backupCommand == null){
            System.err.println("I AM STUCK");
            return null;
        }

        State resolvedState = null;

        if (fixForBox){ // Updating the box
            conflictingState.boxes[conflictingRow][conflictingCol] = conflictingState.boxes[desiredAgent.row][desiredAgent.col];
            conflictingState.boxes[desiredAgent.row][desiredAgent.col] = null;

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

        // Move the desired agent
        // conflictingState.updateAgent(desiredAgent.name, desiredAgent.row, desiredAgent.col);
        conflictingState = conflictingState.combineTwoStates(conflictingState, desiredState);
        System.err.println(conflictingState);
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


        resolvedState = conflictingState;

        // Return state
        // System.err.println("Resolved state");
        // System.err.println(resolvedState);
        return resolvedState;
    }

}