package TicTacToeRemote;

import javax.swing.*;
import java.rmi.RemoteException;

public class MainServer {
    public static void main(String[] args) throws RemoteException {
        System.setProperty("java.rmi.server.hostname", "172.16.1.4" );
        JFrame frame = new JFrame("TicTacToe As A Service");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700  ,700);
        JButton button = new JButton("Press");
        TTTFeld feld = new TTTFeld();
        frame.getContentPane().add(feld.getPanel1()); // Adds Button to content pane of frame
        frame.setVisible(true);
    }
}
