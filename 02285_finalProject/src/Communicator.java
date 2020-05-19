public class Communicator {
    
    //TODO fix this methid
    //Should return a new state where the conflict is resolves
    //Meaning either an agent or box has been moved so that the desiredState is now possible    
    //Thr return state should have been combined with the desired State                
    public State pleaseMove(State conflictingState, State desiredState){

        System.err.println("conflictingState");
        System.err.println(conflictingState.toString());
        System.err.println("desiredState");
        System.err.println(desiredState.toString());

        // Find coordinates for desired state agent
        Agent desiredAgent = desiredState.agent.get(0);
        System.err.println("Desired Agent");
        System.err.println(desiredAgent.toString());
        int desiredCol = desiredAgent.col; // 8
        int desiredRow = desiredAgent.row; // 1
    


        // Find the agent blocking those coordinates on the conflicting state
        // Check whether the blockage is from another agent or another box
        // Below line only takes agents into consideration
        Agent conflictingAgent = conflictingState.agent.stream().filter(a->a.col == desiredCol && a.row == desiredRow).findFirst().orElse(null);

        
        System.err.println("Conflicting Agent");
        System.err.println(conflictingAgent.toString());
        System.err.println(conflictingAgent.col + " " + conflictingAgent.row);
        int conflictingCol = conflictingAgent.col;
        int conflictingRow = conflictingAgent.row;

        // Find a cell around the conflicting agent that is free
        int freeCellCol = -1;
        int freeCellRow = -1;
        if (conflictingState.isCellEmpty(conflictingRow + 1, conflictingCol + 0)){ // Move Right
            freeCellRow = conflictingRow + 1;
            freeCellCol = conflictingCol;
        } else if (conflictingState.isCellEmpty(conflictingRow - 1, conflictingCol + 0)){ // Move Left
            freeCellRow = conflictingRow - 1;
            freeCellCol = conflictingCol;
        } else if (conflictingState.isCellEmpty(conflictingRow + 0, conflictingCol + 1)){ // Move Down
            freeCellRow = conflictingRow;
            freeCellCol = conflictingCol + 1;
        } else if (conflictingState.isCellEmpty(conflictingRow + 0, conflictingCol - 1)){ // Move Up
            freeCellRow = conflictingRow;
            freeCellCol = conflictingCol - 1;
        } else {
            return null;
        }

        State resolvedState = null;
        
        // Move the conflicting agent
        conflictingState.updateAgent(conflictingAgent.name, freeCellRow, freeCellCol);

        // Move the desired agent
        conflictingState.updateAgent(desiredAgent.name, conflictingRow, conflictingCol);
        
        resolvedState = conflictingState;

        // Return state
        System.err.println("Resolved state");
        System.err.println(resolvedState);
        return resolvedState;

    }
}