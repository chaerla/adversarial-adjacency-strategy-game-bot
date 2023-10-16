package lib;

public class Coordinate {
    private int row;
    private int col;

    public Coordinate(int row, int col){
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    @Override
    public String toString() {
        return "(" + this.row + ", " + this.col + ")";
    }
}
