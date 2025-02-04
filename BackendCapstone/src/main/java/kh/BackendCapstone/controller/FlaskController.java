package kh.BackendCapstone.controller;

import kh.BackendCapstone.dto.request.TextBoardReqDto;
import kh.BackendCapstone.dto.request.UnivReqDto;
import kh.BackendCapstone.entity.TextBoard;
import kh.BackendCapstone.entity.Univ;
import kh.BackendCapstone.service.FlaskService;
import kh.BackendCapstone.service.UnivService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5000")
@RequestMapping("/flask")
@Slf4j
public class FlaskController {
	private final FlaskService flaskService;
	
	// 대학 정보 업로드 API
	@PostMapping("/univ")
	public ResponseEntity<List<Boolean>> univCsvUpload(@RequestBody List<UnivReqDto> univReqDtoList) {
		return flaskService.convertCsvToUniv(univReqDtoList);
	}
	
	@PostMapping("/text")
	public ResponseEntity<List<Boolean>> textCsvUpload(@RequestBody List<TextBoardReqDto> textBoardReqDtoList) {
		return flaskService.convertCsvToTextBoard(textBoardReqDtoList);
	}
	
	
	
	
	
}
