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
    public int timeBudget = 20;
    
    public MCTSSimulator(){
        initialiseSimulator();
    }
    
    public void initialiseSimulator(){
        currentWorld = new LevelScene();
        currentWorld.init();
        currentWorld.level = new Level(1500,15);
    }
    
    public void updateInternalWorld(Environment e){
        byte[][] scene = e.getLevelSceneObservationZ(0);
    	float[] enemies = e.getEnemiesFloatPos();
        float[] realMarioPos = e.getMarioFloatPos();
        setLevelPart(scene, enemies, realMarioPos);
    }
    
    public void setLevelPart(byte[][] levelPart, float[] enemies, float[] realMarioPos){
        currentWorld.setLevelScene(levelPart);
        currentWorld.setEnemies(enemies);
        currentWorld.mario.x = realMarioPos[0];
        currentWorld.mario.y = realMarioPos[1];
    }
    
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
    
    private boolean[] randomAction(LevelScene ls){
        ArrayList<boolean[]> possibleActions = new ArrayList<boolean[]>();

    	// jump
    	if (ls.mario.mayJump()) possibleActions.add(MCTSAgent.createAction(false, false, false, true, false, false));
    	if (ls.mario.mayJump()) possibleActions.add(MCTSAgent.createAction(false, false, false, true, true, false));
        // run right
    	possibleActions.add(MCTSAgent.createAction(false, true, false, false, true, false));
    	if (ls.mario.mayJump())  possibleActions.add(MCTSAgent.createAction(false, true, false, true, true, false));
    	possibleActions.add(MCTSAgent.createAction(false, true, false, false, false, false));
    	if (ls.mario.mayJump())  possibleActions.add(MCTSAgent.createAction(false, true, false, true, false, false));
         // run left
    	possibleActions.add(MCTSAgent.createAction(true, false, false, false, false, false));
    	if (ls.mario.mayJump())  possibleActions.add(MCTSAgent.createAction(true, false, false, true, false, false));
    	possibleActions.add(MCTSAgent.createAction(true, false, false, false, true, false));
    	if (ls.mario.mayJump())  possibleActions.add(MCTSAgent.createAction(true, false, false, true, true, false));
        Random r = new Random();
        return possibleActions.get(r.nextInt(possibleActions.size()-1));
    }
}
