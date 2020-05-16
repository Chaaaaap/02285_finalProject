import java.util.ArrayList;
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
                State tmp = tempState.combineTwoStates(preState, states.get(i));                      
                if(tmp == null){
                    actions.add(new Command());
                }
                else{
                    tmp = tempState.combineTwoStates(tempState, states.get(i));   
                    if(tmp == null){
                        actions.add(new Command());
                    }
                    else{
                        tempState = tmp;
                        indices[i]++;
                        actions.add(states.get(i).action);
                    }
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

    public State SuperMerger(ArrayList<ArrayList<State>> states){
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
            ArrayList<Command> temp = merge(preState, oneStepStates);
            
            if(preState.isGoalState()){
                return preState;
            }

            if(temp.stream().filter(c->c.actionType == Command.Type.NoOp).count() == temp.size()){
                
                for (int i = 0; i < states.size(); i++) {
                    if(indices[i] < states.get(i).size()){
                        preState = new Communicator().pleaseMove(preState, states.get(i).get(indices[i]));                        
                    }
                    
                }
                return preState;
            }
        }
        //Merge hele planer for alle agenter
        //Lav liste af states ud fra indices
        //Kald merge
    }
}