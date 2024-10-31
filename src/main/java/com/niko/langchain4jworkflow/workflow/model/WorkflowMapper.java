package com.niko.langchain4jworkflow.workflow.model;


import com.niko.langchain4jworkflow.workflow.core.Node;
import com.niko.langchain4jworkflow.workflow.core.WorkflowDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WorkflowMapper {
    WorkflowMapper INSTANCE = Mappers.getMapper(WorkflowMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    WorkflowDTO toDto(WorkflowDefinition definition);

    WorkflowDefinition toDefinition(WorkflowDTO dto);

    NodeDTO toDto(Node node);

    Node toNode(NodeDTO dto);

    WorkflowConfigDTO toDto(WorkflowDefinition.WorkflowConfig config);

    WorkflowDefinition.WorkflowConfig toConfig(WorkflowConfigDTO dto);
}
