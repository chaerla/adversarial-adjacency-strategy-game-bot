package lib;

import java.util.Random;

public class MinimaxBot extends Bot {
    public MinimaxBot(Player player) {
        super(player);
    }

    @Override
    public Coordinate findBestMove(Board currentBoard) {
        Random random = new Random(System.currentTimeMillis());
        currentBoard.generateSuccessors(this.player);
        int x = random.nextInt(8);
        int y = random.nextInt(8);
        System.out.println(x + ", " + y);
        return new Coordinate(x, y);
    }

}
