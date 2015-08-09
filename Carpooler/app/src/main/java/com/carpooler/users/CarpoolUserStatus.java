package com.carpooler.users;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by raymond on 6/6/15.
 */
public enum CarpoolUserStatus {
    PENDING{
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{CONFIRMED_FOR_PICKUP, CANCELLED, REJECTED_FOR_PICKUP};
        }
    },
    PENDING_PICK_UP {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{PICKED_UP, NO_SHOW};
        }
    },
    PENDING_DROPOFF {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{DROPPED_OFF};
        }
    },
    PICKED_UP {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{CONFIRMED_PICKED_UP};
        }
    },
    CONFIRMED_PICKED_UP {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{PENDING_DROPOFF};
        }
    },
    CONFIRMED_FOR_PICKUP {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{PENDING_PICK_UP, CANCELLED};
        }
    },
    REJECTED_FOR_PICKUP {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{};
        }
    },
    DROPPED_OFF {
        @Override
        protected CarpoolUserStatus[] getAllowedNextStates() {
            return new CarpoolUserStatus[]{CONFIRMED_DROPPED_OFF};
        }
    },
    CONFIRMED_DROPPED_OFF {
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
