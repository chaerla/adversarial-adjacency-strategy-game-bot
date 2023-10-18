package lib;

import javafx.scene.DepthTest;

import java.util.List;

public class MinimaxBot extends Bot {
    private int maxDepth = -1;
    private long depthEstimatedVisits = 1;
    private final int PRUNING_FACTOR = 5000;
    private final int TIME_PER_VISIT = 50; // microseconds
    private long visitCount = 0;
    private int initialEmptyCnt = 0;

    public MinimaxBot(Player player) {
        super(player);
    }

    @Override
    public Coordinate findBestMove(Board currentBoard) {
        initialEmptyCnt = currentBoard.getEmptyCoordinates().size();
        depthEstimatedVisits = 1;
        maxDepth = -1;
        cutoff(currentBoard, 0);

        int v = Integer.MIN_VALUE;

        List<Coordinate> possibleMoves = currentBoard.getEmptyCoordinates();
        Player nextPlayer = this.player == Player.O ? Player.X : Player.O;
        Coordinate bestMove = possibleMoves.get(0);

        this.visitCount = 0;

        long startTime = System.nanoTime();
        long timeout = 3_500_000_000L; // 3.5 seconds

        try {
            for (Coordinate move : possibleMoves) {
                Board successor = new Board(currentBoard);
                successor.updateCells(move.getRow(), move.getCol(), this.player);
                int successorValue = minValue(successor, nextPlayer, v, Integer.MAX_VALUE, 1);
                if (successorValue > v) {
                    v = successorValue;
                    bestMove = move;
                }

                long currentTime = System.nanoTime();
                if (currentTime - startTime > timeout) {
                    throw new RuntimeException("Search may exceed 5 seconds and was terminated early.");
                }
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000;
        double durationInSeconds = duration / 1000000.0;

        System.out.println("===== VISIT COUNT =====");
        System.out.println("Visit Count: " + visitCount);
        System.out.printf("Duration: %.2f seconds%n", durationInSeconds);
        System.out.println("Empty Cells: " + currentBoard.getEmptyCoordinates().size());
        if (visitCount != 0)
            System.out.println("Time per visit: " + (duration / visitCount) + " micro second");
        System.out.println("MAX DEPTH: " + maxDepth);

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
        if (state.getEmptyCoordinates().size() == 0) {
            maxDepth = depth;
            return true;
        }
        if (maxDepth == -1) {
            depth++;
//            System.out.println("==== DEPTH ==== ");
            int emptyCells = initialEmptyCnt - depth + 1;
            depthEstimatedVisits = depthEstimatedVisits * emptyCells;
            long estVisits = depthEstimatedVisits / PRUNING_FACTOR;
            long estTime = estVisits * TIME_PER_VISIT;
//            System.out.println("Empty Cells: " + emptyCells);
//            System.out.println("Depth est visits: " + depthEstimatedVisits);
//            System.out.println("Est visits: " + estVisits);
//            System.out.println("Est time: " + estTime);
            if (estTime > 5 * 1e6) {
                maxDepth = depth - 1;
                return true;
            }
            return false;
        } else {
            return depth >= maxDepth;
        }
    }

    private int evaluate(Board state) {
        return state.getObjectiveValue();
    }
}
