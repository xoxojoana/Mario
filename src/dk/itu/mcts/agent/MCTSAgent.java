/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.mcts.agent;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.agents.controllers.ForwardJumpingAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.tools.EvaluationInfo;
import java.util.ArrayList;

/**
 *
 * @author Jinhong
 */
public class MCTSAgent extends BasicMarioAIAgent implements Agent {

    public float C = (float) (0.5 / Math.sqrt(2));
    public static int responseTime = 10;
    private MCTSSimulator sim;

    public MCTSAgent() {
        super("MCTSAgent");
        reset();
    }

    public boolean[] getAction() {
        //this is a test: Hello there
//        while (System.currentTimeMillis() < 40 - responseTime) {
//            Node v1 = TreePolicy(current mario node);
//            double reward = DefaultPolicy(v1);
//            Backpropagate(v1, reward);
//        }
//        Node bestChild = BestChild(rootNode);
        long startTime = System.currentTimeMillis();
        String s = "Fire";
    	if (!sim.currentWorld.mario.fire)
    		s = "Large";
    	if (!sim.currentWorld.mario.large)
    		s = "Small";
        boolean[] ac = new boolean[5];
    	ac[Mario.KEY_RIGHT] = true;
    	ac[Mario.KEY_SPEED] = true;
        Node root = new Node(this.environment);
        int t = 5;
        while (t>1) {
            t--;
            System.out.println("1");
            Node v1 = TreePolicy(root);
            float reward = DefaultPolicy(v1);
            Backpropagate(v1, reward);
            System.out.println("2");
        }
        Node bestChild = bestChild(root);
        System.out.println(bestChild==null?"null":bestChild.toString());
        return bestChild==null?tempAction():bestChild.getParentAction();
       /* boolean[][] validMoves = {
         //L    R       D       J       S   U   
        {false, true, false, false, false, false},
        {false, true, false, true, false, false},
        {false, true, false, true, true, false},
        {true, false, false, false, false, false},
        {true, false, false, true, false, false},
        {true, false, false, true, true, false},
        {false, false, true, false, false, false},
        {false, true, true, false, false, false},
        {false, true, true, false, true, false},
        {false, true, false, true, true, false}};
//        System.out.println("called");
//        if(isMarioAbleToJump){
//            return validMoves[9];
//        }
        return validMoves[0];
//        return tempAction();*/
    }
    
    public boolean[] tempAction(){
        action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
        System.out.println(action[0] + " " + action[1] +  " " + action[2] + " " + action[3] + " " + action[4] + " " + action[5]);
        return action;
    }

    //tree policy
    public Node TreePolicy(Node v) {
        while (!v.gameOver()) {
            if (!isFullyExpanded(v)) {
                return expand(v);
            } else {
                v = bestChild(v);
            }
        }
        return null;
    }

    //check if node is fully expanded
    public boolean isFullyExpanded(Node v) {
        /* possible moves        L R D J S U     0 false, 1 true
         *   right               0 1 0 0 0 0
         *   right jump          0 1 0 1 0 0
         *   right jump speed    0 1 0 1 1 0 
         *   left                1 0 0 0 0 0
         *   left jump           1 0 0 1 0 0
         *   left jump speed     1 0 0 1 1 0
         *   down                0 0 1 0 0 0
         *   down right          0 1 1 0 0 0
         *   down right speed    0 1 1 0 1 0
         */
        // no child yet
        if (v.getChildren().size() < 1) {
            v = iniPossMove(v);
            return false;
        }
        // still untried move left
        if (v.getChildren().size() <= v.getValidMoves().size()) {
            return false;
        }
        return false;
    }

    //temp method for initialize the possible moves
    private Node iniPossMove(Node v) {
        /*boolean[][] validMoves = {{false, true, false, false, false, false},
        {false, true, false, true, false, false},
        {false, true, false, true, true, false},
        {true, false, false, false, false, false},
        {true, false, false, true, false, false},
        {true, false, false, true, true, false},
        {false, false, true, false, false, false},
        {false, true, true, false, false, false},
        {false, true, true, false, true, false}};

        v.setValidMoves(validMoves);*/
        ArrayList<boolean[]> possibleActions = new ArrayList<boolean[]>();

    	// jump
    	if (canJumpHigher(v, true)) possibleActions.add(createAction(false, false, false, true, false));
    	if (canJumpHigher(v, true)) possibleActions.add(createAction(false, false, false, true, true));
    	
    	// run right
    	possibleActions.add(createAction(false, true, false, false, true));
    	if (canJumpHigher(v, true))  possibleActions.add(createAction(false, true, false, true, true));
    	possibleActions.add(createAction(false, true, false, false, false));
    	if (canJumpHigher(v, true))  possibleActions.add(createAction(false, true, false, true, false));
 	
    	// run left
    	possibleActions.add(createAction(true, false, false, false, false));
    	if (canJumpHigher(v, true))  possibleActions.add(createAction(true, false, false, true, false));
    	possibleActions.add(createAction(true, false, false, false, true));
    	if (canJumpHigher(v, true))  possibleActions.add(createAction(true, false, false, true, true));
    	
        v.setValidMoves(possibleActions);
        return v;
    }
    
