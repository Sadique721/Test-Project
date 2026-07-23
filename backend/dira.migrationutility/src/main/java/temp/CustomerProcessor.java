//package temp;
//	import com.savbillt.revenuemanagement.core.constants.Constants;
//	import com.savbillt.revenuemanagement.core.entity.customers.CustPlanMappping;
//	import com.savbillt.revenuemanagement.core.entity.customers.CustPlanMapppingRepository;
//	import com.savbillt.revenuemanagement.core.entity.customers.Customers;
//	import com.savbillt.revenuemanagement.core.service.postpaid.PostpaidInvoiceService;
//	import com.savbillt.revenuemanagement.core.service.prepaid.PrepaidInvoiceService;
//	import com.savbillt.revenuemanagement.rabbitmq.MessageReceiverWithThread;
//	import com.savbillt.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
//	import com.savbillt.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.ChangePlanMessage;
//	import lombok.NoArgsConstructor;
//	import org.slf4j.Logger;
//	import org.slf4j.LoggerFactory;
//	import org.springframework.stereotype.Component;
//	import org.springframework.stereotype.Service;
//
//	import javax.persistence.EntityManager;
//	import javax.persistence.PersistenceContext;
//	import java.time.LocalDate;
//	import java.util.List;
//	import java.util.Map;
//	import java.util.stream.Collectors;
//	
//	public class CustomerProcessor implements Runnable{
//
//	    private static final Logger logger = LoggerFactory.getLogger(InvoiceProcessor.class);
//
//	    private CustomerBillingMessage msg;
//	    private MessageReceiverWithThread messageReceiverWithThread;
//
//	    private Customers customers;
//	    @PersistenceContext
//	    EntityManager entityManager;
//
//
//
//	    public CustomerProcessor(MessageReceiverWithThread messageReceiverWithThread, CustomerBillingMessage customerBillingMessage, Customers customers)
//	    {
//	        this.messageReceiverWithThread=messageReceiverWithThread;
//	        this.msg=customerBillingMessage;
//	        this.customers=customers;
//	    }
//
//	    @Override
//	    public void run() {
//	        messageReceiverWithThread.processMessage(msg,customers);
//	    }
//	}
//
//
//}
