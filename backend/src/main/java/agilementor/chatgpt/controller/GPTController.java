package agilementor.chatgpt.controller;

import agilementor.chatgpt.dto.GPTRequest;
import agilementor.chatgpt.dto.GPTResponse;
import agilementor.chatgpt.dto.ProjectResponseDTO;
import agilementor.chatgpt.service.GPTService;
import agilementor.common.exception.ProjectNotFoundException;
import agilementor.project.entity.Project;
import agilementor.project.repository.ProjectMemberRepository;
import agilementor.project.repository.ProjectRespository;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@Tag(name = "스프린트", description = "스프린트 관련 api입니다.")
@RestController
@RequestMapping("/api/projects/{projectId}/ai")
public class GPTController {

    private final GPTService gptService;
    private final ProjectRespository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public GPTController(GPTService gptService, ProjectRespository projectRepository,
        ProjectMemberRepository projectMemberRepository) {
        this.gptService = gptService;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @PostMapping("/generate-task")
    @Operation(summary = "AI 자동 생성", description = "프로젝트의 목적에 맞는 스프린트, 스토리, 백로그를 chat gpt를 이용하여 생성합니다.")
    @ApiResponse(responseCode = "200", description = "AI 자동 생성 성공")
    public ResponseEntity<ProjectResponseDTO> generateTasks(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId,
        @RequestBody GPTRequest request) throws JsonProcessingException {

        // 프로젝트 멤버인지 검증
        projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId)
            .orElseThrow(ProjectNotFoundException::new);

        // Project 조회
        Project project;
        project = projectRepository.findById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        // GPT API 호출
        GPTResponse gptResponse;
        gptResponse = gptService.fetchGPTResponse(request);

        // GPT 응답 저장
        ProjectResponseDTO responseDTO;
        responseDTO = gptService.saveGPTResponse(gptResponse, project);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}