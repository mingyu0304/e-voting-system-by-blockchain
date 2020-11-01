package blockchain_evoting;

import java.io.Serializable;
import java.security.*;

public class Vote implements Serializable {
	private String voterPhoneNumber;
	private String voteParty;

	private String voteHash;
	private PublicKey sender; // 투표를 하는 사람의 PublicKey
	private PublicKey recipient; // 투표를 받는 후보자의 PublicKey -> ***** voteParty 와 voteParty 에게 주어지는 PublicKey 를 매핑하여
									// 사용하면 좋을 듯 *****
	private byte[] signature; // 디지털 서명

	// 내부 클래스 생성자
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

	// 투표 블록 생성을 진행
	public boolean processVote() {
		if (!verifySignature()) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}
		voteHash = calculateHash();

		return true;
	}

	// private key 를 이용한 타원곡선 DSA 기반의 디지털 서명 생성
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient);
		signature = StringUtil.applyECDSASig(privateKey, data);
	}

	// signature 검증
	public boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}

	private String calculateHash() {
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Integer.toString(1));
	}
}