package com.binbard.geu.geuone.models

enum class FetchStatus {
    NOT_FETCHED,
    LOCAL_FETCHED,
    REMOTE_FETCHED,
    FULL_FETCHED,
}

enum class LoginStatus {
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    LOGOUT,
    PREV_LOGGED_IN,
    PREV_LOGGED_OUT,
    LOGGED_IN,
    NOT_LOGGED_IN,
    UNKNOWN,
}