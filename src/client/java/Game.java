import java.util.*;
import java.util.stream.Collectors;

/**
 * Current game
 */
public class Game
{

    /**
     * Enum with the game states
     */
    private enum States
    {
        WAIT,
        FULL
    };

    private String gameID;
    private ArrayList<String> player;
    private ArrayList<Move> moves;
    private String[][] feld;
    private static final int timeout = 30; // seconds
    private int current_player;
    private boolean done = false;
    private States current_state;
    private final int MSTOS = 1000; //MSTOS -> Milliseconds to seconds

    /**
     * Constructor
     * @param playerA player ID
     */
    public Game(String playerA)
    {
        UUID uuid = UUID.randomUUID();
        gameID = uuid.toString().substring(0,5);
        player = new ArrayList<>();
        player.add(playerA);
        feld = new String[3][3];
        current_state = States.WAIT;
        current_player = new Random().nextInt(0,1);
    }

    /**
     * Add a second player to the game
     * @param playerB Player ID for the second player
     */
    public void Join(String playerB)
    {
        this.player.add(playerB);
        this.current_state = States.FULL;
    }

    /**
     * Get the current game ID
     * @return
     */
    public String getGameID()
    {
        return gameID;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getAllMoves()
    {
        if(moves == null)
        {
            moves = new ArrayList<>();
        }
        return moves.stream().map(Move::toString).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * If a Move was made
     * @param x x coordinate
     * @param y y coordinate
     * @return the Answer of the move
     * @throws InterruptedException
     */
    public MoveAnswer makeMove(int x, int y) throws InterruptedException
    {
        String player = getCurrentPlayer();
        Move move = new Move(player,x,y);
        MoveAnswer answer =  this.DoMove(move);
        System.out.println(answer.toString());

        if (answer.is(MoveAnswer.MoveAnswerType.VALID))
        {
            if(this.CheckWin(player))
            {
                done = true;
                System.out.println("WIN");
                synchronized (this)
                {
                    this.notify();
                }
                return new MoveAnswer(MoveAnswer.MoveAnswerType.WIN);
            }
            moves.add(move);
            flipPlayers();
            long lastMove = System.currentTimeMillis();
            this.notify();
            this.wait(timeout * MSTOS);
            long delta = System.currentTimeMillis() - lastMove;

            if(done)
            {
                System.out.println("LOOSE");
                return new MoveAnswer(MoveAnswer.MoveAnswerType.LOSE);
            }

            if(delta/MSTOS > timeout)
            {
                return new MoveAnswer(MoveAnswer.MoveAnswerType.OPP_GONE);
            }
            return new MoveAnswer(moves.get(moves.size() - 1));
        }
        return answer;
    }

    /**
     * Get the player with a specific index
     * @param index index of the player
     * @return The Player ID
     */
    public String getPlayer(int index)
    {
        return this.player.get(index);
    }

    /**
     * Check if the player with the given name is currently in the game
     * @param name Name of the player
     * @return true if player is in a game or list
     */
    public boolean isInGame(String name)
    {
        return player.contains(name);
    }

    /**
     * Get the current player ID
     * @return Get the player ID
     */
    public String getCurrentPlayer()
    {
        return this.player.get(current_player);
    }

    /**
     * change the player
     */
    private void flipPlayers()
    {
        if (current_player == 0) {
            current_player = 1;
        } else {
            current_player = 0;
        }
    }

    /**
     * Check if someone wins the game
     * @param player Player ID
     * @return true if a player won the game
     */
    private boolean CheckWin(String player)
    {
        int horizontal = 0;
        int[] vertical = {0,0,0};

        for (String[] strings : feld)
        {
            for (int j = 0; j < strings.length; j++)
            {
                if (Objects.equals(strings[j], player))
                {
                    horizontal++;
                    vertical[j]++;
                }
            }
            if (horizontal >= 3)
            {
                return true;
            }
            horizontal = 0;
        }

        boolean verticalWin = Arrays.stream(vertical).mapToObj(i->i>=3).reduce((a, b)->a|b).orElse(false);
        if (verticalWin)
        {
            return true;
        }

        boolean leftToRight = true;
        boolean rightToLeft = true;

        for(int i = 0;i < feld.length;i++)
        {
            leftToRight &= Objects.equals(feld[i][i], player);
            rightToLeft &= Objects.equals(feld[i][feld.length - i - 1], player);
        }

        return  leftToRight || rightToLeft;
    }

    /**
     * do a move
     * @param move move that holds the coordinates
     * @return the answer of the move
     */
    private MoveAnswer DoMove(Move move)
    {
        if (this.feld[move.x][move.y] == null || this.feld[move.x][move.y].isEmpty())
        {
            this.feld[move.x][move.y] = move.playerID;
            return new MoveAnswer(move);
        }
        else
        {
            return new MoveAnswer(MoveAnswer.MoveAnswerType.INVALID_MOVE);
        }
    }
}