package de.iu.project.iuipwa0201ghostnetfishing.model;

/* indo docs: NetStatus enum
   Represents the lifecycle states of an abandoned (ghost) net within the system.
   Stored as STRING in the database when used with @Enumerated(EnumType.STRING).
   Note: the enum is package-private and named NetStatus (English).
*/
enum NetStatus {

    /* indo docs: REPORTED
       The net has been reported by a person or automated system but not yet processed.
    */
    REPORTED,

    /* indo docs: RECOVERY_PENDING
       Recovery (removal) is scheduled or in preparation.
    */
    RECOVERY_PENDING,

    /* indo docs: RECOVERED
       The net has been successfully recovered and removed from the environment.
    */
    RECOVERED,

    /* indo docs: MISSING
       The reported net could not be located or its status is unknown.
    */
    MISSING
}
