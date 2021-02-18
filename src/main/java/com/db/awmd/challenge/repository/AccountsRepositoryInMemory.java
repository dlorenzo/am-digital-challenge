package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientFundsException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
              "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Account updateBalance(String accountId, BigDecimal diff) throws InsufficientFundsException, AccountNotFoundException {
    Account updatedAccount = accounts.computeIfPresent(accountId, (key, val) -> {
      if (diff.compareTo(BigDecimal.ZERO) < 0 && val.getBalance().compareTo(diff.abs()) < 0) {
        throw new InsufficientFundsException(
                String.format("Not enough funds in account id %s to perform this operation!", key)
        );
      }

      val.setBalance(val.getBalance().add(diff));
      return val;
    });

    if (Objects.isNull(updatedAccount)) {
      throw new AccountNotFoundException(String.format("The account id %s doesn't exist!", accountId));
    }

    return updatedAccount;
  }

}
