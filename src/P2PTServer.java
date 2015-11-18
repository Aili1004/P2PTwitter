/**
 Server Class:
 It includes two other classes: P2PTServerSend and P2PTServerReceiver,
 which will send and receive information to and from the client using 
 DatagramSocket and DatagramPacket as well.  
*/

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class P2PTServer{

	private DatagramSocket server;
	P2PTServerSend sender;
	P2PTServerReceiver reveiver;
	
	public P2PTServer(DatagramSocket server){
		this.server = server;
		this.sender = new P2PTServerSend();
		this.reveiver = new P2PTServerReceiver();
	}
	
	public void start(){
		this.sender.start();
		this.reveiver.start();
	}
	
	public void close(){
		this.server.close();
		System.exit(0);
	}
	
	public class P2PTServerSend extends Thread{
		public void run(){
			try{
				while(true){
					if(server.isClosed()){
						System.exit(0);
					}
					StringBuffer strbuf = new StringBuffer();
					strbuf.append(P2PTwitter.profile.getUnikey()+":"+P2PTwitter.status);
					
					byte[] message = strbuf.toString().getBytes();
					
					DatagramPacket pack;
					InetAddress address; // represents an internal protocol(IP) address
					for(Map.Entry<String, Map<String, String>> entry : P2PTwitter.map.entrySet()){
						String str = entry.getValue().get("ip");
						address = InetAddress.getByName(str);
						
						if(!address.equals(InetAddress.getLocalHost())){
							pack = new DatagramPacket(message, message.length, address, P2PTwitter.port);
							server.send(pack);
						}
					}
					
					// send to others every 2 seconds
					Thread.sleep(2000);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	// class for server to receive packet from the client
	public class P2PTServerReceiver extends Thread{
		private void sendLocalClient(DatagramPacket pac){
			try{
				StringBuffer message = new StringBuffer("### P2P tweets ###\n");
				message.append("# " + P2PTwitter.profile.getPseudo() + " (myself): " + P2PTwitter.status + "\n");
				
				if(P2PTwitter.curUser.size() > 0){
					for(Map.Entry<String, Profile> entry : P2PTwitter.curUser.entrySet()){
						Profile user = entry.getValue();
						String unikey = entry.getKey();
						String pseudo = user.getPseudo();
						String status = user.getStatus();
						
						if(status == null){
							message.append("# [" + pseudo + " (" + unikey + "): not yet initialized]\n");
						}
						
						else{
							if(user.getTime() < 10){
								message.append("# " + pseudo + " (" + unikey + "): " + status + "\n");
							}
							else if(user.getTime() >= 10 && user.getTime() < 20){
								message.append("# [" + pseudo + " (" + unikey + "): idle]\n");	
							}
							else if(user.getTime() > 20){
							}
						}
					}
				}
				
				message.append("### End tweets ###\n");
				byte[] messageByte = message.toString().getBytes();
				DatagramPacket temp = new DatagramPacket(messageByte, messageByte.length, pac.getAddress(), pac.getPort());
				server.send(temp);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		private void updateStatus(String status){
			P2PTwitter.status = status;
		}
		
		public void run(){
			try{
				String message;
				byte[] buf = new byte[1024];
				DatagramPacket pack = new DatagramPacket(buf, buf.length);
				DatagramPacket packToSend;
				byte[] byteToSend;
				boolean containsColon;
				
				while(true){
					containsColon = false;
					server.receive(pack);
					InetAddress address = pack.getAddress();
					int port = pack.getPort();
					message = new String(pack.getData(), 0, pack.getLength());
					
					if(message.length() > 0){
						if(address.equals(InetAddress.getLocalHost())){
							if(message != null && message.equalsIgnoreCase("ctrl-c")){
								break;
							}
							if(message.contains("Status:")){
								String myStatus = message.substring(message.indexOf(":")+1);
								updateStatus(myStatus);
								this.sendLocalClient(pack);
							}
							else if(message.contains("NEW")){
								String str = new String("Status:");
								byteToSend = str.getBytes();
								packToSend = new DatagramPacket(byteToSend, byteToSend.length, address, port);
								server.send(packToSend);
							}
						}
						// from other IP addresses
						else{
							
							if(message.length() > 0){
								if(message.contains(":")){
									if(message.contains("\\:")){
										containsColon = true;
										message = message.replace("\\:", "\\");
									}
								
									String[] str = message.split(":");
									String status = null;
									if(str.length == 2){
										String unikey = str[0];
										status = str[1];
										
										if(containsColon){
											status = status.replace("\\", ":");
										}
										
										if(!P2PTwitter.curUser.containsKey(unikey)){
											for(Map.Entry<String, Map<String, String>> entry : P2PTwitter.map.entrySet()){
												Map<String,  String> peer = entry.getValue();
												if(unikey.equals(peer.get("unikey"))){
													Profile newPeer = new Profile(unikey, address.toString(), peer.get("pseudo"));
													P2PTwitter.curUser.put(unikey, newPeer);
												}
											}
										}
										else{
											P2PTwitter.curUser.get(unikey).updateStatus(status);
										}
									}
								}
							}
						}
					}
				}
				server.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
