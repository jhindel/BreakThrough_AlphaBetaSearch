package logic;

import players.Player;
import ui.GamePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class controls gameplay and merges logic and GUI activities
 */
public class Breakthru {
    // total processing time of AI player
    public static long timeAI;
    private final Board board;
    private final Player AI;
    private final GamePresenter presenter;
    private final List<Player> players;
    private boolean previousPlayerHadNoMove = false;

    /**
     * Initialize game
     * @param board board which represents current position
     * @param gold gold player
     * @param silver silver player
     * @param AI player who represents AI (whose moving time is measured)
     * @param presenter player to which the GUI adapts (i.e. show win or loss for this player)
     */
    public Breakthru(Board board, Player gold, Player silver, Player AI, GamePresenter presenter) {
        this.board = board;
        this.AI=AI;
        this.presenter = presenter;
        players = new ArrayList<>();
        players.add(gold);
        players.add(silver);
    }

    /**
     * Starts one game.
     */
    public void play() {
        presenter.presentBoard(board.clone());
        while (true) {
            Result result = playRound();
            if (result != Result.NONE) {
                presenter.presentResult(result);
                break;
            }
        }
    }

    /**
     * Play ond round in which each player makes a turn
     * @return result of round (game position: WIN, LOSS or NONE)
     */
    private Result playRound() {
        for (Player p : players) {
            System.out.println(p.getColor());
            // start measure time AI player
            long start = System.currentTimeMillis();
            Turn turn = p.getNextTurn(board);
            System.out.println(turn);
            // if both players didn't move -> declare it a draw
            if (turn == null) {
                if (previousPlayerHadNoMove) {
                    return Result.TIE;
                }
                previousPlayerHadNoMove = true;
                continue;
            }
            previousPlayerHadNoMove = false;

            board.makeTurn(turn);
            presenter.presentBoard(board.clone());

            Result result = board.getResult();
            // end measure time AI player
            if (p==AI){
                timeAI += System.currentTimeMillis()-start;
            }
            if (result != Result.NONE) {
                return result;
            }
        }
        return Result.NONE;
    }
}
