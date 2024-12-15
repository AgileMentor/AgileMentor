package agilementor.sprint.controller;

import agilementor.sprint.dto.CompletedSprintData;
import agilementor.sprint.dto.SprintForm;
import agilementor.sprint.dto.SprintResponse;
import agilementor.sprint.service.SprintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@Tag(name = "스프린트", description = "스프린트 관련 api입니다.")
@RestController
@RequestMapping("/api/projects/{projectId}/sprints")
public class SprintController {

    private final SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "스프린트 생성", description = "프로젝트에 새로운 스프린트를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "스프린트 생성 성공")
    public SprintResponse createSprint(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId) {
        return sprintService.createSprint(memberId, projectId);
    }

    @GetMapping
    @Operation(summary = "스프린트 목록 조회", description = "프로젝트의 스프린트 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "스프린트 목록 조회 성공")
    public List<SprintResponse> getAllSprints(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId) {
        return sprintService.getAllSprints(memberId, projectId);
    }

    @GetMapping("/{sprintId}")
    @Operation(summary = "스프린트 조회", description = "프로젝트의 스프린트 하나를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "스프린트 조회 성공")
    public SprintResponse getSprintById(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId, @PathVariable Long sprintId) {
        return sprintService.getSprintById(memberId, projectId, sprintId);
    }

    @PutMapping("/{sprintId}")
    @Operation(summary = "스프린트 수정", description = "프로젝트의 스프린트 하나를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "스프린트 수정 성공")
    public SprintResponse updateSprint(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId, @PathVariable Long sprintId,
        @RequestBody SprintForm sprintForm) {
        return sprintService.updateSprint(memberId, projectId, sprintId, sprintForm);
    }

    @DeleteMapping("/{sprintId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "스프린트 삭제", description = "프로젝트의 스프린트 하나를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "스프린트 삭제 성공")
    public void deleteSprint(@SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId, @PathVariable Long sprintId) {
        sprintService.deleteSprint(memberId, projectId, sprintId);
    }

    // startSprint 메서드 수정: 네 개의 필드 업데이트를 위한 SprintForm 추가
    @PutMapping("/{sprintId}/start")
    @Operation(summary = "스프린트 시작", description = "프로젝트의 스프린트 하나를 시작합니다.")
    @ApiResponse(responseCode = "200", description = "스프린트 시작 성공")
    public SprintResponse startSprint(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId, @PathVariable Long sprintId,
        @RequestBody SprintForm sprintForm) {
        return sprintService.startSprint(memberId, projectId, sprintId, sprintForm);
    }

    @PutMapping("/{sprintId}/complete")
    @Operation(summary = "스프린트 완료", description = "프로젝트의 진행중인 스프린트를 완료합니다.")
    @ApiResponse(responseCode = "200", description = "스프린트 완료 성공")
    public SprintResponse completeSprint(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId,
        @PathVariable Long sprintId) {
        return sprintService.completeSprint(memberId, projectId, sprintId);
    }

    @GetMapping("/burndown")
    @Operation(summary = "번다운 차트 정보 조회", description = "프로젝트의 번다운 차트 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "번다운 차트 정보 조회 성공")
    public List<CompletedSprintData> getBurndownData(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId) {
        return sprintService.getBurndownData(memberId, projectId);
    }
}
