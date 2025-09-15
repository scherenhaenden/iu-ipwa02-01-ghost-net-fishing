package de.iu.project.iuipwa0201ghostnetfishing.model;

/* NetStatus enum
   Represents the lifecycle states of an abandoned (ghost) net within the system.
   Stored as STRING in the database when used with @Enumerated(EnumType.STRING).
   Public English enum used across the domain model.
*/
public enum NetStatus {

    /* REPORTED
       The net has been reported by a person or automated system but not yet processed.
       Typical next steps: inspect report, verify location and schedule recovery.
    */
    REPORTED,

    /* RECOVERY_PENDING
       Recovery (removal) is scheduled or in preparation.
       Indicates a recovery job exists but has not completed yet.
    */
    RECOVERY_PENDING,

    /* RECOVERED
       The net has been successfully recovered and removed from the environment.
       Records can remain for audit/history purposes.
    */
    RECOVERED,

    /* MISSING
       The reported net could not be located or its status is unknown.
       Use this state when follow-up is required or the report is invalid.
    */
    MISSING
}
