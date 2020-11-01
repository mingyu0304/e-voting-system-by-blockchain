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
	public boolean centrify(String phoneNumber) { //인증이 성공하면 true, 실패하면 false를 반환
		long starttime=System.currentTimeMillis(); //인증번호 보낸 시간 측정
		long endtime=System.currentTimeMillis(); //인증번호 받은 시간 측정
		Scanner scanner=new Scanner(System.in);
		int userInputNumber; //사용자가 입력한 인증번호
		int centrifyNumber=(int)(Math.random()*1000000); //인증번호 랜덤 생성
		msg_send(phoneNumber,centrifyNumber); //인증번호 발송
		while(true){ //3분동안 사용자로부터 인증번호를 입력받는다
			System.out.print("인증번호를 입력하세요 :"); //인증번호를 입력하라는 메시지 출력
			userInputNumber=scanner.nextInt(); //사용자가 인증번호 입력
			endtime=System.currentTimeMillis(); //인증번호 입력된 시간 측정
			if(userInputNumber==centrifyNumber&&endtime-starttime<180000) { //인증번호를 3분이내 맞추면 인증성공
				System.out.println("인증이 완료되었습니다."); //인증 성공 메시지 출력
				scanner.close();
				return true; //true 반환
			}
			else if(endtime-starttime>180000) { //3분 뒤에 입력하면 다시 인증
				System.out.println("3분이 초과되었습니다. 다시 인증해주세요"); //다시 인증하라는 메시지 출력
				scanner.close();
				return false; //false 반환
			}
			else { //3분안에 입력했지만 인증번호가 틀리면 다시 입력받는다
				System.out.println("인증번호가 틀렸습니다. 다시 입력해주세요"); //다시 입력하라는 메시지 출력
			}
		}
	}
	public void msg_send(String phoneNumber) { //문자를 발송하는 함수
		centrifyNumber=(int)(Math.random()*1000000);
		String api_key="NCS0BF2MCGSRSWPY"; //문자를 발송할 때 필요한 키 번호
		String api_secret="ET4B2OJ0U5LVMS7JSC87PVHPCACNSZRR"; //문자를 발송할 때 필요한 비밀키
		String sendmsg="인증번호 "+Integer.toString(centrifyNumber)+"를 3분안에 입력하시오"; //발송할 메시지
		Message coolsms=new Message(api_key, api_secret); //coolsms라는 회사가 제공하는 메시지 발송 기능 클래스
		HashMap<String, String> params = new HashMap<String, String>(); //메시지 발송에 필요한 정보를 입력받는 HashMap
		params.put("to", phoneNumber); //수신번호
		params.put("from", "01095092903"); //발신번호
		params.put("type", "SMS"); //메시지 형식
		params.put("text", sendmsg); //보낼 메시지
		params.put("app_version", "test app 1.2"); //메시지 발송 api 버전
		try {
			JSONObject obj=(JSONObject) coolsms.send(params);
			System.out.println(obj.toString()); //메시지 발송 성공,실패 출력
			System.out.println("인증문자를 "+phoneNumber+"에 발송했습니다."); //발송 완료 메시지
		} catch(CoolsmsException e) { //메시지 발송 실패시 예외처리
			System.out.println(e.getMessage()); //보낸 메시지
			System.out.println(e.getCode()); //실패 이유
		}
	}
	public void msg_send(String phoneNumber,int centrifyNumber) { //문자를 발송하는 함수
		String api_key="NCS0BF2MCGSRSWPY"; //문자를 발송할 때 필요한 키 번호
		String api_secret="ET4B2OJ0U5LVMS7JSC87PVHPCACNSZRR"; //문자를 발송할 때 필요한 비밀키
		String sendmsg="인증번호 "+Integer.toString(centrifyNumber)+"를 3분안에 입력하시오"; //발송할 메시지
		Message coolsms=new Message(api_key, api_secret); //coolsms라는 회사가 제공하는 메시지 발송 기능 클래스
		HashMap<String, String> params = new HashMap<String, String>(); //메시지 발송에 필요한 정보를 입력받는 HashMap
		params.put("to", phoneNumber); //수신번호
		params.put("from", "01095092903"); //발신번호
		params.put("type", "SMS"); //메시지 형식
		params.put("text", sendmsg); //보낼 메시지
		params.put("app_version", "test app 1.2"); //메시지 발송 api 버전
		try {
			JSONObject obj=(JSONObject) coolsms.send(params);
			System.out.println(obj.toString()); //메시지 발송 성공,실패 출력
			System.out.println("인증문자를 "+phoneNumber+"에 발송했습니다."); //발송 완료 메시지
		} catch(CoolsmsException e) { //메시지 발송 실패시 예외처리
			System.out.println(e.getMessage()); //보낸 메시지
			System.out.println(e.getCode()); //실패 이유
		}
	}

}
