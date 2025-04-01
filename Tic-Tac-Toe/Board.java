
public class Board {
    String boardId;
    int rows;
    int cols;
    String gameId;
    Cell[][] board;

    public void createBoard(int row, int col) {
        board = new Cell[row][col];
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
            if (board[rows][i].symbol != symbol) {
                return false;
            }
            if (board[i][rows].symbol != symbol) {
                return false;
            }
        }
        return true;
    }

    public boolean isOccoupied(int row, int col, Character symbol) {
        if (board[row][col].symbol != null) {
            return false;
        } else {
            return true;
        }
    }
}