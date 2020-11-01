package blockchain_evoting;

import java.security.MessageDigest;

public class SHA256 {
	private MessageDigest digest; //암호화를 진행하는 클래스
	public String returnHashString(String baseString) {
		try {
			StringBuffer hashString=new StringBuffer(); //32비트 암호를 문자열로 변환한 결과
			byte[] hashByte; //32비트 암호
			digest=MessageDigest.getInstance("SHA-256"); //SHA-256 암호화를 진행한다고 선언
			hashByte=digest.digest(baseString.getBytes("UTF-8")); //유니코드 형식 UTF-8 선언
			for(int i=0;i<hashByte.length;i++) { //32비트 암호에서 1비트를 문자로 변환아여 합친다.
				String hex=Integer.toHexString(0xff&hashByte[i]); //32비트 암호 중 1비트를 16진법으로 변환 
				if(hex.length()==1) {
					hashString.append('0'); //변환된 16진법 숫자가 1자리면 0X로 표현하기 위해 앞에 0 추가
				}
				hashString.append(hex); //변환된 16진법 숫자를 문자열에 추가
			}
			return hashString.toString(); //변환한 16진법 숫자 반환
		} catch(Exception e) { //예외처리
			throw new RuntimeException(e);
		}
	}
}