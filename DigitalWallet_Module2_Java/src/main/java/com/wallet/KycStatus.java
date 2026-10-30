package com.wallet;

public enum KycStatus {
    BASIC(10_000), FULL(100_000);

    private final double walletLimit;

    KycStatus(double walletLimit) {
        this.walletLimit = walletLimit;
    }

    public double getWalletLimit() {
        return walletLimit;
    }
}
