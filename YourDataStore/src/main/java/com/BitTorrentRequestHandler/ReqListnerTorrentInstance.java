package com.BitTorrentRequestHandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.net.Socket;


import org.apache.log4j.Logger;


import com.dataStore.main.Node;
import com.dataStore.requestHandler.ReqListenerInstance;


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
			// BufferedReader processReader = null;
			OutputStreamWriter writer = new OutputStreamWriter(
					clientSocket.getOutputStream());
			PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(),
					true);

			clientCommand = reader.readLine();
			log.info("Client fired -->" + clientCommand);
			System.out.println("Client fired -->" + clientCommand);

			// check what is the request about. Also if you are the leader, you
			// need to
			// handle the requests well.
			String words[] = clientCommand.split(":");
			if (clientCommand.startsWith("getTorrent")) {
				// String fullFilePath = Node.localFilePath+fileName;
				String[] arr = words;
				String fullFilePath = "/home/upadhyy3/bitTorrentTestFolder/"
						+ arr[2];
				// BufferedReader bufRead = null;
				try {
					// logic to ping the master and get the list of ip's
					Socket newSocket = new Socket(arr[1],
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
					dos.writeUTF(arr[2] + ":" + "Torrent");
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e);
			// check if file exists
			// e.printStackTrace();
		}

	}

}
