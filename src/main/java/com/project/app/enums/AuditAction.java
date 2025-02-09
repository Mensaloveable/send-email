package com.project.app.enums;

import lombok.Getter;

@Getter
public enum AuditAction {
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT"),
    REGISTER("REGISTER"),
    RESET_PASSWORD("RESET_PASSWORD"),
    FORGOT_PASSWORD("FORGOT_PASSWORD"),
    VERIFY_EMAIL("VERIFY_EMAIL"),
    GENERATE_API_KEY("GENERATE_API_KEY"),
    REVOKE_API_KEY("REVOKE_API_KEY");

    private final String action;

    AuditAction(String action) {
        this.action = action;
    }
}
