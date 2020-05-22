import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Collections;

public abstract class Strategy {
    private HashSet<State> explored;
    private final long startTime;

    public Strategy() {
        this.explored = new HashSet<>();
        this.startTime = System.currentTimeMillis();
    }

    public void addToExplored(State n) {
        this.explored.add(n);
    }

    public boolean isExplored(State n) {
        return this.explored.contains(n);
    }

    public int countExplored() {
        return this.explored.size();
    }

    public String searchStatus() {
        return String.format("#Explored: %,6d, #Frontier: %,6d, #Generated: %,6d, Time: %3.2f s \t%s",
                this.countExplored(), this.countFrontier(), this.countExplored() + this.countFrontier(),
                this.timeSpent(), Memory.stringRep());
    }

    public float timeSpent() {
        return (System.currentTimeMillis() - this.startTime) / 1000f;
    }

    public abstract State getAndRemoveLeaf();

    public abstract void addToFrontier(State n);

    public abstract boolean inFrontier(State n);

    public abstract int countFrontier();

    public abstract boolean frontierIsEmpty();

    @Override
    public abstract String toString();

    // BFS
    public static class StrategyBFS extends Strategy {
        private ArrayDeque<State> frontier;
        private HashSet<State> frontierSet;

        public StrategyBFS() {
            super();
            frontier = new ArrayDeque<>();
            frontierSet = new HashSet<>();
        }

        @Override
        public State getAndRemoveLeaf() {
            State n = frontier.pollFirst();
            
            //frontierSet.remove(n);
            return n;
        }

        @Override
        public void addToFrontier(State n) {
            frontier.addLast(n);
            frontierSet.add(n);
        }

        @Override
        public int countFrontier() {
            return frontier.size();
        }

        @Override
        public boolean frontierIsEmpty() {
            return frontier.isEmpty();
        }

        @Override
        public boolean inFrontier(State n) {
            return frontierSet.contains(n);
        }

        @Override
        public String toString() {
            return "Breadth-first Search";
        }
    }

     // BFS
     public static class StrategySimpleBFS extends Strategy {
        private ArrayDeque<State> frontier;
        public HashSet<State> frontierSet;
        Command[] moves = new Command[]{new Command(Command.Dir.N), new Command(Command.Dir.W), new Command(Command.Dir.S), new Command(Command.Dir.E)};

        public StrategySimpleBFS() {
            super();
            frontier = new ArrayDeque<>();
            frontierSet = new HashSet<>();
        }

        @Override
        public State getAndRemoveLeaf() {
            State n = frontier.pollFirst();
            //frontierSet.remove(n);

            for(Command c : moves){
                int newAgentRow = n.agent.get(0).row + Command.dirToRowChange(c.dir1);
                int newAgentCol = n.agent.get(0).col + Command.dirToColChange(c.dir1);

                State t = n.ChildState();               

                if (!State.walls[newAgentRow][newAgentCol]) {
                    t.agent.get(0).row = newAgentRow;
                    t.agent.get(0).col = newAgentCol;
                    if(!inFrontier(t)){
                        frontier.addLast(t);
                        frontierSet.add(t);
                    }
                }
            }   
            return n;
        }

        @Override
        public void addToFrontier(State n) {
            frontier.addLast(n);
            frontierSet.add(n);
        }

        @Override
        public int countFrontier() {
            return frontier.size();
        }

        @Override
        public boolean frontierIsEmpty() {
            return frontier.isEmpty();
        }

        @Override
        public boolean inFrontier(State n) {
            return frontierSet.contains(n);
        }

        @Override
        public String toString() {
            return "Breadth-first Search";
        }
    }

    // DFS
    public static class StrategyDFS extends Strategy {
        private Stack<State> frontier;
        private HashSet<State> frontierSet;

        public StrategyDFS() {
            super();
            frontier = new Stack<>();
            frontierSet = new HashSet<>();
        }

        @Override
        public State getAndRemoveLeaf() {
            State n = frontier.pop();
            frontierSet.remove(n);
            return n;
        }

        @Override
        public void addToFrontier(State n) {
            frontier.push(n);
            frontierSet.add(n);
        }

        @Override
        public int countFrontier() {
            return frontier.size();
        }

        @Override
        public boolean frontierIsEmpty() {
            return frontier.isEmpty();
        }

        @Override
        public boolean inFrontier(State n) {
            return frontierSet.contains(n);
        }

        @Override
        public String toString() {
            return "Depth-first Search";
        }
    }

    // Greedy
    public static class StrategyGREEDY extends Strategy {
        private ArrayList<State> frontier;
        private HashSet<State> frontierSet;
        Heuristic heuristic;

        public StrategyGREEDY(Heuristic h) {
            super();
            frontier = new ArrayList<>();
            frontierSet = new HashSet<>();
            heuristic = h;
        }

        @Override
        public State getAndRemoveLeaf() {
            State n = frontier.get(0);
            frontier.remove(0);

            return n;
        }

        @Override
        public void addToFrontier(State n) {

            int h = heuristic.h(n);
            int pos = 0;

            n.h = h;

            for (int i = 0; i < frontier.size(); i++) {
                if (frontier.get(i).h >= h) {
                    pos = i;
                    break;
                }
            }

            frontier.add(pos, n);
            frontierSet.add(n);
        }

        @Override
        public int countFrontier() {
            return frontier.size();
        }

        @Override
        public boolean frontierIsEmpty() {
            return frontier.isEmpty();
        }

        @Override
        public boolean inFrontier(State n) {
            return frontierSet.contains(n);
        }

        @Override
        public String toString() {
            return "Greedy Search";
        }
    }
}