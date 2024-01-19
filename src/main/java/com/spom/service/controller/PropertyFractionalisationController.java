package com.spom.service.controller;

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
import com.spom.service.dto.PropertyFractionalisationDto;
import com.spom.service.service.PropertyFractionalisationService;

import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("/fractionalisation")
public class PropertyFractionalisationController {

    private final Logger log = LoggerFactory.getLogger(PropertyFractionalisationController.class);

    @Autowired
    private PropertyFractionalisationService propertyFractionalisationService;

    @PostMapping("/savePropertyFractionalisation")
    public ResponseEntity<?> savePropertyFractionalisation(
            @RequestBody PropertyFractionalisationDto propertyFractionalisationDto) {

        log.trace("Start PropertyFractionalisationController.. PropertyFractionalisation RequestBody :: {}",
                propertyFractionalisationDto);
        CommonResponse response = new CommonResponse();
        try {

            PropertyFractionalisationDto savedPropertyFractionalisationDto = propertyFractionalisationService
                    .savePropertyFractionalisation(propertyFractionalisationDto);
            response.addData("SavedPropertyFractionalisation", savedPropertyFractionalisationDto);
			response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Saved Successfully"));
			log.trace("End PropertyFractionalisationController.. PropertyFractionalisation ResponseBody :: {}",
					response);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error in PropertyFractionalisationController.. savePropertyFractionalisation :: {}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @RequestMapping(value = "/projectId/{projectId}", method = RequestMethod.GET)
    public ResponseEntity<?> getByProjecId(@PathVariable("projectId") String projectId) {

        log.trace("Start PropertyFractionalisationController.. getByProjectId :: {}", projectId);
        CommonResponse response = new CommonResponse();
        try {

            PropertyFractionalisationDto propertyFractionalisationDto = propertyFractionalisationService
                    .findByProjecId(projectId);
            if(null!=propertyFractionalisationDto) {
            	response.addData("PropertyFractionalisation", propertyFractionalisationDto);
				response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Get PropertyFractionalisation Successfully"));
            }
            else {
            	response.addErrorMsg(CommonMessageUtil.getExceptionMessage("PropertyFractionalisation Not Found"));
            }
            log.trace("End PropertyFractionalisationController.. getByProjectId Response :: {}", response);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error in PropertyFractionalisationController.. getByProjecId :: {}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @RequestMapping(value = "/deletePropertyFractionalisation/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePropertyFractionalisation(@PathVariable("id") String id) {

        log.trace("Start PropertyFractionalisationController.. deletePropertyFractionalisation Id :: {}", id);
        CommonResponse response = new CommonResponse();
        try {

            Mono<String> msg = propertyFractionalisationService.deletePropertyFractionalisation(id);
            response.addSuccessMsg(CommonMessageUtil.getSuccessMessage(msg.block()));
            log.trace("End PropertyFractionalisationController.. deletePropertyFractionalisation Response :: {}", msg.block());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error in PropertyFractionalisationController.. deleteProperty :: {}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }
    
    @RequestMapping(value = "/propertyId/{propertyId}", method = RequestMethod.GET)
    public ResponseEntity<?> getByPropertyId(@PathVariable("propertyId") String propertyId) {

        log.trace("Start PropertyFractionalisationController.. getByPropertyId  propertyId :: {}", propertyId);
        CommonResponse response = new CommonResponse();
        try {

            PropertyFractionalisationDto propertyFractionalisation = propertyFractionalisationService.findByPropertyId(propertyId);
            if(null!=propertyFractionalisation) {
            	response.addData("PropertyFractionalisation", propertyFractionalisation);
				response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Get PropertyFractionalisation Successfully"));
            }
            else {
            	response.addErrorMsg(CommonMessageUtil.getExceptionMessage("PropertyFractionalisation Not Found"));
            }
            log.trace("End PropertyFractionalisationController.. getByPropertyId  Response :: {}", response);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error in PropertyFractionalisationController getByPropertyId :: {}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
