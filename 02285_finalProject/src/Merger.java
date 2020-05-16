import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Merger {

    int[] indices;
    State preState;

    public Merger(State preState){
        this.preState = preState;
    }

    public ArrayList<Command> merge(State preState, ArrayList<State> states) {
        State tempState;
        tempState = preState;
        ArrayList<Command> actions = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            if(states.get(i) != null){
                State tmp = tempState.combineTwoStates(tempState, states.get(i));       
                if(tmp == null){
                    actions.add(new Command());
                }
                else{
                    tempState = tmp;
                    indices[i]++;
                    actions.add(states.get(i).action);
                }
            }
            else{
                actions.add(new Command());
            }
        }
        this.preState = tempState;
        String listString = actions.stream().map(Object::toString)
                        .collect(Collectors.joining(";"));
        System.out.println(listString);

        return actions;
    }

    public void SuperMerger(ArrayList<ArrayList<State>> states){
        System.err.println(preState);
        indices = new int[states.size()];
        while(true){
            ArrayList<State> oneStepStates = new ArrayList<>();
            for (int i = 0; i < states.size(); i++) {
                if(indices[i] < states.get(i).size()){
                    oneStepStates.add(states.get(i).get(indices[i]));
                }
                else{
                    oneStepStates.add(null);
                }
            }
            preState.commands = merge(preState, oneStepStates);
            System.err.println(preState);
            if(preState.isGoalState()){
                return;
            }
        }
        //Merge hele planer for alle agenter
        //Lav liste af states ud fra indices
        //Kald merge
    }
}