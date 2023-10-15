package lib;

public abstract class Bot {
    protected Player player;

    public Bot(Player player) {
        this.player = player;
    }

    public abstract Coordinate findBestMove(Board currentBoard);
}
