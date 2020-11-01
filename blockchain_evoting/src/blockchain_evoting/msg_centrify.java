package blockchain_evoting;

import java.util.Scanner;
import java.util.HashMap;
import org.json.simple.JSONObject;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;

public class msg_centrify {
	private int centrifyNumber;
	public int getCentrifyNumber() {
		return centrifyNumber;
	}
	public boolean centrify(String phoneNumber) { //������ �����ϸ� true, �����ϸ� false�� ��ȯ
		long starttime=System.currentTimeMillis(); //������ȣ ���� �ð� ����
		long endtime=System.currentTimeMillis(); //������ȣ ���� �ð� ����
		Scanner scanner=new Scanner(System.in);
		int userInputNumber; //����ڰ� �Է��� ������ȣ
		int centrifyNumber=(int)(Math.random()*1000000); //������ȣ ���� ����
		msg_send(phoneNumber,centrifyNumber); //������ȣ �߼�
		while(true){ //3�е��� ����ڷκ��� ������ȣ�� �Է¹޴´�
			System.out.print("������ȣ�� �Է��ϼ��� :"); //������ȣ�� �Է��϶�� �޽��� ���
			userInputNumber=scanner.nextInt(); //����ڰ� ������ȣ �Է�
			endtime=System.currentTimeMillis(); //������ȣ �Էµ� �ð� ����
			if(userInputNumber==centrifyNumber&&endtime-starttime<180000) { //������ȣ�� 3���̳� ���߸� ��������
				System.out.println("������ �Ϸ�Ǿ����ϴ�."); //���� ���� �޽��� ���
				scanner.close();
				return true; //true ��ȯ
			}
			else if(endtime-starttime>180000) { //3�� �ڿ� �Է��ϸ� �ٽ� ����
				System.out.println("3���� �ʰ��Ǿ����ϴ�. �ٽ� �������ּ���"); //�ٽ� �����϶�� �޽��� ���
				scanner.close();
				return false; //false ��ȯ
			}
			else { //3�оȿ� �Է������� ������ȣ�� Ʋ���� �ٽ� �Է¹޴´�
				System.out.println("������ȣ�� Ʋ�Ƚ��ϴ�. �ٽ� �Է����ּ���"); //�ٽ� �Է��϶�� �޽��� ���
			}
		}
	}
	public void msg_send(String phoneNumber) { //���ڸ� �߼��ϴ� �Լ�
		centrifyNumber=(int)(Math.random()*1000000);
		String api_key="NCS0BF2MCGSRSWPY"; //���ڸ� �߼��� �� �ʿ��� Ű ��ȣ
		String api_secret="ET4B2OJ0U5LVMS7JSC87PVHPCACNSZRR"; //���ڸ� �߼��� �� �ʿ��� ���Ű
		String sendmsg="������ȣ "+Integer.toString(centrifyNumber)+"�� 3�оȿ� �Է��Ͻÿ�"; //�߼��� �޽���
		Message coolsms=new Message(api_key, api_secret); //coolsms��� ȸ�簡 �����ϴ� �޽��� �߼� ��� Ŭ����
		HashMap<String, String> params = new HashMap<String, String>(); //�޽��� �߼ۿ� �ʿ��� ������ �Է¹޴� HashMap
		params.put("to", phoneNumber); //���Ź�ȣ
		params.put("from", "01095092903"); //�߽Ź�ȣ
		params.put("type", "SMS"); //�޽��� ����
		params.put("text", sendmsg); //���� �޽���
		params.put("app_version", "test app 1.2"); //�޽��� �߼� api ����
		try {
			JSONObject obj=(JSONObject) coolsms.send(params);
			System.out.println(obj.toString()); //�޽��� �߼� ����,���� ���
			System.out.println("�������ڸ� "+phoneNumber+"�� �߼��߽��ϴ�."); //�߼� �Ϸ� �޽���
		} catch(CoolsmsException e) { //�޽��� �߼� ���н� ����ó��
			System.out.println(e.getMessage()); //���� �޽���
			System.out.println(e.getCode()); //���� ����
		}
	}
	public void msg_send(String phoneNumber,int centrifyNumber) { //���ڸ� �߼��ϴ� �Լ�
		String api_key="NCS0BF2MCGSRSWPY"; //���ڸ� �߼��� �� �ʿ��� Ű ��ȣ
		String api_secret="ET4B2OJ0U5LVMS7JSC87PVHPCACNSZRR"; //���ڸ� �߼��� �� �ʿ��� ���Ű
		String sendmsg="������ȣ "+Integer.toString(centrifyNumber)+"�� 3�оȿ� �Է��Ͻÿ�"; //�߼��� �޽���
		Message coolsms=new Message(api_key, api_secret); //coolsms��� ȸ�簡 �����ϴ� �޽��� �߼� ��� Ŭ����
		HashMap<String, String> params = new HashMap<String, String>(); //�޽��� �߼ۿ� �ʿ��� ������ �Է¹޴� HashMap
		params.put("to", phoneNumber); //���Ź�ȣ
		params.put("from", "01095092903"); //�߽Ź�ȣ
		params.put("type", "SMS"); //�޽��� ����
		params.put("text", sendmsg); //���� �޽���
		params.put("app_version", "test app 1.2"); //�޽��� �߼� api ����
		try {
			JSONObject obj=(JSONObject) coolsms.send(params);
			System.out.println(obj.toString()); //�޽��� �߼� ����,���� ���
			System.out.println("�������ڸ� "+phoneNumber+"�� �߼��߽��ϴ�."); //�߼� �Ϸ� �޽���
		} catch(CoolsmsException e) { //�޽��� �߼� ���н� ����ó��
			System.out.println(e.getMessage()); //���� �޽���
			System.out.println(e.getCode()); //���� ����
		}
	}

}
