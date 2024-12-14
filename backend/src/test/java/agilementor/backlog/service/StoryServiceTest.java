package agilementor.backlog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import agilementor.backlog.dto.request.StoryCreateRequest;
import agilementor.backlog.dto.request.StoryUpdateRequest;
import agilementor.backlog.dto.response.StoryGetResponse;
import agilementor.backlog.entity.Backlog;
import agilementor.backlog.entity.Priority;
import agilementor.backlog.entity.Status;
import agilementor.backlog.entity.Story;
import agilementor.backlog.repository.BacklogRepository;
import agilementor.backlog.repository.StoryRepository;
import agilementor.common.exception.ProjectNotFoundException;
import agilementor.common.exception.StoryNotFoundException;
import agilementor.member.entity.Member;
import agilementor.project.entity.Project;
import agilementor.project.entity.ProjectMember;
import agilementor.project.repository.ProjectMemberRepository;
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
class StoryServiceTest {

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private StoryRepository storyRepository;

    @Mock
    private BacklogRepository backlogRepository;

    @InjectMocks
    private StoryService storyService;

    @Test
    @DisplayName("스토리를 생성할 수 있다.")
    void createStory() {
        // given
        long projectId = 1L;
        long memberId = 1L;

        String title = "title";
        String description = "description";

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);
        StoryCreateRequest storyCreateRequest = new StoryCreateRequest(title, description);

        ReflectionTestUtils.setField(project, "projectId", projectId);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(storyRepository.save(any()))
            .willReturn(new Story(project, title, description));

        // when
        var actual = storyService.createStory(memberId, projectId, storyCreateRequest);

        // then
        assertThat(actual.title()).isEqualTo(title);
        assertThat(actual.description()).isEqualTo(description);
        assertThat(actual.projectId()).isEqualTo(projectId);
        assertThat(actual.status()).isEqualTo(Status.IN_PROGRESS);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트에 스토리를 생성할 수 없다.")
    void createStoryFailIfNotParticipatingProject() {
        // given
        long projectId = 1L;
        long memberId = 1L;

        String title = "title";
        String description = "description";

        StoryCreateRequest storyCreateRequest = new StoryCreateRequest(title, description);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> storyService.createStory(memberId, projectId, storyCreateRequest))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("스토리 목록을 조회할 수 있다.")
    void getStoryList() {
        // given
        long projectId = 1L;
        long memberId = 1L;

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        Story story1 = new Story(project, "story1", "story1");
        Story story2 = new Story(project, "story2", "story1");
        Story story3 = new Story(project, "story3", "story1");

        List<Story> storyList = List.of(story1, story2, story3);

        Backlog todoBacklog = new Backlog("title", "desc", Priority.MEDIUM, project, null, story2,
            null);
        Backlog doneBacklog1 = new Backlog("title", "desc", Priority.MEDIUM, project, null, story2,
            null);
        Backlog doneBacklog2 = new Backlog("title", "desc", Priority.MEDIUM, project, null, story3,
            null);
        Backlog doneBacklog3 = new Backlog("title", "desc", Priority.MEDIUM, project, null, story3,
            null);

        doneBacklog1.update("title", "desc", Status.DONE, Priority.MEDIUM, null, story2, member);
        doneBacklog2.update("title", "desc", Status.DONE, Priority.MEDIUM, null, story3, member);
        doneBacklog3.update("title", "desc", Status.DONE, Priority.MEDIUM, null, story3, member);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(storyRepository.findByProject(project))
            .willReturn(storyList);
        given(backlogRepository.findByStory(story1))
            .willReturn(List.of());
        given(backlogRepository.findByStory(story2))
            .willReturn(List.of(todoBacklog, doneBacklog1));
        given(backlogRepository.findByStory(story3))
            .willReturn(List.of(doneBacklog2, doneBacklog3));

        // when
        List<StoryGetResponse> actual = storyService.getStoryList(memberId, projectId);

        // then
        assertThat(actual.size()).isEqualTo(3);

        StoryGetResponse storyResponse1 = actual.getFirst();
        assertThat(storyResponse1.status()).isEqualTo(Status.IN_PROGRESS);

        StoryGetResponse storyResponse2 = actual.get(1);
        assertThat(storyResponse2.status()).isEqualTo(Status.IN_PROGRESS);

        StoryGetResponse storyResponse3 = actual.get(2);
        assertThat(storyResponse3.status()).isEqualTo(Status.DONE);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트의 스토리 목록을 조회할 수 없다.")
    void getStoryListFailIfNotParticipatingProject() {
        // given
        long projectId = 1L;
        long memberId = 1L;

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> storyService.getStoryList(memberId, projectId))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("스토리를 조회할 수 있다.")
    void getStory() {
        // given
        long projectId = 1L;
        long memberId = 1L;
        long storyId1 = 1L;
        long storyId2 = 2L;
        long storyId3 = 3L;

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        Story story1 = new Story(project, "story1", "story1");
        Story story2 = new Story(project, "story2", "story1");
        Story story3 = new Story(project, "story3", "story1");

        Backlog todoBacklog = new Backlog("title", "desc", Priority.MEDIUM, project, null, story2,
            null);
        Backlog doneBacklog1 = new Backlog("title", "desc", Priority.MEDIUM, project, null, story2,
            null);
        Backlog doneBacklog2 = new Backlog("title", "desc", Priority.MEDIUM, project, null, story3,
            null);
        Backlog doneBacklog3 = new Backlog("title", "desc", Priority.MEDIUM, project, null, story3,
            null);

        doneBacklog1.update("title", "desc", Status.DONE, Priority.MEDIUM, null, story2, member);
        doneBacklog2.update("title", "desc", Status.DONE, Priority.MEDIUM, null, story3, member);
        doneBacklog3.update("title", "desc", Status.DONE, Priority.MEDIUM, null, story3, member);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));

