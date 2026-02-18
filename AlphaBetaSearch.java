package edu.iastate.cs472.proj2;

/**
 * 
 * @author ryanheat
 *
 */


/**
 * This class implements the Alpha-Beta pruning algorithm to find the best 
 * move at current state.
*/
public class AlphaBetaSearch extends AdversarialSearch {
    private static final int MAX_DEPTH = 5;

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

        int bestValue = Integer.MIN_VALUE;
        CheckersMove bestMove = legalMoves[0];

        int alpha = Integer.MIN_VALUE;
        int beta  = Integer.MAX_VALUE;
        
        for (CheckersMove move : legalMoves) {
            CheckersData nextState = copyBoard(this.board);
            nextState.makeMove(move);

            int value = minValue(nextState, MAX_DEPTH - 1, alpha, beta);

            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
            alpha = Math.max(alpha, bestValue);
        }

        return bestMove;
    }
    
    /**
     * BLACK to move.
     */
    private int maxValue(CheckersData state, int depth, int alpha, int beta) {
        CheckersMove[] moves = state.getLegalMoves(CheckersData.BLACK);

        if (moves == null || moves.length == 0) return -100000;

        if (depth == 0) return evaluate(state);

        int value = Integer.MIN_VALUE;

        for (CheckersMove m : moves) {
            CheckersData next = copyBoard(state);
            next.makeMove(m);

            value = Math.max(value, minValue(next, depth - 1, alpha, beta));

            if (value >= beta) {
                return value;
            }
            alpha = Math.max(alpha, value);
        }

        return value;
    }

    /**
     * RED to move.
     */
    private int minValue(CheckersData state, int depth, int alpha, int beta) {
        CheckersMove[] moves = state.getLegalMoves(CheckersData.RED);

        if (moves == null || moves.length == 0) return 100000;

        if (depth == 0) return evaluate(state);

        int value = Integer.MAX_VALUE;

        for (CheckersMove m : moves) {
            CheckersData next = copyBoard(state);
            next.makeMove(m);

            value = Math.min(value, maxValue(next, depth - 1, alpha, beta));

            if (value <= alpha) {
                return value;
            }
            beta = Math.min(beta, value);
        }

        return value;
    }

    /**
     * Heuristic evaluation of a state from BLACK's perspective.
     * Positive score is good for BLACK, negative is good for RED.
     */
    private int evaluate(CheckersData state) {
        int score = 0;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int piece = state.board[r][c];
                switch (piece) {
                    case CheckersData.BLACK:
                        score += 3;
                        break;
                    case CheckersData.BLACK_KING:
                        score += 5;
                        break;
                    case CheckersData.RED:
                        score -= 3;
                        break;
                    case CheckersData.RED_KING:
                        score -= 5;
                        break;
                    default:
                        break;
                }
            }
        }

        CheckersMove[] blackMoves = state.getLegalMoves(CheckersData.BLACK);
        CheckersMove[] redMoves   = state.getLegalMoves(CheckersData.RED);

        int blackMob = (blackMoves == null ? 0 : blackMoves.length);
        int redMob   = (redMoves   == null ? 0 : redMoves.length);

        score += (blackMob - redMob);

        return score;
    }

    /**
     * Create a deep copy of the given CheckersData board.
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
