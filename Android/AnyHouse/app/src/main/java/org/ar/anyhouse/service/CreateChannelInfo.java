package org.ar.anyhouse.service;

public class CreateChannelInfo {


    /**
     * code : 0
     * msg : success.
     * data : {"roomId":"464568","ownerId":"464568","roomName":"111","rType":4,"isPrivate":2,"state":1,"rtcToken":"0062b332bf9b59c3f94b08b374a984b4c14IADOl/23dC2xFaALr2Fp1l7V2XALiUv1RPGLNJTjZdMlzFjIv/xYyL/8IgB88PUDDTxUYAQAAQDl7FJgAgDl7FJgAwDl7FJgBADl7FJg","rtmToken":"0062b332bf9b59c3f94b08b374a984b4c14IAALGouPP2zMnWWU2mw6e4jIueONle6PZRqB52dTjMOz2ljIv/wAAAAAEAArJGQBDTxUYAEA6APl7FJg"}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * roomId : 464568
         * ownerId : 464568
         * roomName : 111
         * rType : 4
         * isPrivate : 2
         * state : 1
         * rtcToken : 0062b332bf9b59c3f94b08b374a984b4c14IADOl/23dC2xFaALr2Fp1l7V2XALiUv1RPGLNJTjZdMlzFjIv/xYyL/8IgB88PUDDTxUYAQAAQDl7FJgAgDl7FJgAwDl7FJgBADl7FJg
         * rtmToken : 0062b332bf9b59c3f94b08b374a984b4c14IAALGouPP2zMnWWU2mw6e4jIueONle6PZRqB52dTjMOz2ljIv/wAAAAAEAArJGQBDTxUYAEA6APl7FJg
         */

        private String roomId;
        private String ownerId;
        private String roomName;
        private int rType;
        private int isPrivate;
        private int state;
        private String rtcToken;
        private String rtmToken;

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public int getRType() {
            return rType;
        }

        public void setRType(int rType) {
            this.rType = rType;
        }

        public int getIsPrivate() {
            return isPrivate;
        }

        public void setIsPrivate(int isPrivate) {
            this.isPrivate = isPrivate;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getRtcToken() {
            return rtcToken;
        }

        public void setRtcToken(String rtcToken) {
            this.rtcToken = rtcToken;
        }

        public String getRtmToken() {
            return rtmToken;
        }

        public void setRtmToken(String rtmToken) {
            this.rtmToken = rtmToken;
        }
    }
}
