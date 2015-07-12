package com.carpooler.payment;

import android.content.Context;
import android.content.Intent;
import android.widget.RelativeLayout;

import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalPayment;
import com.paypal.android.MEP.PayPalResultDelegate;

import java.math.BigDecimal;

/**
 * Uses third party payment service and user stored credentials to complete transactions
 *
 * Created by Kevin on 6/4/2015.
 */
public class PaymentService {

    private Context mContext;

    public PaymentService(Context context) {
        mContext = context;
    }

    /**
     * Sends money from one user to another using third party payment service
     * @param email - User receiving money
     * @param amount - Amount of money
     * @return - Success or failure
     */
    public Intent payToUser(String email, double amount, PayPalResultHandler resultHandler) {
        PayPal pp = getPayPalInstance();

        //Create new payment
        PayPalPayment payment = new PayPalPayment();
        payment.setCurrencyType("USD");

        //While in development, all payments go to PayPal Sandbox Host email
        payment.setRecipient("host@carpooler.com");

        //Set payment amount, assuming no taxes
        payment.setSubtotal(new BigDecimal(amount));
        payment.setPaymentType(PayPal.PAYMENT_TYPE_SERVICE);

        return pp.checkout(payment, mContext, resultHandler);
    }

    /**
     * Get an instance of the official PayPal button
     * @return
     */
    public CheckoutButton getPayPalButton() {
        PayPal pp = getPayPalInstance();
        CheckoutButton checkoutButton = pp.getCheckoutButton(mContext, PayPal.BUTTON_278x43, CheckoutButton.TEXT_PAY);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.bottomMargin = 10;
        checkoutButton.setLayoutParams(params);

        return checkoutButton;
    }

    private PayPal getPayPalInstance() {
        PayPal pp = PayPal.getInstance();

        if (pp == null) {
            //Init paypal with Sandbox App ID, in english
            pp = PayPal.initWithAppID(mContext, "APP-80W284485P519543T", PayPal.ENV_SANDBOX);
            pp.setLanguage("en_US");

            //Carpool users pay paypal fee
            pp.setFeesPayer(PayPal.FEEPAYER_SENDER);
            pp.setShippingEnabled(false);
        }

        return pp;
    }
}
