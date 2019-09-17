package com.revolut.tx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Account implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public Account(){

    }
    public Account(String accountId,Long balance){
        this.accountId = accountId;
        this.balance = balance;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Account)) return false;

        Account account = (Account) o;

        return new EqualsBuilder()
                .append(getAccountId(), account.getAccountId())
                .append(getBalance(), account.getBalance())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getAccountId())
                .append(getBalance())
                .toHashCode();
    }

    @JsonProperty
    private String accountId;
    @JsonProperty(required = true)
    private Long balance;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }


    @Override
    public String toString() {
        return "Account [accountId=" + accountId + ",  balance=" + balance + "]";
    }

}
