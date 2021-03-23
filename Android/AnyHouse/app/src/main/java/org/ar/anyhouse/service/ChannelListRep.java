package org.ar.anyhouse.service;

import java.util.List;
import java.util.Objects;

public class ChannelListRep {


    /**
     * code : 0
     * msg : success.
     * data : {"list":[{"roomName":"liupp的房间","roomId":"346674","ownerUid":"346674","isPrivate":0,"roomPwd":"","avatars":[8,10],"userList":[{"userName":"liupp","avatar":8,"uid":"346674"}],"userTotalNum":2,"speakerTotalNum":1}],"haveNext":0,"totalPageNum":1}
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
         * list : [{"roomName":"liupp的房间","roomId":"346674","ownerUid":"346674","isPrivate":0,"roomPwd":"","avatars":[8,10],"userList":[{"userName":"liupp","avatar":8,"uid":"346674"}],"userTotalNum":2,"speakerTotalNum":1}]
         * haveNext : 0
         * totalPageNum : 1
         */

        private List<ListBean> list;
        private int haveNext;
        private int totalPageNum;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public int getHaveNext() {
            return haveNext;
        }

        public void setHaveNext(int haveNext) {
            this.haveNext = haveNext;
        }

        public int getTotalPageNum() {
            return totalPageNum;
        }

        public void setTotalPageNum(int totalPageNum) {
            this.totalPageNum = totalPageNum;
        }

        public static class ListBean {
            /**
             * roomName : liupp的房间
             * roomId : 346674
             * ownerUid : 346674
             * isPrivate : 0
             * roomPwd :
             * avatars : [8,10]
             * userList : [{"userName":"liupp","avatar":8,"uid":"346674"}]
             * userTotalNum : 2
             * speakerTotalNum : 1
             */

            private String roomName;
            private String roomId;
            private String ownerUid;
            private int isPrivate;
            private String roomPwd;
            private List<Integer> avatars;
            private List<UserListBean> userList;
            private int userTotalNum;
            private int speakerTotalNum;

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

            public String getOwnerUid() {
                return ownerUid;
            }

            public void setOwnerUid(String ownerUid) {
                this.ownerUid = ownerUid;
            }

            public int getIsPrivate() {
                return isPrivate;
            }

            public void setIsPrivate(int isPrivate) {
                this.isPrivate = isPrivate;
            }

            public String getRoomPwd() {
                return roomPwd;
            }

            public void setRoomPwd(String roomPwd) {
                this.roomPwd = roomPwd;
            }

            public List<Integer> getAvatars() {
                return avatars;
            }

            public void setAvatars(List<Integer> avatars) {
                this.avatars = avatars;
            }

            public List<UserListBean> getUserList() {
                return userList;
            }

            public void setUserList(List<UserListBean> userList) {
                this.userList = userList;
            }

            public int getUserTotalNum() {
                return userTotalNum;
            }

            public void setUserTotalNum(int userTotalNum) {
                this.userTotalNum = userTotalNum;
            }

            public int getSpeakerTotalNum() {
                return speakerTotalNum;
            }

            public void setSpeakerTotalNum(int speakerTotalNum) {
                this.speakerTotalNum = speakerTotalNum;
            }

            public static class UserListBean {
                /**
                 * userName : liupp
                 * avatar : 8
                 * uid : 346674
                 */

                private String userName;
                private int avatar;
                private String uid;

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

                public String getUid() {
                    return uid;
                }

                public void setUid(String uid) {
                    this.uid = uid;
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    UserListBean that = (UserListBean) o;
                    return Objects.equals(uid, that.uid);
                }

                @Override
                public int hashCode() {
                    return Objects.hash(userName, avatar, uid);
                }
            }
        }
    }
}
