package osProject;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class RequestClient{
	Socket requestSocket;
	ObjectOutputStream out;
 	ObjectInputStream in;
 	String message="";
 	String ipaddress;
 	Scanner stdin;
 	boolean verified;
 	
 	RequestClient(){}
 	
	void run()
	{
		stdin = new Scanner(System.in);
		
		//connects to server
		connect();
		
		//get Input and Output streams
		inOutStreams();
		
		//gets login info
		//getUser();
		
		login();
		
		//3: Communicating with the server
		//comm();
		
		//cannot enter if username and password not verified
		if(verified){
			int option=0;
			String command;
			String input;
			//commandLoop
			do{
				showMenu();
				System.out.println("Enter Your Choice: ");
				option = stdin.nextInt();
				
				//switch
				switch (option) {
					
				case 1:
	  				//copy file to the server					
					System.out.println("Please Enter your fileDirectory eg C:/myFolder/file1.txt : ");
					input = stdin.next();
				
					command="put*"+input;
					
					sendCommand(command);
					
					try {
						String Response = (String)in.readObject();
						System.out.println(Response);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
					
		  			
					getMethod();
					
					System.out.println("Enter any key + return to continue: ");
					command = stdin.next();
	  				break;
	  				
	  	        case 2:
	  				//send a file to the server	
					//System.out.println("Please Enter your Command eg put*fileName : ");
					//command = stdin.next();
	  	        	
	  	        	System.out.println("Please Enter Directory of file you want eg myFolder/file1.txt : ");
					input = stdin.next();
				
					command="get*"+input;
					sendCommand(command);
					
					//add response
					System.out.println("Please Enter Directory you want to store file to eg C:/Downloads/file1.txt : ");
					input = stdin.next();
				
					command=input;
					sendCommand(command);
					
					try {
						String Response = (String)in.readObject();
						System.out.println(Response);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
	  	        	
	  	        	putMethod();
	  	        	
	  	        	System.out.println("Enter any key + return to continue: ");
					command = stdin.next();
	  				break;
	  				
	  	        case 3:
					//list all files in directory  	  
					//System.out.println("List File : ");
				
					//System.out.println("Please Enter your Command eg list*directory : ");
					//command = stdin.next();
					//sendCommand(command);
	  	        	
	  	        	System.out.println("Please Enter Directory you want to search eg myFolder/file1.txt : ");
					input = stdin.next();
				
					command="list*"+input;
					sendCommand(command);
					
					try {
						String Response = (String)in.readObject();
						System.out.println(Response);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
	  	        	
					System.out.println("Enter any key + return to continue: ");
					command = stdin.next();
					
					break;
					
	  	        case 4:
					//move file to different directory	  	        					
					//System.out.println("Please Enter your Command eg move*oldDir : ");
					//command = stdin.next();
					//sendCommand(command);
	  	        	
	  	        	System.out.println("Please Enter Directory of file you want to move eg myFolder/file1.txt : ");
					input = stdin.next();
				
					command="move*"+input;
					sendCommand(command);
					
					//add response
					//if file exists
					
					System.out.println("Please Enter Directory you want to move file two eg myFolder2 : ");
					input = stdin.next();
				
					command=input;
					sendCommand(command);
					
					try {
						String Response = (String)in.readObject();
						System.out.println(Response);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
					
					System.out.println("Enter any key + return to continue: ");
					command = stdin.next();
					
					break;
					
	  	        case 5:
	  	        	//make new directory      							
					//System.out.println("Please Enter your Command eg new*directory : ");
					//command = stdin.next();
					//sendCommand(command);
					
					System.out.println("Please Enter Directory you want to create eg myFolder/myNewFolder : ");
					input = stdin.next();
				
					command="new*"+input;
					sendCommand(command);
					
					try {
						String Response = (String)in.readObject();
						System.out.println(Response);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
					
					System.out.println("Enter any key + return to continue: ");
					command = stdin.next();
					break;
					
	  	       case 99:
		        	//end program	        			        	
					command = "bye*bye";
					sendCommand(command);
					verified=false;	
					
					/*try {
						String Response = (String)in.readObject();
						System.out.println(Response);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
					break;*/

				default:
					//System.out.println("Invalid Option");
					break;
				}
				
	    	}while(option!=99);
		}
		//ends connection
		closeConnection();
	}	
		
	void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			//System.out.println("client>" + msg);
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	
	void showMenu(){
		System.out.println("1 - Get File: ");
		System.out.println("2 - Send File: ");
		System.out.println("3 - List File: ");
		System.out.println("4 - Move File: ");
		System.out.println("5 - New Directory: ");
		System.out.println("99 - To Exit: ");
	}
	
	void connect()
	{
		try {
		//1. creating a socket to connect to the server
		//System.out.println("Please Enter your IP Address");
		System.out.println("Please Enter IP Address of the Server you wish to connect to:");
		//ipaddress = stdin.next();
		ipaddress="192.168.1.2";
		requestSocket = new Socket(ipaddress, 2004);
		
		} catch (UnknownHostException e) {
			System.err.println("You are trying to connect to an unknown host!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Connected to "+ipaddress+" in port 2004");
	}
	
	void inOutStreams(){
		//2. get Input and Output streams
		try {
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			System.out.println("Welcome to the server");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	

	void login(){
		try {
			String login = (String)in.readObject();
			System.out.println("Please Enter Username and password Eg martin*password");
			//login = stdin.next();
			login="martin*password";
			sendMessage(login);
			
			verified=in.readBoolean();
			if (verified==false){
				System.out.println("Wrong Login or Password");
			}
			
			if (verified){
				System.out.println("Correct Login and Password");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	
	void sendCommand(String command){
		//command = (String)in.readObject();
		 try {
			out.writeObject(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	void comm(){
		//3: Communicating with the server
		do{
			try
			{			
					try {
						message = (String)in.readObject();
						System.out.println("Please Enter the Message to send...");
						message = stdin.next();
						sendMessage(message);
						
					} catch (IOException e) {
						e.printStackTrace();
					}
			
			}
			catch(ClassNotFoundException classNot)
			{
				System.err.println("data received in unknown format");
			}
		}while(!message.equals("bye"));
			
	}
	
	void closeConnection(){
		//4: Closing connection
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
			System.out.println("GoodBye - disconnected from server");
	}
	
	void getMethod(){
		//create new file 
		
		//write to file
		
		//until recieve EOF 
		
	}
	
	void putMethod(){
		//get file location
		
		//read from new file 
		
		//send to server
		
		//recieve responce
	}
	
	void listMethod(){
		
		//recieve responce from server 
	}
	
	void moveMethod(){
		
		//send filenameAndNewDirectory
		
		//recieve responce from server 
	}	
	
	void newMethod(){
		//send new directory to server
		
		//recieve responce from server 
	}
	
//MAIN ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
		public static void main(String args[])
		{
			//creates new request socket
			RequestClient client = new RequestClient();
			//client.getUser();
			client.run();
			
			
		}
	}


	


