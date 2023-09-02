package me.alenalex.notaprisoncore.api.exceptions.vault;

import java.math.BigDecimal;

public class MinRequiredVaultException extends RuntimeException{

    public MinRequiredVaultException(BigDecimal provided){
        super("An illegal vault balance has been provided to set on mine, Provided value is "+provided.toString());;
    }

}
