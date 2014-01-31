package com.funkyquest.app.dto;

import java.util.Set;

public class TeamDTO extends AbstractDTO {

    private String name;

    private Set<Long> teammates;

    public TeamDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getTeammates() {
        return teammates;
    }

    public void setTeammates(Set<Long> teammates) {
        this.teammates = teammates;
    }

}
