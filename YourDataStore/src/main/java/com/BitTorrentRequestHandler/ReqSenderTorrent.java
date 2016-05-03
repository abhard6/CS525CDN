package com.BitTorrentRequestHandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.dataStore.main.Node;
import com.dataStore.requestHandler.ReqSender;

public class ReqSenderTorrent extends Thread{

	
	private static Logger log = Logger.getLogger(ReqSender.class);
	private final String userCommand;
	private final String fileName;
	private final String serverIp;
	private final int serverPort;
	public ReqSenderTorrent(String cmd, String file, String serverip, int p)
	{
		this.userCommand = cmd;
		this.fileName = file;
		this.serverIp = serverip;
		this.serverPort = p;
	}
	
	PrintWriter pw = null;
	BufferedReader serverReader = null;
	Socket socket;
	
	public void run()
	{
		log.info("User command is : "+userCommand+" "+fileName);

		PrintWriter pw = null;
		BufferedReader serverReader = null;
		Socket socket;
		String returnStr = "";
		
		if(userCommand.startsWith("getTorrent")){
			log.info("Inside the if condition of request sender torrent"+userCommand+" "+fileName);
			try {
				socket = new Socket(serverIp, serverPort);
				serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				pw = new PrintWriter(socket.getOutputStream(), true);
				pw.println(userCommand+":"+Node._machineIp+":"+fileName);
				log.info("Message flushed to leader");
				
				//check for potential deadlock as it waits for reply from the server if file exists
				while ((returnStr = serverReader.readLine()) != null) 
				{
					log.info(" Thread Id Torrent request sender " + Thread.currentThread().getId()+ " : " + returnStr);
					if(returnStr.equalsIgnoreCase("NA"))
					{
						log.info("File doesnot exist at server");
						System.out.println("File Doesnot exist at the server");
						break;
					}
					else if(returnStr.equalsIgnoreCase("ok")){
						log.info("File on its way via other port");
						System.out.println("File on its way via other port");
						break;
					}
				}
				pw.close();
				serverReader.close();
				socket.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

