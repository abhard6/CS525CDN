package com.BitTorrentRequestHandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

import org.apache.log4j.Logger;

import com.BitTorrent.CreateTorrentAndSeed;
import com.dataStore.main.Node;
import com.dataStore.requestHandler.ReqListenerInstance;
import com.turn.ttorrent.common.Torrent;
import com.turn.ttorrent.tracker.TrackedTorrent;

public class ReqListnerTorrentInstance extends Thread {

	private static Logger log = Logger.getLogger(ReqListenerInstance.class);
	private Socket clientSocket = null;

	public ReqListnerTorrentInstance(Socket clientSocket) {
		log.info("Connection established at socket = " + clientSocket);
		this.clientSocket = clientSocket;
	}

	public void run() {
		try {
			String clientCommand = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			OutputStreamWriter writer = new OutputStreamWriter(
					clientSocket.getOutputStream());
			PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(),
					true);

			clientCommand = reader.readLine();
			log.info("Client fired -->" + clientCommand);
			System.out.println("Client fired -->" + clientCommand);
			// receives command like getTorrent:130.126.28.10:test.pdf.torrent
			String words[] = clientCommand.split(":");
			if (clientCommand.startsWith("getTorrent")) {

				// check if torrent file is already created and present in the
				// file map
				if (Node._fileMap.containsKey(words[2])) {
					pw.println("ok");
					sendTorrentFile(words[2], words[1]);
				} else {
					// Comes in else condition when there is not torrent file
					// and creates new file and send that file
					File fileFromLocalDirectory = checkFileInDirectory(words[2]);
					if (fileFromLocalDirectory.exists()) {
						// create torrent and send it across
						pw.println("ok");
						CreateTorrentAndSeed cs = new CreateTorrentAndSeed();
						Torrent torrent = cs.createTorrentFromSingleFile(
								fileFromLocalDirectory,
								Node._trackerServer.getTracker(), "shivam");
						log.info("Torrent created at Folder {0} and name is {1}"+Node.torrentFilePath+torrent.getName());
						Node._trackerServer.announceTorrentOnTracker(torrent);
						
						log.info("creating the initial seed for the file at the sever to be downloadable by other clients");
						cs.initialSeed(InetAddress.getLocalHost(), torrent,
								Node.torrentFilePath);
						sendTorrentFile(torrent.getName(), words[1]);
					} else {
						pw.println("NA");
					}
				}
			}
			reader.close();
			writer.close();
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e);
			// check if file exists
			// e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendTorrentFile(String fileName, String receiverIpAdress) {
		String fullFilePath = Node.torrentFilePath + fileName;
		try {
			// logic to ping the master and get the list of ip's
			Socket newSocket = new Socket(receiverIpAdress,
					Node._TCPPorstForTorrentFileRquest);
			// Data.O/p.Stream
			File file = new File(fullFilePath);
			DataOutputStream dos = new DataOutputStream(
					newSocket.getOutputStream());
			FileInputStream fis = new FileInputStream(file);

			BufferedInputStream bis = new BufferedInputStream(fis);
			byte[] mybytearray = new byte[(int) file.length()];
			DataInputStream dis = new DataInputStream(bis);
			dis.readFully(mybytearray, 0, mybytearray.length);
			dos.writeUTF(fileName + ":" + "Torrent");
			long fileSize = file.length();
			dos.writeLong(fileSize);

			dos.write(mybytearray, 0, mybytearray.length);
			dos.flush();
			log.info("File transfered");
			dis.close();
			dos.close();
			newSocket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e);
			// check if file exists
			// e.printStackTrace();
		}
	}
	/*
	 * Creates file from the local folder and returns the file object
	 */
	
	public File checkFileInDirectory(String fileName) {
		return new File(Node.localFilePath + fileName);
	}

}
