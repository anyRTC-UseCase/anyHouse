package org.ar.anyhouse.service;

public class SignInRep {


    /**
     * code : 0
     * msg : success.
     * data : {"uid":"105812","userToken":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NDc1NzE2NzUsImlhdCI6MTYxNjAzNTY3NSwidXNlcmlkIjoiMTA1ODEyIn0.3KnjSnyumSzwCh1vrUybqkbdMjjDHKb5hUpy2XtPjhg","userName":"liuxiaozhong","avatar":9,"appId":"2b332bf9b59c3f94b08b374a984b4c14"}
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
         * uid : 105812
         * userToken : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NDc1NzE2NzUsImlhdCI6MTYxNjAzNTY3NSwidXNlcmlkIjoiMTA1ODEyIn0.3KnjSnyumSzwCh1vrUybqkbdMjjDHKb5hUpy2XtPjhg
         * userName : liuxiaozhong
         * avatar : 9
         * appId : 2b332bf9b59c3f94b08b374a984b4c14
         */

        private String uid;
        private String userToken;
        private String userName;
        private int avatar;
        private String appId;
        private String rtcToken;
        private String rtmToken;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUserToken() {
            return userToken;
        }

        public void setUserToken(String userToken) {
            this.userToken = userToken;
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

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
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
