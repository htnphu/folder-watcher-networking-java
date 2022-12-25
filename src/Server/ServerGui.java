package Server;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ServerGui {
  private final JFrame frame = new JFrame("Folder Watcher Socket");
  private final String[] columns = { "Client", "Change", "Change on" };
  private final JTextField clientTextField = new JTextField(50);
  private final JTextField directoryTextField = new JTextField(50);
  private final DefaultTableModel defaultTableModel = new DefaultTableModel(columns, 0);

  private final JButton watchButton = new JButton("Watch");
  public ServerGui() {
    JPanel topPanel = new JPanel(new java.awt.GridLayout(3, 1));

    JPanel clientPanel = new JPanel();
    JLabel clientNameLabel = new JLabel("Client");
    clientPanel.add(clientNameLabel);
    clientPanel.add(clientTextField);

    JPanel directoryPanel = new JPanel();
    JLabel directoryLabel = new JLabel("Directory");
    directoryPanel.add(directoryLabel);
    directoryPanel.add(directoryTextField);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(watchButton);
    watchButton.setFocusable(false);

    topPanel.add(clientPanel);
    topPanel.add(directoryPanel);
    topPanel.add(watchButton);

    JTable table = new JTable(defaultTableModel);
    table.setEnabled(false);

    JPanel centerPanel = new JPanel(new java.awt.BorderLayout());
    centerPanel.add(new JScrollPane(table));

    this.frame.add(topPanel, BorderLayout.PAGE_START);
    this.frame.add(centerPanel, BorderLayout.CENTER);

    this.frame.setSize(800,600);
    this.frame.setVisible(true);
  }

  public void SetWatchBtn_ActionListener(java.awt.event.ActionListener listener){
    this.watchButton.addActionListener(listener);
  }
}
