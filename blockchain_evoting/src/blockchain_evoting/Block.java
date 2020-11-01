package blockchain_evoting;

import java.io.Serializable; // Serialize �ʿ伺 : *** ���� �ۼ� ***
import java.security.*;
import java.util.ArrayList;
import java.util.Date;

public class Block implements Serializable {
	private Vote voteObj;

	private String previousHash; // ���� ����� �ؽð�
	private String blockHash; // ���� ����� �ؽð�
	private String merkleRoot;
	private long timeStamp; // 1/1/1970�������� �и�������
	private int nonce; // �۾� ����� �ʿ��� ����

	// ������
	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();

		this.blockHash = calculateHash();
	}

	// Getter, Setter
	public Vote getVoteObj() {
		return voteObj;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public String getBlockHash() {
		return blockHash;
	}

	// ����� �ν��Ͻ� ������ ������� hash ���� ��ȯCalculate new hash based on blocks contents
	public String calculateHash() {
		String calculatedHash = StringUtil
				.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
		return calculatedHash;
	}

	// nonce �� �÷����鼭 ���ǿ� �´� hash ���� ã�� ��� ä�� -> �ð� �ҿ�
	public void mineBlock(int difficulty) {
		merkleRoot = StringUtil.getMerkleRoot(voteObj);

		String target = StringUtil.getDifficultyString(difficulty); // ���̵��� ���� target ����
		while (!blockHash.substring(0, difficulty).equals(target)) {
			nonce++;
			blockHash = calculateHash();
		}
	}

	// ��ǥ ������ ��Ͽ� �߰�
	public boolean addVote(Vote vote) {
		if (vote == null)
			return false;
		if (!previousHash.equals("0")) {
			if (!vote.processVote()) {
				return false;
			}
		}
		voteObj = vote;
		return true;
	}

	// ����� ��
	@Override
	public String toString() {
		if (this.voteObj == null)
			return null;
		return "Voter Id:" + this.voteObj.getVoterPhoneNumber() + "\nVoter Name: " + "\nVoted for party: "
				+ this.voteObj.getVoteParty();
	}
}
