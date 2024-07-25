package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.controller.MeetingApiController;
import com.example.demo.domain.Meetings;
import com.example.demo.domain.MeetingsRepository;
import com.example.demo.dto.MeetingDto;
import com.example.demo.service.MeetingApiService;


@SpringBootTest
class MeetingSampleApplicationTests {

	@Autowired
	private MeetingApiController controller;
	
	@Autowired
	private MeetingApiService service;
	
	@Value("${zoom.client-id}")
    private String clientId;

    @Value("${zoom.client-secret}")
    private String clientSecret;

    @Value("${zoom.token-url}")
    private String tokenUrl;
    
    @Value("${zoom.account-id}")
    private String accountId;
    
    @Autowired
    private MeetingsRepository repository;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); 
    
	@Test
	void contextLoads() {
	}
	@After
	void cleanUp() {
		repository.deleteAll();
	}
	@Test
	void repositoryTest() throws ParseException {
		Long id = (long) 1234556788;
		String topic = "test1";
		String start_time = "2024-07-19T04:54:53Z";
		Timestamp created_at = new Timestamp(format.parse("2024-07-19T04:54:53Z").getTime());
		String join_url = "https://us05web.zoom.us/j/87282951295?pwd=j4HbQAwuRmT26btgzLIziRz8bkUUDg.1";
		String host_email = "testemail@email.com";
		
		//dto 생성
		MeetingDto dto = MeetingDto.builder()
				.id(id)
				.topic(topic)
				.start_time(start_time)
				.created_at(created_at)
				.join_url(join_url)
				.host_email(host_email)
				.build();
		//save 실행
		repository.save(dto.toEntity());
		//결과 확인
		//when
        Meetings meetings = repository.findById((long) 1234556788)
        		.orElseThrow(()->new IllegalArgumentException("존재하지 않는 화상회의 입니다. id=" + 1234556788)); 

        //then
        assertThat(meetings.getId()).isEqualTo(id);
        assertThat(meetings.getTopic()).isEqualTo(topic);
        assertThat(meetings.getStart_time()).isEqualTo(new Timestamp(format.parse(start_time).getTime()));
        assertThat(meetings.getCreated_at()).isEqualTo(created_at);
        assertThat(meetings.getJoin_url()).isEqualTo(join_url);
        assertThat(meetings.getHost_email()).isEqualTo(host_email);
	}

}
