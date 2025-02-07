package kh.BackendCapstone.service;

import kh.BackendCapstone.constant.Active;
import kh.BackendCapstone.constant.Authority;
import kh.BackendCapstone.constant.TextCategory;
import kh.BackendCapstone.dto.request.AiReqDto;
import kh.BackendCapstone.dto.request.TextBoardReqDto;
import kh.BackendCapstone.dto.request.UnivReqDto;
import kh.BackendCapstone.dto.response.AiResDto;
import kh.BackendCapstone.entity.*;
import kh.BackendCapstone.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor @Service @Slf4j
public class FlaskService {
	private final UnivRepository univRepository;
	private final UnivService univService;
	private final MemberService memberService;
	private final RestTemplate restTemplate;
	private final TextBoardRepository textBoardRepository;
	private final PsWriteRepository psWriteRepository;
	private final PayRepository payRepository;
	private final PsContentsRepository psContentsRepository;
	
	@Transactional
	public ResponseEntity<List<Boolean>> convertCsvToUniv(List<UnivReqDto> univReqDtoList) {
		List<Boolean> resultList = new ArrayList<>();
		try {
			if (univReqDtoList == null || univReqDtoList.isEmpty()) {
				log.error("입력된 대학 데이터가 비어있습니다.");
				return ResponseEntity.badRequest().body(null); // 400 Bad Request
			}
			
			// Univ 데이터 저장
			for (UnivReqDto univReqDto : univReqDtoList) {
				try {
					Univ univ = new Univ();
					univ.setUnivName(univReqDto.getUnivName());
					univ.setUnivDept(univReqDto.getUnivDept());
					univ.setUnivImg(univReqDto.getUnivImg());
					boolean isSaved = univService.saveUniv(univ);
					resultList.add(isSaved);
					if (!isSaved) {
						log.error("대학 정보 저장 실패: UnivReqDto={} -> Univ={} (이유: 저장 실패)", univReqDto, univ);
					}
				} catch (Exception e) {
					log.error("대학 정보 처리 중 오류 발생: UnivReqDto={} (오류 메시지: {})", univReqDto, e.getMessage(), e);
					resultList.add(false);
				}
			}
			
			// 일부라도 실패했다면 로그를 남기고, 성공/실패 결과를 반환
			long failedCount = resultList.stream().filter(success -> !success).count();
			if (failedCount > 0) {
				log.error("{}개의 대학 정보 저장 실패", failedCount);
			}
			
			return ResponseEntity.ok(resultList);
			
		} catch (Exception e) {
			// 모든 예외를 포괄적으로 처리하고, 상세한 오류 메시지 기록
			log.error("대학 정보 입력 중 전체 오류 발생: {}", e.getMessage(), e);
			return ResponseEntity.status(500).body(null); // 500 Internal Server Error
		}
	}
	
	@Transactional
	public ResponseEntity<List<Boolean>> convertCsvToTextBoard(List<TextBoardReqDto> textBoardReqDtoList) {
		List<Boolean> resultList = new ArrayList<>();
		try {
			if (textBoardReqDtoList == null || textBoardReqDtoList.isEmpty()) {
				log.error("입력된 대학 데이터가 비어있습니다.");
				return ResponseEntity.badRequest().body(null); // 400 Bad Request
			}
			
			// Univ 데이터 저장
			for (TextBoardReqDto textBoardReqDto : textBoardReqDtoList) {
				try {
					TextBoard textBoard = new TextBoard();
					textBoard.setActive(Active.ACTIVE);
					textBoard.setContent(textBoardReqDto.getContent());
					textBoard.setTitle(textBoardReqDto.getTitle());
					textBoard.setTextCategory(TextCategory.fromString(textBoardReqDto.getTextCategory()));
					textBoardRepository.save(textBoard);
					resultList.add(true);
					log.error("대학 정보 저장 실패: TextBoardReqDto={} -> TextBoard={} (이유: 저장 실패)", textBoardReqDto, textBoard);
				} catch (Exception e) {
					log.error("대학 정보 처리 중 오류 발생: TextBoardReqDto={} (오류 메시지: {})", textBoardReqDto, e.getMessage(), e);
					resultList.add(false);
				}
			}
			// 일부라도 실패했다면 로그를 남기고, 성공/실패 결과를 반환
			long failedCount = resultList.stream().filter(success -> !success).count();
			if (failedCount > 0) {
				log.error("{}개의 대학 정보 저장 실패", failedCount);
			}
			return ResponseEntity.ok(resultList);
		} catch (Exception e) {
			// 모든 예외를 포괄적으로 처리하고, 상세한 오류 메시지 기록
			log.error("대학 정보 입력 중 전체 오류 발생: {}", e.getMessage(), e);
			return ResponseEntity.status(500).body(null); // 500 Internal Server Error
		}
	}
	
