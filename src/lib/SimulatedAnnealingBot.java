package lib;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class SimulatedAnnealingBot extends Bot {
    private static final double MAX_TIME = 4950;
    private static final double PROBABILITY_THRESHOLD = 0.75;

    public SimulatedAnnealingBot(Player player) {
        super(player);
    }

    @Override
    public Coordinate findBestMove(Board currentBoard) {
        List<Coordinate> emptyCoordinates = currentBoard.getEmptyCoordinates();

        List<Coordinate> currentPath = this.getRandomCoordinatesPath(emptyCoordinates);
        double currentPathScore = evaluate(currentBoard, currentPath);

        long startTime = System.currentTimeMillis();
        double temperature = Double.MAX_VALUE;

        while(true) {
            temperature = schedule(System.currentTimeMillis() - startTime);

            if (temperature <= 0 || System.currentTimeMillis() - startTime >= MAX_TIME) {
                return new Coordinate(currentPath.get(0).getRow(), currentPath.get(0).getCol());
            }

            List<Coordinate> nextPath = this.getRandomCoordinatesPath(currentPath);
            double nextPathScore = evaluate(currentBoard, nextPath);

            if (nextPathScore > currentPathScore ||
                    Math.exp((nextPathScore - currentPathScore) / temperature) > PROBABILITY_THRESHOLD) {
                currentPath = nextPath;
                currentPathScore = nextPathScore;
            }
        }
    }

    private List<Coordinate> getRandomCoordinatesPath(List<Coordinate> emptyCoordinates) {
        List<Coordinate> shuffledCoordinates = new ArrayList<>(emptyCoordinates);

        Random random = new Random(System.currentTimeMillis());
        int x = random.nextInt(emptyCoordinates.size());
        int y = random.nextInt(emptyCoordinates.size());

        Collections.swap(shuffledCoordinates, x, y);

        return shuffledCoordinates;
    }

    private double evaluate(Board currentBoard, List<Coordinate> path) {
        Board successorBoard = new Board(currentBoard);
        Player currentPlayer = this.player;
        int heuristic = 0;

        for (int i = 0 ; i< path.size(); i++) {
            successorBoard.updateCells(path.get(i).getRow(), path.get(i).getCol(), currentPlayer);
            if (i == 0) {
                heuristic = successorBoard.getObjectiveValue();
            }
            currentPlayer = currentPlayer == Player.O ? Player.X : Player.O;
        }

        return 0.6 * heuristic + 0.4 * successorBoard.getObjectiveValue();
    }

    private double schedule(long timeElapsed) {
        return (1 - timeElapsed / MAX_TIME);
    }
}
