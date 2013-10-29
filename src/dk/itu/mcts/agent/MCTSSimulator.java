/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dk.itu.mcts.agent;

import ch.idsia.benchmark.mario.environments.Environment;
import competition.cig.robinbaumgarten.astar.LevelScene;
import competition.cig.robinbaumgarten.astar.level.Level;

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
        setLevelPart(scene, enemies);
    }
    
    public void setLevelPart(byte[][] levelPart, float[] enemies){
        currentWorld.setLevelScene(levelPart);
        currentWorld.setEnemies(enemies);
    }
    
}
