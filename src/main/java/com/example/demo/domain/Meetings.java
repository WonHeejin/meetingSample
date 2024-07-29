package com.example.demo.domain;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.example.demo.dto.MeetingDto;
import com.example.demo.dto.MeetingSetting;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Meetings {
	
	@Id
	private Long id;
	
	@Column(length = 200, nullable = false)
	private String topic;
	
	private int type;
	private boolean default_password;
	private int duration;
	private String password;
	private Timestamp start_time;
	private Timestamp created_at;
	private String join_url;
	private String start_url;
	private String host_email;
	@LastModifiedDate
	private Date updated_date;
	
	@Builder
	public Meetings(Long id, String topic, int type, boolean default_password, int duration, String password, Timestamp start_time
			, Timestamp created_at, String join_url, String host_email) {
		this.id = id;
		this.topic = topic;
		this.type = type;
		this.default_password = default_password;
		this.duration = duration;
		this.password = password;
		this.start_time = start_time;		
		this.created_at = created_at;
		this.join_url = join_url;
		this.host_email = host_email;
	}
	
	public void update(MeetingDto dto) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
		this.topic = dto.getTopic();
		this.type = dto.getType();
		this.default_password = dto.isDefault_password();
		this.duration = dto.getDuration();
		this.password = dto.getPassword();
		this.start_time = new Timestamp(format.parse(dto.getStart_time()).getTime());		
	}
	
}
