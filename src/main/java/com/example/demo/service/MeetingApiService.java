package com.example.demo.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.demo.domain.Meetings;
import com.example.demo.domain.MeetingsRepository;
import com.example.demo.dto.MeetingDto;
import com.example.demo.dto.MeetingListDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MeetingApiService {

    @Value("${zoom.client-id}")
    private String clientId;

    @Value("${zoom.client-secret}")
    private String clientSecret;

    @Value("${zoom.token-url}")
    private String tokenUrl;
    
    @Value("${zoom.account-id}")
    private String accountId;
    
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final RestTemplate restTemplate = new RestTemplate();

	private final String ZOOM_API_URL = "https://api.zoom.us/v2";
	
	@Autowired
	private MeetingsRepository repository; 
    
	/**
	 * access token을 받아온다.
	 * @return
	 */
	public String getAccessToken() {
		//header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);
        
        //body 생성
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "account_credentials");
        map.add("account_id", accountId);

        // http body, header 담기
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        // 응답 받기
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
        	log.info("success getting token");
            return response.getBody().get("access_token").toString();
        } else {
        	log.warn("fail to get token");
            throw new RuntimeException("토큰 가져오기 실패 : " + response.getStatusCode());
        }
    }
    
	/**
	 * 회의를 생성한다.
	 * @return
	 * @throws JsonProcessingException 
	 * @throws ParseException 
	 */
    public Meetings createMeeting(MeetingDto dto) throws JsonProcessingException, ParseException {
    	ObjectMapper mapper = new ObjectMapper();
    	HttpHeaders headers = new HttpHeaders();
    	String path = "/users/me/meetings"; //For user-level apps, pass the me value.
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");         
         
		// 토큰 받아오기
		String token = getAccessToken();
		
		// 헤더 생성
		headers.set("Authorization", "Bearer " + token);
		headers.set("Content-Type", "application/json");

		// 미팅 변수 담기
		
		log.info(mapper.writeValueAsString(dto)); 
		
		// http body, header 담기
		HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(dto), headers);

		// 응답 받기
		ResponseEntity<MeetingDto> response = restTemplate.exchange(ZOOM_API_URL+path, HttpMethod.POST, entity, MeetingDto.class);

		//결과 리턴
		  if (response.getStatusCode() == HttpStatus.CREATED) { //STATUS : 201
			  log.info("success create meeting \n url : "+ZOOM_API_URL+ path+"\n meeting : "+response.getBody().toString()); 
			  
			  //db 저장
			  repository.save(response.getBody().toEntity());
			  return repository.findById(response.getBody().getId()).orElseThrow(()->new IllegalArgumentException("회의 저장 실패 id: "+response.getBody().getId())); 
		  } else {
			  log.warn("fail to create meeting \n url : "+ZOOM_API_URL+path); throw new
			  RuntimeException("회의 생성 실패 : " + response.getStatusCode()); 
		  }
		 
    }
    
    /**
     * 사용자 별 전체 미팅 리스트 조회
     * @param userId
     * @return
     * @throws JsonProcessingException 
     */
    public MeetingListDto getListMeetings(String userId, MeetingListDto dto) throws JsonProcessingException {
    	String path = "/users/"+userId+"/meetings";
    	HttpHeaders headers = new HttpHeaders();
    	ObjectMapper mapper = new ObjectMapper();
    	//토큰 받아오기
    	String token = getAccessToken();
    	//헤더 담기
    	headers.set("Authorization", "Bearer " + token);
		headers.set("Content-Type", "application/json");
		
		//body 담기
		log.info(mapper.writeValueAsString(dto)); 
		//요청 보내기
		HttpEntity<String> entity = new HttpEntity<String>(mapper.writeValueAsString(dto), headers);
		
		//응답 받기
		ResponseEntity<MeetingListDto> response = restTemplate.exchange(ZOOM_API_URL+path, HttpMethod.GET, entity, MeetingListDto.class);
		
		if(response.getStatusCode() == HttpStatus.OK) {
			log.info("리스트 조회 성공 : \n 총 회의 건 수 : "+response.getBody().getTotal_records());
			return response.getBody();
		} else {
			log.warn("fail to get meeting list \n url : "+ZOOM_API_URL+path); throw new
			RuntimeException("회의 리스트 조회 실패 : " + response.getStatusCode()); 
		}
    }
    
    /**
     * 회의 id로 생성된 회의의 상세를 조회한다.
     * @param meetingId 회의 id
     * @return
     */
    public MeetingDto getMeeting(long meetingId) {
    	String path = "/meetings/"+meetingId;
    	// 토큰 받아오기
		String token = getAccessToken();

		//헤더 생성
    	HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.set("Content-Type", "application/json");
		
    	
    	//헤더 담기
		HttpEntity<Long> entity = new HttpEntity<>(meetingId, headers);
    	
    	//응답 받기
		ResponseEntity<MeetingDto> response = restTemplate.exchange(ZOOM_API_URL+path, HttpMethod.GET, entity, MeetingDto.class);
    	
		//결과 리턴
		if(response.getStatusCode() == HttpStatus.OK) {
			log.info("success getting meeting \n url : "+ZOOM_API_URL+path+"\n meeting id : "+meetingId);
			return response.getBody();
		} else {
			log.warn("fail to get meeting \n url : "+ZOOM_API_URL+path+"\n meeting id : "+meetingId);
			throw new RuntimeException("회의 조회 실패 : " + response.getStatusCode());
		}
    }
    
    /**
     * 회의 id로 생성된 회의를 수정한다.
     * @param meetingId
     * @return
     */
    public String patchMeeting(Long meetingId) {
    	String path = "/meetings/"+meetingId;
    	// 토큰 받아오기
		String token = getAccessToken();

		//헤더 생성
    	HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.set("Content-Type", "application/json");
		
		// 미팅 변수 담기(바디 생성)
		String meetingDetails = "{ \"topic\": \"Test Meeting Patch\", \"type\": 2 }";

		//바디, 헤더 담기
		HttpEntity<String> entity = new HttpEntity<>(meetingDetails, headers);
    	
    	//응답 받기
		ResponseEntity<Map> response = restTemplate.exchange(ZOOM_API_URL+path, HttpMethod.GET, entity, Map.class);
    	
		//결과 리턴
		if(response.getStatusCode() == HttpStatus.OK) {
			log.info("success getting meeting \n url : "+ZOOM_API_URL+path+"\n meeting id : "+meetingId);
			return response.getBody().toString();
		} else {
			log.warn("fail to get meeting \n url : "+ZOOM_API_URL+path+"\n meeting id : "+meetingId);
			throw new RuntimeException("회의 조회 실패 : " + response.getStatusCode());
		}
    }
}
