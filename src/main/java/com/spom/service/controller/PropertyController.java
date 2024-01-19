package com.spom.service.controller;

import com.spom.service.common.CommonMessageUtil;
import com.spom.service.common.CommonResponse;
import com.spom.service.constant.SPOMarketsConstants;
import com.spom.service.dto.PropertyDto;
import com.spom.service.dto.PropertyFilterDto;
import com.spom.service.service.PropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import reactor.core.publisher.Mono;

import java.io.File;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/property")
public class PropertyController {

    private final Logger log = LoggerFactory.getLogger(PropertyController.class);

    @Autowired
    private PropertyService propertyService;
    
    @Value("${property.image-path}")
    private String localDirectoryPath;

    @PostMapping("/saveProperty")
    public ResponseEntity<?> saveProperty(@ModelAttribute PropertyDto propertyDto) {

        log.trace("Start PropertyController.. saveProperty RequestBody :: {}", propertyDto);
        CommonResponse response = new CommonResponse();
        try {
        	
        	if(null!=propertyDto.getFile()) {
        	MultipartFile file = propertyDto.getFile();
        	
            // Save the file to a local directory
            String fileName = localDirectoryPath + file.getOriginalFilename();
            file.transferTo(new File(fileName));
            propertyDto.setImage(fileName);
        	}
        	
            
        	if(null!=propertyDto.getVideo()) {
            MultipartFile video = propertyDto.getVideo();
            String videofileName = localDirectoryPath + video.getOriginalFilename();
            video.transferTo(new File(videofileName));
            
            propertyDto.setVideoLink(videofileName);
            }
            
            PropertyDto savedProperty = propertyService.saveProperty(propertyDto);
            response.addData("SavedProperty", savedProperty);
			response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Saved Successfully"));
			log.trace("End PropertyController.. saveProperty ResponseBody :: {}", response);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error in PropertyController saveProperty Error Massage :: {}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @RequestMapping(value = "/propertyId/{propertyId}", method = RequestMethod.GET)
    public ResponseEntity<?> getByPropertyId(@PathVariable("propertyId") String propertyId) {

        log.trace("Start PropertyController.. getByPropertyId  propertyId :: {}", propertyId);
        CommonResponse response = new CommonResponse();
        try {

            PropertyDto property = propertyService.findByPropertyId(propertyId);
            if(null!=property) {
            	response.addData("Property", property);
				response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Get Property Successfully"));
            }
            else {
            	response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Property Not Found"));
            }
            log.trace("End PropertyController.. getByPropertyId  Response :: {}", response);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error in PropertyController getByPropertyId :: {}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @RequestMapping(value = "/getAllProperty", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPropertys() {

        log.trace("Start PropertyController.. getAllPropertys()");
        CommonResponse response = new CommonResponse();
        try {

            List<PropertyDto> propertys = propertyService.findProperty();
            if (propertys.size() > 0) {
				response.addData("PropertyList", propertys);
				response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Get Property List Successfully"));
			} else {
				response.addData("PropertyList", propertys);
				response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Property list is empty"));
			}
            log.trace("End PropertyController.. getAllPropertys  Response :: {}", response);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error in PropertyController-getAllPropertys :: {}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @RequestMapping(value = "/getFilter", method = RequestMethod.POST)
    public ResponseEntity<?> getPropertyByFilters(@RequestBody PropertyFilterDto propertyFilterDto ) {

        log.trace("Start PropertyController.. getPropertyByFilters  PropertyFilterBody :: {}", propertyFilterDto);
        CommonResponse response = new CommonResponse();
        try {

            List<PropertyDto> propertys = propertyService.findByFilters(propertyFilterDto);
            if (propertys.size() > 0) {
				response.addData("PropertyList", propertys);
				response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Get Property List Successfully"));
			} else {
				response.addData("PropertyList", propertys);
				response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Property list is empty"));
			}
            log.trace("End PropertyController.. getPropertyByFilters  Response :: {}", response);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error in PropertyController getPropertyByFilters :: {}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @RequestMapping(value = "/deleteProperty/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProperty(@PathVariable("id") String id) {

        log.trace("Start PropertyController.. deleteProperty Id :: {}",id);
        CommonResponse response = new CommonResponse();
        try {

            Mono<String> msg = propertyService.deleteProperty(id);
            response.addSuccessMsg(CommonMessageUtil.getSuccessMessage(msg.block()));
			log.trace("End PropertyController.. deleteProperty Response :: {}", msg.block());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error in PropertyController-deleteProperty :: {}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }
}
