package de.iu.ipwa02.ghostnet.model;

/**
 * Enumeration for Ghost Net status
 */
public enum NetStatus {
    REPORTED("Reported"),
    CONFIRMED("Confirmed"),
    IN_RECOVERY("In Recovery"),
    RECOVERED("Recovered"),
    CANCELLED("Cancelled");

    private final String displayName;

    NetStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}