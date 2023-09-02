package me.alenalex.notaprisoncore.api.exceptions.vault;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class InsufficientVaultBalanceException extends Exception {

    private final String currentBalance;
    private final String requestedWithdrawAmount;

    public InsufficientVaultBalanceException(String currentBalance, String requestedWithdrawAmount) {
        super("Tried to withdraw "+requestedWithdrawAmount+" from the vault when the current vault balance is "+currentBalance);
        this.currentBalance = currentBalance;
        this.requestedWithdrawAmount = requestedWithdrawAmount;
    }
}
