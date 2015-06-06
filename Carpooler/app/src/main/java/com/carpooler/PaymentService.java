package com.carpooler;

/**
 * Uses third party payment service and user stored credentials to complete transactions
 *
 * Created by Kevin on 6/4/2015.
 */
public class PaymentService {

    /**
     * Sends money from one user to another using third party payment service
     *
     * @param toUser - User receiving money
     * @param fromUser - User sending money
     * @return - Success or failure
     */
    public boolean payUserToUser(User toUser, User fromUser) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Reverses a transaction that has already been made using third party payment service
     *
     * @param toUser - User being refunded
     * @param fromUser - User sending money
     * @param amount - Amount of money (USD)
     * @return - Success or failure
     */
    public boolean refundUserToUser(User toUser, User fromUser, double amount) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
