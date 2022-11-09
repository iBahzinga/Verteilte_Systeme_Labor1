import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class TTTFeld {
    private JPanel panel1;
    private JButton field00;
    private JButton field20;
    private JButton field10;
    private JButton field01;
    private JButton field11;
    private JButton field21;
    private JButton field02;
    private JButton field12;
    private JButton field22;
    private JTextField ipBox;
    private JButton connectButton;
    private JLabel statusLabel;
    private JButton Start;
    private JLabel serverStatLabel;
    private JTextField clientID;
    private JTextField gameIDVal;

    /**
     * Tic tac toe field
     *
     * @throws RemoteException
     */
    public TTTFeld() throws RemoteException
    {
        Start.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    svr.Start();
                }
                catch (RemoteException ex)
                {
                    throw new RuntimeException(ex);
                }
                serverStatLabel.setText("Server started");
            }
        });
        connectButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String host = ipBox.getText();
                try
                {
                    connectAndStart(host);
                }
                catch (RemoteException ex)
                {
                    throw new RuntimeException(ex);
                }
                catch (NotBoundException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        });
        ActionListener listener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (int x = 0; x < field.length; x++)
                {
                    for (int y = 0; y < field[x].length; y++)
                    {
                        if(field[x][y] == e.getSource())
                        {
                            statusLabel.setText("Please wait");
                            int finalX = x;
                            int finalY = y;
                            clt.addMove(x,y);
                            updateField(clt.getMoves());
                            SwingUtilities.invokeLater(() ->
                            {
                                String status = null;
                                try
                                {
                                    status = clt.makeMove(finalX, finalY);
                                }
                                catch (RemoteException ex)
                                {
                                    throw new RuntimeException(ex);
                                }
                                statusLabel.setText(status);
                                updateField(clt.getMoves());
                            });
                        }
                    }
                }
            }
        };
        field00.addActionListener(listener);
        field20.addActionListener(listener);
        field10.addActionListener(listener);
        field01.addActionListener(listener);
        field11.addActionListener(listener);
        field21.addActionListener(listener);
        field02.addActionListener(listener);
        field12.addActionListener(listener);
        field22.addActionListener(listener);
    }

    /**
     * Connect to the server and start the game if two player exist
     * @param hostname hostname of the client
     * @throws NotBoundException
     * @throws RemoteException
     */
    private void connectAndStart(String hostname) throws NotBoundException, RemoteException
    {
        SwingUtilities.invokeLater(() ->
        {
            setStatusLabel(Labels.labelConnecting());
            try
            {
                clt.connect(hostname);
            }
            catch (RemoteException e)
            {
                throw new RuntimeException(e);
            }
            catch (NotBoundException e)
            {
                throw new RuntimeException(e);
            }
            if( gameIDVal.getText().equals(""))
            {
                setStatusLabel(Labels.labelFindGame());
                SwingUtilities.invokeLater(() ->
                {
                    try
                    {
                        if (clt.findGame(clientID.getText()))
                        {
                            gameIDVal.setEditable(false);
                            gameIDVal.setText(clt.getGameID());
                            updateField(clt.getMoves());
                            setStatusLabel(Labels.labelYourMove());
                        }
                        else
                        {
                            setStatusLabel(Labels.labelNoGameFound());
                        }
                    }
                    catch (RemoteException e)
                    {
                        throw new RuntimeException(e);
                    }
                });
            }
            else
            {

                setStatusLabel(Labels.labelRejoin());
                SwingUtilities.invokeLater(() ->
                {
                    try
                    {
                        gameIDVal.setEditable(false);
                        if (clt.resumeGame(gameIDVal.getText(), clientID.getText()))
                        {
                            updateField(clt.getMoves());
                            setStatusLabel(Labels.labelYourMove());
                        }
                        else
                        {
                            setStatusLabel(Labels.labelNoGameFound());
                            gameIDVal.setEditable(true);
                        }
                    }
                    catch (RemoteException e)
                    {
                        throw new RuntimeException(e);
                    }
                });
            }


        });
    }

    /**
     * Method to get the panel and start the application
     * @return
     */
    public JPanel getPanel1()
    {
        return panel1;
    }

    private final JButton[][] field = {
            {this.field00, this.field01, this.field02},
            {this.field10, this.field11, this.field12},
            {this.field20, this.field21, this.field22}
    };

    /**
     * get the coordinates and set the specific field in the toc tac toe field
     * @param x coordinate x
     * @param y coordinate y
     * @param value player
     */
    public void setSpecificField(int x, int y, String value){
        field[x][y].setText(value);
    }

    /**
     * set a field in the toc tac toe field
     * @param move Move with x and y coordinates
     */
    public void setField(Move move)
    {
        if (move.playerID == null)
        {
            setSpecificField(move.x, move.y, nope());
        }
        else
        {
            setSpecificField(move.x, move.y, "" + move.playerID + "");
        }
    }

    /**
     * Message in the tic tac toe fields, if the client is not connected to the server.
     * @return Nope
     */
    public String nope(){ return "Nope"; }

    /**
     * Update a specific field
     * @param moves
     */
    public void updateField(List<Move> moves)
    {
        for (JButton[] jButtons : field)
        {
            for (JButton jButton : jButtons)
            {
                jButton.setText(" ");
            }
        }
        moves.forEach(this::setField);
    }

    /**
     * Server with the logic
     */
    private final Server svr = new Server(new TicTacToeAServiceImpl());

    /**
     * Client that plays the game
     */
    private final Client clt = new Client();

    /**
     * Set the new label in the application to inform the user
     * @param newLabel Text of the new label
     */
    private void setStatusLabel(String newLabel)
    {
        statusLabel.setText(newLabel);
    }
}
