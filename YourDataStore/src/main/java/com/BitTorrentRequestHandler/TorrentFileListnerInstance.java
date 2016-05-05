package com.BitTorrentRequestHandler;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.dataStore.main.Node;

import org.apache.log4j.Logger;

/**
 * 
 */

/**
 * @author pshrvst2
 *
 */
public class TorrentFileListnerInstance extends Thread 
{
	private static Logger log = Logger.getLogger(TorrentFileListnerInstance.class);
	private Socket clientSocket = null;
	
	public TorrentFileListnerInstance(Socket clientSocket) 
	{
		log.info("Torrent File transfer connection established at socket = " + clientSocket);
		this.clientSocket = clientSocket;
	}

	public void run()
	{
		try 
		{
			log.info("Torrent File transfer started at File receiver instance");
			DataInputStream dataIpStream = new DataInputStream(clientSocket.getInputStream());
			String fileNameWithType = dataIpStream.readUTF();
			String keyWord[] = fileNameWithType.split(":");
			String absoluteFilePath = "/home/abhard6/Desktop/local/";
			byte[] buffer = new byte[16*1024];
			int bytesRead;
			
			// TODO *************************
		    if(keyWord[1].equals("Torrent"))
				absoluteFilePath = absoluteFilePath+keyWord[0]; 
			
			log.info("File saved is: "+absoluteFilePath);
            long fileSize = dataIpStream.readLong();
           
            File downloadedFile = new File(absoluteFilePath);
            FileOutputStream fos = new FileOutputStream(downloadedFile);   
            while (fileSize > 0 && (bytesRead = dataIpStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
            	fos.write(buffer, 0, bytesRead);
            	fileSize -= bytesRead;
            }
            fos.close();
            dataIpStream.close();
            clientSocket.close();
            log.info("File received. Socket connection instance closed");
            
            //Torrent Downloader
            
            
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	 }

}
