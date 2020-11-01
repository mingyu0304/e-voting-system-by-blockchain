package blockchain_evoting;

import java.io.Serializable; // Serialize 필요성 : *** 이후 작성 ***
import java.security.*;
import java.util.ArrayList;
import java.util.Date;

public class Block implements Serializable {
	private Vote voteObj;

	private String previousHash; // 이전 블록의 해시값
	private String blockHash; // 현재 블록의 해시값
	private String merkleRoot;
	private long timeStamp; // 1/1/1970때부터의 밀리세컨드
	private int nonce; // 작업 증명시 필요한 변수

	// 생성자
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

	// 블록의 인스턴스 변수를 기반으로 hash 값을 반환Calculate new hash based on blocks contents
	public String calculateHash() {
		String calculatedHash = StringUtil
				.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
		return calculatedHash;
	}

	// nonce 를 늘려가면서 조건에 맞는 hash 값을 찾아 블록 채굴 -> 시간 소요
	public void mineBlock(int difficulty) {
		merkleRoot = StringUtil.getMerkleRoot(voteObj);

		String target = StringUtil.getDifficultyString(difficulty); // 난이도에 따른 target 저장
		while (!blockHash.substring(0, difficulty).equals(target)) {
			nonce++;
			blockHash = calculateHash();
		}
	}

	// 투표 내역을 블록에 추가
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

	// 디버깅 용
	@Override
	public String toString() {
		if (this.voteObj == null)
			return null;
		return "Voter Id:" + this.voteObj.getVoterPhoneNumber() + "\nVoter Name: " + "\nVoted for party: "
				+ this.voteObj.getVoteParty();
	}
}
