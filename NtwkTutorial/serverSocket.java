import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class serverSocket
{

  public static void main(String args[])
  {
  
    serverSocket server= new serverSocket();
    try
    {
			//comment this line and uncomment the next one to run in multiple threads.
      server.runServer();
      //server.runServerThread();
    }
    catch (IOException e)
    { }
  }
  
  public void runServer() throws IOException
  {
    ServerSocket serverSocket = new ServerSocket(9001); // establish a server socket to receive messages over the network from clients
    System.out.println("Server ready...");
  
    while (true) // runs forever
    {
      String message=null;
      Socket socket=serverSocket.accept(); // listen for a connection to be made to this socket and accept it
      try
      {
        BufferedReader inFromClient= new BufferedReader(new InputStreamReader(socket.getInputStream())); // BufferedReader: reads text from a character-input stream 
                    //socket.getInputStream(): Returns an input stream for this socket.
                    //If this socket has an associated channel then the resulting input stream delegates all of its operations to the channel
  
        PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true); //PrintWriter: Prints formatted representations of objects to a text-output stream
            //socket.getOutputStream(): Returns an output stream for this socket.
            //If this socket has an associated channel then the resulting output stream delegates all of its operations to the channel
        while ((message = inFromClient.readLine())!=null) //Reads a line of text. A line is considered to be terminated by any one of a line feed ('\n'), 
                  //a carriage return ('\r'), or a carriage return followed immediately by a linefeed.
        {
          simpleMath sm=new simpleMath();
          String[] params =  message.split(",");
          int x= Integer.parseInt(params[1]);
          int y= Integer.parseInt(params[2]);
          int res=0;
          System.out.println(params[0] +"--"+params[1]+"--"+params[2]);
          if (params[0].equals("mul"))
             res=sm.mul(x,y);
          else if (params[0].equals("add"))
             res=sm.add(x,y);
           outToClient.println("hello client from server, your result is: " + res );
        }
      }
      catch (IOException e) {}
    }
  }


  public void runServerThread() throws IOException
  {
    ServerSocket serverSocket = new ServerSocket(9001);
    System.out.println("Server ready...");
    while (true)
    {
      Socket socket=serverSocket.accept();
      new serverSocketThread(socket).start();
    }
  }
}
