package com.example.demo.dto;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.example.demo.domain.Meetings;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class MeetingDto {

	private Long id;
	private String topic;
	private int type;
	private boolean default_password;
	private int duration;
	private String password;
	private MeetingSetting settings;
	private String start_time;
	private Timestamp created_at;
	private String join_url;
	private String start_url;
	private String host_email;

	@Builder
	public MeetingDto(Long id, String topic, int type, boolean default_password, int duration, 
			String password, MeetingSetting settings, String start_time, Timestamp created_at, String join_url,
			String start_url, String host_email) {
		this.id = id;
		this.topic = topic;
		this.type = type;
		this.default_password = default_password;
		this.duration = duration;
		this.password = password;
		this.settings = settings;
		this.start_time = start_time;
		this.created_at = created_at;
		this.join_url = join_url;
		this.start_url = start_url;
		this.host_email = host_email;
	}
	
	public Meetings toEntity() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
		return Meetings.builder()
				.id(id)
				.topic(topic)
				.start_time( new Timestamp(format.parse(start_time).getTime()))
				.created_at(created_at)
				.join_url(join_url)
				.host_email(host_email)
				.build();
	}

}

