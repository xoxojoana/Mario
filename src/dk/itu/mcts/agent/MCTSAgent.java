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
    public static int responseTime = 25;
    private MCTSSimulator sim;

    public MCTSAgent() {
        super("MCTSAgent");
        reset();
    }

    public boolean[] getAction() {
        long startTime = System.currentTimeMillis();
        if(this.environment == null){
            return null;
        }
        sim.updateInternalWorld(this.environment);
        Node root = new Node(this.environment);
        root = iniPossMove(root);
        int t = root.getValidMoves().size();
        while(System.currentTimeMillis() - startTime < responseTime && t > 0){
            t--;
            Node v1 = TreePolicy(root);
            float reward = DefaultPolicy(v1);
            Backpropagate(v1, reward);
        }
        Node bestChild = bestChild(root);
       // L    R    D    J    S   U
        return bestChild==null?tempAction():bestChild.getParentAction();
    }
    
    /**
     * Temporary method, in case there is null value in best child
     * @return 
     */
    public boolean[] tempAction(){
        action[Mario.KEY_SPEED] = action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
        return action;
    }

    /**
     * Tree policy, check if current node is expandable
     * if true, expand, else return the best child
     * @param v
     * @return 
     */
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

    /**
     * For checking if node is fully expanded
     * @param v
     * @return 
     */
    public boolean isFullyExpanded(Node v) {
        if (v.getChildren().size() < v.getValidMoves().size()) {
            return false;
        }
        return false;
    }

    /**
     * Initialise the valid moves for current node
     * @param v
     * @return 
     */
    private Node iniPossMove(Node v) {
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
        ArrayList<boolean[]> possibleActions = new ArrayList<boolean[]>();
        boolean jump = canJumpHigher(v, true);
        
        //check if mario is able to right, jump and shoot(speed)
        if(!v.environment.isMarioAbleToJump() && !v.environment.isMarioOnGround())possibleActions.add(createAction(false, true, false, true, true, false));
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
        
        v.setValidMoves(possibleActions);
        return v;
    }
    
    /**
     * For creating a boolean array action
     * @param left
     * @param right
     * @param down
     * @param jump
     * @param speed
     * @param up
     * @return 
     */
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
    
      /**
       * check if Mario is able to jump
       * @param v
       * @param checkParent
       * @return 
       */
    private boolean canJumpHigher(Node v, boolean checkParent){
        if(v.getParent() != null && checkParent && canJumpHigher(v.getParent(), false)) return true;
        return v.environment.isMarioAbleToJump() || sim.currentWorld.mario.onGround;
    }

    /**
     * Expand current node with next valid move
     * @param v
     * @return 
     */
    public Node expand(Node v) {
        int index = v.getChildren().size();
        boolean[] nextValidMove = v.getValidMoves().get(index);
        Environment e = v.environment;
        //input next action, and boolean value indicates if this is the playout
        sim.advanceStep(nextValidMove, false);
        float[] pos = new float[2];
        pos[0] =  sim.simulatedWorld.mario.x;
        pos[1] =  sim.simulatedWorld.mario.y;
        Node child = new Node(e,pos, sim.simulatedWorld.mario.status);
        
        child.setParent(v);
        child.setParentAction(nextValidMove);
        v.addChild(child);
        return child;
    }

    /**
     * calculate the best child according to the reward and value C
     * @param v
     * @return 
     */
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

    /**
     * DefaultPolicy for random playout in order to find the best child 
     * @param v
     * @return 
     */
    public float DefaultPolicy(Node v) {
        if(v == null) return -100;
        Environment copy = v.environment;
        int count = 3;
        while (!copy.isLevelFinished() && count > 0 && sim.simulatedWorld.mario.status!=Mario.STATUS_DEAD && sim.simulatedWorld.mario.status != Mario.STATUS_WIN) {
            count--;          
            sim.advanceStep(null, true); //simulator need to be continuesly used
        }
       // float reward = evaluate(v.environment, copy);
        float reward = evaluateByLevelScene(v);
        return reward;
    }
    /**
     *  The parameters which could be used to evaluate are:
     * v.environment.getEvaluationInfoAsInts
     * 0: passed cells
     * 1: passed phys
     * 6: kills total
     * 8: collisions with creatures
     * 10: coin
     * 11: time left
     * 12: time spent
     * 
     * 
     * @param v
     * @return 
     */
    private float evaluateByLevelScene(Node v){
        //only evaluate by the x and y coordinator for now
        float xDiff =sim.simulatedWorld.mario.x - v.getMarioPos()[0];
        float yDiff = sim.simulatedWorld.mario.y - v.getMarioPos()[1];
//        System.out.println("Real  status: "+ v.environment.getMarioStatus() + "\tfire:" + v.environment.getMarioMode());
//        System.out.println("Curre status " + sim.currentWorld.mario.status + "\tfire:" + sim.currentWorld.mario.fire+"\tlarge:" +sim.currentWorld.mario.large);
//        System.out.println("Mario status " + sim.simulatedWorld.mario.status + "\tfire:" + sim.simulatedWorld.mario.fire+"\tlarge:" +sim.simulatedWorld.mario.large);
        if(sim.simulatedWorld.mario.status == Mario.STATUS_DEAD){
            return -100;
        }
        if(sim.currentWorld.mario.fire && !sim.simulatedWorld.mario.fire){
            return -100;
        }
        if(sim.currentWorld.mario.large && ! sim.simulatedWorld.mario.large){
            return -100;
        }
        int coinGained = 0;
        coinGained = sim.simulatedWorld.coinsCollected;
        boolean extra = v.getParentAction()[1] &&  v.getParentAction()[3] &&  v.getParentAction()[4];
        System.out.println("Coin: " + coinGained);
        //System.out.println("Coin " + coinGained +"\tSimWorld: " + sim.simulatedWorld.getCoinsCollected()+"\tRealWorld: "+ v.environment.getEvaluationInfoAsInts()[10]);
//        int c = 0;
//        System.out.println("******");
//        for(int i: v.environment.getEvaluationInfoAsInts()){
//            System.out.println(c + ": " +i);
//            c++;
//        }
//        System.out.println("******");
        //add extra reward for specific move right, speed, jump
        int e = extra?10:0;
        return xDiff + Math.abs(yDiff) + e + coinGained;
    }
    
    /**
     * since the original game doesn't support advance game mode, this method current is useless
     * @param e1
     * @param e2
     * @return 
     */
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

    /**
     * Backpropagate the reward to each node
     * @param v
     * @param reward 
     */
    public void Backpropagate(Node v, float reward) {
        while (v != null) {
            v.setTimesvisited(v.getTimesvisited() + 1);
            v.setReward(reward);
            v = v.getParent();
        }
    }


    @Override
    public void reset() {
        action = new boolean[Environment.numberOfKeys];
        sim = new MCTSSimulator();
    }
}
