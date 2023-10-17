package lib;

import com.sun.org.apache.xpath.internal.patterns.NodeTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneticBot extends Bot{

    private static final double MAX_TIME = 4950;
    private static final int INDIVIDUAL_SIZE = 5;
    private static final int POPULATION_SIZE = 50;

    public GeneticBot(Player player) {
        super(player);
    }
    @Override
    public Coordinate findBestMove(Board currentBoard) {
        Set<List<Coordinate>> initialPopulation = new HashSet<>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<Coordinate> individual = this.makeInitialIndividual(currentBoard);
            initialPopulation.add(individual);
        }

        NodeTree reservationTree = makeReservationTree(initialPopulation);

        return new Coordinate(0,0);
    }

    private NodeTree makeReservationTree(Set<List<Coordinate>> population, Board initialBoard) {
        NodeTree nt = new NodeTree(0);
        NodeTree currentNode;

        for (List<Coordinate> individual : population) {
            currentNode = nt;
            for (int i = 0; i < individual.size(); i++) {
                Coordinate move = individual.get(i);

                if (!currentNode.childExists(move)) {
                    currentNode.addChildren(evaluateIndividual(initialBoard, individual), move);
                }

                currentNode = currentNode.getChild(move);
            }
        }

        return nt;
    }

    private List<Coordinate> makeInitialIndividual(Board currentBoard) {
        List<Coordinate> individual = new ArrayList<Coordinate>();
        List<Coordinate> emptyCoordinates = currentBoard.getEmptyCoordinates();
        int individualSize = Math.min(INDIVIDUAL_SIZE, emptyCoordinates.size());

        for (int i = 0; i < individualSize; i++) {
            int idx = (int) (Math.random() * (emptyCoordinates.size() - 1));
            individual.add(emptyCoordinates.get(idx));
            emptyCoordinates.remove(idx);
        }

        return individual;
    }

    private double evaluateIndividual(Board currentBoard, List<Coordinate> individual) {
        Board successorBoard = new Board(currentBoard);
        Player currentPlayer = this.player;

        for (int i = 0 ; i< individual.size(); i++) {
            successorBoard.updateCells(individual.get(i).getRow(), individual.get(i).getCol(), currentPlayer);
            currentPlayer = currentPlayer == Player.O ? Player.X : Player.O;
        }

        return successorBoard.getObjectiveValue();
    }
}
