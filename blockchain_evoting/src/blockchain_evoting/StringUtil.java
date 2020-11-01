package blockchain_evoting;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class StringUtil {
    // Sha256 암호화 알고리즘
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 입력에 Sha256 적용
            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            StringBuffer hexString = new StringBuffer(); // 해시를 16진수로 저장
            for(int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString(); // 문자열로 바꾸어 반환
        }
        catch(Exception e) {
            throw new RuntimeException(e); // 예외처리
        }
    }

    // 타원곡선 DSA 를 적용하여 전자 서명을 반환 (byte)
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

    // 디지털 서명 검증
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            throw new RuntimeException(e); // 예외처리
        }
    }

    // 채굴의 난이도 설정 -> 블록 생성 시간을 조정
    public static String getDifficultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }

    // Key 로부터 문자열 반환
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // merkleRoot 반환
    public static String getMerkleRoot(Vote vote) {
        if(vote == null) {
            return null;
        }
        else {
            return vote.getVoteHash();
        }
    }
}