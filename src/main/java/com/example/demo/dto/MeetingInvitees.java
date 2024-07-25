package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetingInvitees {
	String email;
	
	@Builder
	public MeetingInvitees(String email) {
		this.email = email;
	}
}
