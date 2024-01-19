package com.spom.service.service;

import com.spom.service.dto.ProjectDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public interface ProjectService {

    List<ProjectDto> findProject() throws Exception;

    ProjectDto saveProject(ProjectDto projectDto) throws Exception;

    Mono<String> deleteProject(String id) throws Exception;

	ProjectDto findByProjectId(String projectId) throws Exception;

}
