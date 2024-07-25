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
	 * access token�� �޾ƿ´�.
	 * @return
	 */
	public String getAccessToken() {
		//header ����
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);
        
        //body ����
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "account_credentials");
        map.add("account_id", accountId);

        // http body, header ���
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        // ���� �ޱ�
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
        	log.info("success getting token");
            return response.getBody().get("access_token").toString();
        } else {
        	log.warn("fail to get token");
            throw new RuntimeException("��ū �������� ���� : " + response.getStatusCode());
        }
    }
    
	/**
	 * ȸ�Ǹ� �����Ѵ�.
	 * @return
	 * @throws JsonProcessingException 
	 * @throws ParseException 
	 */
    public Meetings createMeeting(MeetingDto dto) throws JsonProcessingException, ParseException {
    	ObjectMapper mapper = new ObjectMapper();
    	HttpHeaders headers = new HttpHeaders();
    	String path = "/users/me/meetings"; //For user-level apps, pass the me value.
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");         
         
		// ��ū �޾ƿ���
		String token = getAccessToken();
		
		// ��� ����
		headers.set("Authorization", "Bearer " + token);
		headers.set("Content-Type", "application/json");

		// ���� ���� ���
		
		log.info(mapper.writeValueAsString(dto)); 
		
		// http body, header ���
		HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(dto), headers);

		// ���� �ޱ�
		ResponseEntity<MeetingDto> response = restTemplate.exchange(ZOOM_API_URL+path, HttpMethod.POST, entity, MeetingDto.class);

		//��� ����
		  if (response.getStatusCode() == HttpStatus.CREATED) { //STATUS : 201
			  log.info("success create meeting \n url : "+ZOOM_API_URL+ path+"\n meeting : "+response.getBody().toString()); 
			  
			  //db ����
			  repository.save(response.getBody().toEntity());
			  return repository.findById(response.getBody().getId()).orElseThrow(()->new IllegalArgumentException("ȸ�� ���� ���� id: "+response.getBody().getId())); 
		  } else {
			  log.warn("fail to create meeting \n url : "+ZOOM_API_URL+path); throw new
			  RuntimeException("ȸ�� ���� ���� : " + response.getStatusCode()); 
		  }
		 
    }
    
    /**
     * ����� �� ��ü ���� ����Ʈ ��ȸ
     * @param userId
     * @return
     * @throws JsonProcessingException 
     */
    public MeetingListDto getListMeetings(String userId, MeetingListDto dto) throws JsonProcessingException {
    	String path = "/users/"+userId+"/meetings";
    	HttpHeaders headers = new HttpHeaders();
    	ObjectMapper mapper = new ObjectMapper();
    	//��ū �޾ƿ���
    	String token = getAccessToken();
    	//��� ���
    	headers.set("Authorization", "Bearer " + token);
		headers.set("Content-Type", "application/json");
		
		//body ���
		log.info(mapper.writeValueAsString(dto)); 
		//��û ������
		HttpEntity<String> entity = new HttpEntity<String>(mapper.writeValueAsString(dto), headers);
		
		//���� �ޱ�
		ResponseEntity<MeetingListDto> response = restTemplate.exchange(ZOOM_API_URL+path, HttpMethod.GET, entity, MeetingListDto.class);
		
		if(response.getStatusCode() == HttpStatus.OK) {
			log.info("����Ʈ ��ȸ ���� : \n �� ȸ�� �� �� : "+response.getBody().getTotal_records());
			return response.getBody();
		} else {
			log.warn("fail to get meeting list \n url : "+ZOOM_API_URL+path); throw new
			RuntimeException("ȸ�� ����Ʈ ��ȸ ���� : " + response.getStatusCode()); 
		}
    }
    
    /**
     * ȸ�� id�� ������ ȸ���� �󼼸� ��ȸ�Ѵ�.
     * @param meetingId ȸ�� id
     * @return
     */
    public MeetingDto getMeeting(long meetingId) {
    	String path = "/meetings/"+meetingId;
    	// ��ū �޾ƿ���
		String token = getAccessToken();

		//��� ����
    	HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.set("Content-Type", "application/json");
		
    	
    	//��� ���
		HttpEntity<Long> entity = new HttpEntity<>(meetingId, headers);
    	
    	//���� �ޱ�
		ResponseEntity<MeetingDto> response = restTemplate.exchange(ZOOM_API_URL+path, HttpMethod.GET, entity, MeetingDto.class);
    	
		//��� ����
		if(response.getStatusCode() == HttpStatus.OK) {
			log.info("success getting meeting \n url : "+ZOOM_API_URL+path+"\n meeting id : "+meetingId);
			return response.getBody();
		} else {
			log.warn("fail to get meeting \n url : "+ZOOM_API_URL+path+"\n meeting id : "+meetingId);
			throw new RuntimeException("ȸ�� ��ȸ ���� : " + response.getStatusCode());
		}
    }
    
    /**
     * ȸ�� id�� ������ ȸ�Ǹ� �����Ѵ�.
     * @param meetingId
     * @return
     */
    public String patchMeeting(Long meetingId) {
    	String path = "/meetings/"+meetingId;
    	// ��ū �޾ƿ���
		String token = getAccessToken();

		//��� ����
    	HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.set("Content-Type", "application/json");
		
		// ���� ���� ���(�ٵ� ����)
		String meetingDetails = "{ \"topic\": \"Test Meeting Patch\", \"type\": 2 }";

		//�ٵ�, ��� ���
		HttpEntity<String> entity = new HttpEntity<>(meetingDetails, headers);
    	
    	//���� �ޱ�
		ResponseEntity<Map> response = restTemplate.exchange(ZOOM_API_URL+path, HttpMethod.GET, entity, Map.class);
    	
		//��� ����
		if(response.getStatusCode() == HttpStatus.OK) {
			log.info("success getting meeting \n url : "+ZOOM_API_URL+path+"\n meeting id : "+meetingId);
			return response.getBody().toString();
		} else {
			log.warn("fail to get meeting \n url : "+ZOOM_API_URL+path+"\n meeting id : "+meetingId);
			throw new RuntimeException("ȸ�� ��ȸ ���� : " + response.getStatusCode());
		}
    }
}
