package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientFundsException;

import java.math.BigDecimal;

public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;

  Account getAccount(String accountId);

  void clearAccounts();

  /**
   * Adds a given value to the account balance.
   *
   * @param accountId id of the account
   * @param diff      quantity to add
   * @return updated account values
   * @throws InsufficientFundsException not enough funds
   */
  Account updateBalance(String accountId, BigDecimal diff) throws InsufficientFundsException;

}
