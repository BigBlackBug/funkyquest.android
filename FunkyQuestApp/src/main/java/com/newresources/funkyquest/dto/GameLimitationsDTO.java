package com.newresources.funkyquest.dto;

import java.util.ArrayList;
import java.util.List;

public class GameLimitationsDTO extends AbstractDTO {

    private Integer maxPlayers = -1;

    private Integer maxTeams = -1;

    private Boolean invitedOnly = false;

    private List<PlayerInviteDTO> playersInvites = new ArrayList<PlayerInviteDTO>();

    public GameLimitationsDTO() {
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Integer getMaxTeams() {
        return maxTeams;
    }

    public void setMaxTeams(Integer maxTeams) {
        this.maxTeams = maxTeams;
    }

    public Boolean getInvitedOnly() {
        return invitedOnly;
    }

    public void setInvitedOnly(Boolean invitedOnly) {
        this.invitedOnly = invitedOnly;
    }

    public List<PlayerInviteDTO> getPlayersInvites() {
        return playersInvites;
    }

    public void setPlayersInvites(List<PlayerInviteDTO> playersInvites) {
        this.playersInvites = playersInvites;
    }

}
