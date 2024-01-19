package com.spom.service.service;

import com.spom.service.constant.SPOMarketsConstants;
import com.spom.service.dto.User;
import com.spom.service.dto.UserInfo;
import com.spom.service.dto.UserRoleDto;
import com.spom.service.model.RoleEntity;
import com.spom.service.model.UserEntity;
import com.spom.service.repository.RolePermissionRepository;
import com.spom.service.repository.RoleRepository;
import com.spom.service.repository.UserMstRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class UserServiceImpl implements UserService {

	private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserMstRepository userMstRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private RolePermissionRepository rolePermissionRepository;

	private Long failedAttempts = 0l;

	@Value("${MAX_FAILED_ATTEMPTS}")
	private Long maxfailedAttempts;

	@Value("${LOCKOUT_DURATION_HOURS}")
	private Long lockOutDurationHours;

    @Value("${MAX_KYC_FAILED_ATTEMPTS}")
    private Long maxkycFailedAttempts;

	@Override
	public Mono<UserEntity> findUserById(String id) throws DataAccessException {
		Mono<UserEntity> userMono = userMstRepository.findById(id);

		return userMono;
	}

	private void convertEntityToDto(UserEntity userEntity, User user) {

		BeanUtils.copyProperties(userEntity, user);

	}

	@Override
	public Collection<User> findAllUsers() throws DataAccessException {
		Flux<UserEntity> usersFlux = userMstRepository.findAll();
		List<UserEntity> userEntites = usersFlux.collectList().block();
		Collection<User> users = new ArrayList<>();
		User user = null;
		for (UserEntity userEntity : userEntites) {
			user = new User();
			this.convertEntityToDto(userEntity, user);
			users.add(user);
		}
		return users;
	}

	@Override
	public UserEntity saveUser(User user) throws DataAccessException {
		UserEntity userEntity = new UserEntity();
		this.convertDtoToEntity(user, userEntity);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(user.getPassword());
		userEntity.setPassword(hashedPassword);
		userEntity.setCreatedBy(user.getEmail());
		Flux<RoleEntity> flux = this.roleRepository.findByNameOrCode(SPOMarketsConstants.ROLE_USER,
				SPOMarketsConstants.ROLE_USER);
		if (null != flux) {
			RoleEntity roleEntity = flux.blockFirst();
			if (null != roleEntity) {
				userEntity.setRole(roleEntity.getId());
			}
		}
		userEntity.setCreatedDate(new Date());
		userEntity.setKycVerification(SPOMarketsConstants.KYC_PENDING);
		userEntity.setUserActiveFlag(true);
		userEntity = userMstRepository.save(userEntity).block();
		userEntity.setPassword(null);
		return userEntity;
	}

	private void convertDtoToEntity(User user, UserEntity userEntity) {
		BeanUtils.copyProperties(user, userEntity);

	}

	@Override
	public Flux<UserEntity> findUserByUsername(String username) throws DataAccessException {
		Flux<UserEntity> userByUsername = this.userMstRepository.findByEmail(username).flatMap(user -> {
			String roleId = user.getRole();
			user.setPassword(null);
			return this.roleRepository.findById(roleId).flatMap(role -> {
				user.setRoleEntity(role);
				return rolePermissionRepository.findByRole(role.getId()).collectList()
						.doOnNext(permissions -> role.setPermissions(permissions)).thenReturn(user);

			}).doOnError(error -> log.error("Error in getRole ", error))
					.onErrorResume(error -> Mono.error(new Exception(error)));
		}).doOnError(error -> log.error("Error in getUser ", error))
				.onErrorResume(error -> Flux.error(new Exception(error)));
		/*
		 * Flux<UserEntity> userByUsername =
		 * this.userMstRepository.findByEmail(username) .flatMap(user -> { Long userId =
		 * user.getId(); user.setPassword(null); List<UserRoleEntity> roles = new
		 * ArrayList<>(); user.setRoles(roles);
		 * 
		 * return this.userRoleRepository.findByUser(userId) .flatMap(userRole -> {
		 * roles.add(userRole);
		 * 
		 * return rolePermissionRepository.findByRole(userRole.getRole()) .collectList()
		 * .doOnNext(permissions -> userRole.setPermissions(permissions))
		 * .thenReturn(user); // Return the user after setting roles and permissions })
		 * .doOnError(error -> log.error("Error in getUser ", error))
		 * .onErrorResume(error -> Flux.error(new Exception(error))); })
		 * .doOnError(error -> log.error("Error in getUser ", error))
		 * .onErrorResume(error -> Flux.error(new Exception(error)));
		 */
		return userByUsername;
	}

	@Override
	public Boolean isDuplicateUser(User user) {
		Flux<UserEntity> userEntitis = userMstRepository.findByMobileNoOrEmail(user.getMobileNo(), user.getEmail());
		if (userEntitis != null) {
			if (userEntitis.collectList().block().size() > 0)
				return true;
			else
				return false;

		} else
			return false;
	}

	@Override
	public Boolean isDuplicateUserForUpdate(User user) {
		Flux<UserEntity> userEntitis = userMstRepository.findByIdAndMobileNo(user.getId(), user.getMobileNo());
		if (userEntitis != null) {
			if (userEntitis.collectList().block().size() > 0)
				return true;
			else
				return false;

		} else
			return false;
	}

	@Override
	public UserEntity updateUser(User user) throws Exception {
		Mono<UserEntity> monoEntity = userMstRepository.findById(user.getId());
		if (null != monoEntity) {

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserInfo userInfo = (UserInfo) authentication.getPrincipal();

			UserEntity userEntity = monoEntity.block();
			if (null != userEntity) {
				if (SPOMarketsConstants.KYC_PENDING.equalsIgnoreCase(userEntity.getKycVerification())) {
					userEntity.setFirstName(user.getFirstName());
					userEntity.setLastName(user.getLastName());
					userEntity.setModifiedDate(new Date());
					userEntity.setModifiedBy(userInfo.getEmail());
					userEntity.setCountryCode(user.getCountryCode());
					userEntity.setMobileNo(user.getMobileNo());
					userEntity.setStreetAddress(user.getStreetAddress());
					userEntity.setCity(user.getCity());
					userEntity.setState(user.getState());
					userEntity.setZipcode(user.getZipcode());
					userEntity.setCountry(user.getCountry());
					userEntity.setKycVerificationDate(user.getKycVerificationDate());
					userEntity = userMstRepository.save(userEntity).block();
					userEntity.setPassword(null);
					return userEntity;
				} else {
					throw new Exception("User Profile can't be updated as KYC is already completed.");
				}

			} else {
				throw new Exception("User Not Found");
			}

		} else {
			throw new Exception("User Not Found");
		}
		// this.convertDtoToEntity(user,userEntity);

	}

	@Override
	public Mono<UserEntity> assignUserRole(UserRoleDto userRole) throws Exception {

		Mono<RoleEntity> monoRole = roleRepository.findById(userRole.getRole().getId());
		if (null == monoRole || null == monoRole.block()) {
			throw new Exception("Role not found");
		}

		Mono<UserEntity> monoUser = userMstRepository.findById(userRole.getUser().getId());
		if (null != monoUser) {
			UserEntity userEntity = monoUser.block();
			if (null != userEntity) {
				userEntity.setRole(userRole.getRole().getId());
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				UserInfo userInfo = (UserInfo) authentication.getPrincipal();
				userEntity.setModifiedBy(userInfo.getEmail());
				userEntity.setModifiedDate(new Date());
				return userMstRepository.save(userEntity);

			} else {
				throw new Exception("User not found");
			}

		} else {
			throw new Exception("User not found");
		}

	}

	@Override
	public String resetPassword(User userDto) {
		log.trace("Start UserService.. resetPassword user={}", userDto);

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserEntity userEntity = this.userMstRepository.findUserByEmail(userDto.getEmail())
				.switchIfEmpty(Mono.error(new RuntimeException("User not found"))).block();
		if (null != userEntity.getFailedAttempts()) {
			this.failedAttempts = userEntity.getFailedAttempts() + 1;
		}

		Date currentDate = new Date();
		if (null != userEntity.getLockoutEndTime() && currentDate.compareTo(userEntity.getLockoutEndTime()) < 0) {
			throw new RuntimeException("Account is Temporarily locked. Please connect with SPO Markets Team!");

		}
//		if (!passwordEncoder.matches(userDto.getOldPassword(), userEntity.getPassword())||passwordEncoder.matches(userDto.getPassword(), userEntity.getPassword())) {
//		if (this.maxfailedAttempts <= (this.failedAttempts)) {
//
//			this.failedAttempts = 0l;
//			Mono<String> msg = this.userMstRepository.findUserByEmail(userDto.getEmail()).flatMap(user -> {
//				user.setFailedAttempts(0l);
//				user.setLockoutEndTime(
//						new Date(System.currentTimeMillis() + this.lockOutDurationHours * 60 * 60 * 1000L));
//				return this.userMstRepository.save(user)
//						.thenReturn("Account is Temporarily locked due to too many failed attempts!");
//
//			}).switchIfEmpty(Mono.error(new RuntimeException("User not found")));
//
//			throw new RuntimeException(msg.block());
//		}
//		}

		Mono<String> msg = this.userMstRepository.findUserByEmail(userDto.getEmail()).flatMap(user -> {
			// Verify that the old password matches the stored hashed password
			if (passwordEncoder.matches(userDto.getOldPassword(), user.getPassword())) {
				// Check if the new password is different from the old password
				if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
					// Hash the new password before updating
					String hashedPassword = passwordEncoder.encode(userDto.getPassword());
					user.setPassword(hashedPassword);
					user.setFailedAttempts(0l);
					// Save the updated user with the new password
					return this.userMstRepository.save(user).thenReturn("Password reset successfully");
				} else {
					log.trace(" UserService.. resetPassword New password must be different from the old password");
					if (this.maxfailedAttempts <= (this.failedAttempts)) {

						this.failedAttempts = 0l;
						return this.userMstRepository.findUserByEmail(userDto.getEmail()).flatMap(userToUpdate -> {
							userToUpdate.setFailedAttempts(0l);
							userToUpdate.setLockoutEndTime(
									new Date(System.currentTimeMillis() + this.lockOutDurationHours * 60 * 60 * 1000L));
							return this.userMstRepository.save(userToUpdate)
									.thenReturn("Account is Temporarily locked due to too many failed attempts!");

						}).switchIfEmpty(Mono.error(new RuntimeException("User not found")))
								.flatMap(ex -> Mono.error(new RuntimeException(
										"Account is Temporarily locked due to too many failed attempts!")));

					} else {
						return this.userMstRepository.findUserByEmail(userDto.getEmail()).flatMap(user1 -> {
							user1.setFailedAttempts(this.failedAttempts);
							return this.userMstRepository.save(user1).thenReturn("FailedAttempts updated successfully");
						}).switchIfEmpty(Mono.error(new RuntimeException("User not found"))).flatMap(ex -> Mono.error(
								new RuntimeException("New password must be different from the old password. You have "
										+ (this.maxfailedAttempts - this.failedAttempts) + " attempts remaining!")));
					}
				}
			} else {
				log.trace(" UserService.. resetPassword Invalid old password");
				if (this.maxfailedAttempts <= this.failedAttempts) {

					this.failedAttempts = 0l;
					return this.userMstRepository.findUserByEmail(userDto.getEmail()).flatMap(userToUpdate -> {
						userToUpdate.setFailedAttempts(0l);
						userToUpdate.setLockoutEndTime(
								new Date(System.currentTimeMillis() + this.lockOutDurationHours * 60 * 60 * 1000L));
						return this.userMstRepository.save(userToUpdate)
								.thenReturn("Account is Temporarily locked due to too many failed attempts!");

					}).switchIfEmpty(Mono.error(new RuntimeException("User not found"))).flatMap(ex -> Mono.error(
							new RuntimeException("Account is Temporarily locked due to too many failed attempts!")));

				} else {
					return this.userMstRepository.findUserByEmail(userDto.getEmail()).flatMap(userToUpdate -> {
						userToUpdate.setFailedAttempts(this.failedAttempts);
						return this.userMstRepository.save(userToUpdate)
								.thenReturn("FailedAttempts updated successfully");
					}).switchIfEmpty(Mono.error(new RuntimeException("User not found")))
							.flatMap(ex -> Mono.error(new RuntimeException("Invalid old password. You have "
									+ (this.maxfailedAttempts - this.failedAttempts) + " attempts remaining!")));
				}
			}
		}).switchIfEmpty(Mono.error(new RuntimeException("User not found")));
		log.trace("End UserService.. resetPassword Invalid old password");
		return msg.block();
	}

	@Override
	public User findByEmail(String username) {
		log.trace("Start UserServiceImpl.. username :: {}", username);

		Mono<UserEntity> getUser = this.userMstRepository.findUserByEmail(username)
				.doOnError(error -> log.error("Error in getProject ", error)).onErrorResume(error -> {
					return Mono.error(new Exception(error));
				});

		User userDto = new User();

		return getUser.flatMap(user -> {
			BeanUtils.copyProperties(user, userDto);
			return Mono.just(userDto);
		}).block();
	}

    @Override
    public Mono<UserEntity> kycUpdate(User user) throws Exception {
        Flux<UserEntity> fluxUser = userMstRepository.findByEmail(user.getEmail());
        if (null != fluxUser) {
            UserEntity userEntity = fluxUser.blockFirst();
            if (null != userEntity) {
                
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UserInfo userInfo = (UserInfo) authentication.getPrincipal();
                userEntity.setModifiedBy(userInfo.getEmail());
                userEntity.setModifiedDate(new Date());
                userEntity.setKycVerification(user.getKycVerification());
                userEntity.setKycVerificationDate(new Date());
                if(null != user.getKycVerification() && user.getKycVerification().equalsIgnoreCase("success")){
                    userEntity.setKycAttempts((long) 0);
                    userEntity.setKycVerificationBlocked(false);
                }else{
                    if(null == userEntity.getKycAttempts()){
                        userEntity.setKycAttempts((long)1);
                    }else{
                        userEntity.setKycAttempts(userEntity.getKycAttempts() + 1);
                    }
                }
                if(userEntity.getKycAttempts() >= maxkycFailedAttempts){
                    userEntity.setKycVerificationBlocked(true);
                }
                return userMstRepository.save(userEntity);

            } else {
                throw new Exception("User not found");
            }

        } else {
            throw new Exception("User not found");
        }
    }

}
