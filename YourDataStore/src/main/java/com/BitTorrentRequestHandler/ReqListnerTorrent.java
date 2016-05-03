package com.BitTorrentRequestHandler;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import com.BitTorrent.*;
import com.dataStore.main.Node;
import com.dataStore.requestHandler.ReqListener;

public class ReqListnerTorrent extends Thread{
	private static Logger log = Logger.getLogger(ReqListener.class);
	private final int port;
	
	public ReqListnerTorrent(int port)
	{
		this.port = port;
	}
	
	public void run()
	{
		log.info("Request listener Torrent is up! ");
		ServerSocket serverSocketListener = null;
		try 
		{
			serverSocketListener = new ServerSocket(port);
			if (serverSocketListener.equals(null))
			{
				System.out.println("Server socket failed to open! terminating");
				log.info("Server socket failed to open! terminating");
				serverSocketListener.close();
				return;
			} 
			else 
			{
				System.out.println(" Request Torrent Listner up and running, listening at port: "+port);
				log.info("Request Torrent Listner up and running, listening at port: "+port);
			}
			
			//Torrent request listner
			while (!Node._reqListenerThreadStop) 
			{
//				new TorrentFileListner(serverSocketListener.accept()).start();
				new ReqListnerTorrentInstance(serverSocketListener.accept()).start();
				log.info("Listening new Req");
			}
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		finally
		{
			try {
				serverSocketListener.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
}
