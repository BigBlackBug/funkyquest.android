package com.newresources.funkyquest.dto;

import java.math.BigDecimal;

public class PaymentDTO extends AbstractDTO {

    private Currency currency;

    private BigDecimal value;

    // read only
    private String uuid;

    public PaymentDTO() {
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
