package com.newresources.funkyquest.dto;

public abstract class AbstractDTO {

    protected Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractDTO)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        AbstractDTO ae = (AbstractDTO) obj;
        return ae.getId() == null ? false : ae.getId().equals(getId());
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }
}
