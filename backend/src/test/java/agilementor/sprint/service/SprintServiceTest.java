package agilementor.sprint.service;

import agilementor.backlog.entity.Backlog;
import agilementor.backlog.entity.Priority;
import agilementor.backlog.entity.Status;
import agilementor.common.exception.EndDateNullException;
import agilementor.common.exception.ProjectNotFoundException;
import agilementor.common.exception.SprintNotFoundException;
import agilementor.common.exception.TitleNullException;
import agilementor.member.entity.Member;
import agilementor.project.entity.Project;
import agilementor.project.entity.ProjectMember;
import agilementor.project.repository.ProjectMemberRepository;
import agilementor.project.repository.ProjectRespository;
import agilementor.sprint.dto.CompletedSprintData;
import agilementor.sprint.dto.SprintForm;
import agilementor.sprint.dto.SprintResponse;
import agilementor.sprint.entity.Sprint;
import agilementor.sprint.repository.SprintRepository;
import agilementor.backlog.repository.BacklogRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

class SprintServiceTest {

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private BacklogRepository backlogRepository;

    @Mock
    private ProjectRespository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @InjectMocks
    private SprintService sprintService;

    private final Long memberId = 1L;
    private final Long projectId = 1L;
    private final Long sprintId = 1L;

    private Project project;
    private Sprint sprint;
    private ProjectMember projectMember;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock Data Setup
        project = new Project("Test Project");
        ReflectionTestUtils.setField(project, "projectId", projectId);

        sprint = new Sprint(project, "Sprint 1");
        ReflectionTestUtils.setField(sprint, "id", sprintId);
        ReflectionTestUtils.setField(sprint, "endDate", LocalDate.now()); // 활성 스프린트 종료일

        Member mockedMember = mock(Member.class);
        ReflectionTestUtils.setField(mockedMember, "memberId", memberId);
        given(mockedMember.getEmail()).willReturn("test@example.com");
        given(mockedMember.getName()).willReturn("Test User");

        projectMember = new ProjectMember(project, mockedMember, true);

