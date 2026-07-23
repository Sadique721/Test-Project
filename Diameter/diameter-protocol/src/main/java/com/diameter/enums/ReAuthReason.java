package com.diameter.enums;

/**
 * Reason a server-initiated Gx RAR is being sent, used to select the RAR
 * packet mapping and to gate reason-specific side effects.
 *
 * <ul>
 *   <li>{@link #FUP} - quota exhausted with over-usage allowed (throttle rules).</li>
 *   <li>{@link #PLAN_EXPIRE} - subscriber plan expired; sent on the next CCR-Update.</li>
 * </ul>
 */
public enum ReAuthReason {
    FUP,
    PLAN_EXPIRE
}
