package com.example.contracts;

public final class MessagingTopology {
    private MessagingTopology() {}

    public static final String WALLET_EVENTS_EXCHANGE = "wallet.events";
    public static final String WALLET_EVENTS_QUEUE = "transaction.wallet-events";
    public static final String WALLET_CREATED_KEY = "wallet.created.v1";

    public static final String TRANSACTION_COMMANDS_EXCHANGE = "transaction.commands";
    public static final String TRANSACTION_CREATE_QUEUE = "transaction.create";
    public static final String TRANSACTION_CREATE_KEY = "transaction.create.v1";

    public static final String TRANSACTION_EVENTS_EXCHANGE = "transaction.events";
    public static final String TRANSACTION_RESULTS_QUEUE = "wallet.transaction-results";
    public static final String TRANSACTION_CREATED_KEY = "transaction.created.v1";
    public static final String TRANSACTION_REJECTED_KEY = "transaction.rejected.v1";

    public static final String DEAD_LETTER_EXCHANGE = "wallet.dlx";
    public static final String DEAD_LETTER_QUEUE = "wallet.dead-letter";
    public static final String DEAD_LETTER_KEY = "dead-letter";
}
