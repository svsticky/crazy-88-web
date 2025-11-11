package nl.svsticky.crazy88.model;

public enum UserRole {
    /**
     * Board member with all permissions.
     */
    ADMIN,
    /**
     * Mentor of an entire 'super' team (usually based on colors).
     */
    SUPER_MENTOR,
    /**
     * Mentor of a single team, which is a part of a 'super' team.
     */
    MENTOR,
    /**
     * Default role, no privileges. Assigned when a user is created.
     */
    UNPRIVILEGED
}
