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

    private int[] state;
    public Environment environment;
    private List<Node> children = new ArrayList<Node>();
    private Node parent = null;
    private boolean[][] validMoves = new boolean[9][6];
    private boolean[] parentAction = null;
    private float reward = 0;
    private int timesvisited = 0;

    Node(Environment e) {
        this.environment = e;
        this.state = e.getMarioState();
    }

    public boolean gameOver() {
        if (getState()[0] == 2) {
            return false;
        }
        return true;
    }

    /**
     * @return the Mario state
     */
    public int[] getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(int[] state) {
        this.state = state;
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
    public boolean[][] getValidMoves() {
        return validMoves;
    }

    /**
     * @param validMoves the validMoves to set
     */
    public void setValidMoves(boolean[][] validMoves) {
        this.validMoves = validMoves;
    }

}
