/*
 * Implementation of the two-way message client in java
 */

// Package for I/O related stuff
import java.io.*;

// Package for socket related stuff
import java.net.*;

/*
 * This class does all the client's job
 * It connects to the server at the given address
 * and sends messages typed by the user to the server
 */
public class TwoWayMesgClient {
	/*
	 * The client program starts from here
	 */
	public static void main(String args[])
	{
		// Client needs server's contact information
		if (args.length != 3) {
			System.out.println("usage: java TwoWayMesgClient <localhost> <server port> <client name>");
			System.exit(1);
		}

		// Get server's whereabouts
		String serverName = args[0];
		int serverPort = Integer.parseInt(args[1]);
    String name = args[2];

		// Be prepared to catch socket related exceptions
		try {
			// Connect to the server at the given host and port
			Socket sock = new Socket(serverName, serverPort);
			System.out.println(
					"Connected to server at ('" + serverName + "', '" + serverPort + "')");

			// Prepare to write to server with auto flush on
			PrintWriter toServerWriter =
					new PrintWriter(sock.getOutputStream(), true);

      // Prepare to read from sever
      BufferedReader serverReader = new BufferedReader(
          new InputStreamReader(sock.getInputStream()));

			// Prepare to read from keyboard
			BufferedReader fromUserReader = new BufferedReader(
					new InputStreamReader(System.in));

			// Keep doing till we get EOF from user
			while (true) {
				// Read a line from the keyboard
        String message = fromUserReader.readLine();

				// If we get null, it means user is done
				if (message == null) {
					System.out.println("Closing connection");
					break;
				}

        // Send the line to the server
        toServerWriter.println(message);

        //read line from user
        String line = serverReader.readLine();

        //print line from server
        System.out.println(name + ": " + line);

			}

      // close the socket and exit
      toServerWriter.close();
      sock.close();
			
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
}
