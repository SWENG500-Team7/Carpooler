package com.carpooler.trips;

/**
 * Created by raymond on 7/5/15.
 */
public interface AddressErrorCallback {
    void doError(String message);

    void doException(Exception exception);

    void doSuccess(String address);
}
