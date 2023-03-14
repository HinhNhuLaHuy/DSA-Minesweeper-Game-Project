public class GameClearScreen {
    public void action() {
        // The GameClearScreen class contains a single method called action().
        // This method clears the terminal screen by printing out ANSI escape codes.
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
