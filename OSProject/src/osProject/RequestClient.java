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
		login();
		
		//cannot enter if username and password not verified
		if(verified){
			int option=0;
			String command;
			String input;
			//commandLoop until 99 entered as option
			do{
				showMenu();
				System.out.println("Enter Your Choice: ");
				option = stdin.nextInt();
				//switch on option
				switch (option) {
					
				case 1:
					//send a file to the server		  	        	
	  	        	System.out.println("Please Enter Directory of file you want eg file1.txt : ");
					input = stdin.next();
				
					command="get*"+input;
					sendCommand(command);
					
					boolean found=false;
					try {
						found = in.readBoolean();
					} catch (IOException e2) {
						System.out.println("cannot read found boolean from server");
						e2.printStackTrace();
					}
					if(found){	
						//add response
						System.out.println("Please Enter Directory you want to store file locally to eg C:/Downloads/file1.txt : ");
						String localDir = stdin.next();
					
						try {
							createFile(localDir);
							//recieve file method nested in createFile method					
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else {
						System.out.println("Server Cannot find File");
					}
					
					System.out.println("Enter any key to continue: ");
					try {
						System.in.read();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	  				break;
	  				
	  	        case 2:					
					//copy file from the server					
					System.out.println("Please Enter your Directory of file on your PC eg C:/myFolder/file1.txt : ");
					String localName = stdin.next();
				
					System.out.println("Please Enter what you want to name file on server eg file1.txt : ");
					input = stdin.next();
					
					command="put*"+input;
					sendCommand(command);
					
					String path = localName;
						
					File sFile = new File(path);
					
					try {
						sendFile(sFile);
						
					} catch (NumberFormatException e2) {					
						e2.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
	  	        	
	  	        	System.out.println("Enter any key to continue: ");
					try {
						System.in.read();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	  				break;
	  				
	  	        case 3:
					//list all files in directory  	  			        	
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
	  	        	
					System.out.println("Enter any key to continue: ");
					try {
						System.in.read();
					} catch (IOException e1) {					 
						e1.printStackTrace();
					}				
					break;
					
	  	        case 4:
					//move file to different directory	  	        					  	        	
	  	        	System.out.println("Please Enter Directory of file you want to move eg myFolder/file1.txt : ");
					input = stdin.next();
				
					//get target directory
					command="move*"+input;
					sendCommand(command);
				
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
					
					System.out.println("Enter any key to continue: ");
					try {
						System.in.read();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					break;
					
	  	        case 5:
	  	        	//make new directory      												
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
					
					System.out.println("Enter any key to continue: ");
					try {
						System.in.read();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
					
	  	       case 99:
		        	//end program	        			        	
					command = "bye*bye";
					sendCommand(command);
					verified=false;						
					break;

				default:
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
		Boolean connect=false;
		do{
			try {
			//1. creating a socket to connect to the server
			System.out.println("Please Enter IP Address of the Server you wish to connect to:");
			ipaddress = stdin.next();
			requestSocket = new Socket(ipaddress, 2004);
			connect= requestSocket.isConnected();
			
			} catch (UnknownHostException e) {
				System.err.println("You are trying to connect to an unknown host!");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(connect==false){
				System.out.println("Try connecting to the server again");
			}
		}while(connect!=true);
		
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
	}//end inOutStreams
	
	void login(){
		try {
			String login = (String)in.readObject();
			System.out.println("Please Enter Username and password Eg martin*password");
			login = stdin.next();

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
		 try {
			out.writeObject(command);
		} catch (IOException e) {
			
			e.printStackTrace();
		}

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
	
	void createFile(String fileName) throws IOException{
		//create new file 
		String path = fileName;

		File f = new File(path);
		//check if parent directories exist
		f.getParentFile().mkdirs(); 
		try {
			f.createNewFile();
			recieveFile(f);
		} catch (IOException e) {
			//unable to create file
			e.printStackTrace();
		}
	}//end createFile
 	
	void recieveFile(File file) throws NumberFormatException, IOException {
		String text = "";
		//String txtFile ;
		BufferedWriter writer = new BufferedWriter( new FileWriter(file));
		
        boolean ready=true;
		
		out.writeBoolean(ready);
		
		while(text!= "EOF"){
			
			try {
				//add to file
				text = (String)in.readObject();
				writer.append(text);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.out.println("file transter error");			
			}
		}//while	
		
		writer.close();
		System.out.println("file transter complete");
		
		
	}//end recievefile

	void sendFile(File file) throws NumberFormatException, IOException {
		String text = "";
		
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		boolean ready=true;
			
		out.writeBoolean(ready);
			
		if(ready){
			while((text = br.readLine())!= null){	
				//sends text to server
				sendMessage(text);			
			}//while	
			
			sendMessage("EOF");
		}

		br.close();
		System.out.println("file transter complete");
		
	}//end recievefile
	
	
//MAIN ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void main(String args[])
	{
		//creates new request socket
		RequestClient client = new RequestClient();
		//client.getUser();
		client.run();
	}//end main
}//end requestClient