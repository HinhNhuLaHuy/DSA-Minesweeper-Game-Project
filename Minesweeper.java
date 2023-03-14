import java.util.Random;
import java.util.Scanner;

public class Minesweeper {
    // The Minesweeper class represents the Minesweeper game.
    // Define objects.
    private GameState gameState;
    private GameClearScreen clear = new GameClearScreen();

    // Set the Board Size. The default size is 10 x 10.
    private static final int BOARD_SIZE = 10;

    // Set Difficulty for the game.
    private static final int EASY_MINES = 10;
    private static final int MEDIUM_MINES = 20;
    private static final int HARD_MINES = 30;
    private static final int EXTREME_MINES = 40;

    // Board Symbols. These represent the characters used to display the gameboard.
    private static final char UNREVEALED = '-';
    private static final char MINE = '*';
    private static final char FLAG = 'F';
    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8' };

    // Define board, revealed tiles and mines.
    private char[][] board; // represents the game board.
    private boolean[][] revealed; // represents the state of each tile (whether it has been revealed or not).
    private boolean[][] mines; // represents the location of the mines on the board.

    // Define the number of Mines and Revealed tiles.
    private int numMines;
    private int numRevealed = 0;

    private Scanner scanner;
    private Random random;

    // The Minesweeper() constructor initializes the game by creating the board,
    // revealed, and mines arrays, and initializing the scanner and random objects.