	@Transactional
	public boolean postAi(AiReqDto aiReqDto, String token) {
		try {
			// 1. 회원 정보 가져오기
			Member member = memberService.convertTokenToEntity(token);
			
			// 2. 자소서 정보 가져오기
			PsContents psContents = psContentsRepository.findByPsContentsId(aiReqDto.getPsContentsId())
				.orElseThrow(() -> new RuntimeException("해당 항목이 없습니다."));
			
			// 3. 결제 정보 가져오기
			Pay pay = payRepository.findById(aiReqDto.getPayId())
				.orElseThrow(() -> new RuntimeException("해당 결제 내역이 존재하지 않습니다."));
			
			// 4. 결제자와 작성자가 동일한지 확인
			if (!(member.equals(psContents.getPsWrite().getMember()) && member.equals(pay.getMember()))) {
				return false;
			}
			
			// 5. Flask API URL 설정
			String flaskUrl = "http://localhost:5000/spring/ai/ps";
			
			// 6. HTTP 헤더 및 바디 설정
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);  // Content-Type 설정 (필요한 경우)
			
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file_url", pay.getFileBoard().getMainFile()); // 전달할 데이터 추가
			body.add("request", aiReqDto.getPrompt());
			HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
			
			// 7. Flask API로 POST 요청 보내기
			ResponseEntity<String> responseEntity = restTemplate.exchange(
				flaskUrl,
				HttpMethod.POST,
				entity,
				String.class
			);
			
			// 8. Flask에서 반환된 응답 받기
			String response = responseEntity.getBody();
			log.warn("플라스크 통신으로 인한 결과 : {}", response);
			
			psContents.setPsContent(response);
			psContentsRepository.save(psContents);
			
			return true;
			
		} catch (Exception e) {
			log.error("ai 사용중 오류 발생: {}", e.getMessage());
			return false;
		}
	}
		
		
		
		
		
	
}






//@Async
//	@Transactional
//	public CompletableFuture<String> csvUploader(String token) {
//		// Member 객체를 가져오고, 권한을 체크
//		Member member = memberService.convertTokenToEntity(token);
//		if (member.getAuthority().equals(Authority.ROLE_ADMIN)) {
//			String flaskUrl = "http://localhost:5000/spring/upload/csv";
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.APPLICATION_JSON); // 적절한 Content-Type 설정 (필요시)
//
//			// 본문 없이 POST 요청 보내기
//			HttpEntity<String> entity = new HttpEntity<>(headers);
//
//			// 비동기 방식으로 Flask 서버와의 통신 처리
//			return CompletableFuture.supplyAsync(() -> {
//				try {
//					// Flask API로 POST 요청 보내기 (본문 없이)
//					ResponseEntity<String> response = restTemplate.exchange(flaskUrl, HttpMethod.POST, entity, String.class);
//					log.warn("플라스크 통신으로 인한 결과 : {}", response);
//					// Flask 서버의 응답 처리
//					return response.getBody();
//				} catch (Exception e) {
//					log.error("Flask 서버와의 통신 중 오류 발생: ", e);
//					return "파일 업로드 중 오류가 발생했습니다.";
//				}
//			});
//		} else {
//			// 권한이 없는 경우 적절한 메시지 반환
//			return CompletableFuture.completedFuture("권한이 없습니다.");
//		}
//	}
