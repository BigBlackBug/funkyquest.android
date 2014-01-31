package com.funkyquest.app.dto;

public class PlayerInviteDTO extends AbstractDTO {

    private Long user;

    public PlayerInviteDTO() {
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

}
