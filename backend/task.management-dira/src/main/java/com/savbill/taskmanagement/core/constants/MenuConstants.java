package com.savbill.taskmanagement.core.constants;

public class MenuConstants {


    public interface tatmatrixmanagement{
        String TAT_MATRIX = "tat_task";
        String TAT_MATRIX_CREATE="tat_task_create";
        String TAT_MATRIX_EDIT="tat_task_edit";
        String TAT_MATRIX_DELETE="tat_task_delete";
    }

    public interface taskCategory{
        String TASK_CATEGORY = "task_category";
        String TASK_CATEGORY_CREATE="task_category_create";
        String TASK_CATEGORY_EDIT="task_category_edit";
        String TASK_CATEGORY_DELETE="task_category_delete";
    }

    public interface taskSubCategory{
        String TASK_SUB_CATEGORY = "task_sub_category";
        String TASK_SUB_CATEGORY_CREATE="task_sub_category_create";
        String TASK_SUB_CATEGORY_EDIT="task_sub_category_edit";
        String TASK_SUB_CATEGORY_DELETE="task_sub_category_delete";
    }


    public interface rootcause{
        String ROOT_CAUSE = "root_cause_master";
        String ROOT_CAUSE_CREATE="root_cause_create";
        String ROOT_CAUSE_EDIT="root_cause_edit";
        String ROOT_CAUSE_DELETE="root_cause_delete";
    }
    public interface Task {
        String TICKET = "task";
        String TICKET_CREATE = "task_create";
        String TASK_EDIT="task_edit";
        String TASK_DELETE="task_delete";
        String BULK_REASIGN="task_bulk_reassign";
        String LINK_TICKET="task_link_task";
        String UPLOAD_DOCUMENT="task_upload_doc";
        String TASK_ETR="task_etr";
        String TASK_REMARKS="task_remarks";

    }

    public interface TicketFollowUp {
        String TICKET_FOLLOWUP = "task_follow_up";
        String TICKET_FOLLOWUP_CREATE = "task_follow_up_create";
        String TICKET_FOLLOWUP_EDIT="task_follow_up_edit";
        String TICKET_FOLLOWUP_DELETE="task_follow_up_delete";

    }
    public interface TaskConversation {
        String TASK_CONVERSATION = "task_conversation";
        String TICKET_CONVERSATION_CREATE = "task_conversation_create";
        String TASK_CONVERSATION_EDIT="task_conversation_edit";
        String TASK_CONVERSATION_DELETE="task_conversation_delete";
        String CHANGE_STATUS="task_change_status";
        String BULK_REASIGN="task_bulk_reassign";
        String TICKET_FOLLOWUP="task_follow_up";
        String CHANGE_PRIORITY="task_change_priority";
        String TICKET_ASSIGN="task_assign";
        String LINK_TICKET="task_line_task";
        String CHANGE_PROBLEM_DOMAIN="task_change_pb_domain";
        String SLA_COUNTER="task_sla_counter";

    }

    public static final String AUDIT_LOG = "audit_log";

}
