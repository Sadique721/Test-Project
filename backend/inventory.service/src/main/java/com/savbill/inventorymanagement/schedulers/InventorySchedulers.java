package com.savbill.inventorymanagement.schedulers;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.kafka.KafkaMessageData;
import com.savbill.inventorymanagement.kafka.KafkaMessageSender;
import com.savbill.inventorymanagement.modules.ClientService.ClientService;
import com.savbill.inventorymanagement.modules.ClientService.ClientServiceRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.InventoryMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.InventoryMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.Item;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMappingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.WarrantyNotificationMessage;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDevices;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.DevicePortNotificationMessage;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.NetworkDeviceRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagement;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagementRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.repository.IPPoolDtlsRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaRepository;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.rabbitmq.MessageSender;
import com.savbill.inventorymanagement.rabbitmq.RabbitMqConstants;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "spring.enable.scheduling")
public class InventorySchedulers {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    IPPoolDtlsRepository ipPoolDtlsRepository;
    @Autowired
    ItemWarrantyMappingRepository itemWarrantyMappingRepository;
    @Autowired
    InwardRepository inwardRepository;
    @Autowired
    InOutWardMacRepo inOutWardMacRepo;
    @Autowired
    StaffUserRepository staffUserRepository;
    @Autowired
    ProductRepository productRepository;

    @Autowired
    private NetworkDeviceRepository networkDeviceRepository;

    @Autowired
    private InventoryMappingRepo inventoryMappingRepo;

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private ClientServiceRepository clientServiceRepository;

    @Autowired
    private PopManagementRepository popManagementRepository;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Scheduled(cron = "${cronJobTimeForwarrentydays}")
    public void getitemwarrantyUpdatation() throws Exception {
        System.out.println("***** -------------Savbill Inventory Scheduler for Item Warranty Action Starting----------------- *****");
//        List<Item> itemList = itemRepository.findBywarranty();
        List<Item> itemList = itemRepository.findAllByWarranty(CommonConstants.ITEM_WARRENTY_STATUS.INWARRANTY);
        itemList.stream().forEach(r -> {
            LocalDateTime expDate = r.getExpireDate();
            LocalDateTime currentDate = LocalDateTime.now();
            if (r.getExpireDate() != null && currentDate != null) {
                Duration duration = Duration.between(currentDate, expDate);
                if ((duration.toDays() == 0L && duration.toHours() == 0L && duration.toMinutes() == 0L) || (currentDate.isAfter(expDate))) {
                    r.setWarranty("Expired");
                    r.setRemainingDays(null);
                    itemRepository.save(r);
                    List<ItemWarrantyMapping> itemWarrantyMapping = itemWarrantyMappingRepository.findByItemId(r.getId());
                    if (!(itemWarrantyMapping.isEmpty())) {
                        ItemWarrantyMapping itemWarrantyMapping1 = itemWarrantyMapping.get(itemWarrantyMapping.size() - 1);
                        if (itemWarrantyMapping1.getWarranty().equalsIgnoreCase("InWarranty"))
                            itemWarrantyMapping1.setWarranty("Expired");
                        itemWarrantyMappingRepository.save(itemWarrantyMapping1);
                    }
                } else {
                    r.setRemainingDays(String.valueOf(duration.toDays()));
                    itemRepository.save(r);
                }
            }
        });
        System.out.println("***** -------------Savbill Inventory Scheduler for Item Warranty Action End----------------- *****");
    }

    @Scheduled(cron = "${cronjobtimeforiprelease}}")
    public void cronJob() {
        System.out.println("***** -------------Savbill Inventory Scheduler for IP Release Action Starting----------------- *****");
        ApplicationLogger.logger.info("CRON JOB FOR IP RELEASE : " + LocalDateTime.now());
        ipPoolDtlsRepository.releaseIP(LocalDateTime.now());
        System.out.println("***** -------------Savbill Inventory Scheduler for IP Release Action End----------------- *****");
    }


