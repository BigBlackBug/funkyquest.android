package com.funkyquest.app.dto;

/**
 * Created by BigBlackBug on 1/23/14.
 */
public class GameStatusDTO {

	private long gameID;

	private GameStatus gameStatus;

	public GameStatusDTO(long gameID, GameStatus gameStatus) {
		this.gameID = gameID;
		this.gameStatus = gameStatus;
	}

	public long getGameID() {
		return gameID;
	}

	public GameStatus getGameStatus() {

		return gameStatus;
	}
}

