//package com.BitTorrentRequestHandler;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.lang.Thread.State;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.apache.log4j.Logger;
//
//import com.dataStore.data.FileData;
//import com.dataStore.data.NodeData;
//import com.dataStore.gossip.FileListSenderThread;
//import com.dataStore.main.Node;
//import com.dataStore.requestHandler.ReqListenerInstance;
//import com.dataStore.requestHandler.ReqSender;
//
//public class ReqListnerTorrentInstance extends Thread {
//
//	private static Logger log = Logger.getLogger(ReqListenerInstance.class);
//	private Socket clientSocket = null;
//
//
//	public ReqListnerTorrentInstance(Socket clientSocket) {
//		log.info("Connection established at socket = " + clientSocket);
//		this.clientSocket = clientSocket;
//	}
//
//	public void run() {
//		try {
//			String clientCommand = "";
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					clientSocket.getInputStream()));
//			// BufferedReader processReader = null;
//			OutputStreamWriter writer = new OutputStreamWriter(
//					clientSocket.getOutputStream());
//			PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(),
//					true);
//
//			clientCommand = reader.readLine();
//			log.info("Client fired -->" + clientCommand);
//			System.out.println("Client fired -->" + clientCommand);
//
//			// check what is the request about. Also if you are the leader, you
//			// need to
//			// handle the requests well.
//			String words[] = clientCommand.split(":");
//			// its not a file operation. Mostly this request is for leader.
//			if (words[0].equalsIgnoreCase("get")) {
////				if (!Node._torrentFileMap.containsKey(words[1])) {
////					pw.println("NA");
////				} else {
////					FileData fileObject = Node._torrentFileMap.get(words[1]);
////					String fileName = fileObject.getFileName();
////					if (fileObject.isTorrentCreated()) {
////						// send torrent file
////						
////					} else {
////						// create torrent and send torrent file
////					}
//				
//				// Testing hardcoded file value
//				//	String fullFilePath = Node.localFilePath+fileName+".torrent";
//					String fileName = "index.html.torrent";
//					String fullFilePath = "/home/abhard6/bitTorrentTestFolder/" + fileName;
//					//BufferedReader bufRead = null;
//					try 
//					{
//						//Data.O/p.Stream
//						File file = new File(fullFilePath);
//						DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
//						FileInputStream fis = new FileInputStream(file);
//						
//						BufferedInputStream bis = new BufferedInputStream(fis);
//						byte[] mybytearray = new byte[(int) file.length()];
//						DataInputStream dis = new DataInputStream(bis);
//						dis.readFully(mybytearray, 0, mybytearray.length);
//						dos.writeUTF(fileName+":put");
//						long fileSize = file.length();
//						dos.writeLong(fileSize);
//						
//						dos.write(mybytearray, 0, mybytearray.length);
//						dos.flush();
//						
//						/*bufRead = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//						int index;
//		                while((index=bufRead.read())!=-1)
//		                {
//		                    dos.write(index);
//		                }*/
//		                log.info("File transfered");
//		                //bufRead.close();
//		                dis.close();
//		                dos.close();
//					//clisocket.close();
//
//					}
//					catch (IOException e) 
//					{
//						// TODO Auto-generated catch block
//						log.error(e);
//						//e.printStackTrace();
//					}
//				}
////			}
//			pw.close();
//			reader.close();
//			writer.close();
//			clientSocket.close();
//			log.info("All connections closed, bye");
//			System.out.println("All connections closed, bye");
//
//		} catch (IOException e) {
//			// e.printStackTrace();
//		}
//	}
//
//	// this will return no more than 3 random ip except this machine's and the
//	// leader's
//	public static Set<String> getrandom3IpAddresses() {
//		HashMap<String, NodeData> gossipMap = new HashMap<String, NodeData>();
//		gossipMap.putAll(Node._gossipMap);
//		Set<String> ips = new HashSet<String>();
//
//		int len = gossipMap.size();
//		if (len != 0) {
//			// retrieve the ip list from membership list
//			String[] retVal = new String[len];
//			int i = 0;
//			for (Map.Entry<String, NodeData> rec : gossipMap.entrySet()) {
//				String id = rec.getKey();
//				String[] temp = id.split(":");
//				retVal[i] = temp[0];
//				++i;
//			}
//			// get two random ip address
//			// if there only one member beside this machine.
//			if (len == 1) {
//				ips.add(retVal[0]);
//			}
//			// if there're two members other than itself
//			else if (len == 2) {
//				ips.add(retVal[0]);
//				ips.add(retVal[1]);
//			}
//			// if there're three members other than itself
//			else if (len == 2) {
//				ips.add(retVal[0]);
//				ips.add(retVal[1]);
//				ips.add(retVal[2]);
//			}
//			// when there're more than 2 member, randomly select two
//			else {
//				while (ips.size() < 3) {
//					// logic here only works for process num less than 10
//					double rand = Math.random();
//					rand = rand * 100;
//					int index = (int) (rand % len);
//					ips.add(retVal[index]);
//				}
//			}
//		} else {
//			// System.out.println("No member of the membership list");
//		}
//		return ips;
//	}
//
//	public void updateFileList(Set<String> ipSet, String fileName,
//			String operation) {
//		log.info(operation + " on " + fileName);
//		if (Node._fileMap.isEmpty()) {
//			// a new method
//			String messageCounts = "msg#";
//			List<String> firstList = new ArrayList<String>();
//			firstList.add("0");
//			firstList.add("0");
//			firstList.add("0");
//			Node._fileMap.put(messageCounts, firstList);
//		}
//
//		if (operation.equalsIgnoreCase("put")) {
//			String counts = String.valueOf(++Node._fileMsgCounter);
//			Node._fileMap.get("msg#").set(0, counts);
//			List<String> addressList = new ArrayList<String>();
//
//			for (String addr : ipSet) {
//				addressList.add(addr);
//			}
//			Node._fileMap.put(fileName, addressList);
//			// pass the file list to others
//			Thread fileListThread = new FileListSenderThread(
//					Node._gossipFileListPort, true, null);
//			fileListThread.start();
//		} else if (operation.equalsIgnoreCase("delete")) {
//			String counts = String.valueOf(++Node._fileMsgCounter);
//			Node._fileMap.get("msg#").set(0, counts);
//			Node._fileMap.remove(fileName);
//			// pass the file list to others
//			Thread fileListThread = new FileListSenderThread(
//					Node._gossipFileListPort, true, null);
//			fileListThread.start();
//		}
//	}
//
//	public void putFile(String fileName, String receiverIp, String putFlag) {
//		// put file
//		String fullFilePath = Node.sdfsFilePath + fileName;
//		// BufferedReader bufRead = null;
//		try {
//			// logic to ping the master and get the list of ip's
//			Socket socket = new Socket(receiverIp,
//					Node._TCPPortForFileTransfers);
//			// Data.O/p.Stream
//			File file = new File(fullFilePath);
//			FileInputStream fis = new FileInputStream(file);
//			DataOutputStream dos = new DataOutputStream(
//					socket.getOutputStream());
//			BufferedInputStream bis = new BufferedInputStream(fis);
//			byte[] mybytearray = new byte[(int) file.length()];
//			DataInputStream dis = new DataInputStream(bis);
//			dis.readFully(mybytearray, 0, mybytearray.length);
//
//			fileName = fileName + ":" + putFlag;
//
//			dos.writeUTF(fileName);
//			long fileSize = file.length();
//			dos.writeLong(fileSize);
//			dos.write(mybytearray, 0, mybytearray.length);
//			dos.flush();
//
//			/*
//			 * bufRead = new BufferedReader(new InputStreamReader(new
//			 * FileInputStream(file))); int index;
//			 * while((index=bufRead.read())!=-1) { dos.write(index); }
//			 */
//			log.info("File transfered");
//			// bufRead.close();
//			dis.close();
//			dos.close();
//			socket.close();
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			log.error(e);
//			// e.printStackTrace();
//		}
//	}
//}
