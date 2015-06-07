package com.carpooler;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

/**
 * Created by Kevin on 6/4/2015.
 */
public class PaymentServiceTest {

    @Test
    public void testPayUserToUser() {
        try {
            //ARRANGE
            PaymentService payService = new PaymentService();
            User toUser = new User("test1");
            User fromUser = new User("test2");

            //ACT
            boolean result = payService.payUserToUser(toUser, fromUser);

            //ASSERT
            Assert.assertTrue(result);
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testRefundUserToUser() {
        try {
            //ARRANGE
            PaymentService payService = new PaymentService();
            User toUser = new User("test1");
            User fromUser = new User("test2");
            double amount = 2.50;

            //ACT
            boolean result = payService.refundUserToUser(toUser, fromUser, amount);

            //ASSERT
            Assert.assertTrue(result);
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }
}