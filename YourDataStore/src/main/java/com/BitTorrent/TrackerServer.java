package com.BitTorrent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

import com.turn.ttorrent.common.Torrent;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;

/*
 * Generate the Tracker object and start the tracker on being explicitly called
 */

public class TrackerServer {

	Tracker tracker;

	public TrackerServer(String ip, int port) {
		try {
			this.tracker = new Tracker(new InetSocketAddress(ip,
					port));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Tracker getTracker() {
		return this.tracker;
	}

	/*
	 * start the current tracker object can throw some user generated exceptions
	 */
	public void startTracker() {
		this.tracker.start();
	}

	/*
	 * takes input as TrackedTorrent Object and announce it on the current
	 * Tracker object
	 * 
	 */

	public void announceTorrentOnTracker(Torrent torrent) throws NoSuchAlgorithmException, IOException {
		TrackedTorrent newTrackedTorrent = new TrackedTorrent(torrent);
		this.tracker.announce(newTrackedTorrent);
	}

	/*
	 * remove a particular torrent from the announced list on
	 * invalidation/change in the file torrent represents
	 */

	public void removeTorrrent(Torrent torrent) {
		this.tracker.remove(torrent);
	}
}
