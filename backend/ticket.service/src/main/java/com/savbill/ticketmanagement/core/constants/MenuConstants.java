package com.savbill.ticketmanagement.core.constants;

public class MenuConstants {


    public interface tatmatrixmanagement{
        String TAT_MATRIX = "tat_ticket";
        String TAT_MATRIX_CREATE="tat_ticket_create";
        String TAT_MATRIX_EDIT="tat_ticket_edit";
        String TAT_MATRIX_DELETE="tat_ticket_delete";
    }

    public interface ticketreasonCategory{
        String TICKET_REASON_CATEGORY = "problem_domain";
        String TICKET_REASON_CATEGORY_CREATE="problem_domain_create";
        String TICKET_REASON_CATEGORY_EDIT="problem_domain_edit";
        String TICKET_REASON_CATEGORY_DELETE="problem_domain_delete";
    }

    public interface ticketreasonSubCategory{
        String TICKET_REASON_SUB_CATEGORY = "sub_pb_domain";
        String TICKET_REASON_SUB_CATEGORY_CREATE="sub_pb_domain_create";
        String TICKET_REASON_SUB_CATEGORY_EDIT="sub_pb_domain_edit";
        String TICKET_REASON_SUB_CATEGORY_DELETE="sub_pb_domain_delete";
    }


    public interface rootcause{
        String ROOT_CAUSE = "root_cause_master";
        String ROOT_CAUSE_CREATE="root_cause_create";
        String ROOT_CAUSE_EDIT="root_cause_edit";
        String ROOT_CAUSE_DELETE="root_cause_delete";
    }
    public interface Ticket {
        String TICKET = "ticket";
        String TICKET_CREATE = "ticket_create";
        String TICKET_EDIT="ticket_edit";
        String TICKET_DELETE="ticket_delete";
        String BULK_REASIGN="ticket_bulk_reassign";
        String LINK_TICKET="ticket_link_ticket";
        String UPLOAD_DOCUMENT="ticket_upload_doc";
        String TICKET_ETR="ticket_etr";
        String TICKET_REMARKS="ticket_remarks";

    }

    public interface TicketFollowUp {
        String TICKET_FOLLOWUP = "ticket_follow_up";
        String TICKET_FOLLOWUP_CREATE = "ticket_follow_up_create";
        String TICKET_FOLLOWUP_EDIT="ticket_follow_up_edit";
        String TICKET_FOLLOWUP_DELETE="ticket_follow_up_delete";

    }
    public interface TicketConversation {
        String TICKET_CONVERSATION = "ticket_conversation";
        String TICKET_CONVERSATION_CREATE = "ticket_conversation_create";
        String TICKET_CONVERSATION_EDIT="ticket_conversation_edit";
        String TICKET_CONVERSATION_DELETE="ticket_conversation_delete";
        String CHANGE_STATUS="ticket_change_status";
        String BULK_REASIGN="ticket_bulk_reassign";
        String TICKET_FOLLOWUP="ticket_follow_up";
        String CHANGE_PRIORITY="ticket_change_priority";
        String TICKET_ASSIGN="ticket_assign";
        String LINK_TICKET="ticket_line_ticket";
        String CHANGE_PROBLEM_DOMAIN="ticket_change_pb_domain";
        String SLA_COUNTER="ticket_sla_counter";

    }

    public static final String AUDIT_LOG = "audit_log";

}
