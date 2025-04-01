import java.util.Random;

public class GameController {
    public void gameplay() {
        Board board = new Board();

        Player player1 = new Player(100, 'X');

        Player player2 = new Player(101, '0');
        int row = 3;
        int col = 3;

        board.createBoard(row, col);
        for (int i = 0; i < row * col; i++) {
            Random random = new Random();
            int x = random.nextInt(3); // 0 to 2
            int y = random.nextInt(3);
            if (i % 2 == 0) {

                if (!board.isOccoupied(x, y, player1.getSymbol()))
                    board.moveForward(x, y, player1.getSymbol());
                if (board.iswinningcondition(row, col, player1.getSymbol())) {
                    System.out.println("Player 1 wins");
                }
            } else {
                if (!board.isOccoupied(x, y, player2.getSymbol()))
                    board.moveForward(0, 1, player2.getSymbol());
                System.out.println("Player 1 wins");
            }

        }
    }

}
