package players.alphabeta;

import logic.*;
import pieces.Piece;
import players.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static java.lang.Integer.max;
import static java.lang.Integer.min;

/**
 * AlphaBeta AI agent
 */

public class AlphaBetaPlayer extends Player {
    private static final int TIMEOUT = 30000;
    private static final int inf = 1000000;
    private static final int ninf = -1000000;

    // determines which evaluation function is used
    private final boolean eval;
    private int initDepth;
    private int currentDepth = initDepth;

    private boolean timeout;
    private long start;

    private Turn bestTurnAll;
    private Turn globalBestTurn;

    private Turn[][] killerMove = new Turn[10][2];
    private int killerPos = 0;
    private final TT TT = new TT();
    private long nodeCounter=0;
    private long statistics [][] = new long [5][3];

    /**
     * Initialize alpha beta player
     * @param color color of alpha beta player
     * @param initDepth initial depth used in iterative deepening
     * @param eval boolean for evaluation function
     */
    public AlphaBetaPlayer (Color color, int initDepth, boolean eval) {
        super(color);
        this.initDepth = initDepth;
        this.eval = eval;
    }

    /**
     * Generate next turn t
     * @param b board position
     * @return turn
     */
    @Override
    public Turn getNextTurn(Board b) {
        Turn result;
        timeout = false;
        start = System.currentTimeMillis();
        nodeCounter=0;
        killerPos = 0;
        killerMove = new Turn[10][2];
        bestTurnAll=null;
        int d = 0;
        // compute hash of board
        long hash = TT.getHashBoard(b,getColor());
        // iterative deepening
        while (true) {
            if (d > 0) {
                globalBestTurn = bestTurnAll;
                System.out.println("Completed search with depth " + currentDepth + ". Best move so far: " + globalBestTurn);
                long duration = (System.currentTimeMillis() - start);
                if(currentDepth < 5) {
                    statistics[currentDepth][0] += duration;
                    statistics[currentDepth][1] += nodeCounter;
                    statistics[currentDepth][2] += 1;
                }
                bestTurnAll=null;
                nodeCounter = 0;
                killerPos = 0;
                killerMove = new Turn[10][2];
            }
            currentDepth = initDepth + d;
            int value = alphaBetaNegaMax(b, currentDepth, ninf, inf, getColor(), hash);
            if (timeout) {
                result = globalBestTurn;
                break;
            }
            if (value == inf) {
                System.out.println(bestTurnAll);
                result = bestTurnAll;
                break;
            }
            d++;
        }
        System.out.println("Statistics:"+Arrays.deepToString(statistics));
        return result;
    }

    /**
     * find value of current board position
     * @param b board position
     * @param depth depth at which board position should be searched
     * @param alpha alpha value
     * @param beta beta value
     * @param c player at the root node
     * @param hash hash value of board position
     * @return root value
     */

    public int alphaBetaNegaMax(Board b, int depth, int alpha, int beta, Color c, long hash) {
        if (System.currentTimeMillis() - start > TIMEOUT) {
            timeout = true;
            return alpha;
        }
        nodeCounter++;

        int olda = alpha;

        TTentry n = TT.retrieve(hash);
        Turn tt = null;
        // transposition tables look-up
        if (n != null) {
            if (n.getDepth() >= depth) {
                if (n.getType() == 0) {
                    if (depth == currentDepth){
                        bestTurnAll = n.getTurn();
                    }
                    return n.getValue();
                } else if (n.getType() == -1) {
                    alpha = max(alpha, n.getValue());
                } else if (n.getType() == 1) {
                    beta = min(beta, n.getValue());
                }
                if (alpha >= beta) {
                    if (depth == currentDepth){
                        bestTurnAll = n.getTurn();
                    }
                    return n.getValue();
                }
            } else {
                tt = n.getTurn();
            }
        }

        if (depth == 0 || b.getResult() != Result.NONE) {
            if (eval) {
                return evaluate(b, c);
            } else {
                return evaluate2(b, c);
            }
        }

        // move ordering
        List<Turn> sortedTurns = sortedTurns(b, c, tt, depth);

        Color c1 = Color.GOLD;
        if (c == Color.GOLD) {
            c1 = Color.SILVER;
        }

        Turn bestTurn = sortedTurns.get(0);
        int score = ninf;
        for (Turn t : sortedTurns) {
            b.makeTurn(t);
            int value = -alphaBetaNegaMax(b, depth - 1, -beta, -alpha, c1, TT.computeHash(hash, t));
            b.undoTurn(t);
            if (value > score) {
                score = value;
                bestTurn = t;
                if (score > alpha) {
                    alpha = score;
                    if (depth == currentDepth) {
                        bestTurnAll = t;
                        if (!timeout) {
                            System.out.println(t + " depth:" + depth + " score:" + score);
                        }
                    }
                }
                if (score >= beta) {
                    if (depth > 1 && depth < 10 && !timeout) {
                        killerMove[depth][killerPos] = t;
                        killerPos ^= 1;
                    }
                    break;
                }
            }
        }
        if (!timeout) {
            int type = 0;
            if (score <= olda) {
                type = 1;
            } else if (score >= beta) {
                type = -1;
            }
            TT.saveTTentry(hash, type, score, depth, bestTurn);
        }
        // fail low with TT at maximum depth
        if (depth == currentDepth && bestTurnAll == null) {
            bestTurnAll=bestTurn;
        }


        return score;

    }

