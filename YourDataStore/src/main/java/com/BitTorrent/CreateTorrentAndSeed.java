package com.BitTorrent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.client.Client.ClientState;
import com.turn.ttorrent.common.Torrent;
import com.turn.ttorrent.tracker.Tracker;

public class CreateTorrentAndSeed {

	/*
	 * Create torrent from a file object and return the created torrent object
	 */
	public Torrent createTorrentFromSingleFile(File fileToBeShared,
			Tracker trackerToBeUsed, String author)
			throws NoSuchAlgorithmException, InterruptedException, IOException,
			URISyntaxException {
		Torrent torrent = Torrent.create(fileToBeShared, trackerToBeUsed
				.getAnnounceUrl().toURI(), author);
		FileOutputStream fos = new FileOutputStream(
				"/home/upadhyy3/bitTorrentTestFolder/" + torrent.getName()
						+ ".torrent");
		torrent.save(fos);
		fos.close();

		return torrent;
	}

	/*
	 * Initial seed for the file so that it can be discovered by other peers
	 */

	public void initialSeed(InetAddress address, Torrent torrent,
			String destinationDirectory) throws UnknownHostException,
			FileNotFoundException, NoSuchAlgorithmException, IOException {
		Client seeder = new Client(address, new SharedTorrent(torrent,
				new File(destinationDirectory), true));
		seeder.share();
		while (!ClientState.SEEDING.equals(seeder.getState())) {
			// Check if there's an error
			if (ClientState.ERROR.equals(seeder.getState())) {
				try {
					throw new Exception("ttorrent client Error State");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
