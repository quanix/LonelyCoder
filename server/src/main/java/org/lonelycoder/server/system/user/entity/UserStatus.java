package org.lonelycoder.server.system.user.entity;

/**
 * Author : lihaoquan
 * Description :
 */
public enum UserStatus {

    normal("����״̬"), blocked("��ֹ״̬");

    private final String info;

    UserStatus(String info) {
         this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
