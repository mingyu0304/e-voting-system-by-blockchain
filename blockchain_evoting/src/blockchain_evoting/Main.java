package blockchain_evoting;

import static java.lang.System.exit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 * PMMain
 *
 * Main class of a tiny framework to simulate voting using BlockChain via P2P network.
 *
 * A bi-directional migration between server and client is supported using JAVA Serialization/Reflection and Socket.
 * Detailed system design, user case and limitations are elaborated in report.
 */
public class Main {

    private static final String DEFAULT_SERVER_ADDR = "localhost";
    private static final int DEFAULT_PORT = 6777;

    /*
     * Everything starts from here!
     */
    public static void main(String[] args) {
//        int clientId=0;
        System.out.println(" ----- MAIN MENU ----- \n");
        System.out.println("1. Cast Votes");
        System.out.println("2. View Votes on Blockchain");
        System.out.println("3. Count Votes");
        System.out.println("0. Exit\n");

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your choice: ");
        int ch = scanner.nextInt();

        if(ch == 1)
        {
            System.out.println("\n ----- Casting Votes ----- \n");
            System.out.println("Please choose a role you want to be: server or client.");
            System.out.println("server PORT - The port to listen to; \"6777\" is default port.");
            System.out.println("client SERVER_ADDRESS PORT - The server address and port to connect to; \"localhost:6777\" is default address-prt combination.");
            System.out.println("Make sure run the server first and then run client to connect to it.");
            System.out.println("> ---------- ");

            Scanner in = new Scanner(System.in);
            String line = in.nextLine();
            String[] cmd = line.split("\\s+");

            if (cmd[0].contains("s"))
            {   // server selected

                /* work as server */
                int port = DEFAULT_PORT;
                if (cmd.length > 1) {
                    try {
                        port = Integer.parseInt(cmd[1]);
                    } catch(NumberFormatException e) {
                        System.out.println("Error: port is not a number!");
                        in.close();
                        return;
                    }
                }

                ServerManager _svrMgr =new ServerManager(port);
                new Thread(_svrMgr).start();


            }
            else if (cmd[0].contains("c"))
            {
                //client selected

                /* work as client */
                String svrAddr = DEFAULT_SERVER_ADDR;
                int port = DEFAULT_PORT;
                if (cmd.length > 2) {
                    try {
                        svrAddr = cmd[1];
                        port = Integer.parseInt(cmd[2]);
                    } catch(NumberFormatException e) {
                        System.out.println("Error: port is not a number!");
                        in.close();
                        return;
                    }
                }

                ClientManager _cltMgr = new ClientManager(svrAddr, port);

                /* new thread to receive msg */
                new Thread(_cltMgr).start();

                _cltMgr.startClient();
            }
            else {
                showHelp();
                in.close();
                return;
            }
            in.close();
        }

        // VIEW VOTES
        else if(ch == 2)
        {
            System.out.println("\n ----- Displaying Votes ----- \n");

            String userHomePath = System.getProperty("user.home");
            String fileName;
            fileName=userHomePath+"/Desktop/blockchain_data";
            File f=new File(fileName);

            try
            {
                if(!f.exists())
                    System.out.println("Blockchain file not found");

                ObjectInputStream in=new ObjectInputStream(new FileInputStream(fileName));

                ArrayList<SealedObject> arr=(ArrayList<SealedObject>) in.readObject();
                for(int i=1;i<arr.size();i++) {
                    System.out.println(decrypt(arr.get(i)));
                }
                in.close();

                System.out.println("-------------------------\n");

            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }

        // COUNT VOTES*
        else if(ch == 3)
        {
            String userHomePath = System.getProperty("user.home");
            String fileName;
            fileName=userHomePath+"/Desktop/blockchain_data";
            File f=new File(fileName);

            try
            {
                if(!f.exists())
                    System.out.println("Please cast your votes first !");

                else
                {
                    System.out.println();
                    System.out.println("-------------------------");
                    System.out.println("Vote count: ");
                    ObjectInputStream in=new ObjectInputStream(new FileInputStream(fileName));
                    
                    ArrayList<Block> chkBlock = new ArrayList<Block>();
                    ArrayList<SealedObject> arr=(ArrayList<SealedObject>) in.readObject();
                    HashMap<String,Integer> voteMap = new HashMap<>();
                    chkBlock.add((Block) decrypt(arr.get(0)));
                    
                    for(int i=1; i<arr.size(); i++)
                    {
                        Block blk = (Block) decrypt(arr.get(i));
                        chkBlock.add(blk);
                        String key = blk.getVoteObj().getVoteParty();

                        voteMap.put(key,0);
                    }

                    if (isChainValid(chkBlock)) {
						for (int i = 1; i < arr.size(); i++) {
							Block blk = (Block) decrypt(arr.get(i)); // 블록해독
							String key = blk.getVoteObj().getVoteParty(); // 투표를 받느 후보자의 key

							voteMap.put(key, voteMap.get(key) + 1); // 누적된 투표수 + 1
						}
						in.close(); // 입력스트림 종료

						// 최종 투표 결과 출력
						for (Map.Entry<String, Integer> entry : voteMap.entrySet()) {
							System.out.println(entry.getKey() + " : " + entry.getValue());
						}

						System.out.println("-------------------------\n");
					}
				}

            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }

        else if(ch == 0)
            exit(0);
    }

    public static void showHelp() {
        System.out.println("Restart and select role as server or client.");
        exit(0);
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
 // 블록체인 최종 검증
 	public static boolean isChainValid(ArrayList<Block> blockchain) {
 		Block currentBlock;
 		Block previousBlock;

 		String hashTarget = new String(new char[ClientManager.difficulty]).replace('\0', '0'); // 채굴조건 확인을 위한 문자열

 		for (int i = 1; i < blockchain.size(); i++) {
 			currentBlock = blockchain.get(i);
 			previousBlock = blockchain.get(i - 1);
 			// 블록 해시값 체크
 			if (!currentBlock.getBlockHash().equals(currentBlock.calculateHash())) {
 				System.out.println("#Current Hashes not equal");
 				return false;
 			}
 			// 이전 블록과의 해시값 체크
 			if (!previousBlock.getBlockHash().equals(currentBlock.getPreviousHash())) {
 				System.out.println("#Previous Hashes not equal");
 				return false;
 			}
 			// 해시값이 채굴 조건을 만족하는지 확인
 			if (!currentBlock.getBlockHash().substring(0, ClientManager.difficulty).equals(hashTarget)) {
 				System.out.println("#This block hasn't been mined");
 				return false;
 			}

 			// 투표내역 확인 : 디지털 서명 확인
 			if (!currentBlock.getVoteObj().verifySignature()) {
 				System.out.println("#Signature on Transaction is Invalid");
 				return false;
 			}
 		}
 		System.out.println("Blockchain is valid");
 		return true;
 	}
}
