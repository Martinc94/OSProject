package osProject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
	static Map<String,String> loginMap = new ConcurrentHashMap<String,String>();
	static int id = 0;
	
  public static void main(String[] args) throws Exception {
	//creates a new serverSocket on port 2004 
	ServerSocket socket = new ServerSocket(2004,10);	
		
    //read login from file 
    parseLogin();
    
  //loop that accepts client and starts a new thread
    while (true) {
      //blocking method that waits for connection
      Socket clientSocket = socket.accept();
      //creates new thread with socket and new id
      ClientServiceThread cliThread = new ClientServiceThread(clientSocket, id++);
      //starts thread
      cliThread.start();
    }//end while
    
  }//end main
  
  //METHOD/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 
  
 
  
  static void parseLogin() throws NumberFormatException, IOException {
		String fileName="users.txt";
		BufferedReader br = null;
		
		br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		
		String next;
		
		
		
		while((next = br.readLine())!= null){
			//split the string
			String[] split = next.split(" ");
			
			System.out.println(split[0]);
			System.out.println(split[1]);
			
			String username = split[0];
			String password = split[1];
			
			//pass to new login map
			loginMap.put(split[0], split[1]);
			
			
		}//while
		
		
	}
  
  
  
  
}




class ClientServiceThread extends Thread {
//variables
  Socket clientSocket;
  String message;
  int clientID = -1;
  boolean running = true;
  ObjectOutputStream out;
  ObjectInputStream in;

  ClientServiceThread(Socket s, int i) {
    clientSocket = s;
    clientID = i;
  }

  //method to send a message to client
  void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("client> " + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
  public void run() {
    System.out.println("Accepted Client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
    try 
    {
    	out = new ObjectOutputStream(clientSocket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(clientSocket.getInputStream());
		System.out.println("Accepted Client : ID - " + clientID + " : Address - "
		        + clientSocket.getInetAddress().getHostName());
		
		sendMessage("Connection successful");
		do{
			try{
				
				System.out.println("client>"+clientID+"  "+ message);
				//if (message.equals("bye"))
				sendMessage("server got the following: "+message);
				message = (String)in.readObject();
			}
			catch(ClassNotFoundException classnot){
				System.err.println("Data received in unknown format");
			}
			
    	}while(!message.equals("bye"));
      
		System.out.println("Ending Client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
		
    } catch (Exception e) {
      e.printStackTrace();
    }//end catch
    
  }//end run
  
  void checkLogin(){
	//compare to username and password
	  
  }
  
}//end ClientServiceThread

