package com.carpooler.trips;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.AddressData;
import com.carpooler.dao.dto.GeoPointData;
import com.carpooler.users.Address;

/**
 * Created by raymond on 7/5/15.
 */
public abstract class AddressLoadCallback implements DatabaseService.GeocodeCallback {
    private final AddressErrorCallback errorCallback;

    public AddressLoadCallback(AddressErrorCallback errorCallback) {
        this.errorCallback = errorCallback;
    }

    @Override
    public void doError(String message) {
        if (errorCallback!=null) {
            errorCallback.doError(message);
        }
    }

    @Override
    public void doException(Exception exception) {
        if (errorCallback!=null) {
            errorCallback.doException(exception);
        }
    }

    protected abstract void setAddressData(AddressData addressData);

    @Override
    public void doSuccess(android.location.Address data) {
        AddressData addressData = new AddressData();
        addressData.setLocation(new GeoPointData());
        setAddressData(addressData);
        Address address = new Address(addressData);
        address.setZip(data.getPostalCode());
        address.setCity(data.getLocality());
        address.setState(data.getAdminArea());
        address.setStreetNumber(data.getSubThoroughfare());
        address.setStreet(data.getThoroughfare());
        address.setLon(data.getLongitude());
        address.setLat(data.getLatitude());
        StringBuffer stringBuffer = new StringBuffer();
        for (int i=0; i<=data.getMaxAddressLineIndex();i++){
            stringBuffer.append(data.getAddressLine(i)).append(' ');
        }
        errorCallback.doSuccess(stringBuffer.toString());
    }
}
