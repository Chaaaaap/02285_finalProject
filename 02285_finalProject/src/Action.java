
public abstract class Action {
    public enum Dir {
        N(-1, 0), 
        W(0, -1), 
        E(0, 1), 
        S(1, 0);

        int row;
        int col;

        Dir(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRowDiff(){
            return row;
        }
        
        public int getColDiff(){
            return col;
        }
    }
}

class MoveAction extends Action{
    Dir agentDir;

    public MoveAction(Dir agentDir){
        this.agentDir = agentDir;
    }

    @Override
    public String toString() {
        return String.format("Move(%s)", agentDir);
    }
} 

class PushAction extends Action{
    Dir agentDir;
    Dir boxDir;

    public PushAction(Dir agentDir, Dir boxDir){
        this.agentDir = agentDir;
        this.boxDir = boxDir;
    }

    @Override
    public String toString() {
        return String.format("Push(%s,%s)", agentDir, boxDir);
    }
}

class PullAction extends Action{
    Dir agentDir;
    Dir boxDir;

    public PullAction(Dir agentDir, Dir boxDir){
        this.agentDir = agentDir;
        this.boxDir = boxDir;
    }

    @Override
    public String toString() {
        return String.format("Pull(%s,%s)", agentDir, boxDir);
    }
}

class NoOp extends Action{

    @Override
    public String toString() {
        return "NoOp";
    }
}