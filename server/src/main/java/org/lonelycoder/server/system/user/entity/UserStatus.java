package org.lonelycoder.server.system.user.entity;

/**
 * Author : lihaoquan
 * Description :
 */
public enum UserStatus {

    normal("正常状态"), blocked("禁止状态");

    private final String info;

    UserStatus(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
