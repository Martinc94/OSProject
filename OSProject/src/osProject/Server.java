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
      System.out.println("Waiting For New Connection");
      //blocking method that waits for connection
      Socket clientSocket = socket.accept();
      //creates new thread with socket and new id
      ClientServiceThread cliThread = new ClientServiceThread(clientSocket, id++);
      //starts thread
      System.out.println("starting new thread");
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
			String[] split = next.split("\\*");
			
			//System.out.println(split[0]);
			//System.out.println(split[1]);
			
			String username = split[0];
			String password = split[1];
			
			//pass to new login map
			loginMap.put(split[0], split[1]);	
		}//while	
		
	}//end parseLogin  
  
}//end Server




class ClientServiceThread extends Thread {
//variables
  Socket clientSocket;
  String message;
  int clientID = -1;
  boolean running = true;
  ObjectOutputStream out;
  ObjectInputStream in;
  
  int option=0;

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
    try {
		out = new ObjectOutputStream(clientSocket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(clientSocket.getInputStream());
		
		System.out.println("Connected to Client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
		sendMessage("Connection successful");
		
	} catch (IOException e1) {
		System.out.println("error");
		e1.printStackTrace();
	}	
    
  	String logindata="";
  	Boolean keepRunning=false;
  	
  	try {
		logindata= (String)in.readObject();
	} catch (ClassNotFoundException | IOException e) {
		System.out.println("Cannot read login Data");
		e.printStackTrace();
	}
	//System.out.println(logindata);
	String[] split = logindata.split("\\*");
	
	//System.out.println(split[0]);
	//System.out.println(split[1]);
	
	String user = split[0];
	String pass = split[1];
  	
    //get login data from client
  	keepRunning=verifyUser(user, pass, keepRunning);
  	
  	String command="";
  	System.out.println("Waiting for command "+keepRunning);
  	while(keepRunning){
  		
  		try {
  			System.out.println("Waiting for command "+keepRunning);
  	  		command= (String)in.readObject();
  	  		
  	  		String[] split2 = command.split("\\*");
  	  		
  	  	    System.out.println(split2[0]);
  			System.out.println(split2[1]);
  			
  			String cmd = split2[0];
  			String cmd2 = split2[1];
  			
  			switch (cmd) {
  			case "get":
  				//copy file to the server
  				sendMessage("Server> Get Method");
  				getFile(cmd2);
  				break;
  				
  	        case "put":
  				//move a file to the server
  	        	sendMessage("Server> Put method");
  	        	putFile();
  				break;
  				
  	        case "list":
				//list all files in directory
  	        	sendMessage("Server> List Command server showing all files");
  	        	System.out.println("list Command server showing all files");
				break;
				
  	        case "move":
				//move file to different directory
  	        	sendMessage("Server> move Command server moving file");
  	        	System.out.println("move Command server moving file");
				break;
				
  	        case "new":
  	        	//make new directory
  	        	sendMessage("Server> new Command server making new directory on server");
  	        	System.out.println("new Command server making new directory on server");
				
				break;
				
  	       case "bye":
	        	//make new directory
	        	System.out.println("Closes Thread");
	        	sendMessage("Server> Goodbye");
	        	keepRunning=false;		
				break;

  			default:
  				//unknown command
  				sendMessage("Server> Unknown Command");
  				break;
  			}
  			
  			System.out.println("verification is "+keepRunning);
  	  		
  	  	} catch (ClassNotFoundException | IOException e2) {
  	  		System.out.println("error reading login data");
  	  		e2.printStackTrace();
  	  	}
  		
  		
  	}

  	System.out.println("Ending Client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
    System.out.println("Closing Thread");
  }//end run
  
  boolean checkLogin(String user,String pass){
	  boolean verified=false;
	//compare to username and password
	 //System.out.println(Server.loginMap.containsKey(user));
	 //Server.loginMap.get(user).equals(pass);
	 //System.out.println( Server.loginMap.get(user).equals(pass));
	 
	 //if username exists 
	 if(Server.loginMap.containsKey(user)){
		 //if password matches
		 if(Server.loginMap.get(user).equals(pass)){
			 verified=true;
		 }//end if
	}//end if
	 
	 return verified;
  }
 
  boolean verifyUser(String user,String pass,boolean keepRunning){
	  try {
	  
			
			keepRunning=checkLogin(user, pass);
			
			out.writeBoolean(keepRunning);
			out.flush();
			
			if(keepRunning)
			System.out.println("User is Verified ");
			
			
	  		
	  	} catch (IOException e2) {
	  		System.out.println("error reading login data");
	  		e2.printStackTrace();
	  	}
	  return keepRunning;
  }//end verifyUser
  
  void getFile(String cmd2){
	  //send file from server eg file1.txt
	  System.out.println("Get Command server giving file");
  }//end getfile
  
  void putFile(){
	  //send file to server
	  System.out.println("put Command server recieving file");
  }//end getfile
  
  
}//end ClientServiceThread

