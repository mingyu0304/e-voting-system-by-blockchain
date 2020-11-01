package blockchain_evoting;

import java.io.BufferedReader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;
import static java.nio.file.attribute.PosixFilePermission.*;

/**
 * ClientManager
 *
 * Responsible for all the network communications in client side.
 *
 * In a new thread, it will run a loop receiving messages sent from server and
 * dispatch it to the main thread to handle.
 *
 * In the main thread, it provides a message handler handling all the incoming
 * messages. Also, it has interfaces serving ProcessManager.
 *
 */
public class ClientManager extends NetworkManager {

	public static int difficulty = 3; // 임시로 3으로 설정

	/* the socket communicating with server */
	private Socket _socket = null;
	private Block genesisBlock;
	private ArrayList<SealedObject> blockList;

	public ArrayList<SealedObject> getBlockList() {
		return blockList;
	}

	private ArrayList<String> parties = null;
	private ArrayList<Wallet> partiesWallet;
	private HashSet<String> hashVotes;
	private String prevHash = "0";
	private Frame f = null;
	private Frame f5 = null;
	private int width, height;
	private String voterPhoneNumber;

	public void set_FrameInfo(Frame f, int width, int height) {
		this.f = f;
		this.width = width;
		this.height = height;
	}

	private int clientId;

