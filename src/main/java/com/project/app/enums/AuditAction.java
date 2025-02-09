package com.project.app.enums;

import lombok.Getter;

@Getter
public enum AuditAction {
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT"),
    UPDATE("UPDATE"),
    REGISTER("REGISTER"),
    VERIFY_EMAIL("VERIFY_EMAIL"),
    REVOKE_API_KEY("REVOKE_API_KEY"),
    RESET_PASSWORD("RESET_PASSWORD"),
    FORGOT_PASSWORD("FORGOT_PASSWORD"),
    GENERATE_API_KEY("GENERATE_API_KEY"),
    ACTIVATE_ACCOUNT("ACTIVATE_ACCOUNT"),
    SEND_EMAIL_WITH_HTML("SEND_EMAIL_WITH_HTML");

    private final String action;

    AuditAction(String action) {
        this.action = action;
    }
}
