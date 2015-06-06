package com.carpooler;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by Kevin on 6/4/2015.
 */
public class PaymentServiceTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

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
        } catch(Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }

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
        } catch(Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }
}