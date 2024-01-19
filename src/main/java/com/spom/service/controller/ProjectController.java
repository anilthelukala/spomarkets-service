package com.spom.service.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spom.service.common.CommonMessageUtil;
import com.spom.service.common.CommonResponse;
import com.spom.service.constant.SPOMarketsConstants;
import com.spom.service.dto.ProjectDto;
import com.spom.service.service.ProjectService;

import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("/project")
public class ProjectController {

	private final Logger log = LoggerFactory.getLogger(ProjectController.class);

	@Autowired
	private ProjectService projectService;

	@RequestMapping(value = "/getAllProjects", method = RequestMethod.GET)
	public ResponseEntity<?> getProject() {

		log.trace("Start ProjectController.. getAllProjects()");
		CommonResponse response = new CommonResponse();
		try {
			List<ProjectDto> projects = new ArrayList<ProjectDto>();
			projects = projectService.findProject();
			if (projects.size() > 0) {
				response.addData("ProjectList", projects);
				response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Get ProjectList Successfully"));
			} else {
				response.addData("ProjectList", projects);
				response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Project list is empty"));
			}
			log.trace("End ProjectController.. getAllProjects Response :: {}", projects);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			log.error("Error in ProjectController getAllProjects :: {}", e.getMessage());
			response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}

	}

	@PostMapping("/saveProject")
	public ResponseEntity<?> saveProject(@RequestBody ProjectDto projectDto) {

		log.trace("Start ProjectController.. saveProject RequestBody :: {}", projectDto);
		CommonResponse response = new CommonResponse();
		try {
			ProjectDto savedproject = projectService.saveProject(projectDto);
			response.addData("SavedProject", savedproject);
			response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Saved Successfully"));
			log.trace("End ProjectController.. saveProject Response :: {}", response);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			log.error("Error in ProjectController saveProject Error Massage::{}", e.getMessage());
			response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}

	}

	@RequestMapping(value = "/deleteProject/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProject(@PathVariable("id") String id) {

		log.trace("Start ProjectController.. deleteProject Id={}", id);
		CommonResponse response = new CommonResponse();
		try {

			Mono<String> msg = projectService.deleteProject(id);
			response.addSuccessMsg(CommonMessageUtil.getSuccessMessage(msg.block()));
			log.trace("End ProjectController.. deleteProject Response :: {}", msg.block());
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			log.error("Error in ProjectController deleteProject Error Massage :: {}", e.getMessage());
			response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}

	}
	
	
	@RequestMapping(value = "/projectId/{projectId}", method = RequestMethod.GET)
    public ResponseEntity<?> getByProjectId(@PathVariable("projectId") String projectId) {

        log.trace("Start ProjectController.. getByProjectId  projectId :: {}", projectId);
        CommonResponse response = new CommonResponse();
        try {

            ProjectDto project = projectService.findByProjectId(projectId);
            if(null!=project) {
            	response.addData("Project", project);
				response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Get Project Successfully"));
            }
            else {
            	response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Project Not Found"));
            }
            log.trace("End ProjectController.. getByProjectId  Response :: {}", response);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error in ProjectController getByProjectId :: {}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
