package com.example.demo.dto;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetingListDto {

	private String next_page_token;
	private int page_count;
	private int page_number;
	private int page_size;
	private int total_records;
	private List<MeetingDto> meetings;
	private Date from;
	private Date to;
	private String timezone;
	
	@Builder
	public MeetingListDto(String next_page_token, int page_count, int page_number,
			int page_size, int total_records, List<MeetingDto> meetings, Date from,
			Date to, String timezone) {
		this.next_page_token = next_page_token;
		this.page_count = page_count;
		this.page_number = page_number;
		this.page_size = page_size;
		this.total_records = total_records;
		this.meetings = meetings;
		this.from = from;
		this.to = to;
		this.timezone = timezone;
	}
}
