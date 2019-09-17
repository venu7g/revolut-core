package com.revolut.tx.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class TransactionDetails implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public void setFromAccountId(String fromAccountId) {
		this.fromAccountId = fromAccountId;
	}

	public void setToAccountId(String toAccountId) {
		this.toAccountId = toAccountId;
	}

	@JsonProperty(required = true)
	private Long amount;

	@JsonProperty(required = true)
	@NotNull(message = "Sender account details are mandatory")
	private String fromAccountId;

	@JsonProperty(required = true)
	@NotNull(message = "Receiver account details are mandatory")
	private String toAccountId;

	public TransactionDetails() {
	}


	public TransactionDetails(Long amount, String fromAccountId, String toAccountId) {
		this.amount = amount;
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
	}

	public Long getAmount() {
		return amount;
	}

	public String getFromAccountId() {
		return fromAccountId;
	}

	public String getToAccountId() {
		return toAccountId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (!(o instanceof TransactionDetails)) return false;

		TransactionDetails that = (TransactionDetails) o;

		return new EqualsBuilder()
				.append(getAmount(), that.getAmount())
				.append(getFromAccountId(), that.getFromAccountId())
				.append(getToAccountId(), that.getToAccountId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(getAmount())
				.append(getFromAccountId())
				.append(getToAccountId())
				.toHashCode();
	}
}
