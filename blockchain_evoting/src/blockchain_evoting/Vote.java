package blockchain_evoting;

import java.io.Serializable;
import java.security.*;

public class Vote implements Serializable {
	private String voterPhoneNumber;
	private String voteParty;

	private String voteHash;
	private PublicKey sender; // ��ǥ�� �ϴ� ����� PublicKey
	private PublicKey recipient; // ��ǥ�� �޴� �ĺ����� PublicKey -> ***** voteParty �� voteParty ���� �־����� PublicKey �� �����Ͽ�
									// ����ϸ� ���� �� *****
	private byte[] signature; // ������ ����

	// ���� Ŭ���� ������
	public Vote(PublicKey sender, PublicKey recipient, String voterPhoneNumber, String voteParty) {
		this.sender = sender;
		this.recipient = recipient;
		this.voterPhoneNumber = voterPhoneNumber;
		this.voteParty = voteParty;
	}

	// Getter, Setter
	public String getVoterPhoneNumber() {
		return voterPhoneNumber;
	}


	public String getVoteParty() {
		return voteParty;
	}

	public String getVoteHash() {
		return voteHash;
	}

	public PublicKey getRecipient() {
		return recipient;
	}

	public byte[] getSignature() {
		return signature;
	}

	// ��ǥ ��� ������ ����
	public boolean processVote() {
		if (!verifySignature()) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}
		voteHash = calculateHash();

		return true;
	}

	// private key �� �̿��� Ÿ��� DSA ����� ������ ���� ����
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient);
		signature = StringUtil.applyECDSASig(privateKey, data);
	}

	// signature ����
	public boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}

	private String calculateHash() {
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Integer.toString(1));
	}
}