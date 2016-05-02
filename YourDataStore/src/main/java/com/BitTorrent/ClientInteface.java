package com.BitTorrent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.Client.ClientState;
import com.turn.ttorrent.client.SharedTorrent;

public class ClientInteface {

	public void testMethod() throws UnknownHostException,
			NoSuchAlgorithmException, IOException {
		// First, instantiate the Client object.
		// InetSocketAddress addr = new InetSocketAddress("192.168.56.1",6969);
		SharedTorrent torrent = SharedTorrent
				.fromFile(
						new File(
								"/home/upadhyy3/bitTorrentTestFolder/test.pdf.torrent"
										+ ""), new File("/home/upadhyy3/bitTorrentUploadFolder/"));
							//	"G:\\TestFolderBitTorrentUpload"));
		System.out.println(torrent.getName());
		// Client client = new Client(addr.getAddress(), torrent);

		Client client = new Client(InetAddress.getLocalHost(), torrent);
		// You can optionally set download/upload rate limits
		// in kB/second. Setting a limit to 0.0 disables rate
		// limits.
		client.setMaxDownloadRate(0.0);
		client.setMaxUploadRate(0.0);

		// At this point, can you either call download() to download the torrent
		// and
		// stop immediately after...
//		client.download();
		client.share();
		while (!ClientState.SEEDING.equals(client.getState())) {
			// Check if there's an error
			if (ClientState.ERROR.equals(client.getState())) {
				try {
					throw new Exception("ttorrent client Error State");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// Display statistics
			System.out.printf(
					"%f %% - %d bytes downloaded - %d bytes uploaded\n",
					torrent.getCompletion(), torrent.getDownloaded(),
					torrent.getUploaded());

			// Wait one second
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("download completed.");
		// client.stop();
		System.out.println("Client Download started");
		// Or call client.share(...) with a seed time in seconds:

		// Which would seed the torrent for an hour after the download is
		// complete.

		// Downloading and seeding is done in background threads.
		// To wait for this process to finish, call:
		// client.waitForCompletion();
		System.out.println("Client completed");
		client.addObserver(new Observer() {
			public void update(Observable observable, Object data) {
				Client client = (Client) observable;
				float progress = client.getTorrent().getCompletion();
				// Do something with progress.
				System.out.println(progress);
			}
		});
	}

	public static void main(String[] args) {
		ClientInteface ci = new ClientInteface();
		try {
			ci.testMethod();
		} catch (UnknownHostException e) {
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
