package blockchain_evoting;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    // ������
    public Wallet() {
        generateKeyPair();
    }

    // ����ڿ��� KeyPair ����
    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // key generator �ʱ�ȭ �� key ����
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

    // ��ǥ�ϱ�
    public Vote throwVote(PublicKey _recipient, String voterPhoneNumber, String voteParty) {
        Vote newVote = new Vote(publicKey, _recipient, voterPhoneNumber, voteParty);
        newVote.generateSignature(privateKey);

        return newVote;
    }
}