/**
 Class contains main method. 
*/

import java.net.*;
import java.io.*;
import java.util.*;

public class P2PTwitter{
	public static int port;
	public static Profile profile;
	public static String status;
	private String unikey;
	
	public static Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
	public static Map<String, Profile> curUser = new HashMap<String, Profile>();
	
	public P2PTwitter(String unikey, int port){
		this.unikey = unikey;
		this.port = port;
	}
	
	// method for reading participants.properties file
	public void readFile(){
		Scanner scan = null;
		
		try{
			String inputFile = "participants.properties";
			File file = new File(inputFile);
			scan = new Scanner(file);
			
			String[] part = null;
			while(scan.hasNextLine()){
				String line = scan.nextLine();
				if(!line.contains("#")){
					if(line.contains("participants") && line.contains("=")){
						part = line.substring(line.indexOf("=")+1).split(",");
						break;
					}
				}
			}
			
			if(part.length > 0){
				for(int i = 0; i < part.length; i++){
					map.put(part[i], new HashMap<String, String>());
				}
			}
			
			String[] str = null;
			while(scan.hasNextLine()){
				String line = scan.nextLine();
				str = line.split("=");
				
				if(str.length == 2){
				
					// trim(): returns a copy of the string, with leading and trailing whitespace omitted.
					String info = str[1].trim();
					String[] peer = str[0].replace(".", ",").trim().split(",");
					
					if(peer.length == 2){
						String name = peer[0].trim();
						String next = peer[1].trim();
						
						Map<String, String> map2 = map.get(name);
						if(map2 != null){
							map2.put(next, info);
							map.put(name, map2);
						}
					}
				}
			}
			scan.close();
		}
		catch(FileNotFoundException e){
			System.out.println("File is missing.");
		}
	}
	
	// method for checking whether the unikey the user entered is valid or not
	public boolean isValid(){
		boolean valid = false;
		if(!map.isEmpty()){
			for(Map.Entry<String, Map<String, String>> entry : map.entrySet()){
				Map<String, String> info = entry.getValue();
				String tempUnikey = info.get("unikey");
				if(unikey.equals(tempUnikey)){
					profile = new Profile(unikey, info.get("ip"), info.get("pseudo"));
					valid = true;
					break;
				}
			}
		}
		return valid;
	}
	
	public void startClient(){
		try{
			DatagramSocket client = new DatagramSocket();
			String message = "NEW";
			byte[] temp = message.getBytes();
			DatagramPacket request = new DatagramPacket(temp, temp.length, InetAddress.getByName("localhost"), this.port);
			
			client.send(request);
			new P2PTClient(client).start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void startServer(){
		try{
			DatagramSocket server = new DatagramSocket(this.port);
			new P2PTServer(server).start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		final int portNumber = 7014;
		String unikey = args[0].toString();
		P2PTwitter twitter = new P2PTwitter(unikey, portNumber);
		twitter.readFile();
		
		if(twitter.isValid()){
			twitter.startServer();
			twitter.startClient();
		}
		else{
			System.out.println("unikey not found in the list.\n");
		}
	}
}