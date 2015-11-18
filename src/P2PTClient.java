/**
 Client Class:
 It includes two other classes: P2PTClientSend and P2PTClientReceive,
 which will send and receive information to and from the server. 
 
 In this class, it uses DatagramSocket which is the sending or receiving
 point for a packet delivery service for communication. Hence, we will need
 DatagramPacket as well. Using DatagramPacket, each message is routed from
 one machine to another based solely on information contained within that packet. 
*/

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.Scanner;

public class P2PTClient{
	
	private DatagramSocket client;
	P2PTClientSend sender;
	P2PTClientReceive receiver;
	
	public P2PTClient(DatagramSocket client){
		this.client = client;
		this.sender = new P2PTClientSend();
		this.receiver = new P2PTClientReceive();
	}
	
	public void start(){
		this.sender.start();
		this.receiver.start();
	}
	
	public void close(){
		this.client.close();
		System.exit(0);
	}
	
	// class for client to send packet to server
	public class P2PTClientSend extends Thread{
		private DatagramPacket sendPacket;
		
		public void run(){
			try{
				// getting information from user input
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				
				String str;
				byte[] buf = new byte[1024];
				DatagramPacket temp = new DatagramPacket(buf, buf.length);

				byte[] sendBuf;
				System.out.print("Status:");
				
				while((str = reader.readLine()) != null){
					if(client.isClosed()){
						System.exit(0);
					}
					
					// if there is a colon contained in a status, it should be converted into "\:"
					if(str.contains(":")){
						str.replace(":", "\\:");
					}
					
					// if user press ctrl-c, exit the program
					if(str.equalsIgnoreCase("ctrl-c")){
						sendBuf = str.getBytes();
						sendPacket = new DatagramPacket(sendBuf, sendBuf.length, InetAddress.getLocalHost(), P2PTwitter.port);
						client.send(sendPacket);
						System.exit(0);
					}
					
					// otherwise, send the packet to server, and display the status.
					str = "Status:" + str;
					sendBuf = str.getBytes();
					sendPacket = new DatagramPacket(sendBuf, sendBuf.length, InetAddress.getLocalHost(), P2PTwitter.port);
					client.send(sendPacket);
				}
				client.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	// class for client to receive packet from server
	public class P2PTClientReceive extends Thread{
		
		// receive message from local server
		public void run() {
			try {
				DatagramPacket receivePacket;
				while (true) {
					byte[] buf = new byte[1024];
					receivePacket = new DatagramPacket(buf, buf.length);
					client.receive(receivePacket);
					
					String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
					if(message.length() > 0 && message.equalsIgnoreCase("ctrl-c")){
						close();
					}
					System.out.println(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
