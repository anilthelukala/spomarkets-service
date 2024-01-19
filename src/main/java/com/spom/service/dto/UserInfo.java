package com.spom.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class UserInfo extends User {

    private static final long serialVersionUID = 1L;

    public UserInfo(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
			String firstName, String lastName, String email,String userId) {
		super(username, password, enabled, accountNonExpired,credentialsNonExpired,accountNonLocked, authorities);
		this.userId=userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullname = firstName  + " " + lastName;
		this.email = username;
	}

    private String userId;
    private String lastName;
    private String firstName;
    private String fullname;
    private String email;
    private Long mobileNo;


    @Override
    public String toString() {
        return "MyUser firstName=" + firstName + ", lastName=" + lastName + ", name=" + fullname + ", emailaddress=" + email + ", mobileNo=" + mobileNo
                + "] " + super.toString();
    }


}
