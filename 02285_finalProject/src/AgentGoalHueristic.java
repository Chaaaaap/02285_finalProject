public class AgentGoalHueristic extends Heuristic {
    
    public int manhattanDistance(Pair a, Pair b){
        return Math.abs(a.row - b.row) + Math.abs(a.col - b.col);
    }

    public int getHome(Pair agentCoordinate, Pair goalCoordinate) {
        return manhattanDistance(agentCoordinate, goalCoordinate);
    }

    public int h(State state) {
        Agent agent = state.agent.get(0);
        Pair coordinates = new Pair(agent.row, agent.col);
        return getHome(coordinates, agent.goal);
    }
}