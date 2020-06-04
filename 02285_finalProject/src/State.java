import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

//Agent position
//Goal static
//Wall static
//Boxes position
//Color 
// H og G => F

public class State {
    private static final Random RNG = new Random(1);

    public int _hash = 0;
    public int h;
    public double g;
    public double f;

    public static boolean isMultiLevel = true;

    public static String levelName;
    public static String domain;

    public static int MAX_ROW = 70;
    public static int MAX_COL = 70;

    public static boolean[][] walls = new boolean[MAX_ROW][MAX_COL];
    // public Box[][] boxes = new Box[MAX_ROW][MAX_COL];
    public ArrayList<Box> boxSparse = new ArrayList<>();
    public ArrayList<Object[]> goalSparse = new ArrayList<>();
    public char[][] goals = new char[MAX_ROW][MAX_COL];
    public ArrayList<Box> box = new ArrayList<>();

    // public Agent[][] agents = new Agent[MAX_ROW][MAX_COL];
    public ArrayList<Agent> agent = new ArrayList<>();

    public State parent;
    public Command action;
    public ArrayList<Command> commands;

    // When creating a new state with the empty constructor we wish to initialize 
    // g to 0 as this is exptected to be the root state
    public State() {
        this.g = 0;
    }

    public State(State parent) {
        this.parent = parent;
        this.g = parent.g + 0.5;
    }

    public void addBox(String color, char chr) {
        box.add(new Box(color, chr));
        // Object[] box = new Object[] {-1, -1, chr};
        // boxSparse.add(box);
    }

    public void addAgent(String color, char chr) {
        agent.add(new Agent(color, chr));
    }

    public void updateAgent(char name, int row, int col) {
        for (int i = 0; i < agent.size(); i++) {
            if ((int) agent.get(i).name == (int) name) {
                agent.get(i).row = row;
                agent.get(i).col = col;
            }
        }
    }

    public void updateBox(char name, int row, int col) {
        String color = box.stream().filter(b -> b.name == name).findFirst().orElse(null).color;
        Box box = new Box(color, name, new Pair(row, col));
        boxSparse.add(box);
        // for (int i = 0; i < box.size(); i++) {
        //     if ((int) box.get(i).name == (int) name) {

        //         boxes[row][col] = box.get(i);
        // //         box.remove(i);
        //     }
        // }
    }

