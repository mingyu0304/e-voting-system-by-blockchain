package com.francis.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.*;

import java.util.ArrayList;
import java.util.HashMap;

import com.francis.blockchain.Block;
import com.francis.network.ClientManager;
import com.francis.network.ServerManager;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import static java.lang.System.exit;

/**
 * Main
 * 서버와 클라이언트가 상호작용하는 P2P 네트워크를 설계
 * 서버 : 지역구 선거위원회
 * 클라이언트 : 유권자
 */

public class Main {
    private static final String DEFAULT_SERVER_ADDR = "localhost";
    private static final int DEFAULT_PORT = 6777;

    // Main 실행문
    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider

        System.out.println(" ------- MAIN MENU ------- \n");
        System.out.println("1. Cast Votes");
        System.out.println("2. View Votes on Blockchain");
        System.out.println("3. Count Votes");
        System.out.println("0. Exit\n");

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your choice: ");
        int ch = scanner.nextInt();

        // ch == 1 : 투표 진행
        if(ch == 1) {
            System.out.println("\n ------- Casting Votes ------- \n");
            System.out.println("Please choose a role you want to be: server or client.");
            System.out.println("server PORT - The port to listen to; \"6777\" is default port.");
            System.out.println("client SERVER_ADDRESS PORT - The server address and port to connect to; \"localhost:6777\" is default address-prt combination.");
            System.out.println("Make sure run the server first and then run client to connect to it.");
            System.out.println("> -------------- ");

            Scanner in = new Scanner(System.in); // 선거위원회 : server 입력, 투표자 : client 입력
            String line = in.nextLine();
            String[] cmd = line.split("\\s+"); // 공백 기준으로 split

            // 선거위원회 : server
            if(cmd[0].equals("server")) {
                int port = DEFAULT_PORT; // 기본 포트 번호 사용

                // 추가로 사용해야 하는 포트 번호에 대한 입력이 들어오는 경우
                if (cmd.length > 1) {
                    try {
                        port = Integer.parseInt(cmd[1]); // 포트 번호 변경
                    } catch(NumberFormatException e) {
                        System.out.println("Error: port is not a number!"); // 예외처리
                        in.close(); // Scanner 연결 중단
                        return;
                    }
                }

                ServerManager _svrMgr =new ServerManager(port); // ServerManager 호출 ***** 추가 작성
                new Thread(_svrMgr).start();
            }
            // 투표자 :  client
            else if (cmd[0].equals("client")) {
                String svrAddr = DEFAULT_SERVER_ADDR; // 기본 서버 주소 사용
                int port = DEFAULT_PORT; // 기본 포트 번호 사용

                // 추가로 사용해야 하는 포트 번호나 서버 주소에 대한 입력이 들어오는 경우
                if (cmd.length > 2) {
                    try {
                        svrAddr = cmd[1];
                        port = Integer.parseInt(cmd[2]);
                    } catch(NumberFormatException e) {
                        System.out.println("Error: port is not a number!"); // 예외처리
                        in.close(); // Scanner 연결 중단
                        return;
                    }
                }

                ClientManager _cltMgr = new ClientManager(svrAddr, port); // ClientManager 호출
                new Thread(_cltMgr).start(); // 메세지를 받을 수 있는 새로운 스레드 생성
                _cltMgr.startClient();
            }
            // 보충 정보 제공
            else {
                showHelp(); // 보충 정보 제공
                in.close(); // Scanner 연결 중단
                return;
            }
            in.close(); // Scanner 연결 중단
        }

        // ch == 2 : 투표 현황과 블록체인 출력
        else if(ch == 2) {
            System.out.println("\n ----- Displaying Votes ----- \n");

            // 파일위치 불러오기
            String userHomePath = System.getProperty("user.home");
            String fileName;
            fileName = userHomePath + "/Desktop/blockchain_data";
            File f = new File(fileName);

            // 파일 불러오고 블록 출력
            try {
                // 파일이 존재하지 않는 경우
                if(!f.exists())
                    System.out.println("Blockchain file not found");

                ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName)); //입력스트림 생성

