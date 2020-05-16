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

    public static boolean isMultiLevel = true;

    public static String levelName;
    public static String domain;

    public static int MAX_ROW = 70;
    public static int MAX_COL = 70;

    public static boolean[][] walls = new boolean[MAX_ROW][MAX_COL];
    public Box[][] boxes = new Box[MAX_ROW][MAX_COL];
    public char[][] goals = new char[MAX_ROW][MAX_COL];
    public ArrayList<Box> box = new ArrayList<>();

    // public Agent[][] agents = new Agent[MAX_ROW][MAX_COL];
    public ArrayList<Agent> agent = new ArrayList<>();

    public State parent;
    public Command action;
    public ArrayList<Command> commands;

    public State() {
    }

    public State(State parent) {
        this.parent = parent;
    }

    public void addBox(String color, char chr) {
        box.add(new Box(color, chr));
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
        for (int i = 0; i < box.size(); i++) {
            if ((int) box.get(i).name == (int) name) {

                boxes[row][col] = box.get(i);
                //box.remove(i);
            }
        }
    }

    public boolean isGoalState() {
        for (int row = 1; row < MAX_ROW - 1; row++) {
            for (int col = 1; col < MAX_COL - 1; col++) {
                char g = goals[row][col];
                if (boxes[row][col] == null && g > 0) {
                    return false;
                } else if (boxes[row][col] == null) {
                    continue;
                } else {
                    char b = (boxes[row][col].name);
                    if (g > 0 && b != g) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isInitialState() {
        return this.parent == null;
    }

    private boolean cellIsFree(int row, int col) {
        return !State.walls[row][col] && this.boxes[row][col] == null;
    }

    private boolean boxAt(int row, int col) {
        return this.boxes[row][col] != null;
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

    private State ChildState() {
        State copy = new State(this);
        for (int row = 0; row < MAX_ROW; row++) {
            System.arraycopy(this.walls[row], 0, copy.walls[row], 0, MAX_COL);
            System.arraycopy(this.boxes[row], 0, copy.boxes[row], 0, MAX_COL);
            System.arraycopy(this.goals[row], 0, copy.goals[row], 0, MAX_COL);
        }

        try {
            ArrayList<Agent> clone = new ArrayList<Agent>(agent.size());
            for (Agent item : agent)
                clone.add((Agent) item.clone());
            copy.agent = clone;
        } catch (Exception e) {
            // TODO: handle exception
        }

        return copy;
    }

    public ArrayList<State> getExpandedStates() {
        ArrayList<State> expandedStates = new ArrayList<>();
            ArrayList<State> states = new ArrayList<>();
            for (Command c : Command.EVERY) {

                // Determine applicability of action
                int newAgentRow = this.agent.get(0).row + Command.dirToRowChange(c.dir1);
                int newAgentCol = this.agent.get(0).col + Command.dirToColChange(c.dir1);

                if (c.actionType == Command.Type.Move) {
                    // Check if there's a wall or box on the cell to which the agent is moving

                    if (this.cellIsFree(newAgentRow, newAgentCol)) {
                        State n = this.ChildState();
                        n.action = c;
                        n.agent.get(0).row = newAgentRow;
                        n.agent.get(0).col = newAgentCol;
                        expandedStates.add(n);
                    }
                } else if (c.actionType == Command.Type.Push) {
                    // Make sure that there's actually a box to move
                    if (this.boxAt(newAgentRow, newAgentCol)) {
                        int newBoxRow = newAgentRow + Command.dirToRowChange(c.dir2);
                        int newBoxCol = newAgentCol + Command.dirToColChange(c.dir2);
                        // .. and that new cell of box is free
                        if (this.cellIsFree(newBoxRow, newBoxCol)) {
                            State n = this.ChildState();
                            n.action = c;
                            n.agent.get(0).row = newAgentRow;
                            n.agent.get(0).col = newAgentCol;
                            n.boxes[newBoxRow][newBoxCol] = this.boxes[newAgentRow][newAgentCol];
                            n.boxes[newAgentRow][newAgentCol] = null;
                            expandedStates.add(n);
                        }
                    }
                } else if (c.actionType == Command.Type.Pull) {
                    // Cell is free where agent is going
                    if (this.cellIsFree(newAgentRow, newAgentCol)) {
                        int boxRow = this.agent.get(0).row + Command.dirToRowChange(c.dir2);
                        int boxCol = this.agent.get(0).col + Command.dirToColChange(c.dir2);
                        // .. and there's a box in "dir2" of the agent
                        if (this.boxAt(boxRow, boxCol)) {
                            State n = this.ChildState();
                            n.action = c;
                            n.agent.get(0).row = newAgentRow;
                            n.agent.get(0).col = newAgentCol;
                            n.boxes[this.agent.get(0).row][this.agent.get(0).col] = this.boxes[boxRow][boxCol];
                            n.boxes[boxRow][boxCol] = null;
                            expandedStates.add(n);
                        }
                    }
                }
        }
        Collections.shuffle(expandedStates, RNG);
        return expandedStates;
    }


    //TODO check også for agent og box på samme felt
    public State combineTwoStates(State s1, State s2) {
        System.err.println(s1);
        System.err.println("S2: ");
        System.err.println(s2);
        State newState = ChildState();

        for (int i = 0; i < s1.agent.size(); i++) {
            for (int j = 0; j < s2.agent.size(); j++) {     
                if(s1.agent.get(i).col == s2.agent.get(j).col && s1.agent.get(i).row == s2.agent.get(j).row){
                    if(s1.agent.get(i).name != s2.agent.get(j).name){
                        return null;
                    }
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

            for (int i = 0; i < s1.boxes.length; i++) {
                for (int j = 0; j < s1.boxes[i].length; j++) {
                    if (s1.boxes[i][j] != null && s2.boxes[i][j] != null && !s1.boxes[i][j].equals(s2.boxes[i][j])) {
                        return null;
                    }
                    // if (newState.boxes[i][j] == null) {
                    //     newState.boxes[i][j] = s1.boxes[i][j];

                    //     System.err.println((s1.action));
                    //     System.err.println(j+(-1*Command.dirToColChange(s1.action.dir2)));
                    //     newState.boxes[i+(-1*Command.dirToRowChange(s1.action.dir2))]
                    //                   [j+(-1*Command.dirToColChange(s1.action.dir2))]= null;                 
                    // }
                 
                    if (newState.boxes[i][j] == null && s2.boxes[i][j] != null) {
                        newState.boxes[i][j] = s2.boxes[i][j];
                        newState.boxes[i+(-1*Command.dirToRowChange(s2.action.dir2))]
                                      [j+(-1*Command.dirToColChange(s2.action.dir2))]= null; 
                    }
                }
            }
        }
        System.err.println(newState);
        return newState;
    }

    public class Box {
        public String color;
        public char name;

        public Box(String color, char name) {
            this.color = color;
            this.name = name;
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Domain: " + this.domain + "\n");
        s.append("LevelName: " + this.levelName + "\n");



        for (int row = 0; row < MAX_ROW; row++) {
            if (!this.walls[row][0]) {
                break;
            }
            for (int col = 0; col < MAX_COL; col++) {
                if (this.boxes[row][col] != null) {
                    s.append(this.boxes[row][col].name);
                } else if (this.goals[row][col] > 0) {
                    s.append(Character.toLowerCase(this.goals[row][col]));
                } else if (this.walls[row][col]) {
                    s.append("+");
                } else if (agent.get(0).row == row && agent.get(0).col == col) {
                    s.append(agent.get(0).name);
                } else {
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
            result = prime * result + Arrays.deepHashCode(this.boxes);
            result = prime * result + Arrays.deepHashCode(this.goals);
            result = prime * result + Arrays.deepHashCode(this.walls);
            result = prime * result + this.box.hashCode();
            this._hash = result;
        }
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
        if (!Arrays.deepEquals(this.boxes, other.boxes))
            return false;
        if (!Arrays.deepEquals(goals, this.goals))
            return false;
        return Arrays.deepEquals(walls, State.walls);
    }


    public void cleanLevel() {

        int rows = MAX_ROW;
        int cols = MAX_COL;

        boolean[][] newWalls = new boolean[rows][cols];
        Box[][] newBoxes = new Box[rows][cols];
        char[][] newGoals = new char[rows][cols];

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
                newWalls[row][col] = this.walls[row][col];
                newBoxes[row][col] = this.boxes[row][col];
                newGoals[row][col] = this.goals[row][col];
            }
        }

        this.walls = newWalls;
        this.boxes = newBoxes;
        this.goals = newGoals;
    }

    public State findInitial(Agent iniAgent){
        State ini = new State();

        int rows = MAX_ROW;
        int cols = MAX_COL;

        Box[][] newBoxes = new Box[rows][cols];
        char[][] newGoals = new char[rows][cols];

        ini.agent = new ArrayList<>();
        ini.agent.add(iniAgent);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if(this.goals[row][col] > 0){
                    int c = row;
                    int d = col;
                    Box tempBox = box.stream().filter(b->b.name == this.goals[c][d]).findFirst().orElse(null);
                    if(tempBox.color.equals(iniAgent.color)){
                        ini.goals[row][col] = this.goals[row][col];
                    }                    
                }
                if(this.boxes[row][col] != null){
                    if(this.boxes[row][col].color.equals(iniAgent.color)){
                        ini.boxes[row][col] = this.boxes[row][col];
                    }
                }
                
            }
        }
        return ini;
    }
}