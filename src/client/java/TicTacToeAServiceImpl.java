import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Serverimplementation with the logic
 */
public class TicTacToeAServiceImpl extends UnicastRemoteObject implements TicTacToeAService
{

    private Game nextGame;
    private HashMap<String, Game> games;
    private final int MSTOS = 1000; //MSTOS -> Milliseconds to seconds

    /**
     * Constructor
     * @throws RemoteException
     */
    public TicTacToeAServiceImpl() throws RemoteException
    {
        super();
        nextGame = null;
        games = new HashMap<>();
    }

    /**
     * Start a game and assign player to a game
     * @param clientName Name of the client
     * @return the hashmap with the game ID, name of the opponent and the move
     * @throws RemoteException
     */
    @Override
    public HashMap<String, String> findGame(String clientName) throws RemoteException
    {
        HashMap<String, String> hmap = new HashMap<>();
        if (nextGame == null)
        {
            nextGame = new Game(clientName);
            try
            {
                long start = System.currentTimeMillis();
                synchronized (nextGame)
                {
                    nextGame.wait(30 * MSTOS);
                }
                long delta = System.currentTimeMillis() - start;

                if (delta >= 29 * MSTOS)
                {
                    hmap.put("Game ID", "");
                    hmap.put("Opponent Name", "");
                    hmap.put("First Move", getFirstMoveStr(""));
                    nextGame = null;
                }
                else
                {
                    hmap.put("Game ID", nextGame.getGameID());
                    hmap.put("Opponent Name", nextGame.getPlayer(1));
                    hmap.put("First Move", getFirstMoveStr(clientName));
                    //nextGame = null;
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        else
        {
            if(nextGame.isInGame(clientName))
            {
                hmap.put("Game ID", nextGame.getGameID());
                hmap.put("Opponent Name", nextGame.getPlayer(0));
                hmap.put("First Move", getFirstMoveStr(clientName));
            }
            else
            {
                System.out.println("Joining " + clientName);
                nextGame.Join(clientName);
                hmap.put("Game ID", nextGame.getGameID());
                hmap.put("Opponent Name", nextGame.getPlayer(0));
                hmap.put("First Move", getFirstMoveStr(clientName));
                games.put(nextGame.getGameID(), nextGame);
                synchronized (nextGame)
                {
                    nextGame.notify();
                }
            }
        }
        return hmap;
    }

    @Override
    public String makeMove(int x, int y, String gameId) throws RemoteException
    {
        System.out.println("Move");
        if (games.get(gameId) != null)
        {
            try
            {
                synchronized (games.get(gameId))
                {
                    return games.get(gameId).makeMove(x, y).toString();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        return MoveAnswer.MoveAnswerType.NO_EXIST.toString();
    }

    @Override
    public ArrayList<String> fullUpdate(String gameId) throws RemoteException
    {
        Game currentGame = games.get(gameId);
        if (currentGame != null)
        {
            return currentGame.getAllMoves();
        }
        return null;
    }

    /**
     * Get the game move
     * @param name Name of the player
     * @return
     */
    private String getFirstMoveStr(String name)
    {
        String[] answers = {"your turn", "wait for turn", "there is no opponent"};
        if(Objects.equals(name, ""))
        {
            return answers[2];
        }
        if(nextGame.getCurrentPlayer().equals(name))
        {
            return answers[0];
        }
        else
        {
            return answers[1];
        }
    }
}
