package com.dataStore.main;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.BitTorrent.TrackerServer;
import com.BitTorrentRequestHandler.ReqListnerTorrent;
import com.BitTorrentRequestHandler.ReqSenderTorrent;
import com.BitTorrentRequestHandler.TorrentFileListner;
//import com.BitTorrentRequestHandler.ReqSenderTorrent;
import com.dataStore.requestHandler.ReqListener;
import com.dataStore.requestHandler.ReqSender;
import com.dataStore.scheduler.LeaderScanThread;
import com.dataStore.scheduler.ListScanThread;
import com.dataStore.data.FileData;
import com.dataStore.data.NodeData;
import com.dataStore.election.ElectionListenerThread;
import com.dataStore.fileTransfer.FileReceiver;
import com.dataStore.gossip.FileListListenerThread;
import com.dataStore.gossip.GossipListenerThread;
import com.dataStore.gossip.GossipSenderThread;
import com.dataStore.gossip.IntroducerRejoinThread;

/**
 * @author pshrvst2
 * @version Beta
 * @author Shivam Upadhyay
 * @Info This is the main class in the application.It has the duty to register
 *       itself, send and receive hearbeats from other peers in the group.
 *
 */
public class Node {
	// Naming convention, variables which begin with _ are class members.
	public static Logger _logger = Logger.getLogger(Node.class);
	public final static int _gossipFileListPort = 2001;
	public final static int _gossipMemberListPort = 2000;
	public final static int _TCPPortForElections = 3000;
	public final static int _TCPPortForRequests = 3001;
	public final static int _TCPPortForFileTransfers = 3002;
	public final static int _TCPPorstForTorrentRquest = 4001;
	public final static int _TCPPorstForTorrentFileRquest = 4002;
	public final static int _bitTorrentPort = 6969;
	public static String _introducerIp = "";
	public static boolean _gossipListenerThreadStop = false;
	public static boolean _fileListListenerThreadStop = false;
	public static boolean _electionListenerThreadStop = false;
	public static boolean _reqListenerThreadStop = false;
	public static boolean _fileReceiverThreadStop = false;
	public static String _machineIp = "";
	public static String _machineId = "";
	public static int _TfailInMilliSec = 3000;
	public static int _TCleanUpInMilliSec = 3000;
	public static TimeUnit unit = MILLISECONDS;
	public static int _totalCounts = 0;
	public static int _lossCounts = 0;
	public final static String _electionMessage = "ELECTION START FROM:";
	public final static String _okMessage = "ok";
	public final static String _coordinatorMessage = "NEW LEADER :";
	public final static int _timeOutForElection = 3;
	public static boolean _isIntroducer = false;
	public static int _fileMsgCounter = 0;
	public static TrackerServer _trackerServer =null;

	// need to remove this after testing the fileList threads
	// public static int _fileNameInt = 0;

	// public static List<NodeData> _gossipList =
	// Collections.synchronizedList(new ArrayList<NodeData>());
	// Thread safe data structure needed to store the details of all the
	// machines in the
	// Gossip group. Concurrent hashmap is our best option as it can store
	// string, nodeData.
	public static Map<String, NodeData> _gossipMap = new ConcurrentHashMap<String, NodeData>();

	// a new HashMap for storing the file list. This map can be accessed by
	// different threads so it better to use concurrent hashmap
	public static Map<String, List<String>> _fileMap = new ConcurrentHashMap<String, List<String>>();
	public static Map<String, FileData> _torrentFileMap = new ConcurrentHashMap<String, FileData>();

	// another Hash Map for Replicate copy
	public static Map<String, List<String>> _fileReplicaMap = new ConcurrentHashMap<String, List<String>>();
	// detect whether we have the leader, if not, may wanna keep above hash map,
	// else, clean up the has map cause job has been done by leader
	public static boolean _hasLeader = false;

	public final static String localFilePath = "/home/upadhyy3/local/";
	public final static String sdfsFilePath = "/home/upadhyy3/sdfs/";
	public final static String torrentFilePath = "/home/upadhyy3/bitTorrentTestFolder/";

	// final static String localFilePath = "/home/xchen135/local/";
	// final static String sdfsFilePath = "/home/xchen135/sdfs/";

