package gregmachado.com.panappfirebase.pagSeguro;

/**
 * Created by gregmachado on 05/10/16.
 */
public enum PagSeguroCurrency {
    /**
            * Country: Brasil, Currency: Real, Code: BRL
    */
    BRASIL("Real", "BRL");
    private String currencyName;
    private String currencyCode;

    private PagSeguroCurrency(String currencyName, String currencyCode) {
        this.currencyName = currencyName;
        this.currencyCode = currencyCode;
    }

    /**
     * @return the currency name
     */
    public String getCurrencyName() {
        return currencyName;
    }

    /**
     * @return the currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
}