    public boolean isBoxGoalState() {
        for (int row = 1; row < MAX_ROW - 1; row++) {
            for (int col = 1; col < MAX_COL - 1; col++) {
                int r = row;
                int c = col;
                char g = goals[row][col];
                if (g > 0) {
                    if (boxSparse.stream()
                    .filter(b -> b.location.row == r && b.location.col == c && b.name == g).count() == 0) 
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isAgentGoalState() {
        for (Agent agent : this.agent) {
            Pair coordinates = new Pair(agent.row, agent.col);
            if (agent.goal == null) continue;
            if (!agent.goal.equals(coordinates)) {
                return false;
            }
        }
        return true;
    }
    public boolean isInitialState() {
        return this.parent == null;
    }

    public boolean cellIsFree(int row, int col, Agent ag) {
        if(ag != null){
            for (Agent a : agent) {
                if(a.row == row && a.col == col && !a.color.equals(ag.color)){
                    return false;
                }
            }
        }

        // Object[] box = boxSparse.stream().filter(b -> (int)b[0] == row && (int)b[1] == col).findFirst().orElse(null);
        return !State.walls[row][col] && !boxAt(row, col);
    }

    private boolean boxAt(int row, int col) {
        return boxSparse.stream().filter(b -> b.location.row == row && b.location.col == col).findFirst().orElse(null) != null;
    }

    public ArrayList<State> extractPlan() {
        ArrayList<State> plan = new ArrayList<>();
        State n = this;
        while (!n.isInitialState()) {
            plan.add(n);
            n = n.parent;
        }
        Collections.reverse(plan);
        return plan;
    }

    public State ChildState() {
        State copy = new State(this);
        for (int row = 0; row < MAX_ROW; row++) {
            // System.arraycopy(this.boxes[row], 0, copy.boxes[row], 0, MAX_COL);
            System.arraycopy(this.goals[row], 0, copy.goals[row], 0, MAX_COL);
        }
        try {
            ArrayList<Agent> clone = new ArrayList<Agent>(agent.size());
            for (Agent item : agent)
                clone.add((Agent) item.clone());
            copy.agent = clone;
            ArrayList<Box> cloneBox = new ArrayList<>();
            for (Box box : boxSparse) {
                cloneBox.add((Box) box.clone());
            }
            copy.boxSparse = cloneBox;
        } catch (Exception e) {
        }
        copy.box = this.box;
        
        return copy;
    }

    public ArrayList<State> getExpandedStates(boolean onlyMove) {
        ArrayList<State> expandedStates = new ArrayList<>();
            for (Command c : Command.EVERY) {

                // Determine applicability of action
                int newAgentRow = this.agent.get(0).row + Command.dirToRowChange(c.dir1);
                int newAgentCol = this.agent.get(0).col + Command.dirToColChange(c.dir1);

                if(newAgentCol < 0 || newAgentRow < 0){
                    continue;
                }

                if (c.actionType == Command.Type.Move) {
                    // Check if there's a wall or box on the cell to which the agent is moving

                    if (this.cellIsFree(newAgentRow, newAgentCol, null)) {
                        State n = this.ChildState();
                        n.action = c;
                        n.agent.get(0).row = newAgentRow;
                        n.agent.get(0).col = newAgentCol;
                        expandedStates.add(n);
                    }
                } else if (c.actionType == Command.Type.Push && !onlyMove) {
                    // Make sure that there's actually a box to move
                    if (this.boxAt(newAgentRow, newAgentCol)) {
                        int newBoxRow = newAgentRow + Command.dirToRowChange(c.dir2);
                        int newBoxCol = newAgentCol + Command.dirToColChange(c.dir2);
                        // .. and that new cell of box is free
                        if (this.cellIsFree(newBoxRow, newBoxCol, null)) {
                            State n = this.ChildState();
                            n.action = c;
                            n.agent.get(0).row = newAgentRow;
                            n.agent.get(0).col = newAgentCol;
                            n.boxSparse.stream()
                              .filter(b -> b.location.row == newAgentRow && b.location.col == newAgentCol)
                              .findFirst().orElse(null).location = new Pair(newBoxRow, newBoxCol);
                            expandedStates.add(n);
                        }
                    }
                } else if (c.actionType == Command.Type.Pull && !onlyMove) {
                    // Cell is free where agent is going
                    if (this.cellIsFree(newAgentRow, newAgentCol, null)) {
                        int boxRow = this.agent.get(0).row + Command.dirToRowChange(c.dir2);
                        int boxCol = this.agent.get(0).col + Command.dirToColChange(c.dir2);
                        // .. and there's a box in "dir2" of the agent
                        if (this.boxAt(boxRow, boxCol)) {
                            State n = this.ChildState();
                            n.action = c;
                            n.agent.get(0).row = newAgentRow;
                            n.agent.get(0).col = newAgentCol;
                            n.boxSparse.stream()
                              .filter(b -> b.location.row == boxRow && b.location.col == boxCol)
                              .findFirst().orElse(null).location = new Pair(agent.get(0).row, agent.get(0).col);
                            expandedStates.add(n);
                        }
                    }
                }
        }
        Collections.shuffle(expandedStates, RNG);
        return expandedStates;
    }


    public State combineTwoStates(State s1, State s2) {
        State newState = s1.ChildState();
        for (int i = 0; i < s1.agent.size(); i++) {
            for (int j = 0; j < s2.agent.size(); j++) {     
                if(s1.agent.get(i).col == s2.agent.get(j).col && s1.agent.get(i).row == s2.agent.get(j).row){ // There is an agent in the way where i want to move
                    if(s1.agent.get(i).name != s2.agent.get(j).name){
                        return null;
                    }
                }
                int index = j;
                Box box = s1.boxSparse.stream().filter(b -> b.location.row == s2.agent.get(index).row && b.location.col == s2.agent.get(index).col).findFirst().orElse(null);
                if(box != null && 
                   !(box.color.equals(s2.agent.get(j).color))){ // If there is an agent where I want to move
                    
                    return null;
                }

                if(s1.agent.get(i).name == s2.agent.get(j).name){
                    newState.agent.set(i, s2.agent.get(j));
                }
                else{
                    newState.agent.set(i, s1.agent.get(i));
                }
            }
        }

        //This if should be tested
        if (s2.action.actionType == Command.Type.Pull || s2.action.actionType == Command.Type.Push) {

            for (int i = 0; i < State.MAX_ROW; i++) {
                for (int j = 0; j < State.MAX_COL; j++) {
                    int rowIndex = i;
                    int colIndex = j;
                    Box box1 = s1.boxSparse.stream().filter(b -> b.location.row == rowIndex && b.location.col == colIndex).findFirst().orElse(null);
                    Box box2 = s2.boxSparse.stream().filter(b -> b.location.row == rowIndex && b.location.col == colIndex).findFirst().orElse(null);
                    if (box1 != null && box2 != null && !box1.equals(box2)) {
                        return null;
                    }

                    if(box2 != null && !(box2.equals(box1)) && !s1.cellIsFree(i, j, s2.agent.get(0))){
                        return null;
                    }
                    Box newStateBox = newState.boxSparse.stream().filter(b -> b.location.row == rowIndex && b.location.col == colIndex).findFirst().orElse(null);
                    if (newStateBox == null && box2 != null) {
                        if(s2.action.actionType == Command.Type.Pull){   
                            newStateBox = newState.boxSparse.stream()
                              .filter(b -> b.location.row == (rowIndex + (Command.dirToRowChange(s2.action.dir2))) && b.location.col == (colIndex +(Command.dirToColChange(s2.action.dir2)))).findFirst().orElse(null);
                            if(newStateBox != null){
                                newStateBox.location = box2.location;
                            } 
                            
                        }
                        else{
                            newStateBox = newState.boxSparse.stream().filter(b -> b.location.row == (rowIndex + (-1*Command.dirToRowChange(s2.action.dir2))) && b.location.col == (colIndex + (-1*Command.dirToColChange(s2.action.dir2)))).findFirst().orElse(null);
                            if(newStateBox != null){
                                newStateBox.location = box2.location;
                            }
                        } 
                    } 
                }
            }
        }
        return newState;
    }

    public class Box implements Cloneable {
        public int _hash = 0;
        public String color;
        public char name;
        public Pair location;

        public Box(String color, char name) {
            this.color = color;
            this.name = name;
        }

        public Box(String color, char name, Pair location) {
            this.color = color;
            this.name = name;
            this.location = location;
        }

        @Override
        public boolean equals(Object o) {
            if(o == null){
                return false;
            }
            return this == o || (color == ((Box)o).color && location.equals(((Box)o).location) && ((Box)o).name == name);
        }

        public Object clone() throws CloneNotSupportedException {
            Box clone = null;
            try {
                clone = (Box) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            return clone;
        }

        @Override
        public int hashCode(){
            int primeRow = 31;
            int primeCol = 37;
            int result = 1;
            result = result * primeRow + location.row;
            result = result * primeCol + location.col;
            result = result * primeCol + name;

            _hash = result;
            return result;
        }
    }

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();
        s.append("Domain: " + State.domain + "\n");
        s.append("LevelName: " + State.levelName + "\n");

        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                int coll = col; // Resloving: Local Variable Defined in an Enclosing Scope Must be Final or Effectively Final Error 
                int roww = row; // For:  } else if (agent.stream().filter(a->a.col == coll && a.row == roww).count() > 0){
                Box box = boxSparse.stream().filter(b -> b.location.row == roww && b.location.col == coll).findFirst().orElse(null);
                if (box != null) {
                    s.append(box.name);
                } else if (this.goals[row][col] > 0) {
                    s.append(Character.toLowerCase(this.goals[row][col]));
                } else if (State.walls[row][col]) {
                    s.append("+");
                } else if (agent.stream().filter(a->a.col == coll && a.row == roww).count() > 0){
                    Agent ag = agent.stream().filter(a->a.col == coll && a.row == roww).findFirst().orElse(null);
                    if (ag != null){
                        s.append(ag.name);
                    } else {
                        System.err.println("Agent is null in State.toString()");
                    }
                }else {
                    s.append(" ");
                }
            }
            s.append("\n");
        }

        for (Agent agent : this.agent){
            s.append(agent.toString());
        }

        return s.toString();
    }

    @Override
    public int hashCode() {
        if (this._hash == 0) {
            final int prime = 31;
            int result = 1;
            for (Agent a : this.agent) {
                result = prime * result + a.col;
                result = prime * result + a.row;
            }
            result = prime * result + Arrays.deepHashCode(this.boxSparse.toArray());
            //result = prime * result + this.boxSparse.hashCode();
            result = prime * result + Arrays.deepHashCode(this.goals);
            result = prime * result + Arrays.deepHashCode(State.walls);
            // result = prime * result + this.box.hashCode();
            this._hash = result;
        }
        // System.err.println(this._hash);
        // System.err.println(this.boxSparse.get(0)._hash);
        // System.err.println(this);
        return this._hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        State other = (State) obj;
        if (Arrays.deepEquals(this.agent.toArray(), other.agent.toArray()))
            return false;
        if (!Arrays.deepEquals(this.boxSparse.toArray(), other.boxSparse.toArray()))
            return false;
        if (!Arrays.deepEquals(goals, this.goals))
            return false;
        return Arrays.deepEquals(walls, State.walls);
    }


    public void cleanLevel() {

        int rows = MAX_ROW;
        int cols = MAX_COL;

        boolean[][] newWalls = new boolean[rows][cols];
        // Box[][] newBoxes = new Box[rows][cols];
        char[][] newGoals = new char[rows][cols];

        

        Collections.sort(agent);
        
        ArrayList<Agent> remove = new ArrayList<>();
        for (Agent a : agent) {
            if(a.row == -1){
                remove.add(a);
            }
        }
        agent.removeAll(remove);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if(this.goals[row][col] > 0){
                    int c = row;
                    int d = col;
                    Box tempBox = box.stream().filter(b->b.name == this.goals[c][d]).findFirst().orElse(null);
                    if (tempBox != null){
                        Agent[] tempAgents = (Agent[]) agent.stream().filter(a->a.color.equals(tempBox.color)).toArray(Agent[]::new);
                        for (Agent agent : tempAgents) {
                            if(agent.goals == null){
                                agent.goals = new char[rows][cols];
                            }
                            agent.goals[row][col] = goals[row][col];
                        }
                    }
                }
                newWalls[row][col] = State.walls[row][col];
                // newBoxes[row][col] = this.boxes[row][col];
                newGoals[row][col] = this.goals[row][col];
            }
        }

        State.walls = newWalls;
        // this.boxes = newBoxes;
        this.goals = newGoals;
    }