      private boolean[] createAction(boolean left, boolean right, boolean down, boolean jump, boolean speed)
    {
    	boolean[] action = new boolean[5];
    	action[Mario.KEY_DOWN] = down;
    	action[Mario.KEY_JUMP] = jump;
    	action[Mario.KEY_LEFT] = left;
    	action[Mario.KEY_RIGHT] = right;
    	action[Mario.KEY_SPEED] = speed;
    	return action;
    }
    
    private boolean canJumpHigher(Node v, boolean checkParent){
        if(v.getParent() != null && checkParent && canJumpHigher(v.getParent(), false)) return true;
        return v.environment.isMarioAbleToJump();
    }

    //expand the node
    public Node expand(Node v) {
        //if(v.getValidMoves().size() >= v.getChildren().size() -1)
        int index = v.getChildren().size()>1?v.getChildren().size()-1:v.getChildren().size();
        boolean[] nextValidMove = v.getValidMoves().get(index);
        Environment e = v.environment;
        e.performAction(nextValidMove);  
        e.tick();
        Node child = new Node(e);
        
        child.setParent(v);
        child.setParentAction(nextValidMove);
        v.addChild(child);
        return child;
    }

    //calculate the best child
    public Node bestChild(Node v) {
        Node bestChild = null;
        double max = Double.NEGATIVE_INFINITY;
        for (Node child : v.getChildren()) {
            double current = child.getReward() / child.getTimesvisited() + C * Math.sqrt(2 * Math.log10(v.getTimesvisited())) / child.getTimesvisited();
            if (max < current) {
                max = current;
                bestChild = child;
            }
        }
        return bestChild;
    }

    //play out
    public float DefaultPolicy(Node v) {
        Environment copy = v.environment;
        int count = 2;
        Agent agent = new ForwardJumpingAgent();//ForwardJumpingAgent();
        while (!copy.isLevelFinished() && count > 0 && copy.getMarioStatus()!=Mario.STATUS_DEAD && copy.getMarioStatus() != Mario.STATUS_WIN) {
            count--;
            agent.integrateObservation(copy);
            copy.performAction(agent.getAction());
            copy.tick();            
        }
        float reward = evaluate(v.environment, copy);
        return reward;
    }
    
    private float evaluate(Environment e1, Environment e2){
        int coinDiff = 0, killDiff = 0;
        float marioPosDiff = 0 , totalScore = 0;
        EvaluationInfo evaluationInfo1 = e1.getEvaluationInfo().clone(), evaluationInfo2 = e2.getEvaluationInfo().clone();
        if(evaluationInfo2.marioStatus == Mario.STATUS_DEAD){ return -1;}
        if(evaluationInfo2.marioStatus == Mario.STATUS_WIN) { return 1000;}
        if(evaluationInfo2.marioStatus == Mario.STATUS_RUNNING){
            coinDiff = evaluationInfo1.coinsGained - evaluationInfo2.coinsGained;
            killDiff = evaluationInfo1.killsTotal - evaluationInfo2.killsTotal;
            marioPosDiff = e1.getMarioFloatPos()[0] - e2.getMarioFloatPos()[0];
        }        
        totalScore = coinDiff + killDiff + marioPosDiff;
        return totalScore;
    }

    //Backpropagate the reward to each node
    public void Backpropagate(Node v, float reward) {
        while (v != null) {
            v.setTimesvisited(v.getTimesvisited() + 1);
            v.setReward(reward);
            v = v.getParent();
        }
    }

    public void reset() {
        action = new boolean[Environment.numberOfKeys];
        sim = new MCTSSimulator();
//        action[Mario.KEY_RIGHT] = true;
//        action[Mario.KEY_SPEED] = true;
    }
}
