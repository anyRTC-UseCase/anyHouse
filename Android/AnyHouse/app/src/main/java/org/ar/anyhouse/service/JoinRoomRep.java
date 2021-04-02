package org.ar.anyhouse.service;

public class JoinRoomRep {


    /**
     * code : 0
     * msg : success.
     * data : {"roomName":"liuxiao的房间","roomId":"216696","state":1,"userName":"liuxiao","avatar":3,"uid":"216696","rtcToken":"0062b332bf9b59c3f94b08b374a984b4c14IADKiuNicMqaGsfkW+TyVMndUqVG2sIq6/qBNJbuzkvgyE+wqVA4x3yjIgAXulABqWlUYAQAAQCBGlNgAgCBGlNgAwCBGlNgBACBGlNg","rtmToken":"0062b332bf9b59c3f94b08b374a984b4c14IACaRucGBV8U9qkw2GUk2lH1CJXeuvQry8AQm7EVreGFCTjHfKMAAAAAEABPZ84BqWlUYAEA6AOBGlNg"}
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
         * roomName : liuxiao的房间
         * roomId : 216696
         * state : 1
         * userName : liuxiao
         * avatar : 3
         * uid : 216696
         * rtcToken : 0062b332bf9b59c3f94b08b374a984b4c14IADKiuNicMqaGsfkW+TyVMndUqVG2sIq6/qBNJbuzkvgyE+wqVA4x3yjIgAXulABqWlUYAQAAQCBGlNgAgCBGlNgAwCBGlNgBACBGlNg
         * rtmToken : 0062b332bf9b59c3f94b08b374a984b4c14IACaRucGBV8U9qkw2GUk2lH1CJXeuvQry8AQm7EVreGFCTjHfKMAAAAAEABPZ84BqWlUYAEA6AOBGlNg
         */

        private String roomName;
        private String roomId;
        private int state;
        private String userName;
        private int avatar;
        private String ownerId;
        private String rtcToken;
        private String rtmToken;

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getAvatar() {
            return avatar;
        }

        public void setAvatar(int avatar) {
            this.avatar = avatar;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(String uid) {
            this.ownerId = uid;
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
