package org.ar.anyhouse.service;

import java.util.List;

public class MemberInfo {

    /**
     * code : 0
     * msg : success.
     * data : [{"uid":"820034","enableAudio":0,"userName":"luu","avatar":8}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * uid : 820034
         * enableAudio : 0
         * userName : luu
         * avatar : 8
         */

        private String uid;
        private int enableAudio;
        private String userName;
        private int avatar;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public int getEnableAudio() {
            return enableAudio;
        }

        public void setEnableAudio(int enableAudio) {
            this.enableAudio = enableAudio;
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
    }
}
