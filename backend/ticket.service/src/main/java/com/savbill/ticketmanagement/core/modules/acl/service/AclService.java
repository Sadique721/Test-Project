//package com.savbill.ticketmanagement.core.modules.acl.service;
//
//import com.savbill.ticketmanagement.core.mapper.CycleAvoidingMappingContext;
//import com.savbill.ticketmanagement.core.modules.acl.domain.AclMenu;
//import com.savbill.ticketmanagement.core.modules.acl.domain.AclMenuQueryEntry;
//import com.savbill.ticketmanagement.core.modules.acl.domain.CustomACLEntry;
//import com.savbill.ticketmanagement.core.modules.acl.mapper.AclClassMapper;
//import com.savbill.ticketmanagement.core.modules.acl.mapper.AclMenuMapper;
//import com.savbill.ticketmanagement.core.modules.acl.model.*;
//import com.savbill.ticketmanagement.core.modules.acl.repository.AclClassRepository;
//import com.savbill.ticketmanagement.core.modules.acl.repository.AclMenuRepository;
//import com.savbill.ticketmanagement.core.modules.acl.repository.CustomACLEntryRepository;
//import com.savbill.ticketmanagement.core.modules.acl.repository.RoleAclRepository;
//import com.savbill.ticketmanagement.core.security.dto.LoggedInUser;
//import com.savbill.ticketmanagement.core.utillity.log.ApplicationLogger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.Query;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class AclService {
//
//    private static final String MODULE = " [AclService()] ";
//
//
//    @Autowired
//    AclClassRepository aclClassRepository;
//
//    @Autowired
//    private AclMenuRepository aclMenuRepository;
//
//    @Autowired
//    private AclMenuMapper aclMenuMapper;
//
//    @Autowired
//    AclClassMapper aclClassMapper;
//
//
//    @Autowired
//    CustomACLEntryRepository customACLEntryRepository;
//
//    @PersistenceContext
//    EntityManager entityManager;
//
//    @Autowired
//    private DemoGraphicMappingService demoGraphicMappingService;
//
//    @Autowired
//    private RoleAclRepository roleAclRepository;
//
//    public List<AclClassDTO> getModuleOperations() {
//
//        List<AclClassDTO> aclClassDTOList = aclClassRepository.findAll().stream()
//                .map(data -> aclClassMapper.domainToDTO(data, new CycleAvoidingMappingContext()))
//                .collect(Collectors.toList());
//        return aclClassDTOList;
//    }
//
//    /**
//     * Method to fetch all menu data and return menu structure
//     * @return
//     */
//    public List<AclMenuStructureDTO> createAclMenuStructure() {
//        List<AclMenu> aclMenus = aclMenuRepository.findAll();
//        List<AclMenuDtoNew> aclMenuDtoNew = aclMenuMapper.domainToDTO(aclMenus, new CycleAvoidingMappingContext());
//        List<AclMenuDtoNew> parent = aclMenuDtoNew.stream().filter(aclMenu -> aclMenu.getParentid().equals(0L)).collect(Collectors.toList());
//        return getChildMenuData(parent, aclMenuDtoNew);
//    }
//
//    /**
//     * Method to insert child sub child data
//     * @param parentDto
//     * @return
//     */
//    public List<AclMenuStructureDTO> getChildMenuData(List<AclMenuDtoNew> parentDto, List<AclMenuDtoNew> aclMenuDtoNew) {
//        if(!CollectionUtils.isEmpty(parentDto)) {
//            List<AclMenuStructureDTO> aclMenuStructureDTOS = new ArrayList<>();
//            for (AclMenuDtoNew aclDto: parentDto) {
//                AclMenuStructureDTO aclMenuStructureDTO = new AclMenuStructureDTO();
//                aclMenuStructureDTO.setData(aclDto);
//                List<AclMenuDtoNew> childdto = aclMenuDtoNew.stream().filter(aclMenu -> aclMenu.getParentid().equals(aclDto.getId())).collect(Collectors.toList());
//                if(!CollectionUtils.isEmpty(childdto))
//                    aclMenuStructureDTO.setChildren(getChildMenuData(childdto, aclMenuDtoNew));
//                aclMenuStructureDTOS.add(aclMenuStructureDTO);
//            }
//            return aclMenuStructureDTOS;
//        }
//        return null;
//    }
//
//    public List<AclMenuDTO> createAclMenuStructure1() {
//    	List<AclMenuDTO> menu = new ArrayList<AclMenuDTO>();
//    	List<AclClassDTO> aclClassDTOList = aclClassRepository.findAll().stream()
//                .map(data -> aclClassMapper.domainToDTO(data, new CycleAvoidingMappingContext()))
//                .collect(Collectors.toList());
//    	List<AclMenuQueryEntry> aclMenuQueryEntry = getAclMenu();
//    	for(AclMenuQueryEntry aclMenu: aclMenuQueryEntry) {
//    		if(aclMenu.getParentid() == null) {
//        		AclMenuDTO menuDto = new AclMenuDTO();
//    			menuDto.setDispName(aclMenu.getDispName());
//    			menuDto.setName(aclMenu.getName());
//    			menuDto.setMenuid(aclMenu.getMenuid());
//    			menuDto.setSubmenu(new ArrayList<AclClassDTO>());
//    			menu.add(menuDto);
//    		} else {
//    			Optional<AclMenuDTO> menuDto = menu.stream().filter(m -> m.getMenuid().equals(aclMenu.getParentid())).findAny();
//    			if(menuDto.isPresent()) {
//    				Optional<AclClassDTO> classDTO = aclClassDTOList.stream().filter(a -> a.getId().equals(aclMenu.getClassid())).findFirst();
//
//    				if(classDTO.isPresent()) {
//    					if(!CollectionUtils.isEmpty(classDTO.get().getAclOperationsList())) {
//    						List<AclOperationsDTO> operations = classDTO.get().getAclOperationsList().stream()
//    								.filter(operation -> operation.getParentOperationId().equals(0L)).collect(Collectors.toList());
//    						classDTO.get().setAclOperationsList(operations);
//    					}
//    					List<AclClassDTO> list = menuDto.get().getSubmenu();
//    					list.add(classDTO.get());
//    					TreeSet<AclClassDTO> finalList = list.stream() // get stream for original list
//    	                .collect(Collectors.toCollection(//distinct elements stored into new SET
//    	                        () -> new TreeSet<>(Comparator.comparing(AclClassDTO::getId))));
//
//                        List<DemoGraphicMappingTable> demoGraphicMappingTables = demoGraphicMappingService.getAll();
//                        finalList.stream()
//                                .filter(record1 -> demoGraphicMappingTables.stream().anyMatch(record2 -> record2.getCurrentName().equalsIgnoreCase(record1.getDispname())))
//                                .forEach(record1 -> {
//                                    DemoGraphicMappingTable matchingRecord = demoGraphicMappingTables.stream().filter(matchRec -> matchRec.getCurrentName().equalsIgnoreCase(record1.getDispname())).findFirst().orElse(null);
//                                    if (matchingRecord != null)
//                                        record1.setDispname(matchingRecord.getNewName());
//                                });
//
//                        menuDto.get().setSubmenu(finalList.stream().collect(Collectors.toList()));
//    				}
//    			}
//    		}
//    	}
//    	Set<AclMenuDTO> unique = menu
//                .stream() // get stream for original list
//                .collect(Collectors.toCollection(//distinct elements stored into new SET
//                    () -> new TreeSet<>(Comparator.comparing(AclMenuDTO::getMenuid))));
//    	return unique.stream().collect(Collectors.toList());
//    }
//    public List<AclMenuQueryEntry> getAclMenu() {
//    	Integer staffId = ((LoggedInUser) SecurityContextHolder
//                .getContext()
//                .getAuthentication()
//                .getPrincipal()).getUserId();
//    	String menuQuery = "WITH RECURSIVE menus AS (\n" +
//                "\n" +
//                "SELECT tam.menuid,tam.name,tam.dispname,tam.classid, tam.parentid,0 as level, t2.aclid \n" +
//                "FROM tblaclmenus tam \n" +
//                "left join tblaclentry t2 on tam.classid=t2.classid and t2.roleid in (select roleid from tbltstaffrolerel t3 where staffid = 1)\n" +
//                "where tam.parentid is null\n" +
//                "UNION\n" +
//                "SELECT tam2.menuid,tam2.name,tam2.dispname,tam2.classid, tam2.parentid, m.level + 1 as level, t2.aclid FROM tblaclmenus tam2 \n" +
//                "INNER JOIN menus m ON m.menuid=tam2.parentid\n" +
//                "left join tblaclentry t2 on tam2.classid=t2.classid and t2.roleid in (select roleid from tbltstaffrolerel t3 where staffid =" +
//                staffId + ")\n" +
//                "\n" +
//                ")\n" +
//                "SELECT * FROM menus order by level;";
//
//    	Query q = entityManager.createNativeQuery(menuQuery, AclMenuQueryEntry.class);
//        List<AclMenuQueryEntry> resultList = q.getResultList();
//
//        return resultList;
//    }
//
//    public Set<AclMenuQueryEntryDTO> getAclMenuByOrder() {
//    	Integer staffId = ((LoggedInUser) SecurityContextHolder
//                .getContext()
//                .getAuthentication()
//                .getPrincipal()).getUserId();
//    	String menuQuery = "WITH RECURSIVE menus AS (\n" +
//                "\n" +
//                "SELECT tam.menuid,tam.name,tam.dispname,tam.classid, tam.parentid,0 as level, t2.aclid \n" +
//                "FROM tblaclmenus tam \n" +
//                "left join tblaclentry t2 on tam.classid=t2.classid and t2.roleid in (select roleid from tbltstaffrolerel t3 where staffid = 1)\n" +
//                "where tam.parentid is null\n" +
//                "UNION\n" +
//                "SELECT tam2.menuid,tam2.name,tam2.dispname,tam2.classid, tam2.parentid, m.level + 1 as level, t2.aclid FROM tblaclmenus tam2 \n" +
//                "INNER JOIN menus m ON m.menuid=tam2.parentid\n" +
//                "left join tblaclentry t2 on tam2.classid=t2.classid and t2.roleid in (select roleid from tbltstaffrolerel t3 where staffid =" +
//                staffId + ")\n" +
//                "\n" +
//                ")\n" +
//                "SELECT * FROM menus order by menuid;";
//
//    	Query q = entityManager.createNativeQuery(menuQuery, AclMenuQueryEntry.class);
//        List<AclMenuQueryEntry> resultList = q.getResultList();
//
//        Set<AclMenuQueryEntryDTO> aclMenuQueryEntryDTOList = new HashSet<>();
//        for (AclMenuQueryEntry aclMenuQueryEntry: resultList) {
//            AclMenuQueryEntryDTO aclMenuQueryEntryDTO = new AclMenuQueryEntryDTO();
//            aclMenuQueryEntryDTO.setMenuid(aclMenuQueryEntry.getMenuid());
//            aclMenuQueryEntryDTO.setName(aclMenuQueryEntry.getName());
//            aclMenuQueryEntryDTO.setDispName(aclMenuQueryEntry.getDispName());
//            aclMenuQueryEntryDTO.setClassid(aclMenuQueryEntry.getClassid());
//            aclMenuQueryEntryDTO.setParentid(aclMenuQueryEntry.getParentid());
//            aclMenuQueryEntryDTO.setLevel(aclMenuQueryEntry.getLevel());
//            aclMenuQueryEntryDTO.setAclid(aclMenuQueryEntry.getAclid());
//            if(aclMenuQueryEntry.getClassid() != null)
//                aclMenuQueryEntryDTO.setPermits(getPermitByClassIdandStaffId(staffId,aclMenuQueryEntry.getClassid().intValue()));
//            aclMenuQueryEntryDTOList.add(aclMenuQueryEntryDTO);
//        }
//        return aclMenuQueryEntryDTOList;
//    }
//
//    public List<AclRoleDTO> getAllRoleOperations() {
//        String SUBMODULE = MODULE + " [getAllRoleOperations()]";
//        List<AclRoleDTO> aclRoleDTOList = new ArrayList<>();
//        try {
//            List<CustomACLEntry> aclEntryDTOList = customACLEntryRepository.findAll();
//            aclRoleDTOList = getMappedList(aclEntryDTOList);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return aclRoleDTOList;
//    }
//
//
//    public AclMenuStructDTO getMenuStructure() {
//
//        Integer staffId = ((LoggedInUser) SecurityContextHolder
//                .getContext()
//                .getAuthentication()
//                .getPrincipal()).getUserId();
//        String menuQuery = "WITH RECURSIVE menus AS (\n" +
//                "\n" +
//                "SELECT tam.menuid,tam.name,tam.dispname,tam.classid, tam.parentid,0 as level, t2.aclid \n" +
//                "FROM tblaclmenus tam \n" +
//                "left join tblaclentry t2 on tam.classid=t2.classid and t2.roleid in (select roleid from tbltstaffrolerel t3 where staffid = 1)\n" +
//                "where tam.parentid is null\n" +
//                "UNION\n" +
//                "SELECT tam2.menuid,tam2.name,tam2.dispname,tam2.classid, tam2.parentid, m.level + 1 as level, t2.aclid FROM tblaclmenus tam2 \n" +
//                "INNER JOIN menus m ON m.menuid=tam2.parentid\n" +
//                "left join tblaclentry t2 on tam2.classid=t2.classid and t2.roleid in (select roleid from tbltstaffrolerel t3 where staffid =" +
//                staffId + ")\n" +
//                "\n" +
//                ")\n" +
//                "SELECT * FROM menus order by level;";
//
//        ApplicationLogger.logger.debug(menuQuery);
//
//        Query q = entityManager.createNativeQuery(menuQuery, AclMenuQueryEntry.class);
//        List<AclMenuQueryEntry> resultList = q.getResultList();
//
//        AclMenuStructDTO rootDTO = new AclMenuStructDTO(0L, "root", "root", 0L);
//        TreeMap<Long, AclMenuStructDTO> nodeMap = new TreeMap<Long, AclMenuStructDTO>();
//        List<Integer> permits = new ArrayList<>();
//        if (resultList != null && resultList.size() > 0) {
//            AclMenuStructDTO chartDTO = null;
//            for (AclMenuQueryEntry aclMenuQueryEntry : resultList) {
//                if (aclMenuQueryEntry.getClassid() != null && aclMenuQueryEntry.getAclid() == null)
//                    continue;
//                //get permit list
//                if(aclMenuQueryEntry.getClassid() != null)
//                    permits = getPermitByClassIdandStaffId(staffId,aclMenuQueryEntry.getClassid().intValue());
//                chartDTO = new AclMenuStructDTO(aclMenuQueryEntry.getMenuid(),
//                        aclMenuQueryEntry.getName(),
//                        aclMenuQueryEntry.getDispName(),
//                        aclMenuQueryEntry.getClassid(),permits);
//
//                if (aclMenuQueryEntry.getParentid() != null && nodeMap.containsKey(aclMenuQueryEntry.getParentid())) {
//                    AclMenuStructDTO parentDTO = nodeMap.get(aclMenuQueryEntry.getParentid());
//                    Set<AclMenuStructDTO> childList = parentDTO.getSubmenu();
//                    if (childList == null) {
////                        childList = new ArrayList<AclMenuStructDTO>();
////                        parentDTO.setSubmenu(childList);
//                    }
//                    childList.add(chartDTO);
//                }
//                nodeMap.put(aclMenuQueryEntry.getMenuid(), chartDTO);
//
//                if (aclMenuQueryEntry.getParentid() == null) {
//                    rootDTO.getSubmenu().add(chartDTO);
//                }
//            }
//        }
//        rootDTO = pruneMenuTree(rootDTO);
//       //ApplicationLogger.logger.info(rootDTO);
//        return rootDTO;
//    }
//
//
//    public List<AclRoleDTO> getRoleOperations(String roles){
//        String SUBMODULE = MODULE + " [getRoleOperations()]";
//        List<AclRoleDTO> aclRoleDTOList = new ArrayList<>();
//        List<Long> roleList = Arrays.stream(roles.split(",")).map(Long::valueOf).collect(Collectors.toList());
//        try {
//            List<CustomACLEntry> aclEntryDTOList = customACLEntryRepository.findAllByRole_IdIn(roleList);
//            aclRoleDTOList = getMappedList(aclEntryDTOList);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return aclRoleDTOList;
//
//    }
//
//    private List<AclRoleDTO> getMappedList(List<CustomACLEntry> customACLEntriesList) {
//        String SUBMODULE = MODULE + " [getMappedList()]";
//        List<AclRoleDTO> aclRoleDTOList = new ArrayList<>();
//        Map<Long, List<AclRoleOperationsDTO>> aclRoleMap = new HashMap<>();
//        try {
//            for (CustomACLEntry aclEntry : customACLEntriesList) {
//                List<AclRoleOperationsDTO> opList = aclRoleMap.get(aclEntry.getRole().getId());
//                AclRoleOperationsDTO op = new AclRoleOperationsDTO();
//                if (null == opList)
//                    opList = new ArrayList<>();
//
//                op.setClassid(aclEntry.getClassid());
//                op.setOpid(aclEntry.getPermit());
//                opList.add(op);
//                aclRoleMap.put(aclEntry.getRole().getId(), opList);
//            }
//
//            for (Map.Entry<Long, List<AclRoleOperationsDTO>> mapEntry : aclRoleMap.entrySet()) {
//                AclRoleDTO roleDTO = new AclRoleDTO();
//                roleDTO.setRoleid(mapEntry.getKey());
//                roleDTO.setOperations(mapEntry.getValue());
//                aclRoleDTOList.add(roleDTO);
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return aclRoleDTOList;
//    }
//
//    private AclMenuStructDTO pruneMenuTree(AclMenuStructDTO root) {
//
//        if (root.getSubmenu() != null && root.getSubmenu().size() > 0) {
//            root.getSubmenu().removeIf(child -> child.getSubmenu().size() == 0);
//        }
//        return root;
//    }
//
//    public List<Integer> getPermitByClassIdandStaffId(Integer staffId,Integer classId){
//        List<Integer> permitIds = new ArrayList<>();
//        String permitQuery = "select * from tblaclentry acl INNER JOIN tbltstaffrolerel t\n" +
//                "ON t.roleid = acl.roleid where classid = "+classId+" and t.staffid = "+staffId+"\n" +
//                "\n";
//        Query q = entityManager.createNativeQuery(permitQuery, CustomACLEntry.class);
//        List<CustomACLEntry> resultList = q.getResultList();
//        for(CustomACLEntry customACLEntry: resultList){
//            permitIds.add(customACLEntry.getPermit());
//        }
//        return permitIds;
//    }
//
//    public List<RoleACLEntryDTO> fetchRoleAclEntryByRoleId(Long aLong) {
//        List<RoleACLEntry> roleACLEntries = roleAclRepository.findAllByRole(aLong);
//        List<RoleACLEntryDTO> roleACLEntryDTOS = roleACLEntries.stream().map(roleACLEntry ->
//                new RoleACLEntryDTO(roleACLEntry.getId(), roleACLEntry.getCode(), roleACLEntry.getMenuid())).collect(Collectors.toList());
//        return roleACLEntryDTOS;
//    }
//}
//
