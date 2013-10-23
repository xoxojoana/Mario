/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.mcts.agent;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

/**
 *
 * @author Jinhong
 */
public class MCTSAgent extends BasicMarioAIAgent implements Agent {
    public float C = (float) (0.5 / Math.sqrt(2));
    public static int responseTime = 10;

    public MCTSAgent() {
        super("MCTSAgent");
        reset();
    }

    public boolean[] getAction() {
        //this is a test: Hello there
//        while (System.currentTimeMillis() < timeDue - responseTime) {
//            Node v1 = TreePolicy(current mario node);
//            double reward = DefaultPolicy(v1);
//            Backpropagate(v1, reward);
//        }
//        Node bestChild = BestChild(rootNode);
        action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
        return action;
    }
    
    //tree policy
    public Node TreePolicy(Node v){
        return null;        
    }
    
    //check if node is fully expanded
    public boolean isFullyExpanded(Node v){
        return false;
    }

    //expand the node
    public Node expend(Node v){
        return null;
    }
    
    //calculate the best child
    public Node bestChild(Node v){
        return null;
    }
    
    //play out
    public double DefaultPolicy(Node v){
        return 0;
    }
    
    //Backpropagate the reward to each node
    public void Backpropagate(Node v){
        
    }
    
    public void reset() {
        action = new boolean[Environment.numberOfKeys];
        action[Mario.KEY_RIGHT] = true;
        action[Mario.KEY_SPEED] = true;
    }
}
