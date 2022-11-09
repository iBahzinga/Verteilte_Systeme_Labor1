import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Server {
    private final int reg_port;
    private final TicTacToeAService tttservice;
    private boolean started = false;

    public void Start() throws RemoteException
    {
        if(!started)
        {
            java.rmi.registry.LocateRegistry.createRegistry(reg_port);
            started = true;
        }
        java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry.getRegistry(reg_port);
        try
        {
            registry.unbind("TicTacToeAService");
        }
        catch (Exception ignored){}
        registry.rebind("TicTacToeAService", this.tttservice);
        System.out.println("RMI Started on: " + reg_port);
    }

    public  Server(TicTacToeAService ttt){
        this(ttt, 1099);
    }
    public Server(TicTacToeAService ttt, int regPort){
        tttservice = ttt;
        this.reg_port = regPort;
    }
}
