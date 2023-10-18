package lib;

import java.util.*;

public class GeneticBot extends Bot{
    private static final int POPULATION_SIZE = 100;
    private static final int MAX_ITERATIONS = 1000;
    private static final int MAX_IND_SIZE = 10;
    private int individualSize = MAX_IND_SIZE;

    public GeneticBot(Player player) {
        super(player);
    }
    @Override
    public Coordinate findBestMove(Board currentBoard) {
        this.individualSize = Math.min(MAX_IND_SIZE, currentBoard.getEmptyCoordinates().size());

        List<Coordinate> emptyCoordinates = currentBoard.getEmptyCoordinates();

        if (individualSize <= 3) {
            int idx = (int) (Math.random() * (emptyCoordinates.size()));
            return emptyCoordinates.get(idx);
        }

        Set<List<Coordinate>> initialPopulation = new HashSet<>();

        while (initialPopulation.size() < POPULATION_SIZE) {
            List<Coordinate> individual = this.makeInitialIndividual(currentBoard);
            initialPopulation.add(individual);
        }

        List<List<Coordinate>> population = new ArrayList<>(initialPopulation);

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            NodeTree reservationTree = makeReservationTree(population, currentBoard);
            minimaxTreeMax(reservationTree);

            List<Integer> fitnessValues = Arrays.asList(new Integer[population.size()]);
            calculateFitnessValues(reservationTree, fitnessValues);

            List<Double> percentagePopulation = calculatePercentage(fitnessValues);

            List<List<Coordinate>> crossoverResult = crossoverPopulation(population, percentagePopulation);
            if (crossoverResult.get(0).size() != individualSize) {
                System.out.println(crossoverResult);
            }
            List<List<Coordinate>> mutatedResult = mutatePopulation(crossoverResult, currentBoard);
            if (mutatedResult.get(0).size() != individualSize) {
                System.out.println(crossoverResult);
            }
            population = mutatedResult;
        }

        List<Coordinate> besIndividual = getBestIndividual(population, currentBoard);

