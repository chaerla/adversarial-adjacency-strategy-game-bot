package lib;

import java.util.List;

public class MinimaxBot extends Bot {
    final int MAX_DEPTH = 5;

    private int visitCount = 0;

    public MinimaxBot(Player player) {
        super(player);
    }

    @Override
    public Coordinate findBestMove(Board currentBoard) {
//        System.out.println("===== BOT'S TURN =====");
        int v = Integer.MIN_VALUE;

        List<Coordinate> possibleMoves = currentBoard.getEmptyCoordinates();
        Player nextPlayer = this.player == Player.O ? Player.X : Player.O;
        Coordinate bestMove = possibleMoves.get(0);

        this.visitCount = 0;

        for (Coordinate move : possibleMoves) {
            Board successor = new Board(currentBoard);
            successor.updateCells(move.getRow(), move.getCol(), this.player);
            int successorValue = minValue(successor, nextPlayer, v, Integer.MAX_VALUE, 1);
            if (successorValue > v) {
                v = successorValue;
                bestMove = move;
            }
        }

//        System.out.println("===== VISIT COUNT =====");
//        System.out.println(visitCount);

        return bestMove;
    }

    private int maxValue(Board state, Player currentPlayer, int alpha, int beta, int depth) {
        if (cutoff(state, depth)) return evaluate(state);
        this.visitCount++;
        int v = Integer.MIN_VALUE;

        List<Board> successors = state.generateSuccessors(currentPlayer);
        Player nextPlayer = currentPlayer == Player.O ? Player.X : Player.O;

        for (Board successor : successors) {
            v = Math.max(v, minValue(successor, nextPlayer, alpha, beta, depth + 1));
            if (v >= beta) {
                return v;
            }
            alpha = Math.max(alpha, v);
        }

        return v;
    }

    private int minValue(Board state, Player currentPlayer, int alpha, int beta, int depth) {
        if (cutoff(state, depth)) return evaluate(state);
        this.visitCount++;
        int v = Integer.MAX_VALUE;

        List<Board> successors = state.generateSuccessors(currentPlayer);
        Player nextPlayer = currentPlayer == Player.O ? Player.X : Player.O;

        for (Board successor : successors) {
            v = Math.min(v, maxValue(successor, nextPlayer, alpha, beta, depth + 1));
            if (v <= alpha) {
                return v;
            }
            beta = Math.min(beta, v);
        }
        return v;
    }

    private boolean cutoff(Board state, int depth) {
        if (state.getEmptyCoordinates().size() == 0) return true;
        return depth >= MAX_DEPTH;
    }

    private int evaluate(Board state) {
        return state.getObjectiveValue();
    }
}
