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
		
		//add switch
		
		//connects to server
		connect();
		
		//get Input and Output streams
		inOutStreams();
		
		//gets login info
		//getUser();
		
		login();
		
		//3: Communicating with the server
		//comm();
		
		if(verified){
			//commandLoop
			do{
				
			String command;
				
				System.out.println("Please Enter your Command eg get*user (bye to exit): ");
				command = stdin.next();
				sendCommand(command);
				
				try {
					String Response = (String)in.readObject();
					System.out.println(Response);
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//switch
				
				
				
				/*try{
					//sendMessage("server got the following: "+message);
					message = (String)in.readObject();
				}
				catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}*/
				
	    	}while(!message.equals("bye"));
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
	
	void connect()
	{
		try {
		//1. creating a socket to connect to the server
		//System.out.println("Please Enter your IP Address");
		System.out.println("Please Enter IP Address of Server 192.168.1.6");
		//ipaddress = stdin.next();
		ipaddress="192.168.1.6";
		requestSocket = new Socket(ipaddress, 2004);
		
		} catch (UnknownHostException e) {
			System.err.println("You are trying to connect to an unknown host!");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Connected to "+ipaddress+" in port 2004");
	}
	
	void getUser(){
		String user;
		String pass;
		String userpass;
		
		System.out.println("Please Enter your Username: ");
		user = stdin.next();
		
		System.out.println("Please Enter your Password: ");
		pass = stdin.next();
		
		//join into one one string seperated by *
		userpass=user+"*"+pass;
		
		try {
			//String Command = (String)in.readObject();
			//System.out.println(Command);
			
			System.out.println("out.write");
			//out.writeObject(userpass);
			out.flush();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(userpass);
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
		//command = (String)in.readObject();
		 try {
			out.writeObject(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//sendMessage(command);

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
			System.out.println("GoodBye");
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


	


