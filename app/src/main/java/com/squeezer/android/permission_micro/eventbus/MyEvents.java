package com.squeezer.android.permission_micro.eventbus;

/**
 * Created by adnen on 1/18/16.
 */
public class MyEvents {

    public static class PlayerEvent {

        private int mStatus;

        public PlayerEvent(){}

        public PlayerEvent(int status) {
            this.mStatus = status;
        }

        public int getStatus() {
            return mStatus;
        }

        public void setStatus(int mStatus) {
            this.mStatus = mStatus;
        }
    }

    public static class UpdateTitleEvent {

        private int mStatus;
        private String mTitle;

        public UpdateTitleEvent(){}

        public UpdateTitleEvent(int status, String title) {
            mTitle = title;
            this.mStatus = status;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setmTitle(String mTitle) {
            this.mTitle = mTitle;
        }

        public int getStatus() {
            return mStatus;
        }

        public void setStatus(int mStatus) {
            this.mStatus = mStatus;
        }
    }
}
