package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Meetings;
import com.example.demo.dto.MeetingDto;
import com.example.demo.dto.MeetingListDto;
import com.example.demo.service.MeetingApiService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class MeetingApiController {

	
	@Autowired
	private MeetingApiService apiService;

	private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@PostMapping("/meeting")
	public Meetings createMeeting(@RequestBody MeetingDto dto) throws Exception {
		log.info("request url :"+"/meeting \n method : POST");
		return apiService.createMeeting(dto);
	}
	
	@GetMapping("/meeting/{userId}/meetings")
	public MeetingListDto getMeetingList(@PathVariable String userId, MeetingListDto dto) throws JsonProcessingException{
		log.info("request url :"+"/meeting/"+userId +"/meetings \n method : GET");
		return apiService.getListMeetings(userId, dto);
	}
	
	@GetMapping("/meeting/{meetingId}")
	public MeetingDto getMeeting(@PathVariable long meetingId) throws Exception {
		log.info("request url :"+"/meeting \n method : GET");
		return apiService.getMeeting(meetingId);
	}

	@PatchMapping("/meeting/{meetingId}")
	public Meetings patchMeeting(@PathVariable long meetingId, @RequestBody MeetingDto dto) throws Exception {
		log.info("request url :"+"/meeting \n method : PATCH");
		return apiService.patchMeeting(meetingId, dto);
	}
	
}
