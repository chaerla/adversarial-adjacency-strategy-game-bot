package lib;


import java.util.ArrayList;
import java.util.List;

public class Board {
    private Player[][] board;
    private final int rowSize;
    private final int colSize;
    private int xCount;
    private int oCount;
    private int objectiveValue;

    public Board(int rowSize, int colSize) {
        this.rowSize = rowSize;
        this.colSize = colSize;
        this.board = new Player[rowSize][colSize];
        this.xCount = 0;
        this.oCount = 0;
    }

    public Board(Board other) {
        this.rowSize = other.getRowSize();
        this.colSize = other.getColSize();
        this.xCount = other.getXCount();
        this.oCount = other.getOCount();
        this.board = new Player[this.rowSize][this.colSize];
        for (int i = 0; i < this.rowSize; i++) {
            for (int j = 0; j < this.colSize; j++) {
                this.board[i][j] = other.getBoard()[i][j];
            }
        }
    }

    public void initialize() {
        for (int i = 1; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                this.setCellValue(this.rowSize - i, j, Player.X);
                this.setCellValue(j, this.colSize - i, Player.O);
            }
        }
    }

    public Player[][] getBoard() {
        return this.board;
    }

    public int getRowSize() {
        return this.rowSize;
    }

    public int getColSize() {
        return this.colSize;
    }

    public int getXCount() {
        return this.xCount;
    }

    public int getOCount() {
        return this.oCount;
    }

    public int getObjectiveValue() {
        return this.objectiveValue;
    }

    public boolean validateCoordinate(int row, int col) {
        return (row >= 0 && row < this.rowSize && col >= 0 && col < this.colSize);
    }

    /***
     *
     * @param row
     * @param col
     * @param val
     *
     * set the value of a particular cell
     */
    public void setCellValue(int row, int col, Player val) {
        this.board[row][col] = val;
        if (val == Player.X) {
            this.xCount += 1;
        } else {
            this.oCount += 1;
        }
    }

    public void flipCellValue(int row, int col, Player val) {
        setCellValue(row, col, val);
        // decrement the opponent's count
        if (val == Player.X) {
            this.oCount -= 1;
        } else {
            this.xCount -= 1;
        }
    }

    /***
     *
     * @param row
     * @param col
     * @param val
     *
     * called everytime player makes a move, to update adjacent cells
     */
    public void updateCells(int row, int col, Player val) {
        this.setCellValue(row, col, val);

        Player adjacent = Player.O;
        if (val == Player.O) {
            adjacent = Player.X;
        }

        // updates coordinate and adjacent coordinates
        for (int i = row - 1; i <= row + 1; i++) {
            if (validateCoordinate(i, col) && (this.board[i][col] == adjacent)) {
                this.flipCellValue(i, col, val);
            }
        }

        for (int j = col - 1; j <= col + 1; j++) {
            if (validateCoordinate(row, j) && (this.board[row][j] == adjacent)) {
                this.flipCellValue(row, j, val);
            }
        }

        // update the objective value
        this.evaluate(val);

        printBoard();
    }


    // for debugging
    public void printBoard() {
        System.out.println("=================================================================");
        for (int i = 0; i < this.getRowSize(); i++) {
            System.out.print("| ");
            for (int j = 0; j < this.getColSize(); j++) {
                if (this.board[i][j] == null) {
                    System.out.print("  | ");
                } else {
                    System.out.print(this.getBoard()[i][j] + " | ");
                }
            }
            System.out.println();
        }
        System.out.println("=================================================================");
    }

    /***
     * Objective Function
     *
     * @param player
     */
    public void evaluate(Player player) {
        int res = this.xCount - this.oCount;
        if (player == Player.O) {
            res *= -1;
        }
        this.objectiveValue = res;
    }

    /***
     *
     * @param currPlayer
     * Function to generate successors
     * used in both Minimax and Simulated Annealing
     *
     * @return List of Board which represents all possible next moves
     */
    public List<Board> generateSuccessors(Player currPlayer) {
        List<Board> boards = new ArrayList<Board>();
        for (int i = 0; i < this.rowSize; i++) {
            for (int j = 0; j < this.colSize; j++) {
                if (this.board[i][j] == null) {
                    Board successor = new Board(this);
                    successor.updateCells(i, j, currPlayer);
                    System.out.println("X = " + successor.xCount);
                    System.out.println("O = " + successor.oCount);
                    System.out.println("Objective value = " + successor.objectiveValue);
                    boards.add(successor);
                }
            }
        }
        return boards;
    }

}
