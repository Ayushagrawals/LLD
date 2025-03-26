
public class Board {
    String boardId;
    int rows;
    int cols;
    String gameId;
    Cell[][] board = new Cell[rows][cols];

    public void createBoard(int row, int col) {
        this.rows = rows;
        this.cols = cols;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                board[i][j] = new Cell(i, j);
            }
        }
    }

    public boolean moveForward(int row, int col, Character symbol) {
        if (board[row][col] != null) {
            board[row][col].symbol = symbol;
            return true;
        } else {
            return false;
        }
    }

    public boolean iswinningcondition(int row, int col, Character symbol) {
        for (int i = 0; i <= rows; i++) {
            if (board[i][0].symbol != symbol) {
                return false;
            }
            if (board[i][rows].symbol != symbol) {
                return false;
            }

        }
        return true;
    }
}