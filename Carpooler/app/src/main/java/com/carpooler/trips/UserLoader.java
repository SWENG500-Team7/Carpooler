package com.carpooler.trips;

import com.carpooler.ui.activities.ServiceActivityCallback;
import com.carpooler.users.User;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.model.people.Person;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by raymond on 7/13/15.
 */
public class UserLoader implements ResultCallback<People.LoadPeopleResult>{
    private User user;
    private final ServiceActivityCallback serviceActivityCallback;
    private final boolean loggedInUser;
    private Queue<Callback> callbacks = new LinkedList<>();
    private Queue<VehicleCallback> vehicleCallbacks = new LinkedList<>();
    public UserLoader(ServiceActivityCallback serviceActivityCallback, String userId) {
        this.serviceActivityCallback = serviceActivityCallback;
        if (userId==null){
            user = serviceActivityCallback.getUser();
            loggedInUser = true;
        }else {
            if (serviceActivityCallback.getUser().getGoogleId().equals(userId)) {
                user = serviceActivityCallback.getUser();
                loggedInUser = true;
            } else {
                PendingResult<People.LoadPeopleResult> result = serviceActivityCallback.getPeople().load(serviceActivityCallback.getGoogleApiClient(), userId);
                result.setResultCallback(this);
                loggedInUser = false;
            }
        }
    }

    public boolean isLoggedInUser() {
        return loggedInUser;
    }

    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {
        if (loadPeopleResult.getPersonBuffer() != null) {
            Person person = loadPeopleResult.getPersonBuffer().iterator().next();
            loadPeopleResult.getPersonBuffer().release();
            user = new User(person, serviceActivityCallback);
            user.addCallback(new User.Callback() {
                @Override
                public void userLoaded() {
                    executeCallbacks();
                }
            });
        }
    }

    public void addCallback(VehicleCallback callback){
        if (user==null){
            vehicleCallbacks.add(callback);
        }else{
            doVehicleCallback(callback);
        }
    }
    private void doVehicleCallback(VehicleCallback callback){
        Vehicle vehicle = user.getVehicle(callback.getVehicleId());
        callback.loadData(vehicle);
    }
    public void addCallback(Callback callback){
        if (user==null){
            callbacks.add(callback);
        }else{
            callback.loadData(user);
        }
    }

    private void executeCallbacks(){
        for (Callback callback:callbacks){
            callback.loadData(user);
        }

        for (VehicleCallback callback:vehicleCallbacks){
            doVehicleCallback(callback);
        }
    }

    public interface Callback{
        public void loadData(User user);
    }

    public interface VehicleCallback{
        public String getVehicleId();
        public void loadData(Vehicle vehicle);
    }
}