    public Minesweeper() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        revealed = new boolean[BOARD_SIZE][BOARD_SIZE];
        mines = new boolean[BOARD_SIZE][BOARD_SIZE];
        scanner = new Scanner(System.in);
        random = new Random();
    }

    // The play() method runs the game.
    public void play() {
        // Clears the console, so that the game board can be displayed properly.
        clear.action();

        // Display prompt to select difficulty.
        System.out.println("Welcome to Minesweeper!");
        System.out.println("Select a difficulty level: ");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");
        System.out.println("4. Extreme");

        // The user selects a difficulty level and the game sets the number of mines
        // accordingly.
        int choice = scanner.nextInt();
        String ModeMesg;
        switch (choice) {
            case 1:
                numMines = EASY_MINES;
                ModeMesg = "You have selected the Easy mode, there are " + Integer.toString(EASY_MINES)
                        + " mines in total!";
                break;
            case 2:
                numMines = MEDIUM_MINES;
                ModeMesg = "You have selected the Medium mode, there are " + Integer.toString(MEDIUM_MINES)
                        + " mines in total!";
                break;
            case 3:
                numMines = HARD_MINES;
                ModeMesg = "You have selected the Hard mode, there are " + Integer.toString(HARD_MINES)
                        + " mines in total!";
                ;
                break;
            case 4:
                numMines = EXTREME_MINES;
                ModeMesg = "You have selected the Extreme mode, there are " + Integer.toString(HARD_MINES)
                        + " mines in total! This is the most difficult mode of the game, Good luck!";
                ;
                break;
            default:
                numMines = MEDIUM_MINES; // If the user inputs wrongfully.
                ModeMesg = "Since you input the wrong number, the Medium mode is selected as a default. There are "
                        + Integer.toString(MEDIUM_MINES) + " mines in total!";
                return;
        }

        // It then generates the game board by using initializeBoard() to randomly place
        // the mines on the board and calculate the numbers for each tile.
        initializeBoard();
        printBoard(false);
        System.out.println(ModeMesg);

        // It then enters a loop. Within the loop, the user's input is processed to
        // determine the action to be taken (reveal, flag/unflag a tile, or undo).

        // Each action will display an operation or error message accordingly.
        try (Scanner scanner = new Scanner(System.in)) {
            boolean gameWon = false;

            while (!gameWon) {
                System.out.println("Choose a cell (row column) or flag/unflag a cell (f row column) or undo (u):");
                String input = scanner.nextLine().trim().toLowerCase();

                if (input.startsWith("f ")) {
                    // If the user selects to flag/unflag a cell, the method checks whether the cell
                    // is already flagged or not, and then either flags or unflags the cell
                    // accordingly.
                    String[] parts = input.substring(2).split(" ");
                    int row = Integer.parseInt(parts[0]) - 1;
                    int col = Integer.parseInt(parts[1]) - 1;

                    if (board[row][col] == UNREVEALED) {
                        gameState.saveSnapshot();
                        board[row][col] = FLAG;
                        printBoard(false);
                        System.out.println("Operation: Flag successfully.");
                    } else if (board[row][col] == FLAG) {
                        gameState.saveSnapshot();
                        board[row][col] = UNREVEALED;
                        printBoard(false);
                        System.out.println("Operation: Unflag successfully.");
                    } else {
                        printBoard(false);
                        System.out.println("Error: You can only flag/unflag an unrevealed cell.");
                    }
                } else if (input.equals("u")) {
                    // If the user selects to undo the last move, the gameState object is used to
                    // restore the game to its previous state.
                    if (gameState.canUndo()) {
                        gameState.undo();
                        board = gameState.getBoard();
                        revealed = gameState.getRevealed();
                        mines = gameState.getMines();
                        printBoard(false);
                        System.out.println("Operation: Undo successfully.");
                    } else {
                        printBoard(false);
                        System.out.println("Error: No further undo can be made!");
                    }
                } else {
                    // Reveal a cell
                    String[] parts = input.split(" ");
                    int row = Integer.parseInt(parts[0]) - 1;
                    int col = Integer.parseInt(parts[1]) - 1;

                    // Checks if the selected cell reveals the mine. The game is over.
                    if (mines[row][col]) {
                        printBoard(true);
                        System.out.println("You hit a mine, game over!");
                        return;
                    }

                    if (board[row][col] != UNREVEALED) {
                        printBoard(false);
                        System.out.println("Error: Cell already revealed or flagged.");
                    } else {
                        gameState.saveSnapshot();
                        revealCell(row, col);

                        // Checks if the game has been won or not. If not, continue the while-loop.
                        if (numRevealed == BOARD_SIZE * BOARD_SIZE - numMines) {
                            gameWon = true;
                            printBoard(true);
                            System.out.println("You win!");
                            return;
                        } else {
                            printBoard(false);
                            System.out.println("Operation: Cell revealed successfully.");
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // The initializeBoard() is responsible for initializing the game board by
    // randomly placing mines and populating the remaining cells with digits
    // representing the number of adjacent mines.
    private void initializeBoard() {
        clear.action();
        // Clear board
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = UNREVEALED;
                revealed[row][col] = false;
                mines[row][col] = false;
            }
        }

        // Add mines randomly and generate digits of adjacent mines.
        int numAdded = 0;
        while (numAdded < numMines) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);

            if (!mines[row][col]) {
                mines[row][col] = true;
                numAdded++;
            }
        }
        gameState = new GameState(board, revealed, mines);
    }

    // The revealCell() method is responsible for revealing a cell on the game
    // board. This method is called when the user chooses to reveal a cell by
    // providing its row and column number as input.
    private void revealCell(int row, int col) {
        revealed[row][col] = true;
        numRevealed++;

        // Reveal mine, if that cell is a mine. Then, the game is over.
        if (mines[row][col]) {
            board[row][col] = MINE;
            return;
        }

        // Reveal digit of adjacent mines, if that cell is not a mine. Return a
        // whitespace if there are no adjacent mines and automatically reveal adjacent
        // cells by using revealAdjacentCells() method.
        int numAdjacentMines = countAdjacentMines(row, col);
        if (numAdjacentMines == 0) {
            board[row][col] = ' ';
            revealAdjacentCells(row, col);
        } else {
            board[row][col] = DIGITS[numAdjacentMines];
        }
    }

    // The revealAdjacentCells() method reveals all adjacent cells from the cell
    // that do not have any adjacent mine. Then, it checks whether the number of
    // mines adjacent to the specified cell is zero. If it is, the method
    // recursively calls itself on all adjacent cells (up to eight).
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

                // This effectively reveals all adjacent cells that do not have mines in them.
            }
        }
    }

    // The countAdjacentMines() method returns the number of mines adjacent to a
    // specified cell.
    private int countAdjacentMines(int row, int col) {
        int count = 0; // The method checks each adjacent cell whether it has mines or not.

        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i < 0 || i >= BOARD_SIZE || j < 0 || j >= BOARD_SIZE) {
                    continue; // If not, check another cell.
                }

                if (mines[i][j]) {
                    count++; // If it has, count the number of mines up.
                }
            }
        }
        return count; // It then returns the final count.
    }

    // The printBoard() method in the Minesweeper class prints the current state of
    // the game board to the console. It takes a boolean parameter revealMines, to
    // reveal the locations of the mines on the board or not.
    private void printBoard(boolean revealAll) {
        clear.action();

        // Printing the column number
        System.out.print("   ");
        for (int col = 0; col < BOARD_SIZE; col++) {
            System.out.print((col + 1) + " ");
        }
        System.out.println();

        // Printing each row
        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.printf("%-3d", (row + 1)); // Printing the row number

            // The method loops through each cell on the board, checks if the cell has been
            // revealed or not.Then prints the appropriate symbol for that cell.

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
}