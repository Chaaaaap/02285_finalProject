import java.util.ArrayList;

public class Heuristic {

    public Heuristic() {    }

    //Distance to goal - manhatten distance
    public int h(State n) {
        int manhattanDistance = 0;

        ArrayList<Agent> agents = n.agent;
        State.Box[][] boxes           = n.boxes;
        char[][] goals                = n.goals; 

        // Distance from each agent to a box of its type
        for (int a = 0 ; a < agents.size() ; a++){

            // Each agent boxes coordinates
            ArrayList<Pair> boxCords = new ArrayList<>();
            
            for (int row = 0 ; row < boxes.length ; row++){
                for (int col = 0 ; col < boxes[row].length ; col++){
                    if (boxes[row][col] != null){
                        
                        //if (!(boxes[row][col].name == goals[row][col])){
                            String color = boxes[row][col].color;
                            if (color.equals(agents.get(a).color)){
                                Pair pair = new Pair(row, col);
                                boxCords.add(pair);
                            }
                        //}
                    }
                }
            }

            // For each cords set add the distance from that set to the agnets coordinates
            Pair agentCords = new Pair(agents.get(a).row, agents.get(a).col);
            
            for (int i = 0 ; i < boxCords.size() ; i ++){
                if (goals[boxCords.get(i).row][boxCords.get(i).col] == ' '){
                    manhattanDistance =+ manhattanDistance(agentCords, boxCords.get(i));
                }
            }
        }

        // Distance for each box to its goal
        for (int boxRow = 0 ; boxRow < boxes.length ; boxRow++){

            for (int boxCol = 0 ; boxCol < boxes[boxRow].length ; boxCol++){
                
                if (boxes[boxRow][boxCol] != null){ 
                    // Assign name and coordinates of box
                    char boxName = boxes[boxRow][boxCol].name;
                    Pair boxCords = new Pair(boxRow, boxCol);
                    
                    // Assign an object for goal coordinates
                    Pair goalCords = new Pair(-1,-1);
                    
                    // Minimum distance from that box type to one of its goals
                    int minDist = Integer.MAX_VALUE;
                    
                    // For each index in goal matrix
                    for (int goalRow = 0 ; goalRow < boxes.length ; goalRow++){
                        for (int goalCol = 0 ; goalCol < boxes[goalRow].length ; goalCol++){
                            
                            // If name of goal at an index equals the box name, they are of same type
                            if (goals[goalRow][goalCol] == boxName){
                                
                                // Find distance between box and goal
                                int dist = manhattanDistance(boxCords, new Pair(goalRow, goalCol));
                                
                                // If this distance is less than previously observed distance, assign minDist and goal coordinates
                                if (dist < minDist){
                                    minDist = dist;
                                    goalCords.row = goalRow;
                                    goalCords.col = goalCol;
                                }
                            }
                            
                        }
                    }
                    // Add the minimum distance to the overall score
                    manhattanDistance += minDist;
                }
            }
        }
        return manhattanDistance;
    }

    public int manhattanDistance(Pair a, Pair b){
        return Math.abs(a.row - b.row) + Math.abs(a.col - b.col);
    }

    public class Pair{

        public int row;
        public int col;

        public Pair (int row, int col){
            //System.err.println("In Heuristic.Pair 1");

            this.row = row;
            //System.err.println("In Heuristic.Pair 2");

            this.col = col;
            //System.err.println("In Heuristic.Pair 3");

        }
    }
    
}