        // Mock 리포지토리 동작
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.of(projectMember));
        given(projectRepository.findById(any()))
            .willReturn(Optional.of(project));

        // 완료된 스프린트 설정
        Sprint completedSprint = new Sprint(project, "Completed Sprint");
        ReflectionTestUtils.setField(completedSprint, "id", 1L);
        ReflectionTestUtils.setField(completedSprint, "startDate", LocalDate.of(2024, 1, 1));
        ReflectionTestUtils.setField(completedSprint, "endDate", LocalDate.of(2024, 2, 1));

        // 활성 스프린트 설정
        Sprint activeSprint = new Sprint(project, "Active Sprint");
        ReflectionTestUtils.setField(activeSprint, "id", 2L);
        ReflectionTestUtils.setField(activeSprint, "startDate", LocalDate.of(2024, 2, 2));
        ReflectionTestUtils.setField(activeSprint, "endDate", LocalDate.now()); // 오늘 날짜로 설정

        // Mock 데이터 저장
        given(projectRepository.findById(any())).willReturn(Optional.of(project));
        given(sprintRepository.findByProject_ProjectIdAndIsDoneTrueOrderByEndDateAsc(any()))
            .willReturn(List.of(completedSprint));
        given(sprintRepository.findByProjectAndIsActivateTrue(any()))
            .willReturn(Optional.of(activeSprint));

        // 백로그 설정
        Backlog completedBacklog = new Backlog("Task 1", "Description", Priority.HIGH, project, sprint, null, null);
        ReflectionTestUtils.setField(completedBacklog, "status", Status.DONE);

        Backlog incompleteBacklog = new Backlog("Task 1", "Description", Priority.HIGH, project, sprint, null, null);
        ReflectionTestUtils.setField(incompleteBacklog, "status", Status.IN_PROGRESS);

        given(backlogRepository.findBySprint(completedSprint))
            .willReturn(List.of(completedBacklog));
        given(backlogRepository.findBySprint(activeSprint))
            .willReturn(List.of(completedBacklog, incompleteBacklog));

        List<Backlog> projectBacklogs = List.of(
            new Backlog("Task 1", "Description", Priority.HIGH, project, sprint, null, null),
            new Backlog("Task 2", "Description", Priority.MEDIUM, project, sprint, null, null)
        );

        given(backlogRepository.findByProject(any()))
            .willReturn(projectBacklogs);
    }

    @Test
    @DisplayName("스프린트를 생성한다.")
    void createSprint() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.of(projectMember));
        given(projectRepository.findById(any())).willReturn(Optional.of(project));
        given(sprintRepository.countByProject_ProjectId(any())).willReturn(0L);
        given(sprintRepository.save(any(Sprint.class))).willReturn(sprint);

        // when
        SprintResponse response = sprintService.createSprint(memberId, projectId);

        // then
        assertThat(response.title()).isEqualTo("Sprint 1");
        then(sprintRepository).should().save(any(Sprint.class));
    }

    @Test
    @DisplayName("프로젝트 ID로 isDone이 false인 모든 스프린트를 조회한다.")
    void getAllSprints() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndIsDoneFalse(any()))
            .willReturn(List.of(sprint));

        // when
        List<SprintResponse> responseList = sprintService.getAllSprints(memberId, projectId);

        // then
        assertThat(responseList).hasSize(1);
        assertThat(responseList.get(0).title()).isEqualTo(sprint.getTitle());
        assertThat(responseList.get(0).isDone()).isFalse();
        then(sprintRepository).should().findByProject_ProjectIdAndIsDoneFalse(projectId);
        then(projectMemberRepository).should().findByMemberIdAndProjectId(memberId, projectId);
    }


    @Test
    @DisplayName("스프린트 ID로 스프린트를 조회한다.")
    void getSprintById() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndId(any(), any())).willReturn(Optional.of(sprint));

        // when
        SprintResponse response = sprintService.getSprintById(memberId, projectId, sprintId);

        // then
        assertThat(response.title()).isEqualTo("Sprint 1");
    }

    @Test
    @DisplayName("스프린트 정보를 업데이트한다.")
    void updateSprint() {
        // given
        SprintForm form = new SprintForm("Updated Title", "Updated Goal", LocalDate.now(), LocalDate.now().plusDays(7), false, false);
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndId(any(), any())).willReturn(Optional.of(sprint));
        given(sprintRepository.save(any(Sprint.class))).willReturn(sprint);

        // when
        SprintResponse response = sprintService.updateSprint(memberId, projectId, sprintId, form);

        // then
        assertThat(response.title()).isEqualTo("Updated Title");
        assertThat(response.goal()).isEqualTo("Updated Goal");
    }

    @Test
    @DisplayName("스프린트를 삭제한다.")
    void deleteSprint() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndId(any(), any())).willReturn(Optional.of(sprint));

        // when
        sprintService.deleteSprint(memberId, projectId, sprintId);

        // then
        then(sprintRepository).should().delete(any(Sprint.class));
    }

    @Test
    @DisplayName("스프린트를 삭제하기 전에 연관된 백로그의 sprintId를 null로 설정한다.")
    void deleteSprint_shouldSetSprintIdToNullInBacklogs() {
        // given
        List<Backlog> backlogs = List.of(
            new Backlog("Task 1", "Description", Priority.HIGH, project, sprint, null, null),
            new Backlog("Task 2", "Description", Priority.HIGH, project, sprint, null, null)
        );
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndId(any(), any())).willReturn(Optional.of(sprint));
        given(backlogRepository.findBySprint(sprint)).willReturn(backlogs);

        // when
        sprintService.deleteSprint(memberId, projectId, sprintId);

        // then
        for (Backlog backlog : backlogs) {
            assertNull(backlog.getSprint(), "Backlog's sprint should be null after deletion.");
        }
        then(backlogRepository).should(times(backlogs.size())).save(any(Backlog.class));
    }

    @Test
    @DisplayName("백로그 저장을 호출한다.")
    void deleteSprint_shouldCallBacklogSave() {
        // given
        List<Backlog> backlogs = List.of(
            new Backlog("Task 1", "Description", Priority.HIGH, project, sprint, null, null),
            new Backlog("Task 2", "Description", Priority.HIGH, project, sprint, null, null)
        );
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndId(any(), any())).willReturn(Optional.of(sprint));
        given(backlogRepository.findBySprint(sprint)).willReturn(backlogs);

        // when
        sprintService.deleteSprint(memberId, projectId, sprintId);

        // then
        then(backlogRepository).should(times(backlogs.size())).save(any(Backlog.class));
    }

    @Test
    @DisplayName("스프린트를 삭제한다.")
    void deleteSprint_shouldDeleteSprint() {
        // given
        List<Backlog> backlogs = List.of(
            new Backlog("Task 1", "Description", Priority.HIGH, project, sprint, null, null),
            new Backlog("Task 2", "Description", Priority.HIGH, project, sprint, null, null)
        );
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndId(any(), any())).willReturn(Optional.of(sprint));
        given(backlogRepository.findBySprint(sprint)).willReturn(backlogs);

        // when
        sprintService.deleteSprint(memberId, projectId, sprintId);

        // then
        then(sprintRepository).should().delete(sprint);
    }

    @Test
    @DisplayName("연결된 백로그가 없는 경우에도 스프린트를 정상적으로 삭제한다.")
    void deleteSprint_shouldHandleNoBacklogs() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndId(any(), any())).willReturn(Optional.of(sprint));
        given(backlogRepository.findBySprint(sprint)).willReturn(Collections.emptyList());

        // when
        sprintService.deleteSprint(memberId, projectId, sprintId);

        // then
        then(backlogRepository).should(never()).save(any(Backlog.class));
        then(sprintRepository).should().delete(sprint);
    }

    @Test
    @DisplayName("스프린트를 찾을 수 없을 때 예외를 발생시킨다.")
    void deleteSprint_shouldThrowExceptionIfSprintNotFound() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndId(any(), any())).willReturn(Optional.empty());

        // when & then
        assertThrows(SprintNotFoundException.class, () ->
            sprintService.deleteSprint(memberId, projectId, sprintId)
        );
    }

    @Test
    @DisplayName("스프린트를 시작한다.")
    void startSprint() {
        // given
        SprintForm form = new SprintForm("Sprint 1", "Goal", LocalDate.now(), LocalDate.now().plusDays(7), false, true);
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndId(any(), any())).willReturn(Optional.of(sprint));
        given(sprintRepository.save(any(Sprint.class))).willReturn(sprint);

        // when
        SprintResponse response = sprintService.startSprint(memberId, projectId, sprintId, form);

        // then
        assertThat(response.isActivate()).isTrue();
        assertThat(response.endDate()).isEqualTo(form.getEndDate());
    }

    @Test
    @DisplayName("종료 날짜가 없을 경우 예외를 발생시킨다.")
    void startSprintThrowsExceptionWhenEndDateIsNull() {
        // given
        SprintForm form = new SprintForm("Sprint 1", "Goal", LocalDate.now(), null, false, true);
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndId(any(), any())).willReturn(Optional.of(sprint));

        // when & then
        assertThatThrownBy(() -> sprintService.startSprint(memberId, projectId, sprintId, form))
            .isInstanceOf(EndDateNullException.class);
    }

    @Test
    @DisplayName("제목이 없을 경우 업데이트에서 예외를 발생시킨다.")
    void updateSprintThrowsExceptionWhenTitleIsNull() {
        // given
        SprintForm form = new SprintForm(null, "Goal", LocalDate.now(), LocalDate.now().plusDays(7), false, false);
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndId(any(), any())).willReturn(Optional.of(sprint));

        // when & then
        assertThatThrownBy(() -> sprintService.updateSprint(memberId, projectId, sprintId, form))
            .isInstanceOf(TitleNullException.class);
    }

    @Test
    @DisplayName("제목이 없을 경우 스프린트 시작에서 예외를 발생시킨다.")
    void startSprintThrowsExceptionWhenTitleIsNull() {
        // given
        SprintForm form = new SprintForm(null, "Goal", LocalDate.now(), LocalDate.now().plusDays(7), false, true);
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndId(any(), any())).willReturn(Optional.of(sprint));

        // when & then
        assertThatThrownBy(() -> sprintService.startSprint(memberId, projectId, sprintId, form))
            .isInstanceOf(TitleNullException.class);
    }

    @Test
    @DisplayName("스프린트를 완료한다.")
    void completeSprint() {
        // given
        // Mock 데이터 생성
        Backlog backlog = new Backlog("Task 1", "Description", Priority.HIGH, project, sprint, null, null);

        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndId(any(), any()))
            .willReturn(Optional.of(sprint));
        given(backlogRepository.findBySprint_IdAndStatusNot(any(), eq(Status.DONE)))
            .willReturn(List.of(backlog)); // isDone이 false인 백로그를 반환
        given(sprintRepository.save(any(Sprint.class))).willReturn(sprint);

        // when
        SprintResponse response = sprintService.completeSprint(memberId, projectId, sprintId);

        // then
        assertThat(response.isDone()).isTrue();
        then(projectMemberRepository).should().findByMemberIdAndProjectId(memberId, projectId);
        then(sprintRepository).should().findByProject_ProjectIdAndId(projectId, sprintId);
        then(backlogRepository).should().findBySprint_IdAndStatusNot(sprintId, Status.DONE);
        then(backlogRepository).should().save(any(Backlog.class)); // 변경된 백로그 저장 검증
        then(sprintRepository).should().save(any(Sprint.class)); // 스프린트 완료 상태 저장 검증
    }


    @Test
    @DisplayName("프로젝트가 없으면 예외를 발생시킨다.")
    void validateProjectThrowsExceptionWhenNotFound() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sprintService.getAllSprints(memberId, projectId))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("스프린트가 없으면 예외를 발생시킨다.")
    void validateSprintThrowsExceptionWhenNotFound() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any())).willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProject_ProjectIdAndId(any(), any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sprintService.getSprintById(memberId, projectId, sprintId))
            .isInstanceOf(SprintNotFoundException.class);
    }

    @Test
    @DisplayName("스프린트 번다운 데이터를 반환한다.")
    void getBurndownData() {
        // when
        List<CompletedSprintData> response = sprintService.getBurndownData(memberId, projectId);

        // then
        assertThat(response).hasSize(2);

        // 완료된 스프린트 검증
        CompletedSprintData completedSprint = response.get(0);
        assertThat(completedSprint.getSprintId()).isEqualTo(1L); // 확인
        assertThat(completedSprint.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(completedSprint.getEndDate()).isEqualTo(LocalDate.of(2024, 2, 1));
        assertThat(completedSprint.getCompletedInSprint()).isEqualTo(1);
        assertThat(completedSprint.getRemainingBacklogs()).isEqualTo(1);

        // 활성 스프린트 검증
        CompletedSprintData activeSprint = response.get(1);
        assertThat(activeSprint.getSprintId()).isEqualTo(2L); // 확인
        assertThat(activeSprint.getStartDate()).isEqualTo(LocalDate.of(2024, 2, 2));
        assertThat(activeSprint.getEndDate()).isEqualTo(LocalDate.now());
        assertThat(activeSprint.getCompletedInSprint()).isEqualTo(1);
        assertThat(activeSprint.getRemainingBacklogs()).isEqualTo(0);
    }

    @Test
    @DisplayName("활성 스프린트가 없을 때 번다운 데이터를 반환한다.")
    void getBurndownData_NoActiveSprint() {
        // given
        given(sprintRepository.findByProjectAndIsActivateTrue(any()))
            .willReturn(Optional.empty()); // 활성 스프린트 없음

        // when
        List<CompletedSprintData> response = sprintService.getBurndownData(memberId, projectId);

        // then
        assertThat(response).hasSize(1); // 완료된 스프린트만 반환

        CompletedSprintData completedSprint = response.get(0);
        assertThat(completedSprint.getSprintId()).isEqualTo(1L); // 완료된 스프린트 ID
        assertThat(completedSprint.getRemainingBacklogs()).isEqualTo(1); // 남은 백로그 수
        assertThat(completedSprint.getCompletedInSprint()).isEqualTo(1); // 완료된 백로그 수
    }

    @Test
    @DisplayName("완료된 스프린트가 없을 때 번다운 데이터를 반환한다.")
    void getBurndownData_NoCompletedSprints() {
        // given
        given(sprintRepository.findByProject_ProjectIdAndIsDoneTrueOrderByEndDateAsc(any()))
            .willReturn(List.of()); // 완료된 스프린트 없음

        // when
        List<CompletedSprintData> response = sprintService.getBurndownData(memberId, projectId);

        // then
        assertThat(response).hasSize(1); // 활성 스프린트만 반환

        CompletedSprintData activeSprint = response.get(0);
        assertThat(activeSprint.getSprintId()).isEqualTo(2L); // 활성 스프린트 ID
        assertThat(activeSprint.getRemainingBacklogs()).isEqualTo(1); // 남은 백로그 수
        assertThat(activeSprint.getCompletedInSprint()).isEqualTo(1); // 완료된 백로그 수
    }

    @Test
    @DisplayName("프로젝트가 없을 때 ProjectNotFoundException을 반환한다.")
    void getBurndownData_ProjectNotFound() {
        // given
        given(projectRepository.findById(any()))
            .willThrow(new ProjectNotFoundException());

        // when & then
        assertThatThrownBy(() -> sprintService.getBurndownData(memberId, projectId))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("활성 스프린트와 완료된 스프린트가 없을 때 빈 데이터를 반환한다.")
    void getBurndownData_NoSprints() {
        // given
        given(sprintRepository.findByProject_ProjectIdAndIsDoneTrueOrderByEndDateAsc(any()))
            .willReturn(List.of()); // 완료된 스프린트 없음
        given(sprintRepository.findByProjectAndIsActivateTrue(any()))
            .willReturn(Optional.empty()); // 활성 스프린트 없음

        // when
        List<CompletedSprintData> response = sprintService.getBurndownData(memberId, projectId);

        // then
        assertThat(response).isEmpty(); // 스프린트 데이터 없음
    }
}

