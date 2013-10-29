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
import java.util.Random;

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
        if(this.environment == null){
            return null;
        }
        sim.updateInternalWorld(this.environment);
        Node root = new Node(this.environment);
        root = iniPossMove(root);
        int t = root.getValidMoves().size();
        while (t>0) {
            t--;
            Node v1 = TreePolicy(root);
            float reward = DefaultPolicy(v1);
            Backpropagate(v1, reward);
        }
        Node bestChild = bestChild(root);
        System.out.println(bestChild==null?"null":bestChild.toString());
        return bestChild.getParentAction();//bestChild==null?tempAction():bestChild.getParentAction();
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
       /* if (v.getChildren().size() < 1) {
            v = iniPossMove(v);
            return false;
        }*/
        // still untried move left
        if (v.getChildren().size() < v.getValidMoves().size()) {
            return false;
        }
        return false;
    }

    //temp method for initialize the possible moves
    private Node iniPossMove(Node v) {
        ArrayList<boolean[]> possibleActions = new ArrayList<boolean[]>();
        boolean jump = canJumpHigher(v, true);
//        System.out.print("jump " + jump);
    	// jump
    	if (jump) possibleActions.add(createAction(false, false, false, true, false, false));
    	if (jump) possibleActions.add(createAction(false, false, false, true, true, false));
    	
    	// run right
    	possibleActions.add(createAction(false, true, false, false, true, false));
    	if (jump)  possibleActions.add(createAction(false, true, false, true, true, false));
    	possibleActions.add(createAction(false, true, false, false, false, false));
    	if (jump)  possibleActions.add(createAction(false, true, false, true, false, false));
 	
    	// run left
    	possibleActions.add(createAction(true, false, false, false, false, false));
    	if (jump)  possibleActions.add(createAction(true, false, false, true, false, false));
    	possibleActions.add(createAction(true, false, false, false, true, false));
    	if (jump)  possibleActions.add(createAction(true, false, false, true, true, false));
        
        //test
        if(!v.environment.isMarioAbleToJump() && !v.environment.isMarioOnGround())possibleActions.add(createAction(false, true, false, true, true, false));
    	
        v.setValidMoves(possibleActions);
        return v;
    }
    
      public static boolean[] createAction(boolean left, boolean right, boolean down, boolean jump, boolean speed, boolean up)
    {
    	boolean[] action = new boolean[6];
    	action[Mario.KEY_DOWN] = down;
    	action[Mario.KEY_JUMP] = jump;
    	action[Mario.KEY_LEFT] = left;
    	action[Mario.KEY_RIGHT] = right;
    	action[Mario.KEY_SPEED] = speed;
        action[Mario.KEY_UP] = up;
    	return action;
    }
    
    private boolean canJumpHigher(Node v, boolean checkParent){
        if(v.getParent() != null && checkParent && canJumpHigher(v.getParent(), false)) return true;
        return v.environment.isMarioAbleToJump() || sim.currentWorld.mario.onGround;
//        if(!v.environment.isMarioAbleToJump()){
//            return true;
//        }else{
//            return false;
//        }
        
    }

    //expand the node
    public Node expand(Node v) {
        //if(v.getValidMoves().size() >= v.getChildren().size() -1)
        //int index = new Random().nextInt(v.getValidMoves().size());
        int index = v.getChildren().size();//==v.getValidMoves().size()?v.getChildren().size()-1:v.getChildren().size();
        boolean[] nextValidMove = v.getValidMoves().get(index);
        Environment e = v.environment;
//        e.performAction(nextValidMove);  
//        e.tick();
        sim.advanceStep(nextValidMove, false);
        float[] pos = new float[2];
        pos[0] =  sim.simulatedWorld.mario.x;
        pos[1] =  sim.simulatedWorld.mario.y;
//        System.out.println("newX: " + pos[0] + " newY " + pos[1]);
        Node child = new Node(e,pos, sim.simulatedWorld.mario.status);
        
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
        int count = 3;
        while (!copy.isLevelFinished() && count > 0 && sim.simulatedWorld.mario.status!=Mario.STATUS_DEAD && sim.simulatedWorld.mario.status != Mario.STATUS_WIN) {
            count--;
            /*agent.integrateObservation(copy);
            copy.performAction(agent.getAction());
            copy.tick();*/            
            sim.advanceStep(null, true); //simulator need to be continuesly used
        }
       // float reward = evaluate(v.environment, copy);
        float reward = evaluateByLevelScene(v);
        return reward;
    }
    
    private float evaluateByLevelScene(Node v){
        //only evaluate by the x and y coordinator for now
        float xDiff =sim.simulatedWorld.mario.x - v.getMarioPos()[0];
        float yDiff = sim.simulatedWorld.mario.y - v.getMarioPos()[1];
        if(sim.simulatedWorld.mario.status == Mario.STATUS_DEAD){
            return -100;
        }
        if(sim.currentWorld.mario.fire && !sim.simulatedWorld.mario.fire){
            return -100;
        }
        if(sim.currentWorld.mario.large && ! sim.simulatedWorld.mario.large){
            return -100;
        }
        boolean extra = v.getParentAction()[1] &&  v.getParentAction()[3] &&  v.getParentAction()[4];
        int e = extra?50:0;
        return xDiff + Math.abs(yDiff) + e;
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
