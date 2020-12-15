package com.orange.volunteers.util

import android.Manifest

enum class Permission(val value: String) {
    UNKNOWN(""),
    ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
    ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION)
}