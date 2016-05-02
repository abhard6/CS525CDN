package com.BitTorrent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.Client.ClientState;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.common.Torrent;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;

public class MainApplication {

	public static void main(String args[]) {
		String sharedFile = "/home/upadhyy3/bitTorrentTestFolder/test.pdf";
		String destinationDirectory = "/home/upadhyy3/bitTorrentTestFolder";

		try {
			TrackerServer ts = new TrackerServer("130.126.28.17", 6969);
			CreateTorrentAndSeed cs = new CreateTorrentAndSeed();
			ts.startTracker();

			/*
			 * Torrent can be saved in memory to keep track of current runnig
			 * torrents
			 */

			// InetAddress should be assigned in a more modular way
			Torrent torrent = cs.createTorrentFromSingleFile(new File(
					sharedFile), ts.getTracker(), "shivam");
			ts.announceTorrentOnTracker(torrent);
			cs.initialSeed(InetAddress.getLocalHost(), torrent,
					destinationDirectory);

			System.out.println("Tracker running.");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
