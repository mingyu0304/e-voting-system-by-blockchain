package blockchain_evoting;

import java.security.MessageDigest;

public class SHA256 {
	private MessageDigest digest; //��ȣȭ�� �����ϴ� Ŭ����
	public String returnHashString(String baseString) {
		try {
			StringBuffer hashString=new StringBuffer(); //32��Ʈ ��ȣ�� ���ڿ��� ��ȯ�� ���
			byte[] hashByte; //32��Ʈ ��ȣ
			digest=MessageDigest.getInstance("SHA-256"); //SHA-256 ��ȣȭ�� �����Ѵٰ� ����
			hashByte=digest.digest(baseString.getBytes("UTF-8")); //�����ڵ� ���� UTF-8 ����
			for(int i=0;i<hashByte.length;i++) { //32��Ʈ ��ȣ���� 1��Ʈ�� ���ڷ� ��ȯ�ƿ� ��ģ��.
				String hex=Integer.toHexString(0xff&hashByte[i]); //32��Ʈ ��ȣ �� 1��Ʈ�� 16�������� ��ȯ 
				if(hex.length()==1) {
					hashString.append('0'); //��ȯ�� 16���� ���ڰ� 1�ڸ��� 0X�� ǥ���ϱ� ���� �տ� 0 �߰�
				}
				hashString.append(hex); //��ȯ�� 16���� ���ڸ� ���ڿ��� �߰�
			}
			return hashString.toString(); //��ȯ�� 16���� ���� ��ȯ
		} catch(Exception e) { //����ó��
			throw new RuntimeException(e);
		}
	}
}