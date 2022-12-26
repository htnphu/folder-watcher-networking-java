package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class Client {
  BufferedWriter writer;
  BufferedReader reader;
  Socket mySocket;

  LinkedList<FileInfo> fileInfoList = new LinkedList<FileInfo>();
  LinkedList<String> cmdQueue = new LinkedList<String>();
  LinkedList<String> msgQueue = new LinkedList<String>();

  int errorCount = 0;

  public static void main(String[] args) {
    try {
      Client client = new Client();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Client() throws UnknownHostException, IOException {
    try (
        Socket s = new Socket("localhost", 8880);
        BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        BufferedReader buffReader = new BufferedReader(new InputStreamReader(s.getInputStream()))) {

      writer = buffWriter;
      reader = buffReader;
      mySocket = s;

      Thread listen = new Thread(new ListenMsg());
      Thread watch = new Thread(new WatchFileChange());

      listen.start();
      watch.start();

      while (mySocket.isConnected() && errorCount <= 10) {
        try {
          String msg = msgQueue.pop();
          WriteMsg(msg);
        } catch (Exception e) {
          // TODO: handle exception
        }        
      }
    }

  }

  private void addFileToWatch(String path) {
    fileInfoList.add(new FileInfo(path));
  }

  private void WriteMsg(String msg) {
    try {
      this.writer.write(msg);
      this.writer.newLine();
      this.writer.flush();
    } catch (Exception e) {
      errorCount += 1;
    }
  }

  private class WatchFileChange implements Runnable {
    @Override
    public void run() {

      while (mySocket.isConnected() && errorCount <= 10) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
          errorCount += 1;
        }

        for (int i = 0; i < fileInfoList.size(); i++) {
          boolean changed = fileInfoList.get(i).hasChanged();
          if (changed) {
            msgQueue.add(fileInfoList.get(i).changeMsg());
          }
        }

      }
    }

  }

  private class ListenMsg implements Runnable {
    @Override
    public void run() {
      while (mySocket.isConnected() && errorCount <= 10) {
        try {
          String msg = reader.readLine();
          System.out.println(msg);
          addFileToWatch(msg); // Đây là do tôi giả sử mọi msg từ server là để watch file
        } catch (IOException e) {
          e.printStackTrace();
          errorCount += 1;
        }
      }
    }
  }

}
