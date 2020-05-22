import java.util.HashSet;
import java.util.Stack;

public class FindGoal {
    
    Command[] moves = new Command[]{new Command(Command.Dir.N),new Command(Command.Dir.E),new Command(Command.Dir.S),new Command(Command.Dir.W)};


    public boolean isGoalPossible(int agentRow, int agentCol, int goalRow, int goalCol){
        Stack<Coordinates> frontier = new Stack<>();
        HashSet<Coordinates> frontierSet = new HashSet<>();

        Coordinates c = new Coordinates(agentRow, agentCol);
        frontier.add(c);
        frontierSet.add(c);

        while(!frontier.isEmpty()){
            Coordinates tempC = frontier.pop();
            for (Command moveCommand : moves) {
                if(!State.walls[tempC.row+Command.dirToRowChange(moveCommand.dir1)][tempC.col+Command.dirToColChange(moveCommand.dir1)]){  
                    Coordinates temp = new Coordinates(tempC.row+Command.dirToRowChange(moveCommand.dir1), tempC.col+Command.dirToColChange(moveCommand.dir1));
                    
                    if(temp.row == goalRow && temp.col == goalCol){
                        return true;
                    }
                    if(!frontierSet.contains(temp)){
                        frontier.add(temp);
                        frontierSet.add(temp);
                    }
                }
            }
        }
        return false;
    }

    private class Coordinates{
        int row, col;

        public Coordinates(int row, int col){
            this.col = col;
            this.row = row;
        }

        @Override
        public boolean equals(Object o){
            return ((Coordinates) o).row == this.row && ((Coordinates) o).col == this.col;
        }

        @Override
        public int hashCode(){
            final int primeCol = 31;
            final int primeRow = 37; 
            int result = 1;
            result = primeCol * result + col;
            result = primeRow * result + row;
            return result;
        }

    }

}