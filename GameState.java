import java.util.Stack;

public class GameState {
    // The GameState class represents the state of the Minesweeper game.
    // We utilize Stack in Java to make "Move Undo" feature.
    // This class contains three 2D arrays:
    private char[][] board; // represents the game board
    private boolean[][] revealed; // represents the state of each tile (revealed or not)
    private boolean[][] mines; // represents the location of the mines on the board.

    // These Stacks below are used to keep track of the state of the game before
    // each move so that the player can undo their moves if they want to.
    private Stack<char[][]> boardStack;
    private Stack<boolean[][]> revealedStack;
    private Stack<boolean[][]> minesStack;

    // Initialize the Minesweeper GameState.
    public GameState(char[][] board, boolean[][] revealed, boolean[][] mines) {
        this.board = board;
        this.revealed = revealed;
        this.mines = mines;
        this.boardStack = new Stack<>();
        this.revealedStack = new Stack<>();
        this.minesStack = new Stack<>();
    }

    // The getBoard() returns the current state of the board as a 2D char array.
    public char[][] getBoard() {
        return board;
    }

    // The getRevealed() returns the state of revealed tiles as a 2D boolean array.
    public boolean[][] getRevealed() {
        return revealed;
    }

    // The getMines() method returns the state of mines as a 2D boolean array.
    public boolean[][] getMines() {
        return mines;
    }

    // The canUndo() method returns true if there are moves to undo (i.e. if the
    // boardStack is not empty).
    public boolean canUndo() {
        return !boardStack.isEmpty();
    }

    // saveSnapshot() is a method that allows the user to save the current state of
    // the Minesweeper game so that they can undo their previous move(s) later if
    // they made a mistake
    public void saveSnapshot() {
        // It creates a copy of the board, revealed, and mines arrays...
        char[][] snapshotBoard = new char[board.length][board[0].length];
        boolean[][] snapshotRevealed = new boolean[revealed.length][revealed[0].length];
        boolean[][] snapshotMines = new boolean[mines.length][mines[0].length];

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                snapshotBoard[row][col] = board[row][col];
                snapshotRevealed[row][col] = revealed[row][col];
                snapshotMines[row][col] = mines[row][col];
            }
        }

        // ... and pushes them onto their respective Stacks.
        boardStack.push(snapshotBoard);
        revealedStack.push(snapshotRevealed);
        minesStack.push(snapshotMines);
    }

    // The undo() method restores the game to the previous state by popping the last
    // saved state from the stacks. It pops the most recent state of the game from
    // the Stacks and sets the board, revealed, and mines arrays to that state.
    public void undo() {
        if (canUndo()) {
            board = boardStack.pop();
            revealed = revealedStack.pop();
            mines = minesStack.pop();
        }
    }
}