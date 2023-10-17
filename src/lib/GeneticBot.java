package lib;

import com.sun.org.apache.xpath.internal.patterns.NodeTest;

import javax.xml.soap.Node;
import java.util.*;

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

        NodeTree reservationTree = makeReservationTree(initialPopulation, currentBoard);
        minimaxTreeMax(reservationTree);

        // HARUSNYA FITNESS VALUE INDIVIDU KE-IDX BERADA DI fitnessValues KE-IDX
        List<Integer> fitnessValues = Arrays.asList(new Integer[POPULATION_SIZE]);
        calculateFitnessValues(reservationTree, fitnessValues);

        return new Coordinate(0,0);
    }

    private void calculateFitnessValues(NodeTree currentTree, List<Integer> fitnessValues) {
        if (currentTree.getChildren().size() == 0) {
            fitnessValues.set(currentTree.getIndividualId(), countFitnessValue(currentTree));
        } else
            for (NodeTree child : currentTree.getChildren().values()) {
                calculateFitnessValues(child, fitnessValues);
            }
    }

    private int countFitnessValue(NodeTree currentTree) {
        int k = 1;
        while (currentTree.getParent() != null && currentTree.getParent().getValue() == currentTree.getValue()) {
            currentTree = currentTree.getParent();
            k++;
        }
        return INDIVIDUAL_SIZE - k + 1;
    }

    private void minimaxTreeMax(NodeTree currentTree) {
        if (currentTree.getChildren().size() == 0) return;

        double v = Double.MIN_VALUE;
        List<NodeTree> children = new ArrayList<>(currentTree.getChildren().values());

        for (NodeTree childTree : children) {
            minimaxTreeMin(childTree);
            if (childTree.getValue() > v) {
                v = childTree.getValue();
            }
        }
        currentTree.setValue(v);
    }

    private void minimaxTreeMin(NodeTree currentTree) {
        if (currentTree.getChildren().size() == 0) return;

        double v = Double.MIN_VALUE;
        List<NodeTree> children = new ArrayList<>(currentTree.getChildren().values());

        for (NodeTree childTree : children) {
            minimaxTreeMax(childTree);
            v = Math.min(childTree.getValue(), v);
        }
        currentTree.setValue(v);
    }

    private NodeTree makeReservationTree(Set<List<Coordinate>> population, Board initialBoard) {
        NodeTree nt = new NodeTree(0);
        NodeTree currentNode;
        List<List<Coordinate>> populationList = new ArrayList<>(population);

        // ASUMSI: URUTAN ELEMEN DI SET SAMA LIST SELALU SAMA
        for (int i = 0; i < populationList.size(); i++) {
            List<Coordinate> individual = populationList.get(i);
            currentNode = nt;

            for (Coordinate move : individual) {
                if (!currentNode.childExists(move)) {
                    currentNode.addChildren(0, move);
                }

                currentNode = currentNode.getChild(move);
            }
            currentNode.setValue(evaluateIndividual(initialBoard, individual));
            currentNode.setIndividualId(i);
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
