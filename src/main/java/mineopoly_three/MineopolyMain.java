package mineopoly_three;

import mineopoly_three.game.GameEngine;
import mineopoly_three.graphics.UserInterface;
import mineopoly_three.replay.Replay;
import mineopoly_three.replay.ReplayIO;
import mineopoly_three.strategy.*;

import javax.swing.*;

public class MineopolyMain {
    private static final int DEFAULT_BOARD_SIZE = 20;
    private static final int PREFERRED_GUI_WIDTH = 750; // Bump this up or down according to your screen size
    private static final boolean TEST_STRATEGY_WIN_PERCENT = false; // Change to true to test your win percent

    // Use this if you want to view a past match replay
    private static final String savedReplayFilePath = null;
    // Use this to save a replay of the current match
    private static final String replayOutputFilePath = null;

    public static void main(String[] args) {
        if (TEST_STRATEGY_WIN_PERCENT) {
            MinePlayerStrategy yourStrategy = new RandomStrategy(); // TODO: Replace this with your strategy
            int[] assignmentBoardSizes = new int[]{14, 20, 26, 32};

            for (int testBoardSize : assignmentBoardSizes) {
                double strategyWinPercent = getStrategyWinPercent(yourStrategy, testBoardSize);
                System.out.println("(Board size, win percent): (" + testBoardSize + ", " + strategyWinPercent + ")");
            }
        } else {
            // Not testing the win percent, show the game instead
            playGameOrReplay();
        }
    }

    private static void playGameOrReplay() {
        final GameEngine gameEngine;
        if (savedReplayFilePath == null) {
            // Not viewing a replay, play a game with a GUI instead
            MinePlayerStrategy redStrategy = new RandomStrategy(); // TODO: Replace this with your strategy
            MinePlayerStrategy blueStrategy = new RandomStrategy();
            long randomSeed = System.currentTimeMillis();
            gameEngine = new GameEngine(DEFAULT_BOARD_SIZE, redStrategy, blueStrategy, randomSeed);
            gameEngine.setGuiEnabled(true);
        } else {
            // Showing a replay
            gameEngine = ReplayIO.setupEngineForReplay(savedReplayFilePath);
            if (gameEngine == null) {
                return;
            }
        }

        if (gameEngine.isGuiEnabled()) {
            // 500 is around the minimum value that keeps everything on screen
            assert PREFERRED_GUI_WIDTH >= 500;
            // Run the GUI code on a separate Thread (The event dispatch thread)
            SwingUtilities.invokeLater(() -> UserInterface.instantiateGUI(gameEngine, PREFERRED_GUI_WIDTH));
        }

        gameEngine.runGame();

        // Record the replay if the output path isn't null and we aren't already watching a replay
        if (savedReplayFilePath == null && replayOutputFilePath != null) {
            Replay gameReplay = gameEngine.getReplay();
            ReplayIO.writeReplayToFile(gameReplay, replayOutputFilePath);
        }
    }

    private static double getStrategyWinPercent(MinePlayerStrategy yourStrategy, int boardSize) {
        final int numTotalRounds = 1000;
        int numRoundsWonByMinScore = 0;
        MinePlayerStrategy randomStrategy = new RandomStrategy();

        /*
         * TODO: Fill in the code here to play 1000 games and calculate your strategy's win percent
         * Note that you should only count a win if your strategy scores enough points to win
         *  by the minimum score. Do not count wins as scoring more than RandomStrategy() (which always scores 0)
         */

        throw new RuntimeException("Testing win percent not yet implemented"); // TODO: Delete this line
        //return ((double) numRoundsWonByMinScore) / numTotalRounds; // TODO: Uncomment this line
    }
}
