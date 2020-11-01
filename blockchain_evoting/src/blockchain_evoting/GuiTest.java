package blockchain_evoting;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;

public class GuiTest {
	static private int tempCentifyNumber;
	static public int width = 600, height = 400;
	static private int prevHash;
	private static ArrayList<String> parties = new ArrayList<String>();
	public static Object decrypt(SealedObject sealedObject) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");

        // Create cipher
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, sks);

        try {
//    		System.out.println(sealedObject.getObject(cipher));
            return sealedObject.getObject(cipher);
        } catch (ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

	public static void main(String[] args) {
		Frame f = new Frame("블록체인 투표 프로그램");
		f.setSize(width, height);
		f.setLayout(new GridLayout(1, 3));
		Button btn1 = new Button("투표방 개설");
		Button btn2 = new Button("투표방 입장");
		Button btn3 = new Button("종료");
		btn3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);

			}
		});
		btn2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Frame f2 = new Frame("투표방 입장");
				f2.setSize(width, height);
				f2.setLayout(new FlowLayout(0, 70, 100));
				f2.setVisible(true);
				Label l1 = new Label("투표방 개설자의 IP : ");
				TextField text1 = new TextField(20);
				Label l2 = new Label("투표방 개설자의 Port:");
				TextField text2 = new TextField(20);
				f2.add(l1);
				f2.add(text1);
				f2.add(l2);
				f2.add(text2);
				Button b = new Button("완료");
				b.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String serverIP = text1.getText();
						int port = Integer.parseInt(text2.getText());
						entererNewFrame1(serverIP, port);
						f2.setVisible(false);
					}
				});
				f2.add(b);

			}
		});
		btn1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Frame f1 = new Frame("투표방 개설");
				f1.setSize(width, height);
				f1.setLayout(new FlowLayout(0, 70, 150));
				f1.setVisible(true);
				TextField text1 = new TextField(5);
				Label l1 = new Label("후보자의 명수를 입력하시오.");
				Button b = new Button("입력");
				Label l2 = new Label("포트번호를 선택하시오.(1024~49151)");
				TextField text2 = new TextField(5);

				b.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int candidateNumber = Integer.parseInt(text1.getText());
						int port = Integer.parseInt(text2.getText());
						openerNewFrame1(candidateNumber, port);
						f1.setVisible(false);
					}
				});
				f1.add(l1);
				f1.add(text1);
				f1.add(l2);
				f1.add(text2);
				f1.add(b);
			}
		});
		f.add(btn1);
		f.add(btn2);
		f.add(btn3);
		f.setVisible(true);

	}

	public static int getWidth() {
		return width;
	}

	public static void setWidth(int width) {
		GuiTest.width = width;
	}

	public static int getHeight() {
		return height;
	}

	public static void setHeight(int height) {
		GuiTest.height = height;
	}

	static private void openerNewFrame1(int candidateNumber, int port) {
		Frame f2 = new Frame("후보자 등록");
		f2.setSize(width, height);
		f2.setLayout(new GridLayout(candidateNumber + 1, 2, 50, 0));
		f2.setVisible(true);
		TextField textArray[] = new TextField[candidateNumber];
		Label labelArray[] = new Label[candidateNumber];
		for (int i = 0; i < candidateNumber; i++) {
			labelArray[i] = new Label((i + 1) + "번 후보자");
			textArray[i] = new TextField();
			f2.add(labelArray[i]);
			f2.add(textArray[i]);
			labelArray[i].setAlignment(Label.CENTER);
		}
		Button b = new Button("완료");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < candidateNumber; i++) {
					parties.add(textArray[i].getText());
				}
				openerNewFrame2(port);
				f2.setVisible(false);
			}
		});
		f2.add(b);
	}

	static private void openerNewFrame2(int port) {
		InetAddress local = null;
		try {
			local = InetAddress.getLocalHost();
		} catch (UnknownHostException ee) {
			ee.printStackTrace();
		}
		String ip = local.getHostAddress();
		Frame f3 = new Frame("투표자 입장 대기중");
		f3.setSize(width, height);
		f3.setLayout(new GridLayout(5, 1));
		f3.setVisible(true);
		Label l1 = new Label("투표방 IP : " + ip);
		Label l3 = new Label("투표방 port : " + port);
		Button b1 = new Button("투표 시작");
		Button b2 = new Button("투표방 입장자 수 갱신");
		ServerManager _svrMgr = new ServerManager(port, parties);
		new Thread(_svrMgr).start();
		Label l2 = new Label("현재 투표방 입장자 수:" + _svrMgr.getEntererNumber());
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_svrMgr.voteStartMessage();
				openerNewFrame3(_svrMgr, _svrMgr.getEntererNumber());
				f3.setVisible(false);
			}
		});
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				l2.setText("현재 투표방 입장자 수:" + _svrMgr.getEntererNumber());
			}
		});
		f3.add(l1);
		f3.add(l3);
		f3.add(l2);
		f3.add(b2);
		f3.add(b1);
	}

	static private void openerNewFrame3(ServerManager _svrMgr, int voterNumber) {
		Frame f4 = new Frame("투표자 투표 대기중");
		f4.setSize(width, height);
		f4.setLayout(new GridLayout(4, 1));
		f4.setVisible(true);
		Label l2 = new Label("현재 투표자 수:" + _svrMgr.get_voterNumber());
		Label l4 = new Label("현재 투표 미완료자 수:" + (voterNumber-_svrMgr.get_voterNumber()));
		Button b2 = new Button("투표자수 갱신");
		Button b1 = new Button("투표 종료");
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_svrMgr.voteEndMessage();
				openerNewFrame4(_svrMgr);
				f4.setVisible(false);

			}
		});
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				l2.setText("현재 투표자 수:" + _svrMgr.get_voterNumber());
				l4.setText("현재 투표 미완료자 수:" + (voterNumber-_svrMgr.get_voterNumber()));
			}
		});
		f4.add(l2);
		f4.add(l4);
		f4.add(b2);
		f4.add(b1);
	}

	static private void openerNewFrame4(ServerManager _svrMgr) {
		Frame f5 = new Frame("투표 종료");
		f5.setSize(width, height);
		f5.setLayout(new GridLayout(2, 1));
		f5.setVisible(true);
		Label l1 = new Label("모든 투표자들 화면에 투표결과가 표시되었습니다.");
		Button b = new Button("종료");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		_svrMgr.close();
		f5.add(l1);
		f5.add(b);
	}
	static private void entererNewFrame1(String ip, int port) {
		Frame f3 = new Frame("투표장");
		f3.setSize(width, height);
		f3.setLayout(new GridLayout(2,3));
		f3.setVisible(true);

		Label l1 = new Label("본인인증할 전화번호 입력 : ");
		TextField text1 = new TextField(20);
		ClientManager _cltMgr = new ClientManager(ip, port);
		new Thread(_cltMgr).start();
		Button b = new Button("인증번호 전송");
		msg_centrify msg=new msg_centrify();
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				msg.msg_send(text1.getText());
				/*
				String userId = text1.getText();
				entererNewFrame2(userId);
				f3.setVisible(false);
				*/
			}
		});
		Label l2=new Label("인증번호 : ");
		TextField text2=new TextField(20);
		Button b2=new Button("인증번호 확인");
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					_cltMgr.sendMsg(new MessageStruct(7, ""));
					_cltMgr.sendMsg(new MessageStruct(6, text1.getText()));
					_cltMgr.setVoterPhoneNumber(text1.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				entererNewFrame2(_cltMgr);
				f3.setVisible(false);
				/*
				if(Integer.parseInt(text2.getText())==msg.getCentrifyNumber()) {
					try {
						_cltMgr.sendMsg(new MessageStruct(7, ""));
						_cltMgr.sendMsg(new MessageStruct(6, text1.getText()));
						_cltMgr.setVoterPhoneNumber(text1.getText());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					entererNewFrame2(_cltMgr);
					f3.setVisible(false);
				} else {
					Label l3=new Label("인증번호가 틀렸습니다. 다시 입력해주세요.");
					f3.add(l3);
				}
				*/
			}
		});
		f3.add(l1);
		f3.add(text1);
		f3.add(b);
		f3.add(l2);
		f3.add(text2);
		f3.add(b2);
	}
	static private void entererNewFrame2(ClientManager _cltMgr) {
		Frame f4=new Frame("투표 시작 대기중");
		f4.setSize(width, height);
		f4.setLayout(new GridLayout(1, 1));
		f4.setVisible(true);
		Label l1=new Label("투표 시작 대기중...");
		f4.add(l1);
		_cltMgr.set_FrameInfo(f4, width, height);
	}

}