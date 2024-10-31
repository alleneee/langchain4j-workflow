package com.niko.langchain4jworkflow.workflow.model;

import com.niko.langchain4jworkflow.workflow.core.Node;
import com.niko.langchain4jworkflow.workflow.core.WorkflowDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工作流对象映射器
 * 负责在工作流定义对象(WorkflowDefinition)和数据传输对象(DTO)之间进行转换
 */
@Component
@Mapper(componentModel = "spring")
public interface WorkflowMapper {

    /**
     * 将工作流定义转换为DTO
     * @param definition 工作流定义对象
     * @return 工作流DTO
     */
    @Mapping(target = "id", source = "name")
    @Mapping(target = "nodes", source = "nodes", qualifiedByName = "mapNodesToList")
    WorkflowDTO toDto(WorkflowDefinition definition);

    /**
     * 将DTO转换为工作流定义
     * @param dto 工作流DTO
     * @return 工作流定义对象
     */
    @Mapping(target = "name", source = "id")
    @Mapping(target = "nodes", source = "nodes", qualifiedByName = "mapNodesToMap")
    WorkflowDefinition toDefinition(WorkflowDTO dto);

    /**
     * 将节点对象转换为DTO
     * @param node 节点对象
     * @return 节点DTO
     */
    @Mapping(target = "inputs", source = "inputs", qualifiedByName = "mapClassToString")
    @Mapping(target = "outputs", source = "outputs", qualifiedByName = "mapClassToString")
    NodeDTO nodeToDto(Node node);
    
    /**
     * 将节点DTO转换为节点对象
     * @param dto 节点DTO
     * @return 节点对象
     */
    @Mapping(target = "inputs", source = "inputs", qualifiedByName = "mapStringToClass")
    @Mapping(target = "outputs", source = "outputs", qualifiedByName = "mapStringToClass")
    Node dtoToNode(NodeDTO dto);

    /**
     * 将节点Map转换为节点DTO列表
     * @param nodes 节点Map
     * @return 节点DTO列表
     */
    @Named("mapNodesToList")
    default List<NodeDTO> mapNodesToList(Map<String, Node> nodes) {
        if (nodes == null) {
            return new ArrayList<>();
        }
        return nodes.values().stream()
                .map(this::nodeToDto)
                .collect(Collectors.toList());
    }

    /**
     * 将节点DTO列表转换为节点Map
     * @param nodes 节点DTO列表
     * @return 节点Map
     */
    @Named("mapNodesToMap")
    default Map<String, Node> mapNodesToMap(List<NodeDTO> nodes) {
        if (nodes == null) {
            return Map.of();
        }
        return nodes.stream()
                .map(this::dtoToNode)
                .collect(Collectors.toMap(Node::getName, node -> node));
    }

    /**
     * 将Class类型Map转换为字符串Map
     * @param map Class类型Map
     * @return 字符串Map
     */
    @Named("mapClassToString")
    default Map<String, String> mapClassToString(Map<String, Class<?>> map) {
        if (map == null) {
            return new HashMap<>();
        }
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getName()
                ));
    }

    /**
     * 将字符串Map转换为Class类型Map
     * @param map 字符串Map
     * @return Class类型Map
     * @throws RuntimeException 如果类型转换失败
     */
    @Named("mapStringToClass")
    default Map<String, Class<?>> mapStringToClass(Map<String, String> map) {
        if (map == null) {
            return new HashMap<>();
        }
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            try {
                                return Class.forName(e.getValue());
                            } catch (ClassNotFoundException ex) {
                                throw new RuntimeException("Failed to map class: " + e.getValue(), ex);
                            }
                        }
                ));
    }
}