package com.carpooler.payment;

import com.paypal.android.MEP.PayPalResultDelegate;

import java.io.Serializable;

/**
 * Created by Kevin on 7/4/2015.
 */
public class PayPalResultHandler implements Serializable, PayPalResultDelegate {

    @Override
    public void onPaymentSucceeded(String payKey, String paymentStatus) {

    }

    @Override
    public void onPaymentFailed(String paymentStatus, String correlationID, String payKey,
                                String errorID, String errorMessage) {

    }

    @Override
    public void onPaymentCanceled(String paymentStatus) {

    }
}
