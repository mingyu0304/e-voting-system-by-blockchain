package blockchain_evoting;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.SealedObject;

/**
 * ServerManager
 * 
 * Responsible for all the network communications in server side.
 * 
 * In a new thread, it will run a loop accepting all the incoming clients and
 * create a new instance of ServerHandler in a new thread reading incoming
 * message from connected clients.
 * 
 * In the main thread, it provides a message handler handling all the incoming
 * messages. Also, it has interfaces serving ClusterManager.
 */
public class ServerManager extends NetworkManager {

	private int voterNumber = 0;
	private int entererNumber=0;
	public int getEntererNumber() {
		return entererNumber;
	}

	public int get_voterNumber() {
		return voterNumber;
	}

	private ArrayList<String> parties = null;

	private ArrayList<String> phoneNumbers = new ArrayList<String>();

	private ServerSocket _svrSocket = null;

	/* manage the increasing client id to assign a new client an id */
	private volatile AtomicInteger _cid = null;

	/* maintain the map between client id and socket of a client */
	private volatile Map<Integer, Socket> _clients = null;

	public ServerManager(int svrPort, ArrayList<String> parties) {
		try {
			this.parties = parties;
			_clients = new ConcurrentSkipListMap<Integer, Socket>();
			_cid = new AtomicInteger(0);

			_svrSocket = new ServerSocket(svrPort);

			System.out.println("Waiting for clients...");
			System.out.println("Please connect to " + InetAddress.getLocalHost() + ":" + svrPort + ".");
		} catch (IOException e) {
			System.out.println("ERROR: failed to listen on port " + svrPort);
			e.printStackTrace();
		}
	}

	/*
	 * Run a loop to accept incoming clients. Once a connection is established,
	 * create a new instance of ServerHandler in a new thread to receive incoming
	 * messages by running a loop.
	 */
	@Override
	public void run() {
		while (true) {
			try {
				/* accepting new clients */
				Socket socket = _svrSocket.accept();
				addClient(socket);
				// System.out.println("New client(cid is " + getCid(socket) + ") connected!");

				/* create a new instance of ServerHandler to receive messages */
				new ServerHandler(this, socket).start();
				/* send the client id to the new client */
				sendMsg(socket, new MessageStruct(2, Integer.valueOf(getCid(socket))));
				sendMsg(socket, new MessageStruct(3, parties));
			} catch (IOException e) {
				/* ServerSocket is closed */
				break;
			}
		}
	}

	public void clientDisconnected(Socket client) {
		int cid = getCid(client);
		System.out.println("Client " + cid + " has disconnected.");

		deleteClient(cid);
	}

	/* ================== Message handlers begin ================== */

	@Override
	public void msgHandler(MessageStruct msg, Socket src) {
		switch (msg._code) {
		case 0:
			/* message type sent from server to client */
			// client manages this.
			break;
		case 1:
			/* message type sent from server to client */
			// client manages this.
			System.out.println("Broadcasting : " + (String) msg._content.toString());
			broadcast((SealedObject) msg._content, src);
			voterNumber++;
			break;
		case 2:
			/* server sends clientid */
			break;
		case 3:
			// server sends parties to client
			break;
		case 4:
			// server tell client that vote is end
			break;
		case 5:
			// server tell client that vote is start
			break;
		case 6:
			phoneNumbers.add((String) msg._content);
			break;
		case 7:
			this.entererNumber++;
			break;
		default:
			break;
		}
	}

	private void broadcast(SealedObject o, Socket src) {

		// broadcast to all except src
		int srcCid = getCid(src);
		// System.out.println("Source id : "+ srcCid);
		for (int i = _cid.get() - 1; i >= 0; i--) {
			if (i != srcCid) {
				try {
					sendMsg(getClient(i), new MessageStruct(0, o));
				} catch (IOException e) {
					System.out.println("ERROR: Connection with " + srcCid + " is broken, message cannot be sent!");
					e.printStackTrace();
				} catch (NullPointerException e) {
					continue;
				}

			}
		}
	}

	public void voteStartMessage() {
		for (int i = _cid.get() - 1; i >= 0; i--) {

			try {
				sendMsg(getClient(i), new MessageStruct(5, ""));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				continue;

			}
		}
	}
	public void voteEndMessage() {
		for (int i = _cid.get() - 1; i >= 0; i--) {

			try {
				sendMsg(getClient(i), new MessageStruct(4, ""));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				continue;

			}
		}
	}

	/* ================== Message handlers end ================== */

	/* ================== Client info manage methods begin ================== */
	private void addClient(Socket socket) {
		_clients.put(Integer.valueOf(_cid.getAndIncrement()), socket);
	}

	private boolean deleteClient(int idx) {
		if (_clients.remove(Integer.valueOf(idx)) == null) {
			System.out.println("delete failed!");
			return false;
		}
		return true;
	}

	private Socket getClient(int cid) {
		return (Socket) _clients.get(Integer.valueOf(cid));
	}

	private int getCid(Socket socket) {
		for (Map.Entry<Integer, Socket> entry : _clients.entrySet()) {
			if (entry.getValue() == socket) {
				return entry.getKey().intValue();
			}
		}
		return -1;
	}

	/* ================== Client info manage methods end ================== */

	public void close() {
		System.out.println("Server is about to close. All connected clients will exit.");
		try {
			_svrSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Bye~");
	}

}
