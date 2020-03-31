package app;

import java.util.ArrayList;

//Agent position
//Goal static
//Wall static
//Boxes position
//Color 
// H og G => F

public class State {

    public static String levelName;
    public static String domain;
    
    public static int MAX_ROW = 70;
    public static int MAX_COL = 70;
    
    public boolean[][] walls = new boolean[MAX_ROW][MAX_COL];
    public Box[][] boxes = new Box[MAX_ROW][MAX_COL];
    public char[][] goals = new char[MAX_ROW][MAX_COL];
    public ArrayList<Box> box = new ArrayList<>();

    public Agent[][] agents = new Agent[MAX_ROW][MAX_COL];
    public ArrayList<Agent> agent = new ArrayList<>();

    public void addBox(String color, char chr){
        box.add(new Box(color, chr));
    }

    public void addAgent(String color, char chr){
        agent.add(new Agent(color, chr));
    }

    public void updateAgent(char name, int row, int col){
        for (int i = 0; i < agent.size(); i++) {
            if(agent.get(i).name == name){
                agents[row][col] = agent.get(i);
                agent.remove(i);
            }
        }
    }

    public void updateBox(char name, int row, int col){
        for (int i = 0; i < box.size(); i++) {
            if(box.get(i).name == name){
                boxes[row][col] = box.get(i);
                box.remove(i);
            }
        }
    }

    public class Box{
        public String color;
        public char name;

        public Box(String color, char name){
            this.color = color;
            this.name = name;
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int row = 0; row < MAX_ROW; row++) {
            if (!this.walls[row][0]) {
                break;
            }
            for (int col = 0; col < MAX_COL; col++) {
                if (this.boxes[row][col] != null) {
                    s.append(this.boxes[row][col].name);
                } else if (this.goals[row][col] > 0) {
                    s.append(this.goals[row][col]);
                } else if (this.walls[row][col]) {
                    s.append("+");  
                              
                }else if (this.agents[row][col] != null){
                    s.append(this.agents[row][col].name);
                } 
                else {
                    s.append(" ");
                }
            }
            s.append("\n");
        }
        return s.toString();
    }

    public class Agent{
        public String color;
        public char name;
        public int row;
        public int col;

        public Agent(String color, char name){
            this.color = color;
            this.name = name;
        }
    }
}