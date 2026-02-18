package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * @author ryanheat
 *
 */

/**
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases)
 * while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 */
public class CheckersData {

  /*  The following constants represent the possible contents of a square
      on the board.  The constants RED and BLACK also represent players
      in the game. */

    static final int
            EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;


    int[][] board;  // board[r][c] is the contents of row r, column c.


    /**
     * Constructor.  Create the board and set it up for a new game.
     */
    CheckersData() {
        board = new int[8][8];
        setUpGame();
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            int[] row = board[i];
            sb.append(8 - i).append(" ");
            for (int n : row) {
                if (n == 0) {
                    sb.append(" ");
                } else if (n == 1) {
                    sb.append(ANSI_RED + "R" + ANSI_RESET);
                } else if (n == 2) {
                    sb.append(ANSI_RED + "K" + ANSI_RESET);
                } else if (n == 3) {
                    sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
                } else if (n == 4) {
                    sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
                }
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        sb.append("  a b c d e f g h");

        return sb.toString();
    }

    /**
     * Set up the board with checkers in position for the beginning
     * of a game.  Note that checkers can only be found in squares
     * that satisfy  row % 2 == col % 2.  At the start of the game,
     * all such squares in the first three rows contain black squares
     * and all such squares in the last three rows contain red squares.
     */
    void setUpGame() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Dark squares are those where row % 2 == col % 2
                boolean darkSquare = (row % 2 == col % 2);
                
                if (row < 3 && darkSquare) {
                    board[row][col] = BLACK;
                } else if (row > 4 && darkSquare) {
                    board[row][col] = RED;
                } else {
                    board[row][col] = EMPTY;
                }
            }
        }
    	// Set up the board with pieces BLACK, RED, and EMPTY
    }


    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col) {
        return board[row][col];
    }


    /**
     * Make the specified move.  It is assumed that move
     * is non-null and that the move it represents is legal.
     *
     * Make a single move or a sequence of jumps
     * recorded in rows and cols.
     *
     */
    void makeMove(CheckersMove move) {
        int l = move.rows.size();
        for(int i = 0; i < l-1; i++)
            makeMove(move.rows.get(i), move.cols.get(i), move.rows.get(i+1), move.cols.get(i+1));
    }


    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
     * assumed that this move is legal.  If the move is a jump, the
     * jumped piece is removed from the board.  If a piece moves to
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     *
     * @param fromRow row index of the from square
     * @param fromCol column index of the from square
     * @param toRow   row index of the to square
     * @param toCol   column index of the to square
     */
    void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        int piece = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        board[toRow][toCol] = piece;

        if (Math.abs(toRow - fromRow) == 2) {
            int capRow = (fromRow + toRow) / 2;
            int capCol = (fromCol + toCol) / 2;
            board[capRow][capCol] = EMPTY;
        }

        if (piece == RED && toRow == 0) {
            board[toRow][toCol] = RED_KING;
        } else if (piece == BLACK && toRow == 7) {
            board[toRow][toCol] = BLACK_KING;
        }
    	// Update the board for the given move. You need to take care of the following situations:
        // 1. move the piece from (fromRow,fromCol) to (toRow,toCol)
        // 2. if this move is a jump, remove the captured piece
        // 3. if the piece moves into the kings row on the opponent's side of the board, crowned it as a king
    }

    /**
     * Return an array containing all the legal CheckersMoves
     * for the specified player on the current board.  If the player
     * has no legal moves, null is returned.  The value of player
     * should be one of the constants RED or BLACK; if not, null
     * is returned.  If the returned value is non-null, it consists
     * entirely of jump moves or entirely of regular moves, since
     * if the player can jump, only jumps are legal moves.
     *
     * @param player color of the player, RED or BLACK
     */
    CheckersMove[] getLegalMoves(int player) {
        if (player != RED && player != BLACK) return null;

        int playerKing = (player == RED ? RED_KING : BLACK_KING);
        int opponent = (player == RED ? BLACK : RED);
        int opponentKing = (player == RED ? BLACK_KING : RED_KING);

        ArrayList<CheckersMove> moves = new ArrayList<>();

        // search for any possible jumps
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int piece = board[row][col];

                if (piece == player || piece == playerKing) {
                    CheckersMove[] jumps = getLegalJumpsFrom(player, row, col);
                    if (jumps != null) {
                        for (CheckersMove jump : jumps)
                            moves.add(jump);
                    }
                }
            }
        }

        if (!moves.isEmpty()) return moves.toArray(new CheckersMove[moves.size()]);

        // no jumps exist
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int piece = board[row][col];

                if (piece != player && piece != playerKing) continue;

                if (piece == RED) {
                    checkAddNormalMove(player, moves, row, col, row - 1, col - 1);
                    checkAddNormalMove(player, moves, row, col, row - 1, col + 1);
                }

                if (piece == BLACK) {
                    checkAddNormalMove(player, moves, row, col, row + 1, col - 1);
                    checkAddNormalMove(player, moves, row, col, row + 1, col + 1);
                }

                if (piece == RED_KING || piece == BLACK_KING) {
                    checkAddNormalMove(player, moves, row, col, row - 1, col - 1);
                    checkAddNormalMove(player, moves, row, col, row - 1, col + 1);
                    checkAddNormalMove(player, moves, row, col, row + 1, col - 1);
                    checkAddNormalMove(player, moves, row, col, row + 1, col + 1);
                }
            }
        }
        if (moves.isEmpty()) return null;

        return moves.toArray(new CheckersMove[moves.size()]);
    }

    /**
     * This helper verifies that the destination square is inside the
     * board and currently empty. If valid, a CheckersMove object is
     * added to the provided move list.
     */
    private void checkAddNormalMove(int player, ArrayList<CheckersMove> moves, int fromRow, int fromCol, int toRow, int toCol) {
        if (toRow < 0 || toRow > 7 || toCol < 0 || toCol > 7) return;
        if (board[toRow][toCol] == EMPTY) moves.add(new CheckersMove(fromRow, fromCol, toRow, toCol));
    }


    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column.  If no such
     * jumps are possible, null is returned.  The logic is similar
     * to the logic of the getLegalMoves() method.
     *
     * Note that each CheckerMove may contain multiple jumps. 
     * Each move returned in the array represents a sequence of jumps 
     * until no further jump is allowed.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */
    CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
        int piece = board[row][col];
        int kingPiece = (player == RED ? RED_KING : BLACK_KING);

        if (piece != player && piece != kingPiece) return null;

        ArrayList<CheckersMove> moves = new ArrayList<>();

        ArrayList<int[]> path = new ArrayList<>();
        path.add(new int[]{row, col});

        boolean hasJump = findJumpsFrom(player, row, col, path, moves);

        if (!hasJump) return null;

        return moves.toArray(new CheckersMove[moves.size()]);
    }

    /**
     * This helper recursively find all jump sequences for the given
     * player starting from (row, col), given the current path of
     * visited positions.
     */
    private boolean findJumpsFrom(int player, int row, int col, ArrayList<int[]> path, ArrayList<CheckersMove> moves) {
        boolean foundAny = false;

        int piece = board[row][col];
        int kingPiece = (player == RED ? RED_KING : BLACK_KING);
        int opponent = (player == RED ? BLACK : RED);
        int opponentKing = (player == RED ? BLACK_KING : RED_KING);

        int[][] directions = { {-1, -1}, {-1, 1},{ 1, -1}, { 1, 1} };

        for (int[] dir : directions) {
            int dr = dir[0];
            int dc = dir[1];

            int midRow = row + dr;
            int midCol = col + dc;
            int toRow  = row + 2 * dr;
            int toCol  = col + 2 * dc;

            if (toRow < 0 || toRow > 7 || toCol < 0 || toCol > 7) continue;
            if (midRow < 0 || midRow > 7 || midCol < 0 || midCol > 7) continue;

            if (board[toRow][toCol] != EMPTY) continue;

            int mid = board[midRow][midCol];
            if (mid != opponent && mid != opponentKing) continue;

            if (piece == RED && toRow > row) continue;
            if (piece == BLACK && toRow < row) continue;

            int fromPiece = board[row][col];
            int capturedPiece = board[midRow][midCol];
            int destBefore = board[toRow][toCol];

            board[row][col] = EMPTY;
            board[midRow][midCol] = EMPTY;
            board[toRow][toCol] = fromPiece;

            boolean crowned = false;
            if (fromPiece == RED && toRow == 0) {
                board[toRow][toCol] = RED_KING;
                crowned = true;
            } else if (fromPiece == BLACK && toRow == 7) {
                board[toRow][toCol] = BLACK_KING;
                crowned = true;
            }

            path.add(new int[]{toRow, toCol});

            boolean further = findJumpsFrom(player, toRow, toCol, path, moves);

            if (!further) {
                CheckersMove m = buildMoveFromPath(path);
                moves.add(m);
            }

            foundAny = true;

            path.remove(path.size() - 1);
            board[row][col] = fromPiece;
            board[midRow][midCol] = capturedPiece;
            board[toRow][toCol] = destBefore;

            if (crowned) {}
        }

        return foundAny;
    }

    /**
     * This helper builds a CheckersMove that encodes this
     * sequence of jumps.
     */
    private CheckersMove buildMoveFromPath(ArrayList<int[]> path) {
        int[] start = path.get(0);
        int[] firstDest = path.get(1);

        CheckersMove move = new CheckersMove(start[0], start[1], firstDest[0], firstDest[1]);

        for (int i = 2; i < path.size(); i++) {
            int[] p = path.get(i);
            move.addMove(p[0], p[1]);
        }

        return move;
    }

}