        given(storyRepository.findByStoryIdAndProject(storyId1, project))
            .willReturn(Optional.of(story1));
        given(storyRepository.findByStoryIdAndProject(storyId2, project))
            .willReturn(Optional.of(story2));
        given(storyRepository.findByStoryIdAndProject(storyId3, project))
            .willReturn(Optional.of(story3));

        given(backlogRepository.findByStory(story1))
            .willReturn(List.of());
        given(backlogRepository.findByStory(story2))
            .willReturn(List.of(todoBacklog, doneBacklog1));
        given(backlogRepository.findByStory(story3))
            .willReturn(List.of(doneBacklog2, doneBacklog3));

        // when
        StoryGetResponse actual1 = storyService.getStory(memberId, projectId, storyId1);
        StoryGetResponse actual2 = storyService.getStory(memberId, projectId, storyId2);
        StoryGetResponse actual3 = storyService.getStory(memberId, projectId, storyId3);

        // then
        assertThat(actual1.status()).isEqualTo(Status.IN_PROGRESS);
        assertThat(actual2.status()).isEqualTo(Status.IN_PROGRESS);
        assertThat(actual3.status()).isEqualTo(Status.DONE);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트의 스토리를 조회할 수 없다.")
    void getStoryFailIfNotParticipatingProject() {
        // given
        long projectId = 1L;
        long memberId = 1L;
        long storyId = 1L;

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> storyService.getStory(memberId, projectId, storyId))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 스토리를 조회할 수 없다.")
    void getStoryFailIfNotExistingStory() {
        // given
        long projectId = 1L;
        long memberId = 1L;
        long storyId = 1L;

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));
        given(storyRepository.findByStoryIdAndProject(storyId, project))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> storyService.getStory(memberId, projectId, storyId))
            .isInstanceOf(StoryNotFoundException.class);
    }

    @Test
    @DisplayName("스토리를 수정할 수 있다.")
    void updateStory() {
        // given
        long projectId = 1L;
        long memberId = 1L;
        long storyId = 1L;

        String newTitle = "newTitle";
        String newDescription = "newDescription";

        StoryUpdateRequest storyUpdateRequest = new StoryUpdateRequest(newTitle, newDescription);

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        Story story = new Story(project, "story", "story");

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));

        given(storyRepository.findByStoryIdAndProject(storyId, project))
            .willReturn(Optional.of(story));

        given(backlogRepository.findByStory(story))
            .willReturn(List.of());

        // when
        var actual = storyService.updateStory(memberId, projectId, storyId, storyUpdateRequest);

        // then
        assertThat(actual.title()).isEqualTo(newTitle);
        assertThat(actual.description()).isEqualTo(newDescription);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트의 스토리를 수정할 수 없다.")
    void updateStoryFailIfNotParticipatingProject() {
        // given
        long projectId = 1L;
        long memberId = 1L;
        long storyId = 1L;

        String newTitle = "newTitle";
        String newDescription = "newDescription";

        StoryUpdateRequest storyUpdateRequest = new StoryUpdateRequest(newTitle, newDescription);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> storyService.updateStory(memberId, projectId, storyId, storyUpdateRequest))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 스토리를 수정할 수 없다.")
    void updateStoryFailIfNotExistingStory() {
        // given
        long projectId = 1L;
        long memberId = 1L;
        long storyId = 1L;

        String newTitle = "newTitle";
        String newDescription = "newDescription";

        StoryUpdateRequest storyUpdateRequest = new StoryUpdateRequest(newTitle, newDescription);

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
            () -> storyService.updateStory(memberId, projectId, storyId, storyUpdateRequest))
            .isInstanceOf(StoryNotFoundException.class);
    }

    @Test
    @DisplayName("스토리를 삭제할 수 있다.")
    void deleteStory() {
        // given
        long projectId = 1L;
        long memberId = 1L;
        long storyId = 1L;

        Project project = new Project("project");
        Member member = new Member("email@email.com", "name", "pic.jpg");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        Story story = new Story(project, "story", "story");

        Backlog backlog = new Backlog("title", "desc", Priority.MEDIUM, project, null, story, null);

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.of(projectMember));

        given(storyRepository.findByStoryIdAndProject(storyId, project))
            .willReturn(Optional.of(story));

        given(backlogRepository.findByStory(story))
            .willReturn(List.of(backlog));

        // when
        storyService.deleteStory(memberId, projectId, storyId);

        // then
        assertThat(backlog.getStory()).isNull();
        then(storyRepository).should().delete(story);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트의 스토리를 삭제할 수 없다.")
    void deleteStoryFailIfNotParticipatingProject() {
        // given
        long projectId = 1L;
        long memberId = 1L;
        long storyId = 1L;

        given(projectMemberRepository.findByMemberIdAndProjectId(memberId, projectId))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> storyService.deleteStory(memberId, projectId, storyId))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 스토리를 삭제할 수 없다.")
    void deleteStoryFailIfNotExistingStory() {
        // given
        long projectId = 1L;
        long memberId = 1L;
        long storyId = 1L;

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
            () -> storyService.deleteStory(memberId, projectId, storyId))
            .isInstanceOf(StoryNotFoundException.class);
    }

}