package agilementor.backlog.service;

import agilementor.backlog.dto.request.BacklogCreateRequest;
import agilementor.backlog.dto.request.BacklogUpdateRequest;
import agilementor.backlog.dto.response.BacklogCreateResponse;
import agilementor.backlog.dto.response.BacklogGetResponse;
import agilementor.backlog.dto.response.BacklogUpdateResponse;
import agilementor.backlog.entity.Backlog;
import agilementor.backlog.entity.Priority;
import agilementor.backlog.entity.Status;
import agilementor.backlog.entity.Story;
import agilementor.backlog.repository.BacklogRepository;
import agilementor.backlog.repository.StoryRepository;
import agilementor.common.exception.BacklogNotFoundException;
import agilementor.common.exception.MemberNotFoundException;
import agilementor.common.exception.ProjectNotFoundException;
import agilementor.common.exception.SprintNotFoundException;
import agilementor.common.exception.StoryNotFoundException;
import agilementor.member.entity.Member;
import agilementor.member.repository.MemberRepository;
import agilementor.project.entity.Project;
import agilementor.project.entity.ProjectMember;
import agilementor.project.repository.ProjectMemberRepository;
import agilementor.sprint.entity.Sprint;
import agilementor.sprint.repository.SprintRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BacklogService {

    private final MemberRepository memberRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final SprintRepository sprintRepository;
    private final BacklogRepository backlogRepository;
    private final StoryRepository storyRepository;

    public BacklogService(MemberRepository memberRepository,
        ProjectMemberRepository projectMemberRepository,
        SprintRepository sprintRepository, BacklogRepository backlogRepository,
        StoryRepository storyRepository) {
        this.memberRepository = memberRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.sprintRepository = sprintRepository;
        this.backlogRepository = backlogRepository;
        this.storyRepository = storyRepository;
    }

    public BacklogCreateResponse createBacklog(Long memberId, Long projectId,
        BacklogCreateRequest backlogCreateRequest) {

        String title = backlogCreateRequest.title();
        String description = backlogCreateRequest.description();
        Priority priority = backlogCreateRequest.priority();

        Long sprintId = backlogCreateRequest.sprintId();
        Long storyId = backlogCreateRequest.storyId();
        Long assigneeId = backlogCreateRequest.memberId();

        Project project = findProject(memberId, projectId);
        Sprint sprint = sprintRepository.findById(sprintId).orElse(null);
        Story story = storyRepository.findById(storyId).orElse(null);
        Member assignee = memberRepository.findById(assigneeId).orElse(null);

        checkIsAttributesValid(projectId, sprint, story, assignee);

        Backlog backlog = new Backlog(title, description, priority, project, sprint, story,
            assignee);
        Backlog savedBacklog = backlogRepository.save(backlog);

        return BacklogCreateResponse.from(savedBacklog);
    }

    public List<BacklogGetResponse> getBacklogList(Long memberId, Long projectId) {

        Project project = findProject(memberId, projectId);

        List<Backlog> backlogList = backlogRepository.findByProject(project);

        return backlogList.stream()
            .filter(backlog -> {
                if (backlog.isDone()) {
                    Sprint sprint = backlog.getSprint();
                    return sprint != null && !sprint.isDone();
                }
                return true;
            })
            .map(BacklogGetResponse::from)
            .toList();
    }

    public BacklogGetResponse getBacklog(Long memberId, Long projectId, Long backlogId) {

        findProject(memberId, projectId);

        Backlog backlog = backlogRepository.findById(backlogId)
            .orElseThrow(BacklogNotFoundException::new);

        return BacklogGetResponse.from(backlog);
    }

    public BacklogUpdateResponse updateBacklog(Long memberId, Long projectId, Long backlogId,
        BacklogUpdateRequest backlogUpdateRequest) {

        String title = backlogUpdateRequest.title();
        String description = backlogUpdateRequest.description();
        Priority priority = backlogUpdateRequest.priority();
        Status status = backlogUpdateRequest.status();

        Long sprintId = backlogUpdateRequest.sprintId();
        Long storyId = backlogUpdateRequest.storyId();
        Long assigneeId = backlogUpdateRequest.memberId();

        Project project = findProject(memberId, projectId);
        Backlog backlog = backlogRepository.findByBacklogIdAndProject(backlogId, project)
            .orElseThrow(BacklogNotFoundException::new);
        Sprint sprint = sprintRepository.findById(sprintId).orElse(null);
        Story story = storyRepository.findById(storyId).orElse(null);
        Member assignee = memberRepository.findById(assigneeId).orElse(null);

        checkIsAttributesValid(projectId, sprint, story, assignee);

        backlog.update(title, description, status, priority, project, sprint, story, assignee);

        return BacklogUpdateResponse.from(backlog);
    }

    public void deleteBacklog(Long memberId, Long projectId, Long backlogId) {
        Project project = findProject(memberId, projectId);

        Backlog backlog = backlogRepository.findByBacklogIdAndProject(backlogId, project)
            .orElseThrow(BacklogNotFoundException::new);

        backlogRepository.delete(backlog);
    }

    public List<BacklogGetResponse> getActiveBacklogList(Long memberId, Long projectId) {

        Project project = findProject(memberId, projectId);

        Sprint activeSprint = sprintRepository.findByProjectAndIsActivateTrue(project)
            .orElseThrow(SprintNotFoundException::new);

        List<Backlog> backlogList = backlogRepository.findBySprint(activeSprint);

        return backlogList.stream()
            .map(BacklogGetResponse::from)
            .toList();
    }

    private Project findProject(Long memberId, Long projectId) {
        ProjectMember projectMember = projectMemberRepository
            .findByMemberIdAndProjectId(memberId, projectId)
            .orElseThrow(ProjectNotFoundException::new);
        return projectMember.getProject();
    }

    public List<BacklogGetResponse> getTasks(Long memberId) {

        List<ProjectMember> projectMemberList = projectMemberRepository.findByMemberId(memberId);

        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);

        Stream<Sprint> activeSprints = projectMemberList.stream()
            .map(projectMember -> sprintRepository
                .findByProjectAndIsActivateTrue(projectMember.getProject()))
            .filter(Optional::isPresent)
            .map(Optional::get);

        return activeSprints
            .map(sprint -> backlogRepository.findByAssigneeAndSprint(member, sprint))
            .flatMap(List::stream)
            .map(BacklogGetResponse::from)
            .toList();
    }

    private void checkIsAttributesValid(Long projectId, Sprint sprint, Story story,
        Member assignee) {
        if (sprint != null && !sprint.getProject().getProjectId().equals(projectId)) {
            throw new SprintNotFoundException();
        }

        if (story != null && !story.getProject().getProjectId().equals(projectId)) {
            throw new StoryNotFoundException();
        }

        if (assignee != null) {
            projectMemberRepository.findByMemberIdAndProjectId(assignee.getMemberId(), projectId)
                .orElseThrow(MemberNotFoundException::new);
        }
    }
}
