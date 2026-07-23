package ticketsystem;

import java.util.List;
import java.util.Map;

import utility.ModuleControlConstant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class CreateTicketData {

    private void CreateTAT() {
        if (ModuleControlConstant.TAT) {
            TAT tat = new TAT();
            List<Map<String, String>> tatMapList = tat.readTATList();
            tat.createTAT(tatMapList);
        }
    }

    private void createProblemDomain() {
        if (ModuleControlConstant.PROBLEMDOMAIN) {
            ProblemDomain problemDomain = new ProblemDomain();
            List<Map<String, String>> problemDomainMapList = problemDomain.readProblemDomainList();
            problemDomain.createProblemDomain(problemDomainMapList);
        }
    }

    private void createSubProblemDomain() {
        if (ModuleControlConstant.SUBPROBLEMDOMAIN) {
            SubProblemDomain subProblemDomain = new SubProblemDomain();
            List<Map<String, String>> subProblemDomainMapList = subProblemDomain.readSubProblemDomainList();
            subProblemDomain.createSubProblemDomain(subProblemDomainMapList);
        }
    }

    private void createRootCause() {
        if (ModuleControlConstant.ROOTCAUSE) {
            RootCauseMaster rootCauseMaster = new RootCauseMaster();
            List<Map<String, String>> rootCauseMasterMapList = rootCauseMaster.readRootCauseList();
            rootCauseMaster.createRootCause(rootCauseMasterMapList);
        }
    }

    private void createTicket() {
        if (ModuleControlConstant.TICKET) {
            Ticket ticket = new Ticket();
            List<Map<String, String>> ticketMapList = ticket.readTicketList();
            ticket.createTicket(ticketMapList);
        }
    }

    private void createTicketUpdation() {
        try {
            if (ModuleControlConstant.TICKETPOSTPUT) {
                ReadWriteExcelFile rw = new ReadWriteExcelFile();
                TicketCreationwithUpdation ticketCreationwithUpdation = new TicketCreationwithUpdation();
                List<Map<String, String>> ticketList = ticketCreationwithUpdation.readTicketUpdationList();
                ticketCreationwithUpdation.createTicketUpdation(ticketList);
                rw.setMultipleColumnInActiveSheetSavana();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getting error in this method (Ticket)..... " + e.getMessage());
        }
    }

    public void generateTicketData() {
        System.out.println("Started to generte TicketData...!");
        Utility.printLog("execution.log", "TicketData", "Started Generting Ticket Data...!", "");

        CreateTAT();
        createProblemDomain();
        createSubProblemDomain();
        createRootCause();
        createTicket();
        createTicketUpdation();

        System.out.println("Ended to generte TicketData...!");
        Utility.printLog("execution.log", "TicketData", "Ended Generting Ticket Data...!", "");
    }

}
