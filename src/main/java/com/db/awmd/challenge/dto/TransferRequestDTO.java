package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TransferRequestDTO {

  @NotNull
  @NotEmpty
  String accountTo;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = false, message = "Transfer amount must be greater than zero.")
  BigDecimal amount;

  @JsonCreator
  public TransferRequestDTO(@JsonProperty("accountTo") String accountTo,
                            @JsonProperty("amount") BigDecimal amount) {
    this.accountTo = accountTo;
    this.amount = amount;
  }

}
