// Copyright 2010 Google Inc. All Rights Reserved.

package pacman;
// gigi
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import org.mortbay.util.ajax.JSON;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

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
  private String userX = null;

  @Persistent
  private String userO = null;

  @Persistent
  private String board = null;

  @Persistent
  private Boolean moveX = null;
  
  @Persistent
  private String winner = null;
  
  @Persistent
  private String gameState = null;
  
  @Persistent
  private String winningBoard = null;

  static final Pattern[] XWins = {
      Pattern.compile("XXX......"),
      Pattern.compile("...XXX..."),
      Pattern.compile("......XXX"),
      Pattern.compile("X..X..X.."),
      Pattern.compile(".X..X..X."),
      Pattern.compile("..X..X..X"),
      Pattern.compile("X...X...X"),
      Pattern.compile("..X.X.X..")
    };

  static final Pattern[] OWins = {
    Pattern.compile("OOO......"),
    Pattern.compile("...OOO..."),
    Pattern.compile("......OOO"),
    Pattern.compile("O..O..O.."),
    Pattern.compile(".O..O..O."),
    Pattern.compile("..O..O..O"),
    Pattern.compile("O...O...O"),
    Pattern.compile("..O.O.O..")
  };

  Game(String userX, String userO, String board, boolean moveX) {
    this.userX = userX;
    this.userO = userO;
    this.board = board;
    this.moveX = moveX;
  }

  public Key getKey() {
    return key;
  }

  public String getUserX() {
    return userX;
  }

  public String getUserO() {
    return userO;
  }

  public void setUserO(String userO) {
    this.userO = userO;
  }

  public String getBoard() {
    return board;
  }

  public void setBoard(String board) {
    this.board = board;
  }

  public boolean getMoveX() {
    return moveX;
  }

  public void setMoveX(boolean moveX) {
    this.moveX = moveX;
  }
  
  public String getGameState() {
	  return gameState;
  }

  public void setGameState(String gameState) {
	  this.gameState = gameState;
  }

  public String getMessageString() {
    Map<String, String> state = new HashMap<String, String>();
    state.put("userX", userX);
    if (userO == null) {
      state.put("userO", null);
    } else {
      state.put("userO", userO);
    }
    state.put("board", board);
    state.put("moveX", moveX.toString());
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
    sendUpdateToUser(userX);
    sendUpdateToUser(userO);
  }

  public void checkWin() {
    final Pattern[] wins;
    if (moveX) {
      wins = XWins; 
    } else {
      wins = OWins;
    }
    
    for (Pattern winPattern: wins) {
      if (winPattern.matcher(board).matches()) {
        if (moveX) {
          winner = userX;
        } else {
          winner = userO;
        }
        winningBoard = winPattern.toString();
      }
    }
  }

  public boolean makeMove(String gameState, String user) {
    String currentMovePlayer;
    char value;
    if (getMoveX()) {
      value = 'X';
      currentMovePlayer = getUserX();
    } else {
      value = 'O';
      currentMovePlayer = getUserO();
    }

//    if (currentMovePlayer.equals(user)) {
//      char[] boardBytes = getBoard().toCharArray();
//      boardBytes[position] = value;
//      setBoard(new String(boardBytes));
//      checkWin();
//      setMoveX(!getMoveX());
//       
//      sendUpdateToClients();
//      return true;
//    }
    
    if ("START".equals(gameState)) {
    	sendUpdateToClients();
    	return true;	
	}
    
    return false;
  }
}
