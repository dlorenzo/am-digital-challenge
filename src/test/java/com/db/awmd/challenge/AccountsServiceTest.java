package com.db.awmd.challenge;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientFundsException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @MockBean
  private NotificationService notificationService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }

  @Test
  public void transferFunds() throws Exception {
    Account accountFrom = new Account("IdFrom-" + System.currentTimeMillis(), new BigDecimal("123.45"));
    this.accountsService.createAccount(accountFrom);

    Account accountTo = new Account("IdTo-" + System.currentTimeMillis(), new BigDecimal("75.33"));
    this.accountsService.createAccount(accountTo);

    this.accountsService.transferFunds(accountFrom.getAccountId(), accountTo.getAccountId(), BigDecimal.valueOf(100));

    assertThat(accountFrom.getBalance().compareTo(BigDecimal.valueOf(23.45))).isEqualTo(0);
    assertThat(accountTo.getBalance().compareTo(BigDecimal.valueOf(175.33))).isEqualTo(0);

    verify(notificationService).notifyAboutTransfer(accountFrom,
            String.format("The amount of %s was transferred to account %s", 100, accountTo.getAccountId()));
    verify(notificationService).notifyAboutTransfer(accountTo,
            String.format("The amount of %s was transferred from account %s", 10, accountFrom.getAccountId()));
    verifyNoMoreInteractions(notificationService);
  }

  @Test
  public void transferFunds_failsOnOverdraft() throws Exception {
    Account accountFrom = new Account("IdFrom-" + System.currentTimeMillis(), new BigDecimal("123.45"));
    this.accountsService.createAccount(accountFrom);

    Account accountTo = new Account("IdTo-" + System.currentTimeMillis(), new BigDecimal("75.33"));
    this.accountsService.createAccount(accountTo);

    try {
      this.accountsService.transferFunds(accountFrom.getAccountId(), accountTo.getAccountId(), BigDecimal.valueOf(200));
      fail("Should have failed when transferring more funds than available");
    } catch (InsufficientFundsException ex) {
      assertThat(ex.getMessage()).isEqualTo(
              String.format("Not enough funds in account id %s to perform this operation!", accountFrom.getAccountId())
      );
    }
  }

  @Test
  public void transferFunds_failsOnSourceAccountNotFound() throws Exception {
    Account accountTo = new Account("IdTo-" + System.currentTimeMillis(), new BigDecimal("75.33"));
    this.accountsService.createAccount(accountTo);

    try {
      this.accountsService.transferFunds("accountFromId", accountTo.getAccountId(), BigDecimal.valueOf(200));
      fail("Should have failed due to source account not existing.");
    } catch (AccountNotFoundException ex) {
      assertThat(ex.getMessage()).isEqualTo(
              String.format("The account id %s doesn't exist!", "accountFromId")
      );
    }
  }

  @Test
  public void transferFunds_failsOnTargetAccountNotFound() throws Exception {
    Account accountFrom = new Account("IdFrom-" + System.currentTimeMillis(), new BigDecimal("123.45"));
    this.accountsService.createAccount(accountFrom);

    try {
      this.accountsService.transferFunds(accountFrom.getAccountId(), "accountToId", BigDecimal.valueOf(200));
      fail("Should have failed due to target account not existing.");
    } catch (AccountNotFoundException ex) {
      assertThat(ex.getMessage()).isEqualTo(
              String.format("The account id %s doesn't exist!", "accountToId")
      );
    }
  }

  @Test
  public void transferFunds_failsOnBothAccountNotFound() throws Exception {
    try {
      this.accountsService.transferFunds("accountFromId", "accountToId", BigDecimal.valueOf(200));
      fail("Should have failed due to target account not existing.");
    } catch (AccountNotFoundException ex) {
      assertThat(ex.getMessage()).isEqualTo(
              String.format("The account id %s doesn't exist!", "accountFromId")
      );
    }
  }

}
