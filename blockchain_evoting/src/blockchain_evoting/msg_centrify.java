package blockchain_evoting;

import java.util.Scanner;
import java.util.HashMap;
import org.json.simple.JSONObject;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;

public class msg_centrify {
	public boolean centrify(String phoneNumber) {
		long starttime=System.currentTimeMillis();
		long endtime=System.currentTimeMillis();
		Scanner scanner=new Scanner(System.in);
		int userInputNumber;
		int centrifyNumber=(int)(Math.random()*1000000);
		msg_send(phoneNumber,centrifyNumber);
		while(true){
			userInputNumber=scanner.nextInt();
			scanner.close();
			endtime=System.currentTimeMillis();
			if(userInputNumber==centrifyNumber&&endtime-starttime<180000) {
				System.out.println("������ �Ϸ�Ǿ����ϴ�.");
				return true;
			}
			else if(endtime-starttime>180000) {
				System.out.println("3���� �ʰ��Ǿ����ϴ�. �ٽ� �������ּ���");
				return false;
			}
			else {
				System.out.println("������ȣ�� Ʋ�Ƚ��ϴ�. �ٽ� �Է����ּ���");
				return false;
			}
		}
	}
	public void msg_send(String phoneNumber,int centrifyNumber) {
		String api_key="NCS0BF2MCGSRSWPY";
		String api_secret="ET4B2OJ0U5LVMS7JSC87PVHPCACNSZRR";
		String sendmsg="������ȣ "+Integer.toString(centrifyNumber)+"�� 3�оȿ� �Է��Ͻÿ�";
		Message coolsms=new Message(api_key, api_secret);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("to", phoneNumber);
		params.put("from", "01095092903");
		params.put("type", "SMS");
		params.put("text", sendmsg);
		params.put("app_version", "test app 1.2");
		try {
			JSONObject obj=(JSONObject) coolsms.send(params);
			System.out.println(obj.toString());
			System.out.println("�������ڸ� "+phoneNumber+"�� �߼��߽��ϴ�.");
		} catch(CoolsmsException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getCode());
		}
	}

}
