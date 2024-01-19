package com.spom.service.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.spom.service.common.CommonMessageUtil;
import com.spom.service.common.CommonResponse;
import com.spom.service.constant.SPOMarketsConstants;
import com.spom.service.dto.UserCartDto;
import com.spom.service.service.UserCartService;

import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("/userCart")
public class UserCartController {
	private final Logger log = LoggerFactory.getLogger(UserCartController.class);

	@Autowired
	private UserCartService userCartService;

	@PostMapping("/saveUserCart")
	public ResponseEntity<?> saveUserCart(@RequestBody UserCartDto userCartDto) {

		log.trace("Start UserCartController.. saveUserCart RequestBody:: {}", userCartDto);
		CommonResponse response = new CommonResponse();
		try {
			UserCartDto savedUserCart = userCartService.saveUserCart(userCartDto);
			response.addData("SavedUserCart", savedUserCart);
			response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Saved Successfully"));
			log.trace("End UserCartController.. saveUserCart ResponseBody:: {}", response);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			log.error("Error in UserCartController.. saveUserCart :: {}", e.getMessage());
			response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(response);
		}

	}

	@RequestMapping(value = "/summary/{userName}", method = RequestMethod.GET)
	public ResponseEntity<?> getByUserName(@PathVariable("userName") String userName) {

		log.trace("Start UserCartController.. getByUserName  UserName={}", userName);
		CommonResponse response = new CommonResponse();
		try {
			List<UserCartDto> userCart = userCartService.findByUserName(userName);
			if (userCart.size()>0) {
				response.addData("CartSummary", userCart);
				response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Get Cart Summary Successfully"));
			} else {
				response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Cart Summary Not Found"));
			}
			log.trace("End UserCartController.. getByUserName  ResponseBody :: {}", response);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			log.error("Error in UserCartController getByUserName:: {}", e.getMessage());
			response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	 @RequestMapping(value = "/removeCart/{id}", method = RequestMethod.DELETE)
	    public ResponseEntity<?> removeCart(@PathVariable("id") String id) {

	        log.trace("Start UserCartController.. removeCart Id :: {}",id);
	        CommonResponse response = new CommonResponse();
	        try {

	            Mono<String> msg = userCartService.removeCart(id);
	            response.addSuccessMsg(CommonMessageUtil.getSuccessMessage(msg.block()));
				log.trace("End UserCartController.. UserCartController Response :: {}", msg.block());
	            return ResponseEntity.status(HttpStatus.OK).body(response);
	        } catch (Exception e) {
	            log.error("Error in UserCartController-UserCartController :: {}", e.getMessage());
	            response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	        }

	    }
	 
	 
	 @RequestMapping(value = "/summary/cartId/{cartId}", method = RequestMethod.GET)
		public ResponseEntity<?> getById(@PathVariable("cartId") String cartId) {

			log.trace("Start UserCartController.. getById  cartId={}", cartId);
			CommonResponse response = new CommonResponse();
			try {
				UserCartDto userCart = userCartService.findById(cartId);
				if (null!=userCart) {
					response.addData("CartSummary", userCart);
					response.addSuccessMsg(CommonMessageUtil.getSuccessMessage("Get Cart Summary Successfully"));
				} else {
					response.addErrorMsg(CommonMessageUtil.getExceptionMessage("Cart Summary Not Found"));
				}
				log.trace("End UserCartController.. getById  ResponseBody :: {}", response);
				return ResponseEntity.status(HttpStatus.OK).body(response);
			} catch (Exception e) {
				log.error("Error in UserCartController getById:: {}", e.getMessage());
				response.addErrorMsg(CommonMessageUtil.getExceptionMessage(SPOMarketsConstants.INTERNAL_SERVER_ERROR));
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}
		}
}
