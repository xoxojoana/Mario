/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.mcts.agent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jinhong
 */
public class Node {

    private int[] state;
    private List<Node> children = new ArrayList<Node>();
    private Node parent = null;
    private List<Integer> validMove = new ArrayList<Integer>();
    public int parentAction = -1;
    private float reward = 0;
    private int timesvisited = 0;

    Node(int[] state) {
        this.state = state;
    }

}
