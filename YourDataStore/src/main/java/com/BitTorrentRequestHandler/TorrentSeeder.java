package com.BitTorrentRequestHandler;

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
	private CreateTorrentAndSeed seeder;
	TorrentSeeder(Torrent torrent,CreateTorrentAndSeed seeder){
		this.torrent = torrent;
		this.seeder = seeder;
	}
	
	public void run(){
		 try {
			seeder.initialSeed(InetAddress.getLocalHost(), torrent,
			 Node.torrentFilePath);
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