                ArrayList<SealedObject> arr = (ArrayList<SealedObject>) in.readObject(); // SealedObject : 암호화된 블록
                for(int i=1; i<arr.size(); i++) {
                    System.out.println(decrypt(arr.get(i))); // 해독하여 출력
                }
                in.close(); // 입력스트림 종료

                System.out.println("-------------------------\n");

            } catch (IOException e) {
                e.printStackTrace(); // 예외 처리
            } catch (ClassNotFoundException e) {
                e.printStackTrace(); // 예외 처리
            } catch (NoSuchPaddingException e) {
                e.printStackTrace(); // 예외 처리
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace(); // 예외 처리
            } catch (InvalidKeyException e) {
                e.printStackTrace(); // 예외 처리
            }
        }

        // ch == 3 : 최종 집계
        else if(ch == 3) {
            // 파일위치 불러오기
            String userHomePath = System.getProperty("user.home");
            String fileName;
            fileName = userHomePath + "/Desktop/blockchain_data";
            File f = new File(fileName);

            try {
                // 파일이 존재하지 않는 경우
                if(!f.exists())
                    System.out.println("Please cast your votes first !");

                else {
                    System.out.println();
                    System.out.println("-------------------------");
                    System.out.println("Vote count: ");
                    ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName)); // 입력스트림 생성

                    ArrayList<Block> chkBlock = new ArrayList<Block>();
                    ArrayList<SealedObject> arr = (ArrayList<SealedObject>) in.readObject(); // 블록체인
                    HashMap<String, Integer> voteMap = new HashMap<>();

                    chkBlock.add((Block) decrypt(arr.get(0)));
                    for(int i = 1; i < arr.size(); i++) {
                        Block blk = (Block) decrypt(arr.get(i)); // 블록해독
                        chkBlock.add(blk);
                        String key = blk.getVoteObj().getVoteParty(); // 투표를 받느 후보자의 key

                        voteMap.put(key, 0); // HashMap 에 후보자 이름을 키로 등록
                    }

                    if(isChainValid(chkBlock)) {
                        for(int i = 1; i < arr.size(); i++) {
                            Block blk = (Block) decrypt(arr.get(i)); // 블록해독
                            String key = blk.getVoteObj().getVoteParty(); // 투표를 받느 후보자의 key

                            voteMap.put(key, voteMap.get(key) + 1); // 누적된 투표수 + 1
                        }
                        in.close(); // 입력스트림 종료

                        // 최종 투표 결과 출력
                        for(Map.Entry<String, Integer> entry : voteMap.entrySet()) {
                            System.out.println(entry.getKey() + " : " + entry.getValue());
                        }

                        System.out.println("-------------------------\n");
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace(); // 예외처리
            } catch (ClassNotFoundException e) {
                e.printStackTrace(); // 예외처리
            } catch (NoSuchPaddingException e) {
                e.printStackTrace(); // 예외처리
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace(); // 예외처리
            } catch (InvalidKeyException e) {
                e.printStackTrace(); // 예외처리
            }
        }

        // ch == 0 : 시스템 종료
        else if(ch == 0)
            exit(0);
    }

    // 보충 정보 제공
    public static void showHelp() {
        System.out.println("Restart and select role as server or client.");
        exit(0); // 프로그램 종료
    }

    // 해독 함수
    public static Object decrypt(SealedObject sealedObject) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
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

        for(int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            // 블록 해시값 체크
            if(!currentBlock.getBlockHash().equals(currentBlock.calculateHash()) ){
                System.out.println("#Current Hashes not equal");
                return false;
            }
            // 이전 블록과의 해시값 체크
            if(!previousBlock.getBlockHash().equals(currentBlock.getPreviousHash()) ) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            // 해시값이 채굴 조건을 만족하는지 확인
            if(!currentBlock.getBlockHash().substring(0, ClientManager.difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            // 투표내역 확인 : 디지털 서명 확인
            if(!currentBlock.getVoteObj().verifySignature()) {
                System.out.println("#Signature on Transaction is Invalid");
                return false;
            }
        }
        System.out.println("Blockchain is valid");
        return true;
    }
}
