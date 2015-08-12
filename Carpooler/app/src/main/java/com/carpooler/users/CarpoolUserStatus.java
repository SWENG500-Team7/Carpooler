package com.carpooler.users;

import com.carpooler.R;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by raymond on 6/6/15.
 */
public enum CarpoolUserStatus {
    PENDING(R.string.user_status_pending){
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{CONFIRMED_FOR_PICKUP, CANCELLED, REJECTED_FOR_PICKUP};
        }
    },
    PENDING_PICK_UP(R.string.user_status_pending_pickup) {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{PICKED_UP, NO_SHOW};
        }
    },
    PENDING_DROPOFF(R.string.user_status_pending_dropoff) {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{DROPPED_OFF};
        }
    },
    PICKED_UP(R.string.user_status_picked_up) {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{CONFIRMED_PICKED_UP};
        }
    },
    CONFIRMED_PICKED_UP(R.string.user_status_confirmed_picked_up) {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{PENDING_DROPOFF};
        }
    },
    CONFIRMED_FOR_PICKUP(R.string.user_status_confirmed_for_pick_up) {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{PENDING_PICK_UP, CANCELLED};
        }
    },
    REJECTED_FOR_PICKUP(R.string.user_status_rejected_picked_up) {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{};
        }
    },
    DROPPED_OFF(R.string.user_status_dropped_off) {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{CONFIRMED_DROPPED_OFF};
        }
    },
    CONFIRMED_DROPPED_OFF(R.string.user_status_confirmed_dropped_off) {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{PAID};
        }
    },
    PAID(R.string.user_status_paid) {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{};
        }
    },
    CANCELLED(R.string.user_status_cancelled){
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{};
        }

    },
    NO_SHOW(R.string.user_status_no_show) {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{};
        }
    };

    private final int textId;
    private CarpoolUserStatus(int textId){
        this.textId = textId;
    }
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

    public int getTextId() {
        return textId;
    }
}