	/**
	 * @param args
	 *            To ensure : Server init has to be command line.
	 */
	public static void main(String[] args) {
		Thread gossipListener = null;
		// Thread electionListener = null;
		try {
			if (initLogging()) {
				_logger.info("Logging is succesfully initialized! Refer log file CS525.log");
				System.out
						.println("Logging is succesfully initialized! Refer log file CS525.log");
			} else {
				_logger.info("Logging could not be initialized!");
				System.out.println("Logging could not be initialized!");
			}

			_machineIp = InetAddress.getLocalHost().getHostAddress().toString();

			// no matter what, just delete your SDFS when you join.
			deleteSDFS();
			long space = 0;
			int bandwidth = 0;
			;
			boolean flag = true;
			while (flag) {
				System.out.println("\tWelcome to the YourDataStore Inc!");
				System.out.println("\tPress 1 to join");
				System.out.println("\tPress 2 for system info");
				System.out.println("\t!!Press any other key to shoot you!!");
				BufferedReader readerKeyboard = new BufferedReader(
						new InputStreamReader(System.in));
				String option = readerKeyboard.readLine();

				if (option.equalsIgnoreCase("1")) {

					// Scanner input = new Scanner(System.in);
					System.out.println("Enter the Space you want to give");
					space = Long.valueOf(readerKeyboard.readLine());
					System.out.println("Enter the Bandwidth speed you have");
					bandwidth = Integer.valueOf(readerKeyboard.readLine());
					flag = false;
				}

				else if (option.equalsIgnoreCase("2")) {
					System.out.println("\tYou are at machine: " + _machineIp);
				} else {
					System.out
							.println("\tYou are dead and will be on the instagram soon! RIP!!!!");
					return;
				}
			}

			// Concatenate the ip address with time stamp.
			Long currTimeInMiliSec = System.currentTimeMillis();
			_machineId = _machineIp + ":" + currTimeInMiliSec;

			_logger.info("Machine IP: " + _machineIp + " and Machine ID: "
					+ _machineId);
			_logger.info("Adding it's entry in the Gossip list!");
			// System.out.println(machineId);
			NodeData node = new NodeData(_machineId, 1l, currTimeInMiliSec,
					true, space, bandwidth, checkSuperNode(space, bandwidth));
			_gossipMap.put(_machineId, node);
			// _gossipList.add(node);
			_logger.info("Attrributes for created Nodes are {}"
					+ node.getSpace() + node.getBandwidth());
			// check systems properties and load them
			if (!loadSystemProperties()) {
				System.out
						.println("System.properties file missing or wrong config! Exiting!");
				return;
			}

			// check for introducer
			checkIntroducer(_machineIp, node);

			if (_machineIp.equalsIgnoreCase(_introducerIp)) {
				_trackerServer = new TrackerServer(
						_machineIp, _bitTorrentPort);
				_logger.info("Created tracker server on the server to announce torrent");
				_trackerServer.startTracker();
				_logger.info("Started Tracker Server on url"+ _trackerServer.getTracker().getAnnounceUrl().getHost().toString());
			}
			Thread fileListener = new FileListListenerThread(
					_gossipFileListPort);
			fileListener.start();

			// Now open your socket and listen to other peers.
			gossipListener = new GossipListenerThread(_gossipMemberListPort);
			gossipListener.start();

			// Now open TCP socket for election
			Thread electionListener = new ElectionListenerThread(
					_TCPPortForElections);
			electionListener.start();

			// open Req Listener socket
			Thread reqListener = new ReqListener(_TCPPortForRequests);
			reqListener.start();

			// open File Receive socket
			Thread fileReceiver = new FileReceiver(_TCPPortForFileTransfers);
			fileReceiver.start();

			// open Req Listner for Torrent at 4001
			Thread reqListenerTorrent = new ReqListnerTorrent(
					_TCPPorstForTorrentRquest);
			reqListenerTorrent.start();

			// open File Listner for Torrent receiving torrent file at 4002
			Thread fileListenerTorrent = new TorrentFileListner(
					_TCPPorstForTorrentFileRquest);
			fileListenerTorrent.start();
			// logic to send periodically
			ScheduledExecutorService _schedulerService = Executors
					.newScheduledThreadPool(4);
			_schedulerService.scheduleAtFixedRate(new GossipSenderThread(
					_gossipMemberListPort), 0, 500, unit);

			// logic to scan the list and perform necessary actions.
			_schedulerService.scheduleAtFixedRate(new ListScanThread(), 0, 100,
					unit);

			// logic to check the leader status, if no leader exist, start an
			// election
			_schedulerService.scheduleAtFixedRate(new LeaderScanThread(), 0,
					2000, unit);

			// logic to check whether the introducer is trying to rejoin again
			if (_machineIp != _introducerIp) {
				// we will check this occasionally
				_schedulerService.scheduleAtFixedRate(
						new IntroducerRejoinThread(), 0, 5000, unit);
			}

			flag = true;
			while (flag) {
				System.out.println("\nHere are your options: ");
				System.out
						.println("Type 'list' to view the current membership list.");
				System.out
						.println("Type 'quit' to quit the group and close servers");
				System.out.println("Type 'info' to know your machine details");

				// new user options for MP3
				System.out
						.println("Type 'put <filename>' to replicate the file on SDFS");
				System.out
						.println("Type 'get <filename>' to to get SDFS file to local file system");
				System.out
						.println("Type 'delete <filename>' to delete file from SDFS");
				System.out.println("Type 'store' to show the file list");
				System.out
						.println("Type getTorrent '<filename>' to get file from the server");

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(System.in));
				String userCmd = reader.readLine();

				if (userCmd.startsWith("getTorrent")) {
					String command[] = userCmd.split("\\s");
					if (command.length != 2) {
						System.out.println("Enter the command correctly.");
						_logger.info("Invalid command. Enter the command correctly.");
					} else {
						String serverip = null;
						serverip = getLeadIp();
						if (serverip != null) {
							// Thread reqInstance = new ReqSender(command[0],
							// command[1], serverip, _TCPPortForRequests);
							Thread reqInstance = new ReqSenderTorrent(
									command[0], command[1], serverip,
									_TCPPorstForTorrentRquest);
							reqInstance.start();
						} else {
							System.out
									.println("There is no Leader. File can't be obtained for now");
						}
					}
				} else if (userCmd.equalsIgnoreCase("list")) {
					if (!_gossipMap.isEmpty()) {
						String delim = "\t||\t";
						System.out.println("*********MachineId********" + delim
								+ "**Last Seen**" + delim + "Hearbeat" + delim
								+ "Is Active?" + delim + "PID" + delim
								+ "Is leader?");
						_logger.info("User want to list the current members");
						_logger.info("*********MachineId********" + delim
								+ "**Last Seen**" + delim + "Hearbeat" + delim
								+ "Is Active?" + delim + "PID" + delim
								+ "Is leader?");
						for (Map.Entry<String, NodeData> record : _gossipMap
								.entrySet()) {
							NodeData temp = record.getValue();
							System.out.println(record.getKey() + delim
									+ temp.getLastRecordedTime() + delim
									+ temp.getHeartBeat() + "\t" + delim
									+ temp.isActive() + "\t" + delim
									+ temp.getPid() + delim + temp.isLeader());
							_logger.info(record.getKey() + delim
									+ temp.getLastRecordedTime() + delim
									+ temp.getHeartBeat() + "\t" + delim
									+ temp.isActive() + "\t" + delim
									+ temp.getPid() + delim + temp.isLeader());
						}
					}
				} else if (userCmd.startsWith("put")) {
					String command[] = userCmd.split("\\s");
					if (command.length != 2) {
						System.out.println("Enter the command correctly.");
						_logger.info("Invalid command. Enter the command correctly.");
					} else {
						// Logic - check for file at local, if exists then
						// contact master.
						// if replicas of file already exists, then master would
						// deny further replicas
						// else, master returns three ip addresses to which file
						// will be replicated.
						// once file is replicated in all the three vm's, master
						// edits the file list and gossips it all.

						// check for the file at local
						File file = new File(localFilePath + command[1]);
						// create torrent
						if (file.exists()) {
							String serverip = null;
							serverip = getLeadIp();
							int p = _TCPPortForRequests;
							if (serverip != null) {
								Thread reqInstance = new ReqSender(command[0],
										command[1], serverip, p);
								reqInstance.start();
							} else {
								System.out
										.println("There is no Leader. File operations cannot be done now");
							}
						} else {
							System.out.println("File not found");
						}
					}
				} else if (userCmd.startsWith("get")) {
					String command[] = userCmd.split("\\s");
					if (command.length != 2) {
						System.out.println("Enter the command correctly.");
						_logger.info("Invalid command. Enter the command correctly.");
					} else {
						String serverip = null;
						serverip = getLeadIp();
						if (serverip != null) {
							Thread reqInstance = new ReqSender(command[0],
									command[1], serverip, _TCPPortForRequests);
							reqInstance.start();
						} else {
							System.out
									.println("There is no Leader. File operations cannot be done now");
						}
					}

				} else if (userCmd.startsWith("delete")) {
					String command[] = userCmd.split("\\s");
					if (command.length != 2) {
						System.out.println("Enter the command correctly.");
						_logger.info("Invalid command. Enter the command correctly.");
					} else {
						String serverip = null;
						serverip = getLeadIp();
						if (serverip != null) {
							Thread reqInstance = new ReqSender(command[0],
									command[1], serverip, _TCPPortForRequests);
							reqInstance.start();
						} else {
							System.out
									.println("There is no Leader. File operations cannot be done now");
						}
					}
				} else if (userCmd.startsWith("store")) {
					String delim = "\t||\t";
					System.out.println("*********File Name********" + delim
							+ "**Address 1**" + delim + "Address 2" + delim
							+ "Address 3");
					_logger.info("User want to list the files");
					_logger.info("*********File Name********" + delim
							+ "**Address 1**" + delim + "Address 2" + delim
							+ "Address 3");
					for (Map.Entry<String, List<String>> record : _fileMap
							.entrySet()) {
						List<String> temp = record.getValue();
						if (temp.size() == 3) {
							System.out.println(record.getKey() + delim
									+ temp.get(0) + "\t" + delim + temp.get(1)
									+ "\t" + delim + temp.get(2) + "\t");
							_logger.info(record.getKey() + delim + temp.get(0)
									+ "\t" + delim + temp.get(1) + "\t" + delim
									+ temp.get(2) + "\t");
						} else if (temp.size() == 2) {
							System.out.println(record.getKey() + delim
									+ temp.get(0) + "\t" + delim + temp.get(1)
									+ "\t");
							_logger.info(record.getKey() + delim + temp.get(0)
									+ "\t" + delim + temp.get(1) + "\t");
						} else if (temp.size() == 1) {
							System.out.println(record.getKey() + delim
									+ temp.get(0) + "\t");
							_logger.info(record.getKey() + delim + temp.get(0)
									+ "\t");
						}
					}
				} else if (userCmd.equalsIgnoreCase("quit")) {
					// send a good bye message to the Introducer so that you are
					// quickly observed by
					// all nodes that you are leaving.
					System.out.println("Terminating");
					_logger.info("Terminating");
					_gossipListenerThreadStop = true;
					_fileListListenerThreadStop = true;
					_electionListenerThreadStop = true;
					_reqListenerThreadStop = true;
					_fileReceiverThreadStop = true;
					Node._gossipMap.get(_machineId).setIsLeader(false);
					Node._gossipMap.get(_machineId).setActive(false);
					Node._gossipMap.get(_machineId).increaseHeartBeat();
					flag = false;
					Thread.sleep(1001);
					_schedulerService.shutdownNow();
				} else if (userCmd.equalsIgnoreCase("info")) {
					NodeData temp = _gossipMap.get(_machineId);
					String delim = "\t||\t";
					System.out.println("*********MachineId********" + delim
							+ "**Last Seen**" + delim + "Hearbeat" + delim
							+ "Is Active?");
					System.out.println(temp.getNodeId() + delim
							+ temp.getLastRecordedTime() + delim
							+ temp.getHeartBeat() + "\t" + delim
							+ temp.isActive());
					_logger.info(temp.getNodeId() + delim
							+ temp.getLastRecordedTime() + delim
							+ temp.getHeartBeat() + "" + delim
							+ temp.isActive());
				}
			}
		} catch (UnknownHostException e) {
			_logger.error(e);
			// e.printStackTrace();
		} catch (IOException e) {
			_logger.error(e);
			// e.printStackTrace();
		} catch (InterruptedException e) {
			_logger.error(e);
			// e.printStackTrace();
		} finally {
			System.out.println("Good Bye!");
			_logger.info("Good Bye!");
		}

	}

	public static boolean checkSuperNode(long space, int bandwidth) {

		if (space >= 50 && bandwidth == 1000) {

			return true;
		} else {
			return false;
		}

	}

	private static boolean loadSystemProperties() {
		boolean flag = true;
		try {
			File file = new File("properties/system.properties");
			FileInputStream fis = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();
			Enumeration<Object> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String val = properties.getProperty(key);
				if (key.equals("introIp")) {
					_introducerIp = val;
					return flag;
				}
			}
			_logger.error("System properties missing! Check configurations!!");
			flag = false;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;

	}

	public static boolean deleteSDFS() {
		try {
			String command = "rm -rf " + sdfsFilePath + "*";
			Runtime rt = Runtime.getRuntime();
			rt.exec(new String[] { "bash", "-c", command });
		} catch (IOException e) {
			_logger.info("No such file");
			_logger.error(e);
		}
		return true;
	}

	public static boolean initLogging() {
		try {
			PatternLayout lyt = new PatternLayout("[%-5p] %d %c.class %t %m%n");
			RollingFileAppender rollingFileAppender = new RollingFileAppender(
					lyt, "CS425_MP2_node.log");
			rollingFileAppender.setLayout(lyt);
			rollingFileAppender.setName("LOGFILE");
			rollingFileAppender.setMaxFileSize("64MB");
			rollingFileAppender.activateOptions();
			Logger.getRootLogger().addAppender(rollingFileAppender);
			return true;
		} catch (Exception e) {
			// do nothing, just return false.
			// We don't want application to crash is logging is not working.
			return false;
		}
	}

	public static List<String> getLowerIdList(String id) {
		List<String> idList = new ArrayList<String>();
		int ownPid = _gossipMap.get(id).getPid();
		for (Map.Entry<String, NodeData> record : Node._gossipMap.entrySet()) {
			if (record.getValue().getPid() < ownPid) {
				_logger.info("Found a lower Id. " + record.getKey());
				idList.add(record.getKey());
			}
		}

		return idList;
	}

	public static void checkIntroducer(String ip, NodeData data) {
		_logger.info("Checking for the introducer.");
		DatagramSocket socket = null;
		try {
			if (!ip.equalsIgnoreCase(_introducerIp)) {
				// if this is the case, either the introducer is the first time
				// initialized or trying to rejoin the existing group
				// so we try to contact all the member to contact all the member
				// add itself to the list and retrieve the existing
				// list from any alive members
				socket = new DatagramSocket();
				int length = 0;
				byte[] buf = null;

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream objOpStream = new ObjectOutputStream(
						byteArrayOutputStream);
				// objOpStream.writeObject(_gossipList);
				HashMap<String, NodeData> map = new HashMap<String, NodeData>();
				for (Map.Entry<String, NodeData> record : _gossipMap.entrySet()) {
					map.put(record.getKey(), record.getValue());
				}
				objOpStream.writeObject(map);
				buf = byteArrayOutputStream.toByteArray();
				length = buf.length;

				DatagramPacket dataPacket = new DatagramPacket(buf, length);
				dataPacket.setAddress(InetAddress.getByName(_introducerIp));
				dataPacket.setPort(_gossipMemberListPort);
				int retry = 3;
				// try three times as UDP is unreliable. At least one message
				// will reach :)
				while (retry > 0) {
					socket.send(dataPacket);
					--retry;
				}
			} else {
				// If the introducer up first time or rejoin, give the highest
				// priority
				data.setPId(1);
				_isIntroducer = true;
			}

		} catch (SocketException ex) {
			_logger.error(ex);
			ex.printStackTrace();
		} catch (IOException ioExcep) {
			_logger.error(ioExcep);
			// ioExcep.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
			_logger.info("Exiting from the method checkIntroducer.");
		}
	}

	// a simple method to pick up the leader id
	public static String getLeadId() {
		String leadId = null;
		for (Map.Entry<String, NodeData> record : Node._gossipMap.entrySet()) {
			if (record.getValue().isLeader() == true) {
				leadId = record.getKey();
				break;
			}
		}
		return leadId;
	}

	public static String getLeadIp() {
		String leadIp = null;
		for (Map.Entry<String, NodeData> record : Node._gossipMap.entrySet()) {
			if (record.getValue().isLeader() == true) {
				String temp[] = record.getKey().split(":");
				leadIp = temp[0];
				break;
			}
		}
		return leadIp;
	}

}
