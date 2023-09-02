package me.alenalex.notaprisoncore.paper.entity.mine;

import me.alenalex.notaprisoncore.api.entity.mine.IMineVault;
import me.alenalex.notaprisoncore.api.exceptions.vault.InsufficientVaultBalanceException;
import me.alenalex.notaprisoncore.api.exceptions.vault.MinRequiredVaultException;

import java.math.BigDecimal;

public class ThreadSafeMineVault implements IMineVault {

    private BigDecimal balance;

    public ThreadSafeMineVault() {
        balance = BigDecimal.ZERO;
    }

    public ThreadSafeMineVault(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public boolean hasBalance(BigDecimal check) {
        return balance.compareTo(check) > 0;
    }

    @Override
    public synchronized void setBalance(BigDecimal newBalance) {
        if(newBalance.compareTo(BigDecimal.ZERO) < 0)
            throw new MinRequiredVaultException(newBalance);

        balance = newBalance;
    }

    @Override
    public synchronized void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    @Override
    public synchronized void withdraw(BigDecimal amount) throws InsufficientVaultBalanceException {
        if (balance.compareTo(amount) >= 0) {
            balance = balance.subtract(amount);
        } else {
            throw new InsufficientVaultBalanceException(balance.toString(), amount.toString());
        }
    }
}
