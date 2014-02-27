package com.newresources.funkyquest.dto;

import java.math.BigDecimal;


public class UserDTO extends AbstractDTO {

	private String nickname;

	private String email;

	private AccountType accountType;

	private AccountLocation accountLocation;

	private BigDecimal balance = new BigDecimal(0);

	public UserDTO() {
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String name) {
		this.nickname = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public AccountLocation getAccountLocation() {
		return accountLocation;
	}

	public void setAccountLocation(AccountLocation accountLocation) {
		this.accountLocation = accountLocation;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

}