	public ClientManager(String addr, int port) {
		try {
			_socket = new Socket(addr, port);
			System.out.println("Connected to server: " + addr + ":" + port);
			genesisBlock = new Block(prevHash);
			hashVotes = new HashSet<>();
			blockList = new ArrayList<>();
			addBlock(genesisBlock);
			prevHash = genesisBlock.getBlockHash();
		} catch (IOException e) {
			System.out.println("Cannot connect to server " + addr + ":" + port);
			e.setStackTrace(e.getStackTrace());
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 블록을 블록체인에 추가
	public void addBlock(Block newBlock) throws Exception {
		newBlock.mineBlock(difficulty);
		blockList.add(encrypt(newBlock));
	}

	public void startClient() {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Welcome to Voting Machine ! ");
		while (parties == null) {
		}
		Block blockObj = null;
		String voteParty = null;

		try {
			Wallet myWallet = new Wallet();

			System.out.println("Vote for parties:");
			int voteChoice;

			partiesWallet = new ArrayList<Wallet>();

			do {
				for (int i = 0; i < parties.size(); i++) {
					System.out.println((i + 1) + ". " + parties.get(i));
					Wallet partyWallet = new Wallet();

					partiesWallet.add(partyWallet);
				}

				System.out.println("Enter your Vote : ");
				voteParty = br.readLine();
				voteChoice = Integer.parseInt(voteParty);
//	                System.out.println("vote choice : "+ voteChoice);
				if (voteChoice > parties.size() || voteChoice < 1)
					System.out.println("Please enter correct index .");
				else
					break;
			} while (true);

			voteParty = parties.get(voteChoice - 1);
			blockObj = new Block(prevHash);
			blockObj.addVote(
					myWallet.throwVote(partiesWallet.get(voteChoice - 1).getPublicKey(), voterPhoneNumber, voteParty)); // 블록
																														// 생성
			if (checkValidity(blockObj)) {
				hashVotes.add(voterPhoneNumber);
				sendMsg(new MessageStruct(1, encrypt(blockObj)));

				addBlock(blockObj); // 블록체인에 추가
				prevHash = blockObj.getBlockHash();
			} else {
				System.out.println("Vote Invalid.");
			}
			/*
			 * System.out.println("Cast Another Vote (y/n) ? "); choice=br.readLine();
			 */

		} catch (IOException e) {
			System.out.println("ERROR: read line failed!");
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		close();
	}

	public SealedObject encrypt(Block b) throws Exception {
		SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");

		// Create cipher
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

		// Code to write your object to file
		cipher.init(Cipher.ENCRYPT_MODE, sks);

		return new SealedObject(b, cipher);
	}

	private boolean checkValidity(Block blockObj) {
		return !hashVotes.contains((String) blockObj.getVoteObj().getVoterPhoneNumber());
	}

	public void sendMsg(MessageStruct msg) throws IOException {
		sendMsg(_socket, msg);
	}

	// Close the socket to exit.
	public void close() {

		String userHomePath = System.getProperty("user.home");
		String fileName;
		fileName = userHomePath + "/Desktop/blockchain_data";
		File f = new File(fileName);

		try {
			if (!f.exists())
				f.createNewFile();
			else {
				f.delete();
				f.createNewFile();
			}

			// Files.setPosixFilePermissions(f.toPath(),
			// EnumSet.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE, GROUP_READ,
			// GROUP_EXECUTE));
			System.out.println(fileName);

			ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(fileName, true));
			o.writeObject(blockList);

			o.close();

			_socket.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.exit(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void msgHandler(MessageStruct msg, Socket src) {
		switch (msg._code) {
		case 0:
			/* message type sent from server to client */
//				System.out.println((String)msg._content.toString()) ;
			try {
				Block decryptedBlock = (Block) decrypt((SealedObject) msg._content); // 블록 해독
				addBlock(decryptedBlock); // 암호화된 블록을 블록체인에 추가
				prevHash = decryptedBlock.getBlockHash(); // **********
				hashVotes.add(decryptedBlock.getVoteObj().getVoterPhoneNumber()); // hashVote 에 clientID 추가
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); // 예외처리
			}
			break;
		case 1:
			/* message type sent from broadcast to all clients */
			// server manages this
			break;
		case 2:
			clientId = (int) (msg._content);
			break;
		case 3:
			this.parties = msg.parties;
			break;
		case 4:
			voteResultFrame();
			f5.setVisible(false);
			break;
		case 5:
			voteStart();
			f.setVisible(false);
			break;
		case 6:
			// Client send server phoneNumber
			break;
		default:
			break;
		}
	}

	/*
	 * Running a loop to receive messages from server. If it fails when receiving,
	 * the connections is broken. Close the socket and exit with -1.
	 */
	@Override
	public void run() {
		while (true) {
			try {
				receiveMsg(_socket);

			} catch (ClassNotFoundException | IOException e) {
				if (_socket.isClosed()) {
					System.out.println("Bye.");
					System.exit(0);
				}

				System.out.println("Connection to server is broken. Please restart client.");
				close(_socket);
				System.exit(-1);
			}
		}
	}

	private void voteStart() {
		Frame f4 = new Frame("후보 선택");
		f4.setSize(width, height);
		f4.setLayout(new GridLayout(2, 1));
		f4.setVisible(true);

		Label labelArray[] = new Label[parties.size()];
		for (int i = 0; i < parties.size(); i++) {
			labelArray[i] = new Label((i + 1) + "번 " + parties.get(i));
			f4.add(labelArray[i]);

		}

		Label l1 = new Label("누구를 투표하시겠습니까? :");
		TextField text1 = new TextField(20);

		Button b = new Button("완료");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int voteChoice = Integer.parseInt(text1.getText());
				String voteParty = parties.get(voteChoice - 1);
				Block blockObj = new Block(prevHash);
				try {
					sendMsg(new MessageStruct(1, encrypt(blockObj)));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				prevHash = blockObj.getBlockHash();
				try {
					getBlockList().add(encrypt(blockObj));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				waitingVoteEnd();
				f4.setVisible(false);
			}
		});
		f4.add(l1);
		f4.add(text1);
		f4.add(b);
	}

	private void waitingVoteEnd() {
		f5 = new Frame("투표 종료 대기중");
		f5.setSize(width, height);
		f5.setLayout(new GridLayout(1, 1));
		f5.setVisible(true);
		Label l1 = new Label("투표 시작 대기중...");
		f5.add(l1);
	}

	private void voteResultFrame() {
		Frame f6 = new Frame("투표 결과");
		f6.setSize(width, height);
		f6.setLayout(new GridLayout(2, 1));
		f6.setVisible(true);
	}

	public void setVoterPhoneNumber(String text) {
		this.voterPhoneNumber = text;

	}

	public static Object decrypt(SealedObject sealedObject)
			throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");

		// Cipher 생성
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, sks);

		try {
			return sealedObject.getObject(cipher);
		} catch (ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			return null;
		}
	}
}