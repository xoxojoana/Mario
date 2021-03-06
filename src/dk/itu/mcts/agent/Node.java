/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.mcts.agent;

import ch.idsia.benchmark.mario.environments.Environment;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jinhong
 */
public class Node {

    private float[] marioPos;
    private int status;
    public Environment environment;
    private List<Node> children = new ArrayList<Node>();
    private Node parent = null;
    private ArrayList<boolean[]> validMoves;
    private boolean[] parentAction = null;
    private float reward = 0;
    private int timesvisited = 0;

    /**
     * for creating parent node
     * @param e 
     */
    Node(Environment e) {
        this.validMoves = new ArrayList<boolean[]>();
        this.environment = e;
        this.marioPos = e.getMarioFloatPos();
        this.status = e.getMarioStatus();
    }
    
    /**
     * For creating child node
     * @param e
     * @param pos
     * @param status 
     */
     Node(Environment e, float[] pos, int status) {
        this.validMoves = new ArrayList<boolean[]>();
        this.environment = e;
        this.marioPos = pos;
        this.status = status;
    }

     /**
      * check if game is over
      * @return 
      */
    public boolean gameOver() {
        return getStatus() != 2;
    }

    /**
     * @return the Mario state
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param state the state to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the children
     */
    public List<Node> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<Node> children) {
        this.children = children;
    }

    /**
     * @param child the child to add
     */
    public void addChild(Node child) {
        this.children.add(child);
    }

    /**
     * @return the parent
     */
    public Node getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * @return the parentAction
     */
    public boolean[] getParentAction() {
        return parentAction;
    }

    /**
     * @param parentAction the parentAction to set
     */
    public void setParentAction(boolean[] parentAction) {
        this.parentAction = parentAction;
    }

    /**
     * @return the reward
     */
    public float getReward() {
        return reward;
    }

    /**
     * @param reward the reward to set
     */
    public void setReward(float reward) {
        this.reward = reward;
    }

    /**
     * @return the timesvisited
     */
    public int getTimesvisited() {
        return timesvisited;
    }

    /**
     * @param timesvisited the timesvisited to set
     */
    public void setTimesvisited(int timesvisited) {
        this.timesvisited = timesvisited;
    }

    /**
     * @return the validMoves
     */
    public ArrayList<boolean[]> getValidMoves() {
        return validMoves;
    }

    /**
     * @param validMoves the validMoves to set
     */
    public void setValidMoves(ArrayList<boolean[]> validMoves) {
        this.validMoves = validMoves;
    }
    
    @Override
    public String toString() {
        String parent = this.parent!=null?" Parent"+this.parent.toString():"";
        return "Mario: " + getMarioPos()[0] + ", " + getMarioPos()[1] + ";" + parent + actionReadable(getParentAction());
    }
    
    /**
     * generate readable actions
     * @param action
     * @return 
     */
    private String actionReadable(boolean[] action){
        if(action == null) return "";
        String s = "\tAction:";
        if(action[0]) s += "Left\t";
        if(action[1]) s += "Right\t";
        if(action[2]) s += "Down\t";
        if(action[3]) s += "Jump\t";
        if(action[4]) s += "Speed\t";
        if(action[5]) s += "Up";
        return s;
    }
    

    /**
     * @return the marioPos
     */
    public float[] getMarioPos() {
        return marioPos;
    }

    /**
     * @param marioPos the marioPos to set
     */
    public void setMarioPos(float[] marioPos) {
        this.marioPos = marioPos;
    }

}
