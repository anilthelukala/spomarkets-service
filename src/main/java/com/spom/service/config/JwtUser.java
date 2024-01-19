package com.spom.service.config;

import java.util.Set;

public record JwtUser(String username,
                      String firstName,
                      String lastName,
                      String fullname,
                      String emailaddress,
                      Long mobileNo,
                      String password,
                      boolean enabled,
                      boolean accountNonExpired,
                      boolean credentialsNonExpired,
                      boolean accountNonLocked,
                      Set<String> authorities) {

}
