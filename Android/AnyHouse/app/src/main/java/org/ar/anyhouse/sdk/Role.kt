package org.ar.anyhouse.sdk

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.SOURCE)
annotation class Role {
    companion object {
        var HOST = 1
        var AUDIENCE = 2
    }
}