    private List<Turn> sortedTurns(Board b, Color c, Turn t, int depth) {
        List<Turn> legalTurns = b.getAllPossibleTurns(c);
        // sort by capture moves first
        Collections.sort(legalTurns);
        // order by killer moves
        if (depth > 1 && depth < 10) {
            for (int pos : Arrays.asList(killerPos, killerPos ^ 1)) {
                if (killerMove[depth][pos] != null) {
                    Turn k = killerMove[depth][pos];
                    if (legalTurns.contains(k)) {
                        legalTurns.remove(k);
                        legalTurns.add(0, k);
                    }
                }
            }
        }
        // order by TT
        if (t != null) {
            if (legalTurns.contains(t)) {
                legalTurns.remove(t);
                legalTurns.add(0, t);
            }
        }
        return legalTurns;
    }

    public int evaluate(Board b, Color c) {
        Result result = b.getResult();
        if (result != Result.NONE) {
            return checkLeafNode(result, c);
        }

        int materialBalance = calculateMaterialBalance(b, c);
        // int mobility = calculateMobility(b, c);
        // int controlledSquares = calculateControlledSquares(b, c);
        int flagshipScore = calculateFlagshipScore(b, c);
        return (2 * materialBalance + flagshipScore)/2;
    }

    public int evaluate2(Board b, Color c) {
        Result result = b.getResult();
        if (result != Result.NONE) {
            return checkLeafNode(result, c);
        }
        int materialBalance = calculateMaterialBalance(b, c);
        // int mobility = calculateMobility(b, c);
        // int controlledSquares = calculateControlledSquares(b, c);
        int flagshipScore = calculateFlagshipScore(b, c);

        return (materialBalance + flagshipScore)/2; //  + mobility/10)/4;
    }

    public int checkLeafNode(Result result, Color c) {
        if (result == Result.SILVER_WON && c == Color.GOLD) {
            return ninf;
        } else if (result == Result.GOLD_WON && c == Color.GOLD) {
            return inf;
        } else if (result == Result.SILVER_WON && c == Color.SILVER) {
            return inf;
        } else if (result == Result.GOLD_WON && c == Color.SILVER) {
            return ninf;
        }
        System.err.println("AN ERROR HAS OCCURRED IN CHECKLEAFNODE");
        return -1;
    }

    public int calculateMaterialBalance(Board b, Color c) {
        int goldScore = 0;
        int silverScore = 0;

        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                if (b.getTile(i, j).isOccupied()) {
                    Piece p = b.getTile(i, j).getPiece();
                    if (p.getOwner() == Color.GOLD) {
                        goldScore += p.getScore();
                    } else {
                        silverScore += p.getScore();
                    }
                }
            }
        }
        if (c == Color.SILVER) {
            return (100 * (silverScore - goldScore)) / silverScore;
        }
        return (100 * (goldScore - silverScore)) / goldScore;
    }


    public int calculateFlagshipScore(Board b, Color c) {
        int[] coordinates = b.findFlagship();
        if (c == Color.GOLD) {
            return b.flagshipFreedom(coordinates); // 100 - b.distanceToFlagship(coordinates); // b.piecesToBoarder(coordinates);
        }
        return b.distanceToFlagship(coordinates);
    }


    public int calculateControlledSquares(Board b, Color c) {
        int silverScore = b.countAllControlledSquares(Color.SILVER);
        int goldScore = b.countAllControlledSquares(Color.GOLD);
        if (c == Color.SILVER) {
            return (100 * (silverScore - goldScore)) / silverScore;
        }
        return (100 * (goldScore - silverScore)) / goldScore;
    }

    public int calculateMobility(Board b, Color c) {
        int silverScore = b.getAllPossibleMoves(Color.SILVER).size();
        int goldScore = b.getAllPossibleMoves(Color.GOLD).size();
        if (c == Color.SILVER) {
            return (100 * (silverScore - goldScore)) / silverScore;
        }
        return (100 * (goldScore - silverScore)) / goldScore;
    }
}

