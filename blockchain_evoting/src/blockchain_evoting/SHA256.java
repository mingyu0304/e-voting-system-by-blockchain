package blockchain_evoting;

import java.security.MessageDigest;

public class SHA256 {
	private MessageDigest digest;
	public String returnHashString(String baseString) {
		try {
			StringBuffer hashString=new StringBuffer();
			byte[] hashByte;
			digest=MessageDigest.getInstance("SHA-256");
			hashByte=digest.digest(baseString.getBytes("UTF-8"));
			for(int i=0;i<hashByte.length;i++) {
				String hex=Integer.toHexString(0xff&hashByte[i]);
				if(hex.length()==1) {
					hashString.append('0');
				}
				hashString.append(hex);
			}
			return hashString.toString();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}