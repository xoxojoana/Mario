/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dk.itu.mcts.agent;

import ch.idsia.benchmark.mario.environments.Environment;
import competition.cig.robinbaumgarten.astar.LevelScene;
import competition.cig.robinbaumgarten.astar.level.Level;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

/**
 *
 * @author Jinhong
 */
public class MCTSSimulator {
    public LevelScene currentWorld, simulatedWorld;
    
    public MCTSSimulator(){
        initialiseSimulator();
    }
    
    /**
     * Initialise the simulator
     */
    public void initialiseSimulator(){
        currentWorld = new LevelScene();
        currentWorld.init();
        currentWorld.level = new Level(1500,15);
    }
    
    /**
     * update internal world to the newest states
     * @param e 
     */
    public void updateInternalWorld(Environment e){
        byte[][] scene = e.getLevelSceneObservationZ(0);
    	float[] enemies = e.getEnemiesFloatPos();
        float[] realMarioPos = e.getMarioFloatPos();
        currentWorld.mario.fire = e.getMarioMode()==2;
        currentWorld.mario.large = e.getMarioMode()>=1;        
        setLevelPart(scene, enemies, realMarioPos);
    }
    
    /**
     * update internal level scene state
     * @param levelPart
     * @param enemies
     * @param realMarioPos 
     */
    public void setLevelPart(byte[][] levelPart, float[] enemies, float[] realMarioPos){
        currentWorld.setLevelScene(levelPart);
        currentWorld.setEnemies(enemies);
        currentWorld.mario.x = realMarioPos[0];
        currentWorld.mario.y = realMarioPos[1];
    }
    
    /**
     * Simulate a "future" game with input actions. The boolean value required here
     * is for checking whether it needs to create a new simulator. If it's true with playout, it means 
     * the actions are made for the same simulator, therefore it is no need to create a new simulator.
     * If the action value is null, the program will generate a new random valid move.
     * 
     * @param action
     * @param playOut 
     */
    public void advanceStep(boolean[] action, boolean playOut){
        //if it is not in playout(defaultPolicy), the simulatedWorld needs to be recloned from currentWorld.
        if(simulatedWorld == null || !playOut){
            try {
                simulatedWorld = (LevelScene)currentWorld.clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(MCTSSimulator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        if(action == null){
            action = randomAction(simulatedWorld);
        }
        simulatedWorld.mario.setKeys(action);
        simulatedWorld.tick();
    }   
    
    /**
     * For generating a new random but valid move for simulator
     * @param ls
     * @return 
     */
    private boolean[] randomAction(LevelScene ls){
        ArrayList<boolean[]> possibleActions = new ArrayList<boolean[]>();
        boolean jump = canJumpHigher();
        if(!simulatedWorld.mario.onGround && !simulatedWorld.mario.mayJump()) possibleActions.add(MCTSAgent.createAction(false, true, false, true, true, false));
    	// jump
    	if (jump) possibleActions.add(MCTSAgent.createAction(false, false, false, true, false, false));
    	if (jump) possibleActions.add(MCTSAgent.createAction(false, false, false, true, true, false));
        // run right
    	possibleActions.add(MCTSAgent.createAction(false, true, false, false, true, false));
    	if (jump)  possibleActions.add(MCTSAgent.createAction(false, true, false, true, true, false));
    	possibleActions.add(MCTSAgent.createAction(false, true, false, false, false, false));
    	if (jump)  possibleActions.add(MCTSAgent.createAction(false, true, false, true, false, false));
         // run left
    	possibleActions.add(MCTSAgent.createAction(true, false, false, false, false, false));
    	if (jump)  possibleActions.add(MCTSAgent.createAction(true, false, false, true, false, false));
    	possibleActions.add(MCTSAgent.createAction(true, false, false, false, true, false));
    	if (jump)  possibleActions.add(MCTSAgent.createAction(true, false, false, true, true, false));
        Random r = new Random();
        return possibleActions.get(r.nextInt(possibleActions.size()-1));
    }    
     private boolean canJumpHigher(){
        return simulatedWorld.mario.mayJump()||simulatedWorld.mario.onGround ||simulatedWorld.mario.jumpTime>0;
    }
}
