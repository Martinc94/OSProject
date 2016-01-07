package osProject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import static java.nio.file.StandardCopyOption.*;

public class Server {
	static Map<String,String> loginMap = new ConcurrentHashMap<String,String>();
	static int id = 0;
	
  public static void main(String[] args) throws Exception {
	//creates a new serverSocket on port 2004 
	ServerSocket socket = new ServerSocket(2004,10);	
		
    //read login from file 
    parseLogin();
    
    //check for root directory
    checkRoot();  
    
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
  
//METHODS/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 
 static void checkRoot(){
	//check for root directory
    if(new File("C:/server/").exists()) {
    	//System.out.println("Server Root Exists");
    	//server root folder exists 
    }
    else{
    	System.out.println("C:/server - does not exist");
    	//server root folder doesnt exist
    	System.out.println("Now creating -C:/server");
    	File dir = new File("C:/server/");
    	createDir(dir);
    }  
  }
  
 static void createDir(File dir){   
	    // attempt to create the directory here
	    boolean success = dir.mkdir();
	    if (success){
	      // creating the directory succeeded
	      System.out.println("directory was created successfully");
	    }
	    else{
	      // creating the directory failed
	      System.out.println("failed trying to create the directory");
	    } 
  }//end createDir

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




//ClientServiceThread Class/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
	String[] split = logindata.split("\\*");

	String user = split[0];
	String pass = split[1];
  	
    //get login data from client
  	keepRunning=verifyUser(user, pass, keepRunning);
  	
  	//check for users root
  	File rootDir = new File("C:/server/"+user+"/");
    
  	boolean root=checkDir(rootDir);
  	
  	if(root){
  		//root directory valid 
  	}
  	else{
  		//directory invalid create it
  		createDir(rootDir);
  	}
  	
  	
  	String command="";

  	while(keepRunning){
  		
  		try {
  	  		command= (String)in.readObject();
  	  		
  	  		String[] split2 = command.split("\\*");
  	  		
  	  	    //System.out.println(split2[0]);
  			//System.out.println(split2[1]);
  			
  			String cmd = split2[0];
  			String cmd2 = split2[1];
  			
  			switch (cmd) {
  			case "get":
  				//copy file from the server 	
  				String getTarget = "C:/server/"+user+"/"+cmd2;
  				getFile(getTarget);
  				
  				//sendMessage("Server> Get Method");
  				break;
  				
  	        case "put":
  				//move a file to the server      
  	        	String putTarget = "C:/server/"+user+"/"+cmd2;
  	        	
  	        	createFile(putTarget);
  	        	
  	        	//putFile();
  	        	
  	        	
  	        	sendMessage("Server> Put method");
  				break;
  				
  	        case "list":
				//list all files in directory
  	        	//System.out.println("list Command server showing all files");
  	        	File listDir = new File("C:/server/"+user+"/"+cmd2);
   
  	        	boolean valid=checkDir(listDir);
  	        	
  	        	if(valid){
  	        		//directory valid - listfiles
  	        		String[] list;
  	        		list=listDir.list();
	        
  	        		//send list back to client
  	        		sendMessage("Server> "+Arrays.toString(list));
  	        	}
  	        	else{
  	        		//directory invalid 
  	        		sendMessage("Server> Invalid Directory");
  	        	}
				break;
				
  	        case "move":
				//move file to different directory  	        
  	        	//System.out.println("move Command server moving file");
   	
  	        	//Path source =source1;
  	        	Path source = Paths.get("C:/server/"+user+"/"+cmd2);
  	        	File SourceCheck = new File("C:/server/"+user+"/"+cmd2);
  	        	boolean valid1=checkDir(SourceCheck);
  	        	
  	        	//get file move target
  	        	command= (String)in.readObject();       	
  	        	
  	        	Path target = Paths.get("C:/server/"+user+"/"+command+"/"+cmd2);
  	        	File targetCheck = new File("C:/server/"+user+"/"+command);      
  	        	boolean valid2=checkDir(targetCheck);
  	        	
  	        	if(valid1&&valid2){
  	        		//both directories valid - move file
  	        		Files.move(source, target, REPLACE_EXISTING);
  	        		sendMessage("Server> File was moved sucessfully"); 	        		
  	        	}
  	        	else{
  	        		//one or more directories/filenames invalid
  	        		sendMessage("Server> File move was unsucessful");
  	        	} 
  	        	
				break;
				
  	        case "new":
  	        	//make new directory
  	        	sendMessage("Server> server attempting to new directory - C:/server/"+user+"/"+cmd2);
  	        	System.out.println("new Command server making new directory on server");
  	        	//makes a new directory from users root only
  	        	File newDir = new File("C:/server/"+user+"/"+cmd2);
  	        	createDir(newDir);          	    				
				break;
				
  	       case "bye":
	        	//end thread

	        	System.out.println("Closes Thread");
	        	sendMessage("Server> Goodbye");
	        	keepRunning=false;		
				break;

  			default:
  				//unknown command
  				sendMessage("Server> Unknown Command");
  				break;
  			}//end switch
  	  		
  	  	} catch (ClassNotFoundException | IOException e2) {
  	  		System.out.println("error reading login data");
  	  		e2.printStackTrace();
  	  	}
  		
  	}//end while

  	System.out.println("Ending Client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
    System.out.println("Closing Thread "+clientID);
  }//end run
  
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
			
			//if(keepRunning)
			//System.out.println("User is Verified ");
			
			
	  		
	  	} catch (IOException e2) {
	  		System.out.println("error reading login data");
	  		e2.printStackTrace();
	  	}
	  return keepRunning;
  }//end verifyUser
  
  void getFile(String fileName) throws IOException{
	  //send file from server eg file1.txt
	  //System.out.println("Get Command server giving file");
	  //check if already exists
	  //create new 
	  //send permission to send file
	  //recieveFile 
	  
	  File file = new File(fileName);
	  boolean send =checkDir(file);
	  
	  //tells client if file is found
	  out.writeBoolean(send);
	  out.flush();
	  
	  boolean cliReady=in.readBoolean();
	   
	  if(send&&cliReady){		  
		  sendFile(file);
	  }
	  else {
		//unable to send file
	  }
	    
	  
  }//end getfile
  
  void putFile(){
	  //send file to server
	 // recieveFile(file);
	  
	  
	  
	 /* //tells client if file is found
	  out.writeBoolean(send);
	  out.flush();
	  
	  boolean cliReady=in.readBoolean();
	   
	  if(send&&cliReady){		  
		  sendFile(file);
	  }
	  else {
		//unable to send file
	  }*/
	  
  }//end putFile 
  
  void createDir(File dir){   
	    // attempt to create the directory here
	    boolean success = dir.mkdir();
	    if (success){
	      // creating the directory succeeded
	      System.out.println("directory was created successfully");
	    }
	    else{
	      // creating the directory failed
	      System.out.println("failed trying to create the directory");
	    } 
	}//end createDir

  boolean checkDir(File dir){
	boolean valid=false;
	//check for directory
	if((dir).exists()) {
		//folder exists 
		//System.out.println("Directory Exists");
		valid=true;
	}
	else{
		//System.out.println("Directory does not exist");
		//Directory doesnt exist
		}  
	
	return valid;
	}

  void createFile(String fileName) throws IOException{
		//create new file 
		String path = fileName;

		File f = new File(path);
		//check if parent directories exist and create
		f.getParentFile().mkdirs(); 
		try {
			f.createNewFile();
			recieveFile(f);
		} catch (IOException e) {
			//unable to create file
			e.printStackTrace();
		}
		
	}//end create file
	
  void recieveFile(File file) throws NumberFormatException, IOException {
		String text = "";
		BufferedWriter writer = new BufferedWriter( new FileWriter( file));
		
		//boolean ready
		  //boolean ready=true;
		  //out.writeBoolean(ready);
		  //out.flush();
		  
		  boolean cliReady=in.readBoolean();
		   
		  if(cliReady){		  
			while(text!= "EOF"){
				
				try {
					//add to file
					text = (String)in.readObject();
					writer.append(text);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					System.out.println("file transter error");			
				}
				
			}//end while
		  
		}//end if	
		else {
			//unable to recieve file
		}  
		
		
		writer.close();
		System.out.println("file transter complete");
		
		
	}//end recievefile

	void sendFile(File file) throws NumberFormatException, IOException {
		String text = "";
		
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		while((text = br.readLine())!= null){	
			//sends text to server
			sendMessage(text);			
		}//while	
		
		sendMessage("EOF");

		br.close();
		System.out.println("file transter complete");
		
		
	}//end recievefile
}//end ClientServiceThread

