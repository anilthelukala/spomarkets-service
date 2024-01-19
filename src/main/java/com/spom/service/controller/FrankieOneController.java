package com.spom.service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spom.service.common.CommonMessageUtil;
import com.spom.service.common.CommonResponse;
import com.spom.service.service.FrankieOneService;

import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/frankieOne")
public class FrankieOneController {
        private final Logger log = LoggerFactory.getLogger(FrankieOneController.class);

        @Autowired
        private FrankieOneService frankieOneService;

        /**
     * @param userDto
     * @param request
     * @return
     */
    @RequestMapping(value = "/createFrankieOneSession", method = RequestMethod.GET)
    public ResponseEntity<CommonResponse> createFrankieOneSession() {
        log.debug("Start createFrankieOneSession..  ");
        CommonResponse response = new CommonResponse();
        
		try {
		         response.addData("Token", this.frankieOneService.createFrankieOneSession().block());
			     response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("FrankieOneSession Created"));
			
		}
        catch(Exception e) {
            response.addData("Token", Mono.just("erwtwtwtwtwtwtw").block());
			response.addErrorMsg(CommonMessageUtil.getExceptionMessage(e.getMessage()));
		}
		log.debug("Return from createFrankieOneSession..  ");
		return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
