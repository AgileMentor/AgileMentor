package agilementor.sprint.controller;

import agilementor.sprint.dto.CompletedSprintData;
import agilementor.sprint.dto.SprintForm;
import agilementor.sprint.dto.SprintResponse;
import agilementor.sprint.service.SprintService;
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

@RestController
@RequestMapping("/api/projects/{projectId}/sprints")
public class SprintController {

    private final SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SprintResponse createSprint(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId) {
        return sprintService.createSprint(memberId, projectId);
    }

    @GetMapping
    public List<SprintResponse> getAllSprints(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId) {
        return sprintService.getAllSprints(memberId, projectId);
    }

    @GetMapping("/{sprintId}")
    public SprintResponse getSprintById(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId, @PathVariable Long sprintId) {
        return sprintService.getSprintById(memberId, projectId, sprintId);
    }

    @PutMapping("/{sprintId}")
    public SprintResponse updateSprint(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId, @PathVariable Long sprintId,
        @RequestBody SprintForm sprintForm) {
        return sprintService.updateSprint(memberId, projectId, sprintId, sprintForm);
    }

    @DeleteMapping("/{sprintId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSprint(@SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId, @PathVariable Long sprintId) {
        sprintService.deleteSprint(memberId, projectId, sprintId);
    }

    // startSprint 메서드 수정: 네 개의 필드 업데이트를 위한 SprintForm 추가
    @PutMapping("/{sprintId}/start")
    public SprintResponse startSprint(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId, @PathVariable Long sprintId,
        @RequestBody SprintForm sprintForm) {
        return sprintService.startSprint(memberId, projectId, sprintId, sprintForm);
    }

    @PutMapping("/{sprintId}/complete")
    public SprintResponse completeSprint(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId,
        @PathVariable Long sprintId) {
        return sprintService.completeSprint(memberId, projectId, sprintId);
    }

    @GetMapping("/burndown")
    public List<CompletedSprintData> getBurndownData(
        @SessionAttribute(name = "memberId", required = false) Long memberId,
        @PathVariable Long projectId) {
        return sprintService.getBurndownData(memberId, projectId);
    }
}
