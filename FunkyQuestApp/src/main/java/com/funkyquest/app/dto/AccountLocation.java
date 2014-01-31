package com.funkyquest.app.dto;

public enum AccountLocation {

	RUS(Currency.RUR),

	EUR(Currency.EUR),

	USA(Currency.USD);

	public final Currency currency;

	private AccountLocation(Currency currency) {
		this.currency = currency;
	}

}
