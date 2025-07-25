package ocsf.server;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client)
  {
    String message = msg.toString();

    // Check for #login command
    if (message.startsWith("#login ")) {
      // Print message received from null (before loginId is set)
      System.out.println("Message received: " + message + " from " + client.getInfo("loginId"));
      // Only allow if login id not already set
      if (client.getInfo("loginId") == null) {
        String loginId = message.substring(7).trim();
        client.setInfo("loginId", loginId);
        System.out.println(loginId + " has logged on.");
        try {
          client.sendToClient(loginId + " has logged on.");
        } catch (Exception e) {}
      } else {
        try {
          client.sendToClient("ERROR: Already logged in. Connection will be closed.");
          client.close();
        } catch (Exception e) {}
      }
      return;
    }

    // If client not logged in, reject any other message
    if (client.getInfo("loginId") == null) {
      try {
        client.sendToClient("ERROR: You must login first. Connection will be closed.");
        client.close();
      } catch (Exception e) {}
      return;
    }

    // Prefix message with login id
    String loginId = (String) client.getInfo("loginId");
    System.out.println("Message received: " + message + " from " + loginId);
    String echoMsg = loginId + ": " + message;
    this.sendToAllClients(echoMsg);
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  @Override
  protected void clientConnected(ConnectionToClient client) {
    System.out.println("A new client has connected to the server.");
  }

  @Override
  protected void clientDisconnected(ConnectionToClient client) {
    Object loginId = client.getInfo("loginId");
    if (loginId != null) {
      System.out.println(loginId + " has disconnected.");
    } else {
      System.out.println("An anonymous client has disconnected.");
    }
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class