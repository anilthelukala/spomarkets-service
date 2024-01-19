package com.spom.service.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.spom.service.dto.ProjectDto;
import com.spom.service.dto.UserInfo;
import com.spom.service.model.ProjectEntity;
import com.spom.service.model.PropertyFractionalisationEntity;
import com.spom.service.repository.ProjectRepository;
import com.spom.service.repository.PropertyFractionalisationRepsitory;
import com.spom.service.repository.PropertyRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProjectServiceImpl implements ProjectService {

	private final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private PropertyRepository propertyRepository;

	@Autowired
	private PropertyFractionalisationRepsitory propertyFractionalisationRepsitory;

	@Override
	public List<ProjectDto> findProject() {
		log.trace("Start ProjectService.. findAllProject");

		List<ProjectDto> projectDtos = new ArrayList<ProjectDto>();

		Flux<ProjectEntity> getProjects = this.projectRepository.findAll().flatMapSequential(project -> {
		    String projectId = project.getId();
		    return this.propertyRepository.findByProjectId(projectId)
		            .flatMapSequential(property -> {
		                return this.propertyFractionalisationRepsitory.findByPropertyId(property.getId())
		                        .switchIfEmpty(Mono.just(new PropertyFractionalisationEntity()))
		                        .map(propertyFraction -> {
		                        	if(null!=propertyFraction.getId())
		                            property.setPropertyFractionalisationEntity(propertyFraction);
		                            return property;
		                        });
		            })
		            .collectList()
		            .doOnNext(propertyEntities -> project.setPropertiesAssociated(propertyEntities))
		            .thenReturn(project);
		}).doOnError(error -> log.error("Error in ProjectService findProject Error Massage::{}", error.getMessage()))
		        .onErrorResume(error -> Mono.error(new RuntimeException(error)));

		List<ProjectEntity> saveProjectEntitys = new ArrayList<ProjectEntity>();
		saveProjectEntitys = getProjects.collectList().block();

		for (ProjectEntity project : saveProjectEntitys) {
			ProjectDto projectDto = new ProjectDto();
			BeanUtils.copyProperties(project, projectDto);
			projectDtos.add(projectDto);
		}

		log.trace("End ProjectService.. findAllProject Response :: {}", projectDtos);
		return projectDtos;
	}

	@Override
	public ProjectDto saveProject(ProjectDto projectDto) {
		log.trace("Start ProjectService.. saveProject project::{}", projectDto);
		Mono<ProjectEntity> projectEntity = null;

		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserInfo userInfo = null;
		if (null != authentication && null != authentication.getPrincipal()) {
			userInfo = (UserInfo) authentication.getPrincipal();
		}

		if (null != projectDto.getId()) {
			if (null != userInfo) {
				projectDto.setModifiedBy(userInfo.getEmail());
			}
			projectEntity = this.projectRepository.findById(projectDto.getId()).flatMap(existingProject -> {
				existingProject.setSummary(projectDto.getSummary());
				existingProject.setProjectName(projectDto.getProjectName());
				existingProject.setPerformanceIndicators(projectDto.getPerformanceIndicators());
				existingProject.setProjectActiveFlag(projectDto.getProjectActiveFlag());
				existingProject.setModifiedBy(projectDto.getModifiedBy());
				existingProject.setModifiedDate(new Date());
				existingProject.setPlatformCharges(projectDto.getPlatformCharges());
				existingProject.setSattlementDate(projectDto.getSattlementDate());
				// Save the updated project
				return projectRepository.save(existingProject);
			}).doOnError(error -> log.error("Error in ProjectService saveProject Error Massage::{}", error.getMessage())).onErrorResume(error -> {
				return Mono.error(new RuntimeException(error));
			});

		} else {
			ProjectEntity project = new ProjectEntity();
			BeanUtils.copyProperties(projectDto, project);
			if (null != userInfo) {
				project.setCreatedBy(userInfo.getEmail());
			}
			project.setCreatedDate(new Date());
			projectEntity = this.projectRepository.save(project)
					.doOnError(error -> log.error("Error in ProjectService saveProject Error Massage::{}", error.getMessage())).onErrorResume(error -> {
						return Mono.error(new RuntimeException(error));
					});
		}
		BeanUtils.copyProperties(projectEntity.block(), projectDto);
		log.trace("End ProjectService.. saveProject Response :: {}", projectDto);
		return projectDto;
	}

	@Override
	public Mono<String> deleteProject(String id) {
		log.trace("Start ProjectService.. deleteProject Id :: {}", id);
		// Delete child records first
		Mono<Void> deleteChildren = Mono.zip(this.propertyRepository.deleteByProjectId(id),
				this.propertyFractionalisationRepsitory.deleteByProjectId(id)).then();

		// Delete the parent record after deleting children
		return deleteChildren.then(this.projectRepository.deleteById(id)).thenReturn("Project deleted successfully")
				.doOnError(error -> log.error("Error in ProjectService deleteProject Error Massage::{}", error.getMessage()))
				.onErrorResume(error -> Mono.error(new RuntimeException(error)));
	}

	@Override
	public ProjectDto findByProjectId(String projectId) throws Exception {
		log.trace("Start ProjectService.. findByProjectId projectId :: {}", projectId);

        Mono<ProjectEntity> monoProject = this.projectRepository.findById(projectId)
                .doOnError(error -> log.error("Error in ProjectService findByProjectId Error Massage::{}", error.getMessage())).onErrorResume(error -> {
                    return Mono.error(new Exception(error));
        });

        ProjectDto projectDto = new ProjectDto();
        if(null != monoProject){
			ProjectEntity projectEntity = monoProject.block();
			if(null != projectEntity){
				BeanUtils.copyProperties(projectEntity, projectDto);
			}else{
				log.trace("End ProjectService.. findByProjectId Response :: {}", "Project is not found with Id " + projectId);
				throw new Exception("Project is not fount with ID::" + projectId);
			}
		}else{
			log.trace("End ProjectService.. findByProjectId Response :: {}", "Project is not found with Id " + projectId);
			throw new Exception("Project is not fount with ID::" + projectId);
		}
		log.trace("End ProjectService.. findByProjectId Response :: {}", projectDto);
		return projectDto;
        // return getProject.flatMap(project -> {
	    //     BeanUtils.copyProperties(project, projectDto);
	    //     return Mono.just(projectDto);
	    // }).block();
		
	}

}
