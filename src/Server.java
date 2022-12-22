
import java.io.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.table.DefaultTableModel;

public class Server extends JFrame {
    private final JTextArea bottomClientChangeLog = new JTextArea();
    private final String[] columns = { "Client", "Change", "Change on" };
    private final DefaultTableModel defaultTableModel = new DefaultTableModel(columns, 0);
    private final JTextField clientTextField = new JTextField(50);
    private final JTextField directoryTextField = new JTextField(50);

    // IO Streams
    private DataOutputStream dos;
    private DataOutputStream dosClient;

    // Main methods
    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        setTitle("Folder Watcher Socket");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 1));

        JPanel clientPanel = new JPanel();
        JLabel clientNameLabel = new JLabel("Client");
        clientPanel.add(clientNameLabel);
        clientPanel.add(clientTextField);

        JPanel directoryPanel = new JPanel();
        JLabel directoryLabel = new JLabel("Directory");
        directoryPanel.add(directoryLabel);
        directoryPanel.add(directoryTextField);

        JPanel buttonPanel = new JPanel();

        JButton watchButton = new JButton("Watch");
        buttonPanel.add(watchButton);
        watchButton.setFocusable(false);
        // create listener on the "watch" button
        watchButton.addActionListener(new watchEventActionListener());
        topPanel.add(clientPanel);
        topPanel.add(directoryPanel);
        topPanel.add(watchButton);

        // GridBagLayout gridBagLayout = new GridBagLayout();
        // GridBagConstraints gridBagConstraints = new GridBagConstraints();
        // topPanel.setLayout(gridBagLayout);
        //
        // JPanel line1Panel = new JPanel();
        // line1Panel.add(clientNameLabel);
        // line1Panel.add(clientTextField);
        //
        // gridBagConstraints.gridx = 0;
        // gridBagConstraints.gridy = 0;
        //
        // JPanel line2Panel = new JPanel();
        // JLabel directoryLabel = new JLabel("Directory");
        // line2Panel.add(directoryLabel);
        // line2Panel.add(directoryTextField);
        //
        // gridBagConstraints.gridx = 1;
        // gridBagConstraints.gridy = 1;
        //
        // JPanel line3Panel = new JPanel();
        // JButton watchButton = new JButton("Watch");
        // watchButton.setFocusable(false);
        // // create listener on the "watch" button
        // watchButton.addActionListener(new watchEventActionListener());
        //
        // line3Panel.add(watchButton);
        // gridBagConstraints.gridx = 0;
        //
        // topPanel.add((line1Panel));
        // topPanel.add(line2Panel);
        // topPanel.add(line3Panel);

        JTable table = new JTable(defaultTableModel);
        table.setEnabled(false);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(new JScrollPane(table));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(new JScrollPane(bottomClientChangeLog));
        bottomClientChangeLog.setEditable(false);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);

        try {
            // Reference: Mr. Nguyen Van Khiet - Java Networking
            @SuppressWarnings("resource")
            ServerSocket serverSocket = new ServerSocket(3200);
            bottomClientChangeLog.append("Started server: " + new Date() + "\n");
            while (true) {
                Socket s = serverSocket.accept();
                dos = new DataOutputStream(s.getOutputStream());
                dosClient = new DataOutputStream(s.getOutputStream());
                bottomClientChangeLog.append("Client ID: " + s.getPort() + " on " + new Date() + "\n");
                // ClientThreadHandling task takes s <-> socket as param
                ClientThreadHandling task = new ClientThreadHandling(s);

                // Start thread
                new Thread(task).start();
                dos.flush();
                dosClient.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // public void watchButtonHandler(ActionEvent e) {
    // try {
    // String clientName = clientTextField.getText();
    // String directoryName = directoryTextField.getText();
    // dos.writeUTF(clientName);
    // dosClient.writeUTF(directoryName);
    // String newMessage = "Watching the folder: " + directoryName + " of Client: "
    // + clientName + "!";
    // JOptionPane.showMessageDialog(null, newMessage);
    // clientTextField.setText("");
    // directoryTextField.setText("");
    // } catch (IOException ex) {
    // ex.printStackTrace();
    // }
    // }

    private class watchEventActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                String clientName = clientTextField.getText();
                String directoryName = directoryTextField.getText();
                dos.writeUTF(clientName);
                dosClient.writeUTF(directoryName);
                // String newMessage = "Watching the folder: " + directoryName + " of Client: "
                // + clientName + "!";
                // JOptionPane.showMessageDialog(null, newMessage);
                // clientTextField.setText("");
                // directoryTextField.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    class ClientThreadHandling implements Runnable {
        private final Socket s;

        public ClientThreadHandling(Socket socket) {
            this.s = socket;
        }

        // Thread run
        @Override
        public void run() {
            try {
                DataInputStream dis = new DataInputStream(s.getInputStream());
                while (true) {
                    String changeMessage = dis.readUTF();
                    String[] message = changeMessage.split("~");
                    defaultTableModel.insertRow(0, new Object[] { s.getPort(), message[0], message[1] });
                }
            } catch (IOException e) {
                bottomClientChangeLog.append(s.getPort() + " disconnected.\n");
                try {
                    // Close connection
                    s.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
