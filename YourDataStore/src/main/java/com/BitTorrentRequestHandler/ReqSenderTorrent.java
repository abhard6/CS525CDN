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

//		String fullFilePath = Node.localFilePath+fileName;
		String fullFilePath = "/home/upadhyy3/bitTorrentTestFolder/"+fileName;
		//BufferedReader bufRead = null;
		try 
		{
			// logic to ping the master and get the list of ip's
			socket = new Socket(serverIp, serverPort);
			//Data.O/p.Stream
			File file = new File(fullFilePath);
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			FileInputStream fis = new FileInputStream(file);
			
			BufferedInputStream bis = new BufferedInputStream(fis);
			byte[] mybytearray = new byte[(int) file.length()];
			DataInputStream dis = new DataInputStream(bis);
			dis.readFully(mybytearray, 0, mybytearray.length);
			dos.writeUTF(fileName+"Torrent");
			long fileSize = file.length();
			dos.writeLong(fileSize);
			
			dos.write(mybytearray, 0, mybytearray.length);
			dos.flush();
            log.info("File transfered");
            dis.close();
            dos.close();
			socket.close();

		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			log.error(e);
			//e.printStackTrace();
		}
	
	}
}
