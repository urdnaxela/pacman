package pacman;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory; 
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


@PersistenceCapable
public class Game {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key = null;
  
  @Persistent
  private Boolean gameStarted;
  
  @Persistent
  private String pacman = null;
  
  @Persistent
  private int pacmanX;

  @Persistent
  private int pacmanY;

  @Persistent
  private String phantom = null;

  @Persistent
  private String board = null;

  @Persistent
  private Boolean pacmanMove = null;
  
  @Persistent
  private String winner = null;
  
  @Persistent
  private String gameState = null;
  
  @Persistent
  private String winningBoard = null;

  Game(String userX, String userO, String board, boolean moveX) {
    this.pacman = userX;
    this.phantom = userO;
    this.board = board;
    this.pacmanMove = moveX;
  }

  public Key getKey() {
    return key;
  }

  public String getPacman() {
    return pacman;
  }

  public String getPhantom() {
    return phantom;
  }

  public void setPhantom(String phantom) {
    this.phantom = phantom;
  }

  public String getBoard() {
    return board;
  }

  public void setBoard(String board) {
    this.board = board;
  }

  public String getGameState() {
	  return gameState;
  }

  public void setGameState(String gameState) {
	  this.gameState = gameState;
  }

  public String getMessageString() {
    Map<String, String> state = new HashMap<String, String>();
    state.put("userX", pacman);
    if (phantom == null) {
      state.put("userO", null);
    } else {
      state.put("userO", phantom);
    }
    state.put("board", board);
    state.put("moveX", pacmanMove.toString());
    state.put("winner", winner);
    if (winner != null && winner != "") {
      state.put("winningBoard", winningBoard);
    }
    
    state.put("gameState", gameState);
    
    JSONObject message = new JSONObject(state);
    return message.toString();
  }

  public String getChannelKey(String user) {
    return user + KeyFactory.keyToString(key);
  }

  private void sendUpdateToUser(String user) {
    if (user != null) {
      ChannelService channelService = ChannelServiceFactory.getChannelService();
      String channelKey = getChannelKey(user);
      channelService.sendMessage(new ChannelMessage(channelKey, getMessageString()));
    }
  }

  public void sendUpdateToClients() {
    sendUpdateToUser(pacman);
    sendUpdateToUser(phantom);
  }

  public void checkWin() {
    
  }

  public boolean makeMove(String gameState, int x, int y, String user) {
    String currentMovePlayer;
    char value;
    if (user.equals(pacman)) {
      pacmanX = x;
      pacmanY = y;
    } else {
      // update phantom
    }

    
    if ("START".equals(gameState)) {
    	this.gameState = gameState;
    	sendUpdateToClients();
    	return true;	
	}
    
    return false;
  }
}
