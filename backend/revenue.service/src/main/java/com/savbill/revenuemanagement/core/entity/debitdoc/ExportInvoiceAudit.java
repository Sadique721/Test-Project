package com.savbill.revenuemanagement.core.entity.debitdoc;

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a Thread audit.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbltexportinvoiceaudit")
@ToString
public class ExportInvoiceAudit {

    /**
     * The unique identifier of the table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the thread.
     */
    @Column(name = "thread_name")
    private String threadName;

    @Column(name = "remarks", columnDefinition = "Text")
    private String remarks;
    /**
     * The status of the thread (e.g., "pending", "in-progress", "completed", "failed").
     */
    @Column(name = "status", nullable = false)
    private String status;

    /**
     * The unique identifier of the request associated with the thread.
     */
    @Column(name = "request_id", nullable = false)
    private String requestId;

    /**
     * The start date and time of the thread execution.
     */
    @Column(name = "execution_start_date")
    private LocalDateTime executionStartDate;

    /**
     * The end date and time of the thread execution.
     */
    @Column(name = "execution_end_date")
    private LocalDateTime executionEndDate;

    /**
     * The date and time when the thread was submitted.
     */
    @Column(name = "submitted_date")
    private LocalDateTime submittedDate;

    @Column(name = "export_count")
    private int exportCount;

    @Column(name = "username")
    private String username;

    @Column(name = "mvno_id")
    private Integer mvnoId;


}
