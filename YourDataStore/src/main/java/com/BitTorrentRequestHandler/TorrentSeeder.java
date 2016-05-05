package com.BitTorrentRequestHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

import com.BitTorrent.CreateTorrentAndSeed;
import com.dataStore.main.Node;
import com.turn.ttorrent.common.Torrent;

public class TorrentSeeder extends Thread{

	private Torrent torrent;
	private File filetorrent;
	private CreateTorrentAndSeed seeder;
	private boolean isTorrent = true;
	public TorrentSeeder(Torrent torrent,CreateTorrentAndSeed seeder){
		this.torrent = torrent;
		this.seeder = seeder;
	}
	
	public TorrentSeeder(File torrent,CreateTorrentAndSeed seeder){
		this.filetorrent = torrent;
		this.seeder = seeder;
		this.isTorrent = false;
	}
	
	public void run(){
		 try {
			if(isTorrent){
			seeder.initialSeed(InetAddress.getLocalHost(), this.torrent,
			 Node.torrentFilePath);
			}
			else{
				//Path Problem at client side(parent directory path should be same at every client)
//				seeder.initialSeed(InetAddress.getLocalHost(), this.filetorrent,
//						 Node.torrentFilePath);
				
				seeder.initialSeed(InetAddress.getLocalHost(), this.filetorrent,
						"/home/abhard6/Desktop/local/");
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
