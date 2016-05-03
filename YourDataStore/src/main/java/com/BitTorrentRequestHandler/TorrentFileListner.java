package com.BitTorrentRequestHandler;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import com.dataStore.fileTransfer.FileReceiver;
import com.dataStore.fileTransfer.FileReceiverInstance;
import com.dataStore.main.Node;

public class TorrentFileListner extends Thread{
	private static Logger log = Logger.getLogger(FileReceiver.class);
	private final int port;

	public TorrentFileListner(int port)
	{
		this.port = port;
	}

	public void run()
	{
		log.info("Torrent File Receiver is up! ");
		ServerSocket serverSocketListener = null;
		try 
		{
			serverSocketListener = new ServerSocket(port);
			if (serverSocketListener.equals(null))
			{
				System.out.println("FileReceiver Server socket failed to open! terminating");
				log.info("FileReceiver Server socket failed to open! terminating");
				serverSocketListener.close();
				return;
			} 
			else 
			{
				System.out.println(" File Receiver socket established, listening at port: "+port);
				log.info(" FileReceiver socket established, listening at port: "+port);
			}

			while (!Node._fileReceiverThreadStop) 
			{
				new TorrentFileListnerInstance(serverSocketListener.accept()).start();
				log.info("Receiving a new file");
			}
			
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try {
				serverSocketListener.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
