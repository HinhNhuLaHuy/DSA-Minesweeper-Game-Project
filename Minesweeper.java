import java.util.Random;
import java.util.Scanner;

public class Minesweeper {

    private static final int BOARD_SIZE = 10;
    private static final int EASY_MINES = 10;
    private static final int MEDIUM_MINES = 20;
    private static final int HARD_MINES = 30;
    private static final int EXTREME_MINES = 40;
    private static final char UNREVEALED = '-';
    private static final char MINE = '*';
    private static final char FLAG = 'F';
    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8' };

    private char[][] board;
    private boolean[][] revealed;
    private boolean[][] mines;

    private int numMines;
    private int numRevealed;

    private Scanner scanner;
    private Random random;

    public Minesweeper() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        revealed = new boolean[BOARD_SIZE][BOARD_SIZE];
        mines = new boolean[BOARD_SIZE][BOARD_SIZE];
        scanner = new Scanner(System.in);
        random = new Random();
    }

    public void play() {
        System.out.println("Welcome to Minesweeper!");
        System.out.println("Select a difficulty level: ");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");
        System.out.println("4. Extreme");
    
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                numMines = EASY_MINES;
                break;
            case 2:
                numMines = MEDIUM_MINES;
                break;
            case 3:
                numMines = HARD_MINES;
                break;
            case 4:
                numMines = EXTREME_MINES;
                break;
            default:
                System.out.println("Invalid choice. Exiting...");
                return;
        }
    
        initializeBoard();
        GameState gameState = new GameState(board, revealed, mines, numMines, numRevealed);
    
        try (Scanner scanner = new Scanner(System.in)) {
            boolean gameWon = false;
    
            while (!gameWon) {
                printBoard(false);
                gameState.saveState();

                System.out.println("Choose a cell (row column) or flag/unflag a cell (f row column) or undo (u):");
                String input = scanner.nextLine().trim().toLowerCase();
    
                if (input.startsWith("f ")) {
                    // Flag/unflag a cell
                    String[] parts = input.substring(2).split(" ");
                    int row = Integer.parseInt(parts[0]) - 1;
                    int col = Integer.parseInt(parts[1]) - 1;
    
                    if (board[row][col] == UNREVEALED) {
                        board[row][col] = FLAG;
                    } else if (board[row][col] == FLAG) {
                        board[row][col] = UNREVEALED;
                    } else {
                        System.out.println("You can only flag/unflag an unrevealed cell.");
                    }
                } else if (input.startsWith("u")) {
                    // Undo the previous action
                    if (gameState.canUndo()) {
                        gameState.undo();
                        this.board = gameState.getBoard();
                        this.revealed = gameState.getRevealed();
                        this.mines = gameState.getMines();
                        this.numMines = gameState.getNumMines();
                        this.numRevealed = gameState.getNumRevealed();
                    } else {
                        System.out.println("Cannot undo further.");
                        continue;
                    }
                } else {
                    // Reveal a cell
                    String[] parts = input.split(" ");
                    int row = Integer.parseInt(parts[0]) - 1;
                    int col = Integer.parseInt(parts[1]) - 1;
    
                    if (board[row][col] == FLAG) {
                        System.out.println("You cannot reveal a flagged cell.");
                    } else if (mines[row][col]) {
                        System.out.println("Game over, you hit a mine!");
                        printBoard(true);
                        break;
                    } else {
                        revealCell(row, col);
                    }
                }
    
                if (numRevealed == BOARD_SIZE * BOARD_SIZE - numMines) {
                    gameWon = true;
                    System.out.println("Congratulations, you win!");
                    printBoard(true);
                }
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    private void initializeBoard() {
        // Clear board
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = UNREVEALED;
                revealed[row][col] = false;
                mines[row][col] = false;
            }
        }

        // Add mines
        int numAdded = 0;
        while (numAdded < numMines) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);

            if (!mines[row][col]) {
                mines[row][col] = true;
                numAdded++;
            }
        }
    }

    private void revealCell(int row, int col) {
        revealed[row][col] = true;
        numRevealed++;

        if (mines[row][col]) {
            board[row][col] = MINE;
            return;
        }

        int numAdjacentMines = countAdjacentMines(row, col);
        if (numAdjacentMines == 0) {
            board[row][col] = ' ';
            revealAdjacentCells(row, col);
        } else {
            board[row][col] = DIGITS[numAdjacentMines];
        }
    }

    private void revealAdjacentCells(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i < 0 || i >= BOARD_SIZE || j < 0 || j >= BOARD_SIZE) {
                    continue;
                }

                if (revealed[i][j] || mines[i][j]) {
                    continue;
                }

                revealCell(i, j);
            }
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i < 0 || i >= BOARD_SIZE || j < 0 || j >= BOARD_SIZE) {
                    continue;
                }

                if (mines[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    private void printBoard(boolean revealAll) {
        System.out.print("   ");
        for (int col = 0; col < BOARD_SIZE; col++) {
            System.out.print((col + 1) + " ");
        }
        System.out.println();
        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.printf("%-3d", (row + 1));
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (mines[row][col] && (revealAll || revealed[row][col])) {
                    System.out.print(MINE);
                } else if (board[row][col] == FLAG) {
                    System.out.print(FLAG);
                } else if (revealed[row][col]) {
                    System.out.print(board[row][col]);
                } else {
                    System.out.print(UNREVEALED);
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Minesweeper game = new Minesweeper();
        game.play();
    }
}