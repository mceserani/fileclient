package it.edu.polomanettiporciatti.fileclient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class FileClient {
	
	private FileClient(){
	}

	/**
	 * 
	 * @param args Command line arguments
	 * @author Matteo Ceserani
	 * @version 1.0
	 */
    public static void main(String[] args){
       
		if(args.length != 2){
			System.out.println("Usage: java -jar FileClient <host> <port>");
			System.exit(1);
		}

		String host = args[0];
		int port = -1;
		try{
			port = Integer.parseInt(args[1]);
		}catch(NumberFormatException e){
			System.out.println("Port must be an integer.");
			System.exit(2);
		}
		if (port < 0 || port > 65535){
			System.out.println("Port must be in the range 0-65535.");
			System.exit(3);
		}

		Socket socket = null;
		try{
			socket = new Socket(host, port);
		}catch(IOException e){
			System.out.println("Could not connect to " + host + ":" + port);
			System.exit(4);
		}

		DataInputStream in = null;
		DataOutputStream out = null;
		try{
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		}catch(IOException e){
			System.out.println("Could not open input/output streams.");
			System.exit(5);
		}

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String userInput = null;
		File f = null;
		DataInputStream fileIn = null;
		
		do{
			try{
				System.out.print("Inserisci il nome del file: ");
				userInput = stdIn.readLine();
			}catch(IOException e){
				System.out.println("Error reading from stdin.");
				System.exit(6);
			}
			
			f = new File(userInput);
			if(!f.exists()){
				System.out.println("File " + userInput + " does not exist.");
			}
		}while(!f.exists());
		
		try{
			out.writeUTF(f.getName());
			if(in.readUTF().equals("OK")){
				fileIn = new DataInputStream(new FileInputStream(f));
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len = fileIn.read(buffer)) != -1){
					out.write(buffer, 0, len);
				}
			}else
				System.out.println("Server didn't accept file " + userInput + ".");
		
		}catch(IOException e){
			System.out.println("Error writing to server.");
			System.exit(7);
		}finally{
			try {
				socket.close();
				fileIn.close();
			} catch (IOException e) {
				System.out.println("Error closing socket.");
				System.exit(8);
			}
		}

    }

}