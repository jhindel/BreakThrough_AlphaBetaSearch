import logic.Board;
import logic.Breakthru;
import logic.Color;
import players.Player;
import players.RandomPlayer;
import players.HumanPlayer;
import players.alphabeta.AlphaBetaPlayer;
import ui.GamePresenter;
import ui.TextPresenter;
import ui.gui.FramePresenter;

public class Main {
    // lock required to synchronize GUI with HumanPlayer logic activities
    private final static Object lock = new Object();
    private final static Color humanPlayer = Color.SILVER; //Color.SILVER; //Color.GOLD; // Color.SILVER;
    private final static Color randomPlayer= null; // Color.SILVER; // Color.GOLD;
    private final static String gamePresenter = "GUI";

    public static void main(String[] args) {
        Board board = new Board();

        GamePresenter boardPresenter = new TextPresenter();
        if (gamePresenter == "GUI") {
            //represents the side of human player, if no human player -> shows result for loosing side
            boardPresenter = new FramePresenter(humanPlayer, lock);
        }

        Player gold = new AlphaBetaPlayer(Color.GOLD, 1, false);
        Player silver = new AlphaBetaPlayer(Color.SILVER, 1, true);
        Player measuredPlayer = gold;

        if (randomPlayer != null && (randomPlayer == humanPlayer)){
            System.out.println("Human and random player can't play with the same color");
            System.exit(1);
        }

        if (randomPlayer == Color.SILVER) {
            silver = new RandomPlayer(Color.SILVER);
        }

        if (randomPlayer == Color.GOLD) {
            gold = new RandomPlayer(Color.GOLD);
            measuredPlayer = silver;
        }

        if (humanPlayer == Color.GOLD) {
            gold = new HumanPlayer(Color.GOLD, boardPresenter, lock);
            measuredPlayer = silver;
        }

        if (humanPlayer == Color.SILVER) {
            silver = new HumanPlayer(Color.SILVER, boardPresenter, lock);
            measuredPlayer=gold;
        }

        Breakthru game = new Breakthru(board, gold, silver, measuredPlayer, boardPresenter);

        game.play();
    }
}

