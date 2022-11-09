import java.awt.event.MouseEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The client is a player. The player can join a game
 */
public class Client {
    private TicTacToeAService service;
    private String opponent;
    private String gameID;
    private String nextPlayer;
    private String clientID;
    private List<Move> moves = new LinkedList<>();

    /**
     * Connect to the server
     * @param hostname Name of the client
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void connect(String hostname) throws RemoteException, NotBoundException
    {
        int regPort = 1099;
        System.out.println("Connecting to... :" + hostname);
        Registry registry = LocateRegistry.getRegistry(hostname, regPort);
        this.service = (TicTacToeAService) registry.lookup("TicTacToeAService");
        System.out.println("Connected to: " + hostname);
    }

    /**
     * resum a previously started agme
     * @param game GameID
     * @param name Name
     * @return true if the game was found
     * @throws RemoteException
     */
    public boolean resumeGame(String game, String name) throws RemoteException
    {
        this.gameID = game;
        this.clientID = name;
        moves = service.fullUpdate(gameID).stream().map(Move::new).toList();
        moves = new LinkedList<>(moves);

        String playerA = moves.get(0).playerID;
        String playerB = moves.get(1).playerID;
        //Check if the name match with a player in the game with the given gameID
        if((!playerA.equals(name)) && (!playerB.equals(name)))
        {
            // If the player name was wrong
            System.err.println("Wrong Player Name!");
            return false;
        }

        //Set opponent based on your name
        if(playerA.equals(name))
        {
            opponent = playerB;
        }
        else
        {
            opponent = playerA;
        }

        //Check if we join as a waiter or as a mover
        if(moves.get(moves.size() -1).playerID.equals(name))
        {
            System.out.println("Rejoined as waiter");
            waitAndUpdateRemote();
        }
        else
        {
            System.out.println("Rejoined as mover");
        }

        return true;
    }

    /**
     * start a new game or join a started new game as second player
     * @param _clientID cliend ID
     * @return true if the clinet can join the game
     * @throws RemoteException
     */
    public boolean findGame(String _clientID) throws RemoteException
    {
        clientID = _clientID;
        HashMap<String, String> hmap = service.findGame(clientID);
        //If we start a game and no opponent was found in the last 30s
        if(hmap.get("First Move").equals("no_opponent_found"))
        {
            System.out.println("Found no Opponent in 30 sec.");
            return false;
        }

        setMapValues(hmap);

        //Wait for the next move
        if(Objects.equals(nextPlayer, "opponent_move"))
        {
            waitAndUpdateRemote();
        }

        return true;
    }

    /**
     * get the current game ID
     * @return
     */
    public String getGameID() {
        return gameID;
    }

    /**
     * get all moves in the current game
     * @return
     */
    public List<Move> getMoves() {
        return moves;
    }

    /**
     * Add a new move
     * @param x x coordinate
     * @param y y coordinate
     */
    public void addMove(int x, int y){
        moves.add(new Move(this.clientID, x, y));
    }

    /**
     * make a new move with the given coordinates
     * @param x x coordinate
     * @param y y coordinates
     * @return A string based on the game step
     * @throws RemoteException
     */
    public String makeMove(int x, int y) throws RemoteException
    {
        MoveAnswer answer = new MoveAnswer(service.makeMove(x,y,gameID), opponent);
        if (answer.is(MoveAnswer.MoveAnswerType.VALID))
        {
            moves.add(answer.getMove());
            return "Your Turn";
        }
        if (answer.is(MoveAnswer.MoveAnswerType.WIN))
        {
            return  "You Win";
        }
        if (answer.is(MoveAnswer.MoveAnswerType.LOSE))
        {
            return "You Lose";
        }
        if (answer.is(MoveAnswer.MoveAnswerType.INVALID_MOVE))
        {
            moves.remove(moves.size() - 1);
            return "Invalid turn";
        }
        if (answer.is(MoveAnswer.MoveAnswerType.OPP_GONE))
        {
            this.clientID = null;
            this.opponent = null;
            this.gameID = null;
            this.moves = new LinkedList<>();
            this.nextPlayer = null;
            return "Timeout :(";
        }
        return "what happened?";
    }

    /**
     * set the basic game settings
     */
    private void setMapValues(HashMap<String, String> hmap)
    {
        gameID = hmap.get("Game ID");
        opponent = hmap.get("Opponent Name");
        nextPlayer = hmap.get("First Move");

        System.out.println("Found Game!");
        System.out.println(gameID);
        System.out.println(opponent);
        System.out.println(nextPlayer);

    }

    /**
     * Wait for opponent and update
     * @throws RemoteException
     */
    private void waitAndUpdateRemote() throws RemoteException
    {
        while (service.fullUpdate(gameID).size() <= moves.size())
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
        this.moves = service.fullUpdate(gameID).stream().map(Move::new).collect(Collectors.toList());
    }
}
