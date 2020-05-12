import java.util.ArrayList;

public class Merger {

    int[] indices;
    State preState;

    public Merger(State preState){
        this.preState = preState;
    }

    public ArrayList<Action> merge(State preState, ArrayList<State> states) {
        State tempState;
        tempState = preState;
        ArrayList<Action> actions = new ArrayList<>();
        //Lav command om til action
        //Add action for states(1) to action list
        for (int i = 0; i < states.size(); i++) {
            State tmp = states.get(i).combineTwoStates(tempState, states.get(i));
            if(tmp == null){
                actions.add(new NoOp());
            }
            else{
                tempState = tmp;
                indices[i]++;
                //Add command to action here for state.get(i)
            }
        }
        //Hold styr på index for hver solution -> 
        preState = tempState;
        return actions;
    }

    public void SuperMerger(ArrayList<ArrayList<State>> states){
        indices = new int[states.size()];
        //Merge hele planer for alle agenter
        //Lav liste af states ud fra indices
        //Kald merge
    }
}