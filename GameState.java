import java.util.Stack;

public class GameState {
    private char[][] board;
    private boolean[][] revealed;
    private boolean[][] mines;
    private int numMines;
    private int numRevealed;
    private Stack<GameState> previousStates;

    public GameState(char[][] board, boolean[][] revealed, boolean[][] mines, int numMines, int numRevealed) {
        this.board = copyBoard(board);
        this.revealed = copyRevealed(revealed);
        this.mines = copyMines(mines);
        this.numMines = numMines;
        this.numRevealed = numRevealed;
        this.previousStates = new Stack<>();
    }
    
    public char[][] getBoard() {
        return board;
    }
    
    public boolean[][] getRevealed() {
        return revealed;
    }
    
    public boolean[][] getMines() {
        return mines;
    }
    
    public int getNumMines() {
        return numMines;
    }
    
    public int getNumRevealed() {
        return numRevealed;
    }
    
    public void saveState() {
        previousStates.push(new GameState(copyBoard(board), copyRevealed(revealed), copyMines(mines), numMines, numRevealed));
    }
    
    public boolean canUndo() {
        return previousStates.size() > 1;
    }
    
    public void undo() {
        if (canUndo()) {
            GameState stateToRestore = previousStates.pop();
            this.board = copyBoard(stateToRestore.getBoard());
            this.revealed = copyRevealed(stateToRestore.getRevealed());
            this.mines = copyMines(stateToRestore.getMines());
            this.numMines = stateToRestore.getNumMines();
            this.numRevealed = stateToRestore.getNumRevealed();
        }
    }
    
    private char[][] copyBoard(char[][] board) {
        char[][] newBoard = new char[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                newBoard[i][j] = board[i][j];
            }
        }
        return newBoard;
    }
    
    private boolean[][] copyRevealed(boolean[][] revealed) {
        boolean[][] newRevealed = new boolean[revealed.length][revealed[0].length];
        for (int i = 0; i < revealed.length; i++) {
            for (int j = 0; j < revealed[i].length; j++) {
                newRevealed[i][j] = revealed[i][j];
            }
        }
        return newRevealed;
    }
    
    private boolean[][] copyMines(boolean[][] mines) {
        boolean[][] newMines = new boolean[mines.length][mines[0].length];
        for (int i = 0; i < mines.length; i++) {
            for (int j= 0; j < mines[i].length; j++) {
                newMines[i][j] = mines[i][j];
            }
        }
        return newMines;
    }
}