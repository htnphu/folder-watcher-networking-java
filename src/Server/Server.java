package Server;

import java.io.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.table.DefaultTableModel;

public class Server {
  ServerGui gui = new ServerGui();
  HashMap<String,ClientInfo> clientMap = new HashMap<String,ClientInfo>();

  public static void main(String[] args) {
    new Server();
  }

  public Server() {
    gui.SetWatchBtn_ActionListener(new watchEventActionListener());

    try (ServerSocket server = new ServerSocket(8880)) {
      do {
        Socket client = server.accept();
        String clientName = String.format("Client: %d",client.getPort());

        this.gui.addClientToComboBox(clientName);

        clientMap.put(clientName, new ClientInfo(client));

        Thread clientThread = new Thread(new HandlerClient(clientName));
        clientThread.start();

        gui.addRow(clientName, "Has connected", "");
      } while (true);
    } catch (Exception e) {}
  }

  private class HandlerClient extends ClientHandelThread{
    public HandlerClient(String name) {
      super(name);
    }

    @Override
    public ClientInfo getClientInfo() {
      return clientMap.get(this.myName);
    }

    @Override
    public void Log(String msg) {
      String[] data = msg.split("&&");
      if(data.length == 2){
        Date changeDate = new Date(Long.parseLong(data[1]));
        String dateString = df.format(changeDate);
        System.out.println(dateString);
        gui.addRow(this.myName, data[0] ,dateString);
        return;
      }
      gui.addRow(this.myName, "Say something","");
    }

    @Override
    public void Disconnect() {
      gui.removeClientToComboBox(myName);
      clientMap.remove(this.myName);

    }

  }

  private class watchEventActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      clientMap.get(gui.getCurrentClient()).sendQueue.add(gui.getPath());
    }
  }

}
