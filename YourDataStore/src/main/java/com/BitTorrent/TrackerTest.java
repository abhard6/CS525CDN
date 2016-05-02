package com.BitTorrent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

import com.turn.ttorrent.tracker.*;

public class TrackerTest {

//	public void trackerTestMethod(Tracker tracker) throws IOException, NoSuchAlgorithmException 
	public void trackerTestMethod() throws IOException, NoSuchAlgorithmException {
		// First, instantiate a Tracker object with the port you want it to
		// listen on.
		// The default tracker port recommended by the BitTorrent protocol is
		// 6969.
		Tracker tracker = new Tracker(new InetSocketAddress(6969));
//		Tracker tracker = new Tracker(InetAddress.getLocalHost());

		// Then, for each torrent you wish to announce on this tracker, simply
		// created
		// a TrackedTorrent object and pass it to the tracker.announce() method:
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".torrent");
			}
		};
		
		System.out.println("Announcing the torrent");
		for (File f : new File("G:\\TestFolderBitTorrent\\").listFiles(filter)) {
			tracker.announce(TrackedTorrent.load(f));
			System.out.println(tracker.getAnnounceUrl());
			System.out.println(tracker.hashCode());
		}
//		File f = new File("G:\\TestFolderBitTorrent\\seed.torrent");
//		tracker.announce(TrackedTorrent.load(f));
//		tracker.announce(TrackedTorrent.load(new File("G:\\TestFolderBitTorrent\\[kat.cr]electrical.engineering.101.you.should.have.learned.in.school.but.probably.didn.t.3rd.edition.2011.pdf.gooner.torrent")
//		, true));

		// Once done, you just have to start the tracker's main operation loop:
		tracker.start();

		// You can stop the tracker when you're done with:
//		tracker.stop();
		System.out.println("Tracker stopped");
	}
	
	public static void main(String[] args){
        TrackerTest trackerTest = new TrackerTest();
        try {
			trackerTest.trackerTestMethod();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
