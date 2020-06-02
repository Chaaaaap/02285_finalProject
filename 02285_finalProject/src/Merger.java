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
                    if(tmp != null){
                        indices[i]++;
                        actions.add(states.get(i).action);
                        tempState = tmp;
                    }
                    else{
                        actions.add(new Command());
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
        //System.err.println(preState);
        //System.err.println("Actions: " + listString);
        return actions;
    }

    public State SuperMerger(ArrayList<ArrayList<State>> states){
        indices = new int[states.size()];
        while(true){
            ArrayList<State> oneStepStates = new ArrayList<>();
            for (int i = 0; i < states.size(); i++) {
                if(states.get(i) == null){
                    oneStepStates.add(null);
                }
                else if(indices[i] < states.get(i).size()){
                    oneStepStates.add(states.get(i).get(indices[i]));
                }
                else{
                    oneStepStates.add(null);
                }
            }
            ArrayList<Command> temp = merge(preState, oneStepStates);
            
            if(preState.isBoxGoalState()){
                return preState;
            }

            if(temp.stream().filter(c->c.actionType == Command.Type.NoOp).count() == temp.size()){   
                System.err.println("THERE IS A CONFLICT");             
                for (int i = 0; i < states.size(); i++) {
                    if(states.get(i) == null){
                        continue;
                    }
                    if(indices[i] < states.get(i).size()){
                        State nextStep = null;
                        if ((indices[i] + 1) >= states.get(i).size()) {
                            nextStep = states.get(i).get(indices[i]);
                        } else {
                            nextStep = states.get(i).get(indices[i] + 1);
                        }
                        State tempState = new Communicator().pleaseMove(preState, states.get(i).get(indices[i]), nextStep);
                        //System.err.println("Fixed state: " + tempState);
                        if(tempState != null){
                            preState = tempState;
                            return tempState;
                        }                     
                    }
                    
                }
                // return preState;
                return null;
            }
        }
        //Merge hele planer for alle agenter
        //Lav liste af states ud fra indices
        //Kald merge
    }
}