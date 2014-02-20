package com.funkyquest.app.dto.util;

public class Subscription {

    public static final Long ID_NOT_SPECIFIED = -1L;
    private Long teamID = ID_NOT_SPECIFIED;
    private Long userID = ID_NOT_SPECIFIED;
    private Long gameID = ID_NOT_SPECIFIED;

    public Subscription() {
    }

    public Subscription(Long gameID, Long teamID, Long userID) {
        this.teamID = teamID;
        this.userID = userID;
        this.gameID = gameID;
    }

    public Subscription(Long gameID) {
        this(gameID, ID_NOT_SPECIFIED);
    }

    public Subscription(Long gameID, Long teamID) {
        this(gameID, teamID, ID_NOT_SPECIFIED);
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getTeamID() {
        return teamID;
    }

    public void setTeamID(Long teamID) {
        this.teamID = teamID;
    }

    public Long getGameID() {
        return gameID;
    }

    public void setGameID(Long gameID) {
        this.gameID = gameID;
    }

    public boolean isUserIDSet() {
        return userID != null && userID != ID_NOT_SPECIFIED;
    }

    public boolean isTeamIDSet() {
        return teamID != null && teamID != ID_NOT_SPECIFIED;
    }

    public boolean isGameIDSet() {
        return gameID != null && gameID != ID_NOT_SPECIFIED;
    }

    @Override
    public boolean equals(Object o) {
	    if (this == o) {
		    return true;
	    }
	    if (o == null || getClass() != o.getClass()) {
		    return false;
	    }

        Subscription that = (Subscription) o;

        if (gameID != null ? !gameID.equals(that.gameID) :
                that.gameID != null) {
            return false;
        }
        if (teamID != null ? !teamID.equals(that.teamID) :
                that.teamID != null) {
            return false;
        }
        if (userID != null ? !userID.equals(that.userID) :
                that.userID != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = teamID != null ? teamID.hashCode() : 0;
        result = 31 * result + (userID != null ? userID.hashCode() : 0);
        result = 31 * result + (gameID != null ? gameID.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "'GameID: " + gameID +
                " TeamID: " + teamID +
                " UserID: " + userID;
    }
}