    public State findInitial(Agent iniAgent){
        State ini = new State();

        int rows = MAX_ROW;
        int cols = MAX_COL;

        ini.agent = new ArrayList<>();
        ini.agent.add(iniAgent);
        ini.boxSparse = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int c = row;
                int d = col;

                if(this.goals[row][col] > 0){
                    Box tempBox = box.stream().filter(b->b.name == this.goals[c][d]).findFirst().orElse(null);
                    if(tempBox != null && tempBox.color.equals(iniAgent.color)){
                        // se om der er en vej fra agent til goal
                        // tilføj kun goal såfremt der er det
                        
                        FindGoal fg = new FindGoal();

                        boolean goal = fg.isGoalPossible(iniAgent.row, iniAgent.col, row, col);

                        if(goal){
                            ini.goals[row][col] = this.goals[row][col];
                        }

                    }    
                    
                    
                }
                Box box = boxSparse.stream().filter(b -> b.location.row == c && b.location.col == d).findFirst().orElse(null);
                if(box != null){
                    if(box.color.equals(iniAgent.color)){
                        try {
                        ini.boxSparse.add((Box)box.clone());
                        } catch (Exception e) {
                            System.err.println("Should never happens... Box failed clone");
                        }
                    }
                }
                
            }
        }
        return ini;
    }

    public boolean isCellEmpty(int row, int col){
        if (boxAt(row, col)){            
            return false;
        } else if (State.walls[row][col]) {
            return false;
        } else if (agent.stream().filter(a->a.col == col && a.row == row).count() > 0){
            return false;
        } else {
            return true;
        }
    }

	public void convertBoxesToWalls() {
        for (Box box : boxSparse) {
            int row = box.location.row;
            int col = box.location.col;

            State.walls[row][col] = true;
        }
	}

}