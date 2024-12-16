package agilementor.backlog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import agilementor.backlog.dto.request.BacklogCreateRequest;
import agilementor.backlog.dto.request.BacklogUpdateRequest;
import agilementor.backlog.dto.response.BacklogGetResponse;
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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BacklogServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private BacklogRepository backlogRepository;

    @Mock
    private StoryRepository storyRepository;

    @InjectMocks
    private BacklogService backlogService;

    @Test
    @DisplayName("백로그를 생성할 수 있다.")
    void createBacklog() {
        // given
        Long backlogId = 1L;
        Long projectId = 1L;
        Long memberId = 1L;
        Long sprintId = null;
        Long storyId = null;
        Long assigneeId = null;

        String title = "title";
        String description = "description";
        Priority priority = Priority.MEDIUM;

        BacklogCreateRequest backlogCreateRequest = new BacklogCreateRequest(title, description,
            priority, sprintId, storyId, assigneeId);

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        ReflectionTestUtils.setField(project, "projectId", projectId);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(backlogRepository.save(any(Backlog.class))).willAnswer(invocationOnMock -> {
            Backlog backlog = invocationOnMock.getArgument(0);
            ReflectionTestUtils.setField(backlog, "backlogId", backlogId);
            return backlog;
        });

        // when
        var actual = backlogService.createBacklog(memberId, projectId, backlogCreateRequest);

        // then
        assertThat(actual.backlogId()).isEqualTo(backlogId);
        assertThat(actual.title()).isEqualTo(title);
        assertThat(actual.description()).isEqualTo(description);
        assertThat(actual.status()).isEqualTo(Status.TODO);
        assertThat(actual.priority()).isEqualTo(priority);
        assertThat(actual.projectId()).isEqualTo(projectId);
        assertThat(actual.sprintId()).isNull();
        assertThat(actual.storyId()).isNull();
        assertThat(actual.memberId()).isNull();
    }

    @Test
    @DisplayName("스프린트, 스토리, 담당자를 지정하여 백로그를 생성할 수 있다.")
    void createBacklogWithSprintAndStoryAndAssignee() {
        // given
        Long backlogId = 1L;
        Long projectId = 1L;
        Long memberId = 1L;
        Long sprintId = 1L;
        Long storyId = 1L;
        Long assigneeId = 2L;

        String title = "title";
        String description = "description";
        Priority priority = Priority.MEDIUM;

        BacklogCreateRequest backlogCreateRequest = new BacklogCreateRequest(title, description,
            priority, sprintId, storyId, assigneeId);

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        Sprint sprint = new Sprint(project, "title");
        Story story = new Story(project, "title", "description");
        Member assignee = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember assigneeProjectMember = new ProjectMember(project, assignee, false);

        ReflectionTestUtils.setField(project, "projectId", projectId);
        ReflectionTestUtils.setField(sprint, "id", sprintId);
        ReflectionTestUtils.setField(story, "storyId", storyId);
        ReflectionTestUtils.setField(assignee, "memberId", assigneeId);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(projectMemberRepository.findByMemberIdAndProjectId(assigneeId, projectId))
            .willReturn(Optional.of(assigneeProjectMember));
        given(sprintRepository.findByIdAndProject(sprintId, project))
            .willReturn(Optional.of(sprint));
        given(storyRepository.findByStoryIdAndProject(storyId, project))
            .willReturn(Optional.of(story));
        given(backlogRepository.save(any(Backlog.class))).willAnswer(invocationOnMock -> {
            Backlog backlog = invocationOnMock.getArgument(0);
            ReflectionTestUtils.setField(backlog, "backlogId", backlogId);
            return backlog;
        });

        // when
        var actual = backlogService.createBacklog(memberId, projectId, backlogCreateRequest);

        // then
        assertThat(actual.backlogId()).isEqualTo(backlogId);
        assertThat(actual.title()).isEqualTo(title);
        assertThat(actual.description()).isEqualTo(description);
        assertThat(actual.status()).isEqualTo(Status.TODO);
        assertThat(actual.priority()).isEqualTo(priority);
        assertThat(actual.projectId()).isEqualTo(projectId);
        assertThat(actual.sprintId()).isEqualTo(sprintId);
        assertThat(actual.storyId()).isEqualTo(storyId);
        assertThat(actual.memberId()).isEqualTo(assigneeId);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트에 백로그를 생성할 수 없다.")
    void createBacklogFailIfNotParticipatingProject() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;
        Long sprintId = 1L;
        Long storyId = 1L;
        Long assigneeId = 2L;

        String title = "title";
        String description = "description";
        Priority priority = Priority.MEDIUM;

        BacklogCreateRequest backlogCreateRequest = new BacklogCreateRequest(title, description,
            priority, sprintId, storyId, assigneeId);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> backlogService.createBacklog(memberId, projectId, backlogCreateRequest))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 스프린트를 지정하여 백로그를 생성할 수 없다.")
    void createBacklogFailIfNotExistingSprint() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;
        Long sprintId = 1L;
        Long storyId = null;
        Long assigneeId = null;

        String title = "title";
        String description = "description";
        Priority priority = Priority.MEDIUM;

        BacklogCreateRequest backlogCreateRequest = new BacklogCreateRequest(title, description,
            priority, sprintId, storyId, assigneeId);

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(sprintRepository.findByIdAndProject(sprintId, project))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> backlogService.createBacklog(memberId, projectId, backlogCreateRequest))
            .isInstanceOf(SprintNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 스토리를 지정하여 백로그를 생성할 수 없다.")
    void createBacklogFailIfNotExistingStory() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;
        Long sprintId = null;
        Long storyId = 1L;
        Long assigneeId = null;

        String title = "title";
        String description = "description";
        Priority priority = Priority.MEDIUM;

        BacklogCreateRequest backlogCreateRequest = new BacklogCreateRequest(title, description,
            priority, sprintId, storyId, assigneeId);

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(storyRepository.findByStoryIdAndProject(storyId, project))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> backlogService.createBacklog(memberId, projectId, backlogCreateRequest))
            .isInstanceOf(StoryNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 담당자를 지정하여 백로그를 생성할 수 없다.")
    void createBacklogFailIfNotExistingAssignee() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;
        Long sprintId = null;
        Long storyId = null;
        Long assigneeId = 2L;

        String title = "title";
        String description = "description";
        Priority priority = Priority.MEDIUM;

        BacklogCreateRequest backlogCreateRequest = new BacklogCreateRequest(title, description,
            priority, sprintId, storyId, assigneeId);

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(projectMemberRepository.findByMemberIdAndProjectId(assigneeId, projectId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> backlogService.createBacklog(memberId, projectId, backlogCreateRequest))
            .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("백로그 목록을 조회할 수 있다.")
    void getBacklogList() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;
        Long sprintId = 1L;

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);
        Sprint sprint = new Sprint(project, "title");
        Sprint doneSprint = new Sprint(project, "doneSprint");
        doneSprint.complete();

        ReflectionTestUtils.setField(sprint, "id", sprintId);

        Backlog backlog1 = new Backlog("title", "desc", Priority.MEDIUM, project, null, null, null);
        Backlog backlog2 = new Backlog("title", "desc", Priority.MEDIUM, project, null, null, null);
        Backlog backlog3 = new Backlog("title", "desc", Priority.MEDIUM, project, null, null, null);
        Backlog backlog4 = new Backlog("title", "desc", Priority.MEDIUM, project, sprint, null,
            null);
        Backlog backlog5 = new Backlog("title", "desc", Priority.MEDIUM, project, sprint, null,
            null);
        Backlog backlog6 = new Backlog("title", "desc", Priority.MEDIUM, project, sprint, null,
            null);
        Backlog backlog7 = new Backlog("title", "desc", Priority.MEDIUM, project, doneSprint, null,
            null);

        backlog2.update("title", "desc", Status.IN_PROGRESS, Priority.MEDIUM, null, null, null);
        backlog3.update("title", "desc", Status.DONE, Priority.MEDIUM, null, null, null);
        backlog5.update("title", "desc", Status.IN_PROGRESS, Priority.MEDIUM, sprint, null, null);
        backlog6.update("title", "desc", Status.DONE, Priority.MEDIUM, sprint, null, null);
        backlog7.update("title", "desc", Status.DONE, Priority.MEDIUM, doneSprint, null, null);

        List<Backlog> backlogList = List.of(backlog1, backlog2, backlog3, backlog4, backlog5,
            backlog6, backlog7);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(backlogRepository.findByProject(project))
            .willReturn(backlogList);

        // when
        List<BacklogGetResponse> actual = backlogService.getBacklogList(memberId, projectId);

        // then
        assertThat(actual.size()).isEqualTo(5);
        assertThat(actual.stream()
            .filter(backlogGetResponse -> backlogGetResponse.sprintId() == null)
            .toList().size()).isEqualTo(2);
        assertThat(actual.stream()
            .filter(backlogGetResponse -> backlogGetResponse.sprintId() != null)
            .toList().size()).isEqualTo(3);
        assertThat(actual.stream()
            .filter(backlogGetResponse -> backlogGetResponse.status() == Status.DONE)
            .toList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트의 백로그 목록을 조회할 수 없다.")
    void getBacklogListFailIfNotParticipatingProject() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> backlogService.getBacklogList(memberId, projectId))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("백로그를 조회할 수 있다.")
    void getBacklog() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;
        Long backlogId = 1L;
        Long sprintId = 1L;
        Long storyId = 1L;
        Long assigneeId = 2L;

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        Sprint sprint = new Sprint(project, "title");
        Story story = new Story(project, "title", "description");
        Member assignee = new Member("email@email.com", "name", "pic.jpg");

        ReflectionTestUtils.setField(sprint, "id", sprintId);
        ReflectionTestUtils.setField(story, "storyId", storyId);
        ReflectionTestUtils.setField(assignee, "memberId", assigneeId);

        Backlog backlog = new Backlog("title", "desc", Priority.MEDIUM, project, sprint, story, assignee);

        ReflectionTestUtils.setField(backlog, "backlogId", backlogId);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(backlogRepository.findById(backlogId))
            .willReturn(Optional.of(backlog));

        // when
        BacklogGetResponse actual = backlogService.getBacklog(memberId, projectId, backlogId);

        // then
        assertThat(actual.backlogId()).isEqualTo(backlogId);
        assertThat(actual.sprintId()).isEqualTo(sprintId);
        assertThat(actual.storyId()).isEqualTo(storyId);
        assertThat(actual.memberId()).isEqualTo(assigneeId);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트의 백로그를 조회할 수 없다.")
    void getBacklogFailIfNotParticipatingProject() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;
        Long backlogId = 1L;

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> backlogService.getBacklog(memberId, projectId, backlogId))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 백로그를 조회할 수 없다.")
    void getBacklogFailIfNotExistingBacklog() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;
        Long backlogId = 1L;

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(backlogRepository.findById(backlogId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> backlogService.getBacklog(memberId, projectId, backlogId))
            .isInstanceOf(BacklogNotFoundException.class);
    }

    @Test
    @DisplayName("백로그를 수정할 수 있다.")
    void updateBacklog() {
        // given
        Long backlogId = 1L;
        Long projectId = 1L;
        Long memberId = 1L;
        Long sprintId = null;
        Long storyId = null;
        Long assigneeId = null;

        String title = "new title";
        String description = "new description";
        Status status = Status.IN_PROGRESS;
        Priority priority = Priority.LOW;

        BacklogUpdateRequest backlogUpdateRequest = new BacklogUpdateRequest(title, description,
            status, priority, sprintId, storyId, assigneeId);

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);
        Backlog backlog = new Backlog("title", "desc", Priority.MEDIUM, project, null, null, null);

        ReflectionTestUtils.setField(project, "projectId", projectId);
        ReflectionTestUtils.setField(backlog, "backlogId", backlogId);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(backlogRepository.findByBacklogIdAndProject(backlogId, project))
            .willReturn(Optional.of(backlog));

        // when
        var actual = backlogService.updateBacklog(memberId, projectId, backlogId,
            backlogUpdateRequest);

        // then
        assertThat(actual.backlogId()).isEqualTo(backlogId);
        assertThat(actual.title()).isEqualTo(title);
        assertThat(actual.description()).isEqualTo(description);
        assertThat(actual.status()).isEqualTo(status);
        assertThat(actual.priority()).isEqualTo(priority);
        assertThat(actual.projectId()).isEqualTo(projectId);
        assertThat(actual.sprintId()).isNull();
        assertThat(actual.storyId()).isNull();
        assertThat(actual.memberId()).isNull();
    }

    @Test
    @DisplayName("스프린트, 스토리, 담당자를 지정하여 백로그를 수정할 수 있다.")
    void updateBacklogWithSprintAndStoryAndAssignee() {
        // given
        Long backlogId = 1L;
        Long projectId = 1L;
        Long memberId = 1L;
        Long sprintId = 1L;
        Long storyId = 1L;
        Long assigneeId = 2L;

        String title = "new title";
        String description = "new description";
        Status status = Status.IN_PROGRESS;
        Priority priority = Priority.HIGH;

        BacklogUpdateRequest backlogUpdateRequest = new BacklogUpdateRequest(title, description,
            status, priority, sprintId, storyId, assigneeId);

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        Sprint sprint = new Sprint(project, "title");
        Story story = new Story(project, "title", "description");
        Member assignee = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember assigneeProjectMember = new ProjectMember(project, assignee, false);
        Backlog backlog = new Backlog("title", "desc", Priority.MEDIUM, project, null, null, null);

        ReflectionTestUtils.setField(project, "projectId", projectId);
        ReflectionTestUtils.setField(sprint, "id", sprintId);
        ReflectionTestUtils.setField(story, "storyId", storyId);
        ReflectionTestUtils.setField(assignee, "memberId", assigneeId);
        ReflectionTestUtils.setField(backlog, "backlogId", backlogId);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(projectMemberRepository.findByMemberIdAndProjectId(assigneeId, projectId))
            .willReturn(Optional.of(assigneeProjectMember));
        given(sprintRepository.findByIdAndProject(sprintId, project))
            .willReturn(Optional.of(sprint));
        given(storyRepository.findByStoryIdAndProject(storyId, project))
            .willReturn(Optional.of(story));
        given(backlogRepository.findByBacklogIdAndProject(backlogId, project))
            .willReturn(Optional.of(backlog));

        // when
        var actual = backlogService.updateBacklog(memberId, projectId, backlogId,
            backlogUpdateRequest);

        // then
        assertThat(actual.backlogId()).isEqualTo(backlogId);
        assertThat(actual.title()).isEqualTo(title);
        assertThat(actual.description()).isEqualTo(description);
        assertThat(actual.status()).isEqualTo(status);
        assertThat(actual.priority()).isEqualTo(priority);
        assertThat(actual.projectId()).isEqualTo(projectId);
        assertThat(actual.sprintId()).isEqualTo(sprintId);
        assertThat(actual.storyId()).isEqualTo(storyId);
        assertThat(actual.memberId()).isEqualTo(assigneeId);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트의 백로그를 수정할 수 없다.")
    void updateBacklogFailIfNotParticipatingProject() {
        // given
        Long backlogId = 1L;
        Long projectId = 1L;
        Long memberId = 1L;
        Long sprintId = 1L;
        Long storyId = 1L;
        Long assigneeId = 2L;

        String title = "new title";
        String description = "new description";
        Status status = Status.IN_PROGRESS;
        Priority priority = Priority.HIGH;

        BacklogUpdateRequest backlogUpdateRequest = new BacklogUpdateRequest(title, description,
            status, priority, sprintId, storyId, assigneeId);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> backlogService.updateBacklog(memberId, projectId, backlogId,
                backlogUpdateRequest))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 백로그를 수정할 수 없다.")
    void updateBacklogFailIfNotExistingBacklog() {
        // given
        Long backlogId = 1L;
        Long projectId = 1L;
        Long memberId = 1L;
        Long sprintId = 1L;
        Long storyId = 1L;
        Long assigneeId = 2L;

        String title = "new title";
        String description = "new description";
        Status status = Status.IN_PROGRESS;
        Priority priority = Priority.HIGH;

        BacklogUpdateRequest backlogUpdateRequest = new BacklogUpdateRequest(title, description,
            status, priority, sprintId, storyId, assigneeId);

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(backlogRepository.findByBacklogIdAndProject(backlogId, project))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> backlogService.updateBacklog(memberId, projectId, backlogId,
                backlogUpdateRequest))
            .isInstanceOf(BacklogNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 스프린트를 지정하여 백로그를 수정할 수 없다.")
    void updateBacklogFailIfNotExistingSprint() {
        // given
        Long backlogId = 1L;
        Long projectId = 1L;
        Long memberId = 1L;
        Long sprintId = 1L;
        Long storyId = 1L;
        Long assigneeId = 2L;

        String title = "new title";
        String description = "new description";
        Status status = Status.IN_PROGRESS;
        Priority priority = Priority.HIGH;

        BacklogUpdateRequest backlogUpdateRequest = new BacklogUpdateRequest(title, description,
            status, priority, sprintId, storyId, assigneeId);

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);
        Backlog backlog = new Backlog("title", "desc", Priority.MEDIUM, project, null, null, null);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(backlogRepository.findByBacklogIdAndProject(backlogId, project))
            .willReturn(Optional.of(backlog));
        given(sprintRepository.findByIdAndProject(sprintId, project))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> backlogService.updateBacklog(memberId, projectId, backlogId,
                backlogUpdateRequest))
            .isInstanceOf(SprintNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 스토리를 지정하여 백로그를 수정할 수 없다.")
    void updateBacklogFailIfNotExistingStory() {
        // given
        Long backlogId = 1L;
        Long projectId = 1L;
        Long memberId = 1L;
        Long sprintId = 1L;
        Long storyId = 1L;
        Long assigneeId = 2L;

        String title = "new title";
        String description = "new description";
        Status status = Status.IN_PROGRESS;
        Priority priority = Priority.HIGH;

        BacklogUpdateRequest backlogUpdateRequest = new BacklogUpdateRequest(title, description,
            status, priority, sprintId, storyId, assigneeId);

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);
        Backlog backlog = new Backlog("title", "desc", Priority.MEDIUM, project, null, null, null);
        Sprint sprint = new Sprint(project, "title");

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(backlogRepository.findByBacklogIdAndProject(backlogId, project))
            .willReturn(Optional.of(backlog));
        given(sprintRepository.findByIdAndProject(sprintId, project))
            .willReturn(Optional.of(sprint));
        given(storyRepository.findByStoryIdAndProject(storyId, project))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> backlogService.updateBacklog(memberId, projectId, backlogId,
                backlogUpdateRequest))
            .isInstanceOf(StoryNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 담당자를 지정하여 백로그를 수정할 수 없다.")
    void updateBacklogFailIfNotExistingAssignee() {
        // given
        Long backlogId = 1L;
        Long projectId = 1L;
        Long memberId = 1L;
        Long sprintId = 1L;
        Long storyId = 1L;
        Long assigneeId = 2L;

        String title = "new title";
        String description = "new description";
        Status status = Status.IN_PROGRESS;
        Priority priority = Priority.HIGH;

        BacklogUpdateRequest backlogUpdateRequest = new BacklogUpdateRequest(title, description,
            status, priority, sprintId, storyId, assigneeId);

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);
        Backlog backlog = new Backlog("title", "desc", Priority.MEDIUM, project, null, null, null);
        Sprint sprint = new Sprint(project, "title");
        Story story = new Story(project, "title", "description");

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(backlogRepository.findByBacklogIdAndProject(backlogId, project))
            .willReturn(Optional.of(backlog));
        given(sprintRepository.findByIdAndProject(sprintId, project))
            .willReturn(Optional.of(sprint));
        given(storyRepository.findByStoryIdAndProject(storyId, project))
            .willReturn(Optional.of(story));
        given(projectMemberRepository.findByMemberIdAndProjectId(assigneeId, projectId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> backlogService.updateBacklog(memberId, projectId, backlogId,
                backlogUpdateRequest))
            .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("백로그를 삭제할 수 있다.")
    void deleteBacklog() {
        // given
        Long backlogId = 1L;
        Long projectId = 1L;
        Long memberId = 1L;

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);
        Backlog backlog = new Backlog("title", "desc", Priority.MEDIUM, project, null, null, null);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(backlogRepository.findByBacklogIdAndProject(backlogId, project))
            .willReturn(Optional.of(backlog));

        // when
        backlogService.deleteBacklog(memberId, projectId, backlogId);

        // then
        then(backlogRepository).should().delete(backlog);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트의 백로그를 삭제할 수 없다.")
    void deleteBacklogFailIfNotParticipatingProject() {
        // given
        Long backlogId = 1L;
        Long projectId = 1L;
        Long memberId = 1L;

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> backlogService.deleteBacklog(memberId, projectId, backlogId))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 백로그를 삭제할 수 없다.")
    void deleteBacklogFailIfNotExistingBacklog() {
        // given
        Long backlogId = 1L;
        Long projectId = 1L;
        Long memberId = 1L;

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(backlogRepository.findByBacklogIdAndProject(backlogId, project))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> backlogService.deleteBacklog(memberId, projectId, backlogId))
            .isInstanceOf(BacklogNotFoundException.class);
    }

    @Test
    @DisplayName("진행중인 스프린트의 백로그 목록을 조회할 수 있다.")
    void getActiveBacklogList() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        Sprint activeSprint = new Sprint(project, "activeSprint");
        Backlog backlog1 = new Backlog("title", "desc", Priority.MEDIUM, project, activeSprint,
            null, null);
        Backlog backlog2 = new Backlog("title", "desc", Priority.MEDIUM, project, activeSprint,
            null, null);
        Backlog backlog3 = new Backlog("title", "desc", Priority.MEDIUM, project, activeSprint,
            null, null);
        List<Backlog> backlogList = List.of(backlog1, backlog2, backlog3);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProjectAndIsActivateTrue(project))
            .willReturn(Optional.of(activeSprint));
        given(backlogRepository.findBySprint(activeSprint))
            .willReturn(backlogList);

        // when
        var actual = backlogService.getActiveBacklogList(memberId, projectId);

        // then
        assertThat(actual.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트의 진행중인 스프린트의 백로그 목록을 조회할 수 없다.")
    void getActiveBacklogListFailIfNotParticipatingProject() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> backlogService.getActiveBacklogList(memberId, projectId))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("진행중인 스프린트가 없는 프로젝트의 진행중인 스프린트의 백로그 목록을 조회할 수 없다.")
    void getActiveBacklogListFailIfNotExistingActiveSprint() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(sprintRepository.findByProjectAndIsActivateTrue(project))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> backlogService.getActiveBacklogList(memberId, projectId))
            .isInstanceOf(SprintNotFoundException.class);
    }

    @Test
    @DisplayName("사용자에게 할당된 진행중인 작업 목록을 조회할 수 있다.")
    void getTasks() {
        Long memberId = 1L;

        Member member = new Member("email@email.com", "name", "pic.jpg");

        Project project1 = new Project("project");
        Project project2 = new Project("project");
        Project project3 = new Project("project");

        ProjectMember projectMember1 = new ProjectMember(project1, member, true);
        ProjectMember projectMember2 = new ProjectMember(project2, member, false);
        ProjectMember projectMember3 = new ProjectMember(project3, member, false);
        List<ProjectMember> projectMemberList = List.of(projectMember1, projectMember2,
            projectMember3);

        Sprint activeSprint1 = new Sprint(project1, "sprint", "goal");
        Sprint activeSprint2 = new Sprint(project2, "sprint", "goal");

        Backlog backlog1 = new Backlog("title", "desc", Priority.MEDIUM, project1, activeSprint1,
            null, null);
        Backlog backlog2 = new Backlog("title", "desc", Priority.MEDIUM, project2, activeSprint2,
            null, null);
        Backlog backlog3 = new Backlog("title", "desc", Priority.MEDIUM, project2, activeSprint2,
            null, null);
        List<Backlog> backlogList1 = List.of(backlog1);
        List<Backlog> backlogList2 = List.of(backlog2, backlog3);

        given(memberRepository.findById(memberId))
            .willReturn(Optional.of(member));
        given(projectMemberRepository.findByMemberId(memberId))
            .willReturn(projectMemberList);
        given(sprintRepository.findByProjectAndIsActivateTrue(project1))
            .willReturn(Optional.of(activeSprint1));
        given(sprintRepository.findByProjectAndIsActivateTrue(project2))
            .willReturn(Optional.of(activeSprint2));
        given(sprintRepository.findByProjectAndIsActivateTrue(project3))
            .willReturn(Optional.empty());
        given(backlogRepository.findByAssigneeAndSprint(member, activeSprint1))
            .willReturn(backlogList1);
        given(backlogRepository.findByAssigneeAndSprint(member, activeSprint2))
            .willReturn(backlogList2);

        // when
        List<BacklogGetResponse> actual = backlogService.getTasks(memberId);

        // then
        assertThat(actual.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("사용자가 아니면 할당된 진행중인 작업 목록을 조회할 수 없다.")
    void getTasksFailIfNotMember() {
        Long memberId = 0L;

        given(memberRepository.findById(memberId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> backlogService.getTasks(memberId))
            .isInstanceOf(MemberNotFoundException.class);

    }

}