package blockchain_evoting;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class StringUtil {
    // Sha256 ��ȣȭ �˰���
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // �Է¿� Sha256 ����
            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            StringBuffer hexString = new StringBuffer(); // �ؽø� 16������ ����
            for(int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString(); // ���ڿ��� �ٲپ� ��ȯ
        }
        catch(Exception e) {
            throw new RuntimeException(e); // ����ó��
        }
    }

    // Ÿ��� DSA �� �����Ͽ� ���� ������ ��ȯ (byte)
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    // ������ ���� ����
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            throw new RuntimeException(e); // ����ó��
        }
    }

    // ä���� ���̵� ���� -> ��� ���� �ð��� ����
    public static String getDifficultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }

    // Key �κ��� ���ڿ� ��ȯ
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // merkleRoot ��ȯ
    public static String getMerkleRoot(Vote vote) {
        if(vote == null) {
            return null;
        }
        else {
            return vote.getVoteHash();
        }
    }
}