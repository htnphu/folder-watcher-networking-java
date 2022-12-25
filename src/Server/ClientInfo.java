package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;

public class ClientInfo {
  private Socket mySocket;

  private BufferedReader reader;
  private BufferedWriter writer;

  public LinkedList<String> sendQueue = new LinkedList<String>();
  public LinkedList<String> msgQueue = new LinkedList<String>();

  public ClientInfo(Socket clienSocket) throws IOException{
    this.mySocket = clienSocket;
    this.writer = new BufferedWriter(new OutputStreamWriter(this.mySocket.getOutputStream()));
    this.reader = new BufferedReader(new InputStreamReader(this.mySocket.getInputStream()));
  }

  public String waitForString() throws IOException {
    return this.reader.readLine();
  }

  public void sendString(String msg) throws IOException {
    this.writer.write(msg);
    this.writer.newLine();
    this.writer.flush();
  }
}
