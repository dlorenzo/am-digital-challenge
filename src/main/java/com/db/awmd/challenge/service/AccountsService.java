package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  private NotificationService notificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  @Autowired
  public void setNotificationService(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  /**
   * Transfer funds from one account to another.
   *
   * @param accountIdFrom source account
   * @param accountIdTo     destination account
   * @param amount        amount to transfer
   * @throws AccountNotFoundException account not found
   */
  public Account transferFunds(String accountIdFrom, String accountIdTo, BigDecimal amount) throws AccountNotFoundException {
    requireAccountExists(accountIdFrom);
    requireAccountExists(accountIdTo);

    Account accountFrom = accountsRepository.updateBalance(accountIdFrom, amount.negate());
    Account accountTo = accountsRepository.updateBalance(accountIdTo, amount);

    notificationService.notifyAboutTransfer(accountFrom,
            String.format("The amount of %s was transferred to account %s", amount, accountIdTo)
    );
    notificationService.notifyAboutTransfer(accountTo,
            String.format("The amount of %s was transferred from account %s", amount, accountIdFrom)
    );

    return accountFrom;
  }

  /**
   * Check if an account exists or throws exception if not.
   *
   * @param accountId of the account to check
   * @throws AccountNotFoundException the account was not found
   */
  private void requireAccountExists(String accountId) throws AccountNotFoundException {
    if (Objects.isNull(accountsRepository.getAccount(accountId))) {
      throw new AccountNotFoundException(String.format("The account id %s doesn't exist!", accountId));
    }
  }

}
