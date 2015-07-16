package com.carpooler.trips;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Aidos on 07.06.2015.
 */
public enum TripStatus {
    OPEN {
        @Override
        protected TripStatus[] getAllowedNextStates() {
            return new TripStatus[]{IN_ROUTE,CANCELLED};
        }
    },
    CANCELLED {
        @Override
        protected TripStatus[] getAllowedNextStates() {
            return new TripStatus[0];
        }
    },
    IN_ROUTE {
        @Override
        protected TripStatus[] getAllowedNextStates() {
            return new TripStatus[]{COMPLETED};
        }
    },
    COMPLETED {
        @Override
        protected TripStatus[] getAllowedNextStates() {
            return new TripStatus[0];
        }
    };

    private Collection<TripStatus> allowedNextStates;
    protected abstract TripStatus[] getAllowedNextStates();
    private void loadNextStates(){
        if (allowedNextStates==null){
            allowedNextStates = Arrays.asList(getAllowedNextStates());
        }
    }
    public boolean isValidateNextState(TripStatus nextState){
        loadNextStates();
        return allowedNextStates.contains(nextState);
    }
}
