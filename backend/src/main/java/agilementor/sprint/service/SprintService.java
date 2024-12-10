package agilementor.sprint.service;

import agilementor.backlog.entity.Backlog;
import agilementor.backlog.entity.Status;
import agilementor.backlog.repository.BacklogRepository;
import agilementor.common.exception.EndDateNullException;
import agilementor.common.exception.ProjectNotFoundException;
import agilementor.common.exception.SprintNotFoundException;
import agilementor.common.exception.TitleNullException;
import agilementor.project.entity.Project;
import agilementor.project.repository.ProjectMemberRepository;
import agilementor.project.repository.ProjectRespository;
import agilementor.sprint.dto.CompletedSprintData;
import agilementor.sprint.dto.SprintForm;
import agilementor.sprint.dto.SprintResponse;
import agilementor.sprint.entity.Sprint;
import agilementor.sprint.repository.SprintRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SprintService {

    private final SprintRepository sprintRepository;
    private final BacklogRepository backlogRepository;
    private final ProjectRespository projectRepository; // 추가: ProjectRepository
    private final ProjectMemberRepository projectMemberRepository; // 추가

    public SprintService(SprintRepository sprintRepository, BacklogRepository backlogRepository,
        ProjectRespository projectRepository,
        ProjectMemberRepository projectMemberRepository) {
        this.sprintRepository = sprintRepository;
        this.backlogRepository = backlogRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    // 프로젝트 멤버인지 확인하는 메서드
    private void validateProjectMember(Long memberId, Long projectId) {
        projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId)
            .orElseThrow(ProjectNotFoundException::new);
    }

    // 스프린트 존재 확인 메서드
    private Sprint validateSprintExists(Long projectId, Long sprintId) {
        return sprintRepository.findByProject_ProjectIdAndId(projectId, sprintId)
            .orElseThrow(SprintNotFoundException::new);
    }

    public SprintResponse createSprint(Long memberId, Long projectId) {
        // 프로젝트 멤버인지 검증
        validateProjectMember(memberId, projectId);

        // Project 조회
        Project project = projectRepository.findById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        // 스프린트 개수 기반으로 제목 생성
        long sprintCount = sprintRepository.countByProject_ProjectId(projectId);
        String title = "Sprint " + (sprintCount + 1);

        Sprint sprint = new Sprint(project, title);
        return sprintRepository.save(sprint).toSprintResponse();
    }

    public List<SprintResponse> getAllSprints(Long memberId, Long projectId) {
        // 프로젝트 멤버인지 검증
        validateProjectMember(memberId, projectId);

        // Project 조회
        Project project = projectRepository.findById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        // Fetch only sprints where isDone is false
        List<Sprint> sprints = sprintRepository.findByProject_ProjectIdAndIsDoneFalse(projectId);
        return sprints.stream()
            .map(Sprint::toSprintResponse)
            .collect(Collectors.toList());
    }

    public SprintResponse getSprintById(Long memberId, Long projectId, Long sprintId) {
        // 프로젝트 멤버인지 검증
        validateProjectMember(memberId, projectId);

        // Project 조회
        Project project = projectRepository.findById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        // 스프린트 조회
        Sprint sprint = validateSprintExists(projectId, sprintId);
        return sprint.toSprintResponse();
    }

    public SprintResponse updateSprint(Long memberId, Long projectId, Long sprintId,
        SprintForm sprintForm) {
        // 프로젝트 멤버인지 검증
        validateProjectMember(memberId, projectId);
        // Project 조회
        Project project = projectRepository.findById(projectId)
            .orElseThrow(ProjectNotFoundException::new);
        // 스프린트 조회
        Sprint sprint = validateSprintExists(projectId, sprintId);
        // 제목을 입력하지 않으면 예외 발생
        if (sprintForm.getTitle() == null) {
            throw new TitleNullException();
        }
        // 업데이트 로직
        sprint.update(
            sprintForm.getTitle(),
            sprintForm.getGoal(),
            sprintForm.getEndDate(),
            sprint.isActivate() // 현재 활성화 상태 확인
        );

        // 저장 후 반환
        return sprintRepository.save(sprint).toSprintResponse();
    }

    public void deleteSprint(Long memberId, Long projectId, Long sprintId) {
        // 프로젝트 멤버인지 검증
        validateProjectMember(memberId, projectId);
        // Project 조회
        Project project = projectRepository.findById(projectId)
            .orElseThrow(ProjectNotFoundException::new);
        // 스프린트 조회
        Sprint sprint = validateSprintExists(projectId, sprintId);

        // 스프린트에 연관된 백로그 조회 및 스프린트와의 연관 해제
        List<Backlog> backlogs = backlogRepository.findBySprint(sprint);
        for (Backlog backlog : backlogs) {
            backlog.setSprint(null); // Sprint와의 연관 해제
            backlogRepository.save(backlog); // 변경사항 저장
        }
        sprintRepository.delete(sprint);
    }

    public SprintResponse startSprint(Long memberId, Long projectId, Long sprintId,
        SprintForm sprintForm) {
        // 프로젝트 멤버인지 검증
        validateProjectMember(memberId, projectId);
        // Project 조회
        Project project = projectRepository.findById(projectId)
            .orElseThrow(ProjectNotFoundException::new);
        // 스프린트 조회
        Sprint sprint = validateSprintExists(projectId, sprintId);
        // 제목을 입력하지 않으면 예외 발생
        if (sprintForm.getTitle() == null) {
            throw new TitleNullException();
        }
        // 종료 날짜를 입력하지 않으면 예외 발생
        if (sprintForm.getEndDate() == null) {
            throw new EndDateNullException();
        }
        // 스프린트 시작
        sprint.start(); // isActivate를 true로 설정하고 시작날짜를 현재날짜로 설정

        // title, goal, endDate만 업데이트
        sprint.update(
            sprintForm.getTitle(),
            sprintForm.getGoal(),
            sprintForm.getEndDate(),
            true // 활성화 상태로 강제 설정
        );

        // 저장 후 반환
        return sprintRepository.save(sprint).toSprintResponse();
    }

    public SprintResponse completeSprint(Long memberId, Long projectId, Long sprintId) {
        // 프로젝트 멤버인지 검증
        validateProjectMember(memberId, projectId);
        // Project 조회
        Project project = projectRepository.findById(projectId)
            .orElseThrow(ProjectNotFoundException::new);
        // 스프린트 조회
        Sprint sprint = validateSprintExists(projectId, sprintId);

        // 해당 스프린트에 할당된 백로그 중 isDone이 false인 백로그 조회
        // status가 DONE이 아닌 백로그 조회
        List<Backlog> incompleteBacklogs = backlogRepository.findBySprint_IdAndStatusNot(sprintId,
            Status.DONE);

        // 각 백로그의 스프린트 ID를 null로 설정하여 완료하지 않은 백로그는 백로그 목록으로 이동
        incompleteBacklogs.forEach(backlog -> {
            backlog.setSprint(null);
            backlogRepository.save(backlog); // 변경 내용을 저장
        });

        sprint.complete(); // isDone 을 true 로 설정하고 종료날짜를 현재날짜로 설정
        return sprintRepository.save(sprint).toSprintResponse();
    }

    public List<CompletedSprintData> getBurndownData(Long memberId, Long projectId) {
        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 프로젝트 멤버 검증
        validateProjectMember(memberId, projectId);

        // Project 조회
        Project project = projectRepository.findById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        // 프로젝트 전체 백로그 조회
        List<Backlog> projectBacklogs = backlogRepository.findByProject(project);

        // 완료된 백로그 누적 계산
        AtomicLong cumulativeCompletedBacklogs = new AtomicLong();

        // 종료된 스프린트 조회
        List<Sprint> completedSprints = sprintRepository.findByProject_ProjectIdAndIsDoneTrueOrderByEndDateAsc(
            projectId);

        // 종료된 스프린트 데이터 생성
        List<CompletedSprintData> sprintData = completedSprints.stream()
            .map(sprint -> {
                // 해당 스프린트의 완료된 백로그 계산
                List<Backlog> sprintBacklogs = backlogRepository.findBySprint(sprint);
                long completedInSprint = sprintBacklogs.stream()
                    .filter(backlog -> backlog.getStatus() == Status.DONE)
                    .count();

                // 누적 완료된 백로그 업데이트
                cumulativeCompletedBacklogs.addAndGet(completedInSprint);

                // 남은 백로그 계산
                long remainingBacklogsAtEndDate = Math.max(
                    projectBacklogs.size() - cumulativeCompletedBacklogs.get(),
                    0
                );

                // CompletedSprintData 생성
                return new CompletedSprintData(
                    sprint.getId(),
                    sprint.getStartDate(),
                    sprint.getEndDate(),
                    remainingBacklogsAtEndDate,
                    completedInSprint,
                    false // 완료된 스프린트는 활성 상태가 아님
                );
            })
            .collect(Collectors.toList());

        // 활성 스프린트 조회
        Optional<Sprint> activeSprint = sprintRepository.findByProjectAndIsActivateTrue(project);

        // 활성 스프린트 데이터 처리
        activeSprint.ifPresent(sprint -> {
            List<Backlog> activeSprintBacklogs = backlogRepository.findBySprint(sprint);
            long completedInActiveSprint = activeSprintBacklogs.stream()
                .filter(backlog -> backlog.getStatus() == Status.DONE)
                .count();

            long remainingBacklogsAtToday = Math.max(
                projectBacklogs.size() - cumulativeCompletedBacklogs.get()
                    - completedInActiveSprint,
                0
            );

            sprintData.add(new CompletedSprintData(
                sprint.getId(),
                sprint.getStartDate(),
                today, // 활성 스프린트는 종료일을 오늘로 설정
                remainingBacklogsAtToday,
                completedInActiveSprint,
                true // 활성 스프린트는 활성 상태
            ));
        });

        return sprintData;
    }
}