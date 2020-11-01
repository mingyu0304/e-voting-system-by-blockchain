package blockchain_evoting;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    // 생성자
    public Wallet() {
        generateKeyPair();
    }

    // 사용자에게 KeyPair 제공
    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // key generator 초기화 및 key 생성
            keyGen.initialize(ecSpec, random); //256
            KeyPair keyPair = keyGen.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Getter
    public PublicKey getPublicKey() { return publicKey; }

    // 투표하기
    public Vote throwVote(PublicKey _recipient, String voterPhoneNumber, String voteParty) {
        Vote newVote = new Vote(publicKey, _recipient, voterPhoneNumber, voteParty);
        newVote.generateSignature(privateKey);

        return newVote;
    }
}