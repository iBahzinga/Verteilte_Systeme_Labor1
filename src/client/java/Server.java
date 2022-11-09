import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * The Server with the whole game logic
 */
public class Server
{
    private final int reg_port;
    private final TicTacToeAService tttservice;
    private boolean started = false;

    /**
     * Constructor with the tistactoe service
     * @param ttt
     */
    public Server(TicTacToeAService ttt){
        this(ttt, 1099);
    }

    /**
     * Copnstructor with the service and a port
     * @param ttt
     * @param regPort
     */
    public Server(TicTacToeAService ttt, int regPort)
    {
        tttservice = ttt;
        this.reg_port = regPort;
    }

    /**
     * Start the server.
     * @throws RemoteException
     */
    public void Start() throws RemoteException
    {
        //If not started
        if(!started)
        {
            java.rmi.registry.LocateRegistry.createRegistry(reg_port);
            started = true;
        }
        java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry.getRegistry(reg_port);

        // If the service was/is bound to a socket, unbind them. Can throw an exception if nothing was bound before.
        //Catch the exception and rebind the server
        try
        {
            registry.unbind("TicTacToeAService");
        }
        catch (Exception ignored){}
        registry.rebind("TicTacToeAService", this.tttservice);
        System.out.println("RMI Started on: " + reg_port);
    }
}