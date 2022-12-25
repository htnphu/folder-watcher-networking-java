package Server;

import java.io.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.table.DefaultTableModel;

public class Server extends JFrame {
    //Gui
    ServerGui gui = new ServerGui();

    // IO Streams

    // Main methods
    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        
        
    }

    private class watchEventActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            
        }
    }

    
}
