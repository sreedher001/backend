package com.mindoot.onlinestore.utility;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
	private Long id;
	private String sub;
    private List<String> roles;
    private String name;
    private String email;
    private String phone;
    private long iat;
    private long exp;
}
