package com.savbill.notification.spring;


import com.savbill.notification.singleton.LoggedInUserSingleton;
import lombok.extern.slf4j.Slf4j;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.SpringSecurityAuthorProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
@Slf4j
@Configuration
public class JaversConfiguration {
    @Autowired
    LoggedInUserSingleton loggedInUserSingleton;

    //private static final log log = logFactory.getlog(JaversConfiguration.class);

//    public LoggedInUser getLoggedInUser() {
//        LoggedInUser loggedInUser = null;
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
//            }
//        } catch (Exception e) {
//        }
//        return loggedInUser;
//    }

    @Bean
    public SpringSecurityAuthorProvider authorProvider() {
        return new SpringSecurityAuthorProvider() {
            @Override
            public String provide() {
                String authorName = "";
                if (loggedInUserSingleton != null) {
                    authorName = loggedInUserSingleton.getUserName();
                    return authorName;
                }else {
                    return  "admin";
                }
            }
        };
    }


    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new CommitPropertiesProvider() {

            @Override
            public Map<String, String> provide() {
                Map<String, String> props = new HashMap<>();
                String ipAddress = "";
                HttpServletRequest request = null;
                if (RequestContextHolder.getRequestAttributes() != null) {
                request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest() ;
                    ipAddress =(String) getIpAddressFromHeader(request);
                    log.info("******************** REMOTE ADDRESS START ********************");
                    log.info("IPV4 ADDRESS : "+ipAddress);
                    log.info("******************** REMOTE ADDRESS END ********************");

                } else {
                    ipAddress = "0.0.0.1";
                    log.info("******************** REMOTE ADDRESS START ********************");
                    log.info("IPV4 ADDRESS : "+ipAddress);
                    log.info("******************** REMOTE ADDRESS END ********************");
                }
                if (loggedInUserSingleton != null) {
                    try {
                        props.put("user_id", String.valueOf(loggedInUserSingleton.getUserId()));
                        props.put("user_name", String.valueOf(loggedInUserSingleton.getUserName()));
                        props.put("mvnoId", String.valueOf(loggedInUserSingleton.getMvnoId()));
                        props.put("Teams", String.valueOf(loggedInUserSingleton.getTeams()));
                        props.put("ip_address", ipAddress);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return props;
            }
        };
    }

//	//.. JaVers setup ..
//
//	/**
//	 * Creates JaVers instance with {@link JaversSqlRepository}
//	 */
//	@Bean
//	public Javers javers(PlatformTransactionManager txManager) {
//		JaversSqlRepository sqlRepository = SqlRepositoryBuilder
//				.sqlRepository()
//				.withConnectionProvider(jpaConnectionProvider())
//				.withDialect(DialectName.H2)
//				.build();
//
//		return TransactionalJaversBuilder
//				.javers()
//				.withTxManager(txManager)
//				.withObjectAccessHook(new HibernateUnproxyObjectAccessHook<>())
//				.registerJaversRepository(sqlRepository)
//				.build();
//	}
//
//	/**
//	 * Enables auto-audit aspect for ordinary repositories.<br/>
//	 *
//	 * Use {@link org.javers.spring.annotation.JaversAuditable}
//	 * to mark data writing methods that you want to audit.
//	 */
//	@Bean
//	public JaversAuditableAspect javersAuditableAspect(Javers javers) {
//		return new JaversAuditableAspect(javers, authorProvider(), commitPropertiesProvider());
//	}
//
//	/**
//	 * Enables auto-audit aspect for Spring Data repositories. <br/>
//	 *
//	 * Use {@link org.javers.spring.annotation.JaversSpringDataAuditable}
//	 * to annotate CrudRepository, PagingAndSortingRepository or JpaRepository
//	 * you want to audit.
//	 */
//	@Bean
//	public JaversSpringDataJpaAuditableRepositoryAspect javersSpringDataAuditableAspect(Javers javers) {
//		return new JaversSpringDataJpaAuditableRepositoryAspect(javers, authorProvider(), commitPropertiesProvider());
//	}
//
//	/**
//	 * Required by auto-audit aspect. <br/><br/>
//	 *
//	 * Creates {@link SpringSecurityAuthorProvider} instance,
//	 * suitable when using Spring Security
//	 */
////	@Bean
////	public AuthorProvider authorProvider() {
////		return new SpringSecurityAuthorProvider();
////	}
//
//	/**
//	 * Optional for auto-audit aspect. <br/>
//	 * @see CommitPropertiesProvider
//	 */
////	@Bean
////	public CommitPropertiesProvider commitPropertiesProvider() {
////		return new CommitPropertiesProvider() {
////			@Override
////			public Map<String, String> provideForCommittedObject(Object domainObject) {
////				if (domainObject instanceof DummyObject) {
////					return Maps.of("dummyObject.name", ((DummyObject)domainObject).getName());
////				}
////				return Collections.emptyMap();
////			}
////		};
////	}
//
//	/**
//	 * Integrates {@link org.javers.repository.sql.JaversSqlRepository\} with Spring {@link JpaTransactionManager}
//	 */
//	@Bean
//	public ConnectionProvider jpaConnectionProvider() {
//		return new JpaHibernateConnectionProvider();
//	}
//	//.. EOF JaVers setup ..
//
//
//	//.. Spring-JPA-Hibernate setup ..
//	@Bean
//	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//		em.setDataSource(dataSource());
//		em.setPackagesToScan("org.javers.spring.model");
//
//		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//		em.setJpaVendorAdapter(vendorAdapter);
//		em.setJpaProperties(additionalProperties());
//
//		return em;
//	}
//
//	@Bean
//	public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
//		JpaTransactionManager transactionManager = new JpaTransactionManager();
//		transactionManager.setEntityManagerFactory(emf);
//
//		return transactionManager;
//	}
//
//	@Bean
//	public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
//		return new PersistenceExceptionTranslationPostProcessor();
//	}
//
//	@Bean
//	public DataSource dataSource(){
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName("org.h2.Driver");
//		dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
//		return dataSource;
//	}
//
//	Properties additionalProperties() {
//		Properties properties = new Properties();
//		properties.setProperty("hibernate.hbm2ddl.auto", "create");
//		properties.setProperty("hibernate.connection.autocommit", "false");
//		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
//		return properties;
//	}

    public static final List<String> POSSIBLE_IP_HEADERS = Arrays.asList(
            "X-Forwarded-For",
            "HTTP_FORWARDED",
            "HTTP_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_CLIENT_IP",
            "HTTP_VIA",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "REMOTE_ADDR"
    );

    public static String getIpAddressFromHeader(HttpServletRequest request) {
        for (String ipHeader : POSSIBLE_IP_HEADERS) {
            String headerValue = Collections.list(request.getHeaders(ipHeader)).stream()
                    .filter(StringUtils::hasLength)
                    .findFirst()
                    .orElse(null);

            if (headerValue != null && !"0:0:0:0:0:0:0:1".equals(headerValue)) {
                return headerValue;
            }
        }


        return request.getRemoteAddr();
    }

}
