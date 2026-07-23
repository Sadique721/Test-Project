package com.savbill.radius.services.impl;

import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableMetadataService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<String> getColumnNames(Class<?> entityClass) {
        List<String> ignoredFields = new ArrayList<>();
        ignoredFields.add("mvnoId");
        ignoredFields.add("lastModifiedOn");
        ignoredFields.add("createdOn");
        ignoredFields.add("priority");
        ignoredFields.add("status");
        ignoredFields.add("checkItem");

        EntityType<?> entityType = entityManager.getMetamodel().entity(entityClass);

        return entityType.getSingularAttributes()
                .stream()
                // Filter out the ignored field names
                .map(SingularAttribute::getName)
                .filter(columnName -> !ignoredFields.contains(columnName))
                .collect(Collectors.toList());
    }
}
