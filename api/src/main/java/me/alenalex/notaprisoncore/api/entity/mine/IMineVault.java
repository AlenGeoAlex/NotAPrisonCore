package me.alenalex.notaprisoncore.api.entity.mine;

import me.alenalex.notaprisoncore.api.exceptions.vault.InsufficientVaultBalanceException;

import java.math.BigDecimal;

public interface IMineVault {

    BigDecimal getBalance();
    boolean hasBalance(BigDecimal check);
    void setBalance(BigDecimal balance);
    void deposit(BigDecimal amount);
    void withdraw(BigDecimal amount) throws InsufficientVaultBalanceException;

}
