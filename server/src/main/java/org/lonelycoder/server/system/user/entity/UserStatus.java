package org.lonelycoder.server.system.user.entity;

/**
 * Author : lihaoquan
 * Description :
 */
public enum UserStatus {

    normal("Õý³£×´Ì¬"), blocked("½ûÖ¹×´Ì¬");

    private final String info;

    UserStatus(String info) {
         this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
