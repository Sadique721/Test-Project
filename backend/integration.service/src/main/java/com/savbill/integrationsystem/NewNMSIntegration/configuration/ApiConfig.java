package com.savbill.integrationsystem.NewNMSIntegration.configuration;

import com.savbill.integrationsystem.NewNMSIntegration.constants.NMSIntegrationConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class ApiConfig {

//    @Bean
    public Map<String, Set<String>> requiredParamsConfig(String operation) {
        Map<String, Set<String>> config = new HashMap<>();

        switch (operation) {
            case NMSIntegrationConstant.API_CONSTANT.ADD_ONU:
                config.put(NMSIntegrationConstant.API_CONSTANT.ADD_ONU, new HashSet<>(Arrays.asList(
                        "serialno", "oltid", "ponid", "ponytype", "authtype", "onuid", "onutype"
                )));
            case NMSIntegrationConstant.API_CONSTANT.DELETE_ONU:
                config.put(NMSIntegrationConstant.API_CONSTANT.DELETE_ONU, new HashSet<>(Arrays.asList(
                        "oltid", "onuid", "onuidtype", "ponid", "serialno"
                )));

                case NMSIntegrationConstant.API_CONSTANT.WAN_SERVICE:
                    config.put(NMSIntegrationConstant.API_CONSTANT.WAN_SERVICE,new HashSet<>(Arrays.asList(
                            "SERIALNO", "OLTID", "PONID", "ONUIDTYPE", "ONUID", "MODE", "CONNTYPE",
                            "VLAN", "COS", "NAT", "IPMODE", "IPSTACKMODE", "IP6SRCTYPE", "IP6PREFIXSRCTYPE",
                            "WANIP", "WANMASK", "WANGATEWAY", "MASTERDNS", "SLAVEDNS", "IP6ADDRESS", "IP6GATEWAY",
                            "IP6MASTERDNS", "IP6SLAVEDNS", "IP6STATICPREFIX", "PPPOEPROXY", "PPPOEUSER", "PPPOEPASSWD",
                            "PPPOENAME", "PPPOEAUTHMODE", "PPPOEMODE", "PPPOEIDLETIME", "QOS", "UPORT", "SSID",
                            "VLANMODE", "TRANSSTATE", "TRANSVALUE", "TRANSCOS", "QINQSTATE", "TPID", "SVLAN",
                            "QINQCOS", "DHCPREMOTEID", "TCONT", "GEMPORT", "UPNP"
                    )));

            case NMSIntegrationConstant.API_CONSTANT.WIFI_CONFIG:
                config.put(NMSIntegrationConstant.API_CONSTANT.WIFI_CONFIG,new HashSet<>(Arrays.asList(
                        "SERIALNO", "OLTID", "PONID", "ONUIDTYPE", "ONUID", "ENABLE", "WIRELESS-AREA",
                        "WIRELESS-CHANNEL", "WIRELESS-STANDARD", "WORKING-FREQUENCY", "T-POWER",
                        "FREQUENCY-BANDWIDTH", "SSID", "SSID-ENABLE", "SSID-NAME", "SSID-VISIBLE", "AUTH-MODE",
                        "ENCRYPT-TYPE", "PRESHARED-KEY", "UPDATEKEY-INTERVAL", "RADIUS-SERVER", "RADIUS-PORT",
                        "RADIUS-KEY", "WEP-ENCRYPTIONLEVEL", "WEP-KEYINDEX", "WEPKEY1", "WEPKEY2", "WEPKEY3",
                        "WEPKEY4", "WAP-IPADDRESS", "WAP-PORT", "MAX-WIFIMAC-COUNT", "PUBLICSSID",
                        "KICKSTATIONSWITCH", "LOWERTHRESHOLD"
                )));

        }
        return config;
    }



//        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

