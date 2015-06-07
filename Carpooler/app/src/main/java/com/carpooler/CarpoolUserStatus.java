package com.carpooler;

import android.webkit.CookieManager;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by raymond on 6/6/15.
 */
public enum CarpoolUserStatus {
    PENDING{
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{PICKED_UP,CANCELLED, NO_SHOW};
        }
    },
    PICKED_UP {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{CONFIRMED_PICK_UP,CANCELLED};
        }
    },
    CONFIRMED_PICK_UP {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{DROPPED_OFF};
        }
    },
    DROPPED_OFF {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{PAID};
        }
    },
    PAID {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{};
        }
    },
    CANCELLED{
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{};
        }

    },
    NO_SHOW {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{};
        }
    };


    private Collection<CarpoolUserStatus> allowedNextStates;

    protected abstract CarpoolUserStatus[] getAllowedNextStates();

    private void loadNextStates(){
        if (allowedNextStates==null){
            allowedNextStates = Arrays.asList(getAllowedNextStates());
        }
    }
    public boolean isValidateNextState(CarpoolUserStatus nextState){
        loadNextStates();
        return allowedNextStates.contains(nextState);
    }
}
