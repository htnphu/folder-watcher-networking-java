package Server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public abstract class ClientHandelThread implements Runnable {
  protected String myName;
  protected DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
  private int errorCout = 0;

  public abstract ClientInfo getClientInfo();
  public abstract void Log(String msg);
  public abstract void Disconnect();

  public ClientHandelThread(String name){
    super();
    this.myName = name;
  }

  @Override
  public void run() {

    Thread listenThread = new Thread(new ListenMsg());
    Thread handleThread = new Thread(new HandleMsg());

    listenThread.start();
    handleThread.start();

    while (getClientInfo().mySocket.isConnected() && errorCout <= 10) {
      if(getClientInfo().sendQueue.size() <= 0){
        continue;
      }

      String cmd = getClientInfo().sendQueue.pop();
      try {
        getClientInfo().sendString(cmd);
      } catch (Exception e) {
        errorCout +=1;
      }
    }
    Disconnect();    
  }
  
  private class HandleMsg implements Runnable{
    @Override
    public void run() {
      while (getClientInfo().mySocket.isConnected() && errorCout <= 10) {
        if(getClientInfo().msgQueue.size() <= 0){
          continue;
        }
        String msg = getClientInfo().msgQueue.pop();
        Log(msg);
      }
    }

  }

  private class ListenMsg implements Runnable{
    @Override
    public void run() {
      while (getClientInfo().mySocket.isConnected() && errorCout <= 10) {
        try {
          String msg = getClientInfo().waitForString();
          getClientInfo().msgQueue.add(msg);
        } catch (Exception e) {
          errorCout += 1;
        }
      }
    }    
  }


}
