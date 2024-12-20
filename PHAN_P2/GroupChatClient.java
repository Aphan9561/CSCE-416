// Group Chat Client by Anna Phan

// Package for socket related stuff
import java.net.*;

// Package for I/O related stuff
import java.io.*;

public class GroupChatClient implements Runnable
{
  // For reading messages from the keyboard
  private BufferedReader fromUserReader;

  // For writing messages to the socket
  private PrintWriter toSockWriter;

  // The name of the client
  private static String clientName;

  // Constructor sets the reader and writer for the child thread
  public GroupChatClient(BufferedReader reader, PrintWriter writer, String name)
  {
    fromUserReader = reader;
    toSockWriter = writer;
    setClientName(name);
  }

  public String getClientName()
  {
    return clientName;
  }

  private void setClientName(String name)
  {
    clientName = name;
  }

  // The child thread starts here
  public void run()
  {
    // Read from the keyboard and write to socket
    try {

      while (true) {
        // Read a line from the user
        String line = fromUserReader.readLine();

        // If we get null, it means EOF, so quit
        if (line == null) {
          System.out.println("*** Client closing connection");
          break;
        }

        toSockWriter.println(getClientName() + ": " + line);

      }
    }
    catch (Exception e) {
      System.out.println(e);
      System.exit(1);
    }

    // End the other thread too
    System.exit(0);
  }

  public static void main(String args[])
  {
    // Client needs server's contact information and user name
    if (args.length != 3) {
      System.out.println("usage: java TwoWayAsyncMesgClient <host> <port> <name>");
      System.exit(1);
    }

    // Connect to the server at the given host and port
    Socket sock = null;
  
    try {
      sock = new Socket(args[0], Integer.parseInt(args[1]));
      System.out.println(
          "Connected to server at " + args[0] + ":" + args[1]);
    }
    catch(Exception e) {
      System.out.println(e);
      System.exit(1);
    }

    // Set up a thread to read from user and write to socket
    try {
      // Prepare to write to socket with auto flush on
      PrintWriter toSockWriter = new PrintWriter(sock.getOutputStream(), true);

      toSockWriter.println(args[2]);

      // Prepare to read from keyboard
      BufferedReader fromUserReader = new BufferedReader(new InputStreamReader(System.in));

      // Spawn a thread to read from user and write to socket
      Thread child = new Thread(new GroupChatClient(fromUserReader, toSockWriter,args[2]));
      child.start();
    }
    catch(Exception e) {
      System.out.println(e);
      System.exit(1);
    }

    // Now read from socket and display to user
    try {
      // Prepare to read from socket
      BufferedReader fromSockReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));

      // Keep doing till server is done
      while (true) {
        // Read a line from the socket
        String line = fromSockReader.readLine();
        if (line == null)
          {
            System.out.println("*** Server quit");
            break;
          }

        // Write the line to the user
        System.out.println(line);
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