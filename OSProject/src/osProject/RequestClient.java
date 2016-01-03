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
 	
 	RequestClient(){}
 	
	void run()
	{
		stdin = new Scanner(System.in);
		
		//add switch
		
		//connects two server
		connect();
		
		//gets login info
		getUser();
		
		//get Input and Output streams
		inOutStreams();
		
		//3: Communicating with the server
		comm();
		
		//ends connection
		closeConnection();
	}	
		
	void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("client>" + msg);
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	
	void connect()
	{
		try {
		//1. creating a socket to connect to the server
		System.out.println("Please Enter your IP Address");
		System.out.println("192.168.1.6 desktop");
		System.out.println("192.168.1.  laptop");
		ipaddress = stdin.next();
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
		
		//System.out.println(userpass);
	}
	
	void login(){
		//send login to server and get response
	}
	
	void inOutStreams(){
		//2. get Input and Output streams
		try {
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			System.out.println("Hello");
			
		} catch (IOException e) {
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


	


