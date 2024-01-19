package com.spom.service.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spom.service.captcha.CaptchaService;
import com.spom.service.captcha.ICaptchaService;
import com.spom.service.common.CommonMessageUtil;
import com.spom.service.common.CommonResponse;
import com.spom.service.dto.User;
import com.spom.service.dto.UserRoleDto;
import com.spom.service.error.ReCaptchaInvalidException;
import com.spom.service.model.UserEntity;
import com.spom.service.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ICaptchaService captchaService;

    /**
     * @param userDto
     * @param request
     * @return
     */
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ResponseEntity<CommonResponse> registerUserAccount(@RequestBody User userDto, HttpServletRequest request) {
        log.trace("Start UserController.. registerUserAccount RequestBody :: {}", userDto);
        CommonResponse response = new CommonResponse();

        try {
            final String res = userDto.getRecaptchaResponse();
            captchaService.processResponse(res, CaptchaService.REGISTER_ACTION);

            Boolean result = this.userService.isDuplicateUser(userDto);
            if (result)
                response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Duplicate User"));
            else {

                response.addData("user", this.userService.saveUser(userDto));
                response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("User Saved Successfully"));
            }
        } catch (ReCaptchaInvalidException e) {
            log.error("Error in UserController registerUserAccount Error Massage::{}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(e.getMessage()));
        } catch (Exception e) {
            log.error("Error in UserController registerUserAccount Error Massage::{}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Error User Save"));
        }
        log.trace("End UserController.. registerUserAccount Response :: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * @param userDto
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
    public ResponseEntity<CommonResponse> updateProfile(@RequestBody User userDto, HttpServletRequest request) {
        log.trace("Start UserController.. updateProfile RequestBody :: {}", userDto);
        CommonResponse response = new CommonResponse();

        try {

            if (null == userDto.getId()) {
                response.addErrorMsg(CommonMessageUtil.getExceptionMessage("User should be saved first"));
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            Boolean result = this.userService.isDuplicateUserForUpdate(userDto);
            if (result)
                response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Mobile No. is duplicate for user"));
            else {

                response.addData("user", this.userService.updateUser(userDto));
                response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("User Updated Successfully"));
            }
        } catch (Exception e) {
            log.error("Error in UserController updateProfile Error Massage::{}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(e.getMessage()));
        }
        log.trace("End UserController.. updateProfile Response :: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * @param userRoleDto
     * @return
     */
    @RequestMapping(value = "/assignRole", method = RequestMethod.POST)
    public ResponseEntity<CommonResponse> assignRole(@RequestBody UserRoleDto userRoleDto) {
        log.trace("Start UserController.. assignRole RequestBody :: {}", userRoleDto);
        CommonResponse response = new CommonResponse();

        try {
            response.addData("userRole", this.userService.assignUserRole(userRoleDto));
            response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Assign Role to user Saved"));
        } catch (Exception e) {
            log.error("Error in UserController assignRole Error Massage::{}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(e.getMessage()));
        }
        log.trace("End UserController.. assignRole Response :: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/userDetail/{userName}", method = RequestMethod.GET)
    public ResponseEntity<CommonResponse> getUserDetail(@PathVariable("userName") String userName) {
        log.trace("Start UserController.. getUserDetail RequestBody :: {}", userName);
        CommonResponse response = new CommonResponse();

        try {
            response.addData("user", this.userService.findUserByUsername(userName).blockFirst());
            response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("User details"));
        }catch (Exception e) {
            log.error("Error in UserController getUserDetail Error Massage::{}", e.getMessage());
            response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Error Getting User"));
        }
        log.trace("End UserController.. getUserDetail Response :: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @PostMapping("/resetPassword")
    public ResponseEntity<?> updatePassword(@RequestBody User user) {
    	log.trace("Start UserController.. resetPassword userEntity={}", user);
		CommonResponse response = new CommonResponse();
		try {

			String msg = userService.resetPassword(user);
			response.addSuccessMsg(CommonMessageUtil.getSuccessMessage(msg));
			log.trace("End UserController.. resetPassword Response :: {}", msg);
			
		} catch (Exception e) {
			log.error("Error in UserController resetPassword Error Massage :: {}", e.getMessage());
			response.addErrorMsg(CommonMessageUtil.getExceptionMessage(e.getMessage()));
		
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/kycStatus")
    public ResponseEntity<?> kycStatus(@RequestBody User user) {
    	log.trace("Start UserController.. kycStatus userEntity={}", user);
		CommonResponse response = new CommonResponse();
		try {

			UserEntity userEntity = userService.kycUpdate(user).block();
            response.addData("USER", userEntity);
			response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Kyc Updated Successfully."));
			log.trace("End UserController.. kycStatus Response :: {}", userEntity);
			
		} catch (Exception e) {
			log.error("Error in UserController resetPassword Error Massage :: {}", e.getMessage());
			response.addErrorMsg(CommonMessageUtil.getExceptionMessage(e.getMessage()));
		
		}
        log.trace("End UserController.. kycStatus Response :: {}", response);
		return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