        return besIndividual.get(0);
    }

    private List<Coordinate> getBestIndividual(List<List<Coordinate>> population, Board currentBoard) {
        NodeTree reservationTree = makeReservationTree(population, currentBoard);
        minimaxTreeMax(reservationTree);

        List<Integer> fitnessValues = Arrays.asList(new Integer[population.size()]);
        calculateFitnessValues(reservationTree, fitnessValues);

        int maxFV = Integer.MIN_VALUE;
        int maxIdx = -1;
        for (int i = 0; i < fitnessValues.size(); i++) {
            int num = fitnessValues.get(i);
            if (num > maxFV) {
                maxFV = num;
                maxIdx = i;
            }
        }

        return population.get(maxIdx);
    }

    private List<List<Coordinate>> crossoverPopulation(List<List<Coordinate>> initialPopulation, List<Double> percentages) {
        List<List<Coordinate>> crossoverCandidate = new ArrayList<>();
        for (int i = 0; i < initialPopulation.size(); i++) {
            double randomCrossover = Math.random();
            for (int j = 0; j < percentages.size(); j++) {
                if (randomCrossover < percentages.get(j)) {
                    crossoverCandidate.add(initialPopulation.get(j));
                    break;
                }
            }
        }
        for (int i = 0; i < crossoverCandidate.size(); i+=2) {
            if (i+1 > crossoverCandidate.size()-1) {
                break;
            }

            List<Coordinate> candidate1 = crossoverCandidate.get(i);
            List<Coordinate> candidate2 = crossoverCandidate.get(i + 1);
            int crossoverPoint = ((int)(Math.random() * (individualSize - 1))) + 1;

            List<Coordinate> newCandidate1 = new ArrayList<>();
            List<Coordinate> newCandidate2 = new ArrayList<>();
            for (int j = 0; j < individualSize; j++) {
                if (j >= crossoverPoint) {
                    newCandidate1.add(candidate2.get(j));
                    newCandidate2.add(candidate1.get(j));
                } else {
                    newCandidate1.add(candidate1.get(j));
                    newCandidate2.add(candidate2.get(j));
                }
            }
            crossoverCandidate.set(i, newCandidate1);
            crossoverCandidate.set(i + 1, newCandidate2);
        }
        return crossoverCandidate;
    }

    private List<List<Coordinate>> mutatePopulation(List<List<Coordinate>> initialPopulation, Board currentBoard) {
        Set<List<Coordinate>> mutated = new HashSet<>();

        for (int i = 0; i < initialPopulation.size(); i++) {
            Board successorBoard = new Board(currentBoard);
            Player currentPlayer = this.player;

            for (int j = 0; j< individualSize; j++) {
                successorBoard.updateCells(initialPopulation.get(i).get(j).getRow(), initialPopulation.get(i).get(j).getCol(), currentPlayer);
                currentPlayer = currentPlayer == Player.O ? Player.X : Player.O;
            }

            List<Coordinate> emptyCoordinates = successorBoard.getEmptyCoordinates();
            List<Coordinate> mutatedIndividual = initialPopulation.get(i);

            if (emptyCoordinates.size() > 6) {
                int mutationIdx = (int) (Math.random() * (individualSize));
                int randomIdx = (int) (Math.random() * (emptyCoordinates.size()));

                mutatedIndividual.set(mutationIdx, emptyCoordinates.get(randomIdx));

                while (mutated.contains(mutatedIndividual)) {
                    randomIdx = (int) (Math.random() * (emptyCoordinates.size()));
                    mutatedIndividual.set(mutationIdx, emptyCoordinates.get(randomIdx));
                }
            }

            mutated.add(mutatedIndividual);
        }

        List<List<Coordinate>> mutatedPopulation = new ArrayList<>(mutated);
        return mutatedPopulation;
    }

    private List<Double> calculatePercentage(List<Integer> fitnessValues) {
        int sumValue = fitnessValues.stream().reduce(0, Integer::sum);
        List<Double> percentages = new ArrayList<>();

        for (int i = 0; i < fitnessValues.size(); i++) {
            double percentage = (double)fitnessValues.get(i)/sumValue;
            if (i != 0) {
                percentage += percentages.get(i-1);
            }
            percentages.add(percentage);
        }
        return percentages;
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
        return k;
    }

    private void minimaxTreeMax(NodeTree currentTree) {
        if (currentTree.getChildren().size() == 0) {
            return;
        }

        int v = Integer.MIN_VALUE;
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
        if (currentTree.getChildren().size() == 0) {
            return;
        }

        int v = Integer.MIN_VALUE;
        List<NodeTree> children = new ArrayList<>(currentTree.getChildren().values());

        for (NodeTree childTree : children) {
            minimaxTreeMax(childTree);
            v = Math.min(childTree.getValue(), v);
        }
        currentTree.setValue(v);
    }

    private NodeTree makeReservationTree(List<List<Coordinate>> populationList, Board initialBoard) {
        NodeTree nt = new NodeTree(0);
        NodeTree currentNode;

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

        for (int i = 0; i < individualSize; i++) {
            int idx = (int) (Math.random() * (emptyCoordinates.size()));
            individual.add(emptyCoordinates.get(idx));
            emptyCoordinates.remove(idx);
        }

        return individual;
    }

    private int evaluateIndividual(Board currentBoard, List<Coordinate> individual) {
        Board successorBoard = new Board(currentBoard);
        Player currentPlayer = this.player;

        for (int i = 0 ; i< individual.size(); i++) {
            successorBoard.updateCells(individual.get(i).getRow(), individual.get(i).getCol(), currentPlayer);
            currentPlayer = currentPlayer == Player.O ? Player.X : Player.O;
        }

        return successorBoard.getObjectiveValue();
    }
}
