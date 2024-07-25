package com.example.demo.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
public class MeetingSetting {

	String audio;
	String auto_recording;
	boolean host_video;	
	boolean mute_upon_entry;
	boolean meeting_authentication;
	List<MeetingInvitees> meeting_invitees;
	boolean participant_video;
	boolean waiting_room;
	
	@Builder
	public MeetingSetting(String audio, String auto_recording, boolean host_video, boolean mute_upon_entry
			, boolean meeting_authentication, List<MeetingInvitees> meeting_invitees, boolean participant_video, boolean waiting_room) {
		
		this.audio = audio;
		this.auto_recording = auto_recording;
		this.host_video = host_video;
		this.mute_upon_entry = mute_upon_entry;
		this.meeting_authentication = meeting_authentication;
		this.meeting_invitees = meeting_invitees;
		this.participant_video = participant_video;
		this.waiting_room = waiting_room;
	}

}
