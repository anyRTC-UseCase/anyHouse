package org.ar.anyhouse.vm

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy


@Retention(RetentionPolicy.SOURCE)
annotation class BroadcastCMD {

    companion object {
        /**
         * 举手
         */
        const val RAISE_HANDS = 1

        /**
         * 邀请说话
         */
        const val INVITE_SPEAK = 2

        /**
         * 拒绝邀请
         */
        const val REJECT_INVITE = 3

        /**
         * 同意邀请
         */
        const val ACCEPT_INVITE = 4

        /**
         * 主持人关闭别人麦克风
         */
        const val CLOSER_MIC = 5

        /**
         * 主持人将别人设成听众
         */
        const val ROLE_CHANGE_GUEST = 6

        /**
         * 取消举手
         */
        const val CANCLE_RAISE_HANDS = 7

        /**
         * 主持人离开
         */
        const val HOSTER_LEAVE = 8

        /**
         * 个人信息
         */

        const val USER_INFO = 9

    }

}
