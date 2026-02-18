package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * 
 * @author ryanheat
 *
 */

/**
 * This class implements the Monte Carlo tree search method to find the best
 * move at the current state.
 */
public class MonteCarloTreeSearch extends AdversarialSearch {
    private static final int ITERATIONS = 500;
    private static final double EXPLORATION_CONSTANT = Math.sqrt(2.0);
    private final Random rand = new Random();

	/**
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers:  fromRow, fromCol, toRow, toCol
     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this
     * move is a jump or a simple move.
     *
     * Each legalMove in the input now contains a single move
     * or a sequence of jumps: (rows[0], cols[0]) -> (rows[1], cols[1]) ->
     * (rows[2], cols[2]).
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        if (legalMoves == null || legalMoves.length == 0) return null;
        if (legalMoves.length == 1) return legalMoves[0];

        CheckersData rootState = copyBoard(this.board);
        ArrayList<CheckersMove> rootUntried = new ArrayList<>(Arrays.asList(legalMoves));

        MCNode<CheckersMove> root = new MCNode<>(rootState, CheckersData.BLACK, null, null, rootUntried);

        for (int i = 0; i < ITERATIONS; i++) {
            MCNode<CheckersMove> node = root;
            CheckersData simState = copyBoard(rootState);
            int player = CheckersData.BLACK;

            // selection
            while (node.untriedMoves.isEmpty() && !node.children.isEmpty()) {
                node = selectChild(node);
                simState.makeMove(node.move);
                player = oppositePlayer(player);
            }

            // expansion
            if (!node.untriedMoves.isEmpty()) {
                int idx = rand.nextInt(node.untriedMoves.size());
                CheckersMove m = node.untriedMoves.remove(idx);

                simState.makeMove(m);
                int nextPlayer = oppositePlayer(player);

                CheckersMove[] childMoves = simState.getLegalMoves(nextPlayer);
                ArrayList<CheckersMove> childUntried = new ArrayList<>();
                if (childMoves != null) childUntried.addAll(Arrays.asList(childMoves));

                MCNode<CheckersMove> child = new MCNode<>(copyBoard(simState), nextPlayer, m, node, childUntried);

                node.children.add(child);
                node = child;
                player = nextPlayer;
            }

            // simulation
            int result = simulateRandomPlayout(simState, player);

            // backpropagation
            backpropagate(node, result);
        }

        MCNode<CheckersMove> bestChild = bestChildByVisits(root);
        return (bestChild == null ? legalMoves[0] : bestChild.move);
    }
    
    /**
     * Select a child of the given node using the UCB1 formula.
     */
    private MCNode<CheckersMove> selectChild(MCNode<CheckersMove> node) {
        MCNode<CheckersMove> best = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (MCNode<CheckersMove> child : node.children) {
            if (child.visits == 0) {
                return child;
            }
            double exploit = child.wins / (double) child.visits;
            double explore = Math.sqrt(
                    2.0 * Math.log(node.visits + 1) / (double) child.visits
            );
            double ucb1 = exploit + EXPLORATION_CONSTANT * explore;

            if (ucb1 > bestValue) {
                bestValue = ucb1;
                best = child;
            }
        }
        return best;
    }

    /**
     * Run a random playout from the given state and player-to-move.
     * Returns +1 if BLACK wins, -1 if RED wins.
     */
    private int simulateRandomPlayout(CheckersData state, int playerToMove) {
        while (true) {
            CheckersMove[] moves = state.getLegalMoves(playerToMove);
            if (moves == null || moves.length == 0) {
                if (playerToMove == CheckersData.BLACK) {
                    return -1;
                } else {
                    return 1;
                }
            }

            CheckersMove move = moves[rand.nextInt(moves.length)];
            state.makeMove(move);

            playerToMove = oppositePlayer(playerToMove);
        }
    }

    /**
     * Backpropagate the result from the given node up to the root.
     */
    private void backpropagate(MCNode<CheckersMove> node, int result) {
        while (node != null) {
            node.visits++;
            node.wins += result;
            node = node.parent;
        }
    }

    /**
     * Pick the child of root with the highest visit count.
     */
    private MCNode<CheckersMove> bestChildByVisits(MCNode<CheckersMove> root) {
        MCNode<CheckersMove> best = null;
        int bestVisits = -1;

        for (MCNode<CheckersMove> child : root.children) {
            if (child.visits > bestVisits) {
                bestVisits = child.visits;
                best = child;
            }
        }
        return best;
    }

    /**
     * Flip player.
     */
    private int oppositePlayer(int player) {
        return (player == CheckersData.BLACK ? CheckersData.RED : CheckersData.BLACK);
    }

    /**
     * Deep copy of a CheckersData board.
     */
    private CheckersData copyBoard(CheckersData original) {
        CheckersData copy = new CheckersData();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                copy.board[r][c] = original.board[r][c];
            }
        }
        return copy;
    }

}
