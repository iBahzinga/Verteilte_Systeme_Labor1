import javax.swing.*;
import java.rmi.RemoteException;

/**
 * Main class to start the application
 */
public class MainServer
{
    /**
     * Main method to start the application
     * @param args no args needed
     * @throws RemoteException can throw a remote exception
     */
    public static void main(String[] args) throws RemoteException
    {
        JFrame frame = new JFrame("TicTacToe As A Service");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700  ,700);
        JButton button = new JButton("Press");
        TTTFeld feld = new TTTFeld();
        frame.getContentPane().add(feld.getPanel1()); // Adds Button to content pane of frame
        frame.setVisible(true);
    }
}
