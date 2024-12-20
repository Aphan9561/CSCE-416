// Group Chat Sever by Anna Phan

// I/O related package
import java.io.*;

// Socket related package
import java.net.*;
import java.util.ArrayList;

public class GroupChatServer implements Runnable
{
  private Socket socket;

  // For managing the different child threads
  private static ArrayList<PrintWriter> clients = new ArrayList<>();


  // Method to add a new client to the list of active clients
  private static synchronized void addClient(PrintWriter client) {
    clients.add(client);
  }

  // Method to remove a client from the list of active clients
  private static synchronized void removeClient(PrintWriter client) {
    clients.remove(client);
  }

  // Method to broadcast a message to all clients except the sender
  private static void relayMessage(PrintWriter client, String message) {
    for (PrintWriter user : clients) {
      if(!user.equals(client)) {
        user.println(message);
      }
    }
  }

  // Constructor sets the socket for the child thread
  public GroupChatServer(Socket socket)
  {
    this.socket = socket;
  }


  // The child thread starts here
  public void run()
  {
    try {

      // Prepare to read from socket
      BufferedReader fromSockReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      String clientName = fromSockReader.readLine();
      System.out.println(clientName + " has joined");

      // Prepare to write to socket with auto flush on
      PrintWriter toSockWriter = new PrintWriter(socket.getOutputStream(), true);

      // Add to the list of active clients
      addClient(toSockWriter);


      // Keep doing till user is done
      while (true) {
        // Read a line from the socket
        String line = fromSockReader.readLine();

        // If we get null, it means EOF
        if (line  == null) {
          // Tell user client quit
          System.out.println(clientName + " has disconnected");
          break;
        }

        relayMessage(toSockWriter, line);
      }
      removeClient(toSockWriter);
    }
    catch (Exception e) {
      System.out.println(e);
      System.exit(1);
    }

  }

  public static void main(String args[])
  {
    // Server needs a port to listen on
    if (args.length != 1) {
      System.out.println("usage: java TwoWayAsyncMesgServer <port>");
      System.exit(1);
    }

    // Be prepared to catch socket related exceptions
    Socket clientSock = null;
    try {
      // Create a server socket with the given port
      ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
      System.out.println("Waiting for a clients ...");

      while(true) {
        clientSock = serverSocket.accept();
        
        // Spawn a thread to read from user and write to socket
        Thread child = new Thread(new GroupChatServer(clientSock));
        child.start();
      }
      
    }
    catch(Exception e) {
      System.out.println(e);
      System.exit(1);
    }

    // End the other thread too
    System.exit(0);
    }
}