    @Scheduled(cron = "${cronJobTimeForOemWarrantyDays}")
    public void getOEMInwardAndItemWarrantyUpdation() throws Exception {
        System.out.println("***** -------------Savbill Inventory Scheduler for OEM Warranty Action Starting----------------- *****");
        LocalDate currentDate=LocalDate.now();
        List<Item> itemList = itemRepository.findAllByOemWarranty();
        itemList=itemList.stream().filter(x->!x.getOemWarrantyStatus().equalsIgnoreCase("Expired")).collect(Collectors.toList());
        itemList.stream().forEach(item -> {
            LocalDate startDate=item.getOemStartDate();
            LocalDate expDate = item.getOemEndDate();

            if (startDate != null && expDate!=null && currentDate != null)
            {
                if(startDate.equals(currentDate)  || currentDate.isAfter(startDate) || currentDate.equals(expDate)) {
                    Long days = Duration.between(currentDate.atStartOfDay(), expDate.atStartOfDay()).toDays();
                    if(currentDate.equals(expDate))
                        days+=1;
                    if (days == 0 || (currentDate.isAfter(expDate))) {
                        item.setOemWarrantyStatus("Expired");
                        item.setOemWarrantyRemainingDays(0);
                    } else {
                        item.setOemWarrantyStatus("InWarranty");
                        if(currentDate.equals(expDate))
                            item.setOemWarrantyRemainingDays(1);
                        else
                            item.setOemWarrantyRemainingDays(days.intValue()-1);

                        if(item.getOwnerType()!=null && item.getOwnerType().equalsIgnoreCase(CommonConstants.STAFF) && item.getOwnerId()!=null)
                        {
                            StaffUser staffUser=staffUserRepository.findById(item.getOwnerId().intValue()).orElse(null);
                            if(staffUser!=null)
                            {
                                Product product=productRepository.findById(item.getProductId()).orElse(null);
                                ClientService clientService=clientServiceRepository.findByNameAndMvnoId(CommonConstants.WARRANTY_REMAINING_DAY,staffUser.getMvnoId());
                                String defaultRemainingDaysNotification="2";
                                if(clientService!=null)
                                    defaultRemainingDaysNotification=clientService.getValue();
                                if(product!=null && product.getHasAssetConsider()!=null && product.getHasAssetConsider() && defaultRemainingDaysNotification.equalsIgnoreCase(item.getOemWarrantyRemainingDays().toString()))
                                {
                                    WarrantyNotificationMessage message=new WarrantyNotificationMessage(staffUser.getFirstname(),item.getAssetId(),item.getSerialNumber(),product.getName(),product.getProductCategory().getName(),defaultRemainingDaysNotification,staffUser.getPhone(),staffUser.getEmail(), RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_INVENTORY,RabbitMqConstants.INVENTORY_WARRANTY_REMAINDER_TO_STAFF_EVENT,staffUser.getMvnoId(),null);
                                    Gson gson = new Gson();
                                    gson.toJson(message);
//                                    messageSender.send(message,RabbitMqConstants.QUEUE_INVENTORY_WARRANTY_REMAINDER_MESSAGE_TO_STAFF_TO_NOTIFICATION);
                                    kafkaMessageSender.send(new KafkaMessageData( message,WarrantyNotificationMessage.class.getSimpleName()));
                                }
                            }
                        }
                    }
                }
                else {
                    Long days = Duration.between(startDate.atStartOfDay(), expDate.atStartOfDay()).toDays();
                    item.setOemWarrantyRemainingDays(days.intValue());
                    item.setOemWarrantyStatus("NotStarted");
                }
                itemRepository.save(item);
            }
        });

        System.out.println("***** -------------Savbill Inventory Scheduler for OEM Warranty Action End----------------- *****");
    }



    @Scheduled(cron = "${cronJobTimeForUsedPortNotificationScheduler}}")
    public void sendDeviceUsedPortNotificationScheduler() throws Exception {
        System.out.println("***** -------------Network Device Port Used Notification Scheduler Starting----------------- *****");

        List<NetworkDevices> networkDevices = networkDeviceRepository.findAll();
        networkDevices=networkDevices.stream().filter(x->x.getIsDeleted()!=null && x.getIsDeleted().equals(false) && x.getStatus()!=null && x.getStatus().equalsIgnoreCase("Active") && x.getTotalInPorts()!=null && x.getAvailableInPorts()!=null && x.getTotalInPorts()>0 && x.getAvailableInPorts()>0).collect(Collectors.toList());
        if(networkDevices!=null && !networkDevices.isEmpty()) {
            networkDevices.stream().forEach(device->{
                Double consumedPercentage=((device.getTotalInPorts()-device.getAvailableInPorts())*100.0d)/device.getTotalInPorts();
                if(consumedPercentage > 60.0d) {
                    if(device.getInventorymappingId()!=null) {
                        InventoryMapping mapping=inventoryMappingRepo.findById(device.getInventorymappingId()).orElse(null);
                        if(mapping!=null && mapping.getStaff()!=null) {
                            String ownerType=null;
                            String ownerName=null;
                            if(mapping.getOwnerType()!=null && mapping.getOwnerType().equalsIgnoreCase("POP")) {
                                PopManagement popManagement=popManagementRepository.findById(mapping.getOwnerId()).orElse(null);
                                if(popManagement!=null)
                                    ownerName = popManagement.getName() +"[Pop]";
                                ownerType="Pop";
                            }
                            if(mapping.getOwnerType()!=null && mapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                                ServiceArea serviceArea=serviceAreaRepository.findById(mapping.getOwnerId()).orElse(null);
                                if(serviceArea!=null)
                                    ownerName = serviceArea.getName()+"[Service-Area]";;
                                ownerType="Service-Area";
                            }

                            StaffUser staffUser=staffUserRepository.findById(mapping.getStaff().getId()).orElse(null);
                            if(staffUser!=null) {
                                DecimalFormat df = new DecimalFormat("0.00");
                                consumedPercentage=Double.parseDouble(df.format(consumedPercentage));
                                DevicePortNotificationMessage message=new DevicePortNotificationMessage(ownerType,ownerName,staffUser.getUsername(),device.getName(),consumedPercentage+"%",staffUser.getPhone(),staffUser.getEmail(),RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_INVENTORY,RabbitMqConstants.NETWORK_DEVICE_INPUT_PORT_USED_PERCENTAGE_NOTIFICATION_SUBJECT+ownerName,staffUser.getMvnoId(),null);
//                                messageSender.send(message,RabbitMqConstants.QUEUE_SEND_USED_PORT_NOTIFICATION_INVENTORY_TO_NOTIFICATION);
                                kafkaMessageSender.send(new KafkaMessageData(message,DevicePortNotificationMessage.class.getSimpleName()));
                            }
                        }
                    }
                }
            });
        }

        System.out.println("***** -------------Network Device Port Used Notification Scheduler Ending----------------- *****");
    }


    //TOdo: This is not possible due to custchargedtls is not preset in inventory, This is an development task
//    @Scheduled(cron = "${everydaycronjobtimeforiprelease}")
//    public void cronJob2() {
//        System.out.println("***** -------------Savbill Inventory Scheduler for Every Day IP Release Action Starting----------------- *****");
//        ApplicationLogger.logger.info("Every Day CRON JOB FOR IP RELEASE : " + LocalDateTime.now());
//        ipPoolDtlsRepository.releaseIP2(LocalDateTime.now());
//        System.out.println("***** -------------Savbill Inventory Scheduler for Every Day IP Release Action End----------------- *****");
//    }
}
