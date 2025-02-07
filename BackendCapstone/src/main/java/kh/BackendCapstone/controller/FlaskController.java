package kh.BackendCapstone.controller;

import kh.BackendCapstone.dto.request.AiReqDto;
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
@RequestMapping("/flask")
@Slf4j
public class FlaskController {
	private final FlaskService flaskService;
	
	@PostMapping("/ai/ps")
	public ResponseEntity<Boolean> postAi(@RequestBody AiReqDto aiReqDto, @RequestHeader("Authorization") String token) {
		boolean rsp = flaskService.postAi(aiReqDto, token);
		return ResponseEntity.ok(rsp);
	}
	
	
	
	
	
}
