/**
 This is a helper class. Use this class to: 
 1. return a person's profile such as unikey, IP address, pseudo and status.
 2. update a person's status.
 3. get the waiting time.
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Calendar;

public class Profile{
	private String unikey;
	private String ip;
	private String status;
	private String pseudo;
	private int update;
	
	public Profile(String unikey, String ip, String pseudo){
		this.unikey = unikey;
		this.ip = ip;
		this.pseudo = pseudo;
	}
	
	public String getUnikey(){
		return this.unikey;
	}
	
	public String getIp(){
		return this.ip;
	}
	
	public String getStatus(){
		return this.status;
	}
	
	public String getPseudo(){
		return this.pseudo;
	}
	
	public void updateStatus(String status){
		this.status = status;	
		this.update = Calendar.getInstance().get(Calendar.SECOND);
	}
	
	public int getTime(){
		return Calendar.getInstance().get(Calendar.SECOND) - this.update;
	}
}