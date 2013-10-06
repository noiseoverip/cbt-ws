package com.cbt.ws.security;

import java.security.Principal;

import com.cbt.core.entity.User;
import com.google.common.base.Objects;

/**
 * User object wrapper for security handling in Jersey
 * 
 * @author SauliusAlisauskas
 *
 */
public class CbtPrinciple extends User implements Principal {

	public static CbtPrinciple fromUser(User user) {
		CbtPrinciple principle = new CbtPrinciple();
		principle.setId(user.getId());
		principle.setName(user.getName());
		return principle;
	}
	
	public static CbtPrinciple build(Long id, String name) {
		CbtPrinciple principle = new CbtPrinciple();
		principle.setId(id);
		principle.setName(name);
		return principle;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("id", getId())
				.add("name", getName())
				.toString();
	}
}
