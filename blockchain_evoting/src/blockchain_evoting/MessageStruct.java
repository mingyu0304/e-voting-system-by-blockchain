package blockchain_evoting;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * MessageStruct
 * 
 * A structure for communicating between server and client. Two fields indicate 
 * the message type(_code) and message body(_content).
 * 
 * Message types and description: 
 * 		type	description						direction
 * 		0		server broadcasts block			server -> client
 * 		1		client sends block  			client -> server
 *		2		server sends clientid			server -> client
 *      3       server sends parties            server -> client
 *      4       server tells client end vote    server -> client
 *      5       server tells client vote start  server -> client
 *      6       client sends phonenumber        client -> server
 *      7       client tells he enter voteroom  client -> server
 */
public class MessageStruct extends Object implements Serializable {
	private static final long serialVersionUID = 3532734764930998421L;
	public int _code;
	public Object _content=null;
	public ArrayList<String> parties=null;
	public MessageStruct() {
		this._code = 0;
		this._content = null;
	}
	
	public MessageStruct(int code, Object content) {
		this._code = code;
		this._content = content;
	}
	public MessageStruct(int code, ArrayList<String> parties) {
		this._code=code;
		this.parties=parties;
	}
}
