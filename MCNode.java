package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * 
 * @author ryanheat
 *
 */

/**
 * Node type for the Monte Carlo search tree.
 */
public class MCNode<E> {
  CheckersData state;
  MCNode<E> parent;
  ArrayList<MCNode<E>> children;
  E move;
  int playerToMove;
  double wins;
  int visits;
  ArrayList<E> untriedMoves;
  
  public MCNode(CheckersData state, int playerToMove, E move, MCNode<E> parent, ArrayList<E> untriedMoves) {
    this.state = state;
    this.playerToMove = playerToMove;
    this.move = move;
    this.parent = parent;
    this.children = new ArrayList<>();
    this.wins = 0.0;
    this.visits = 0;
    this.untriedMoves = (untriedMoves == null ? new ArrayList<>() : untriedMoves);
  }
}


