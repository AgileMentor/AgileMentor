package agilementor.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import agilementor.backlog.entity.Backlog;
import agilementor.backlog.entity.Priority;
import agilementor.backlog.entity.Status;
import agilementor.backlog.repository.BacklogRepository;
import agilementor.backlog.repository.StoryRepository;
import agilementor.common.exception.NotProjectAdminException;
import agilementor.common.exception.ProjectNotFoundException;
import agilementor.member.entity.Member;
import agilementor.member.repository.MemberRepository;
import agilementor.project.dto.request.ProjectCreateRequest;
import agilementor.project.dto.request.ProjectUpdateRequest;
import agilementor.project.dto.response.ProjectResponse;
import agilementor.project.entity.Project;
import agilementor.project.entity.ProjectMember;
import agilementor.project.repository.ProjectMemberRepository;
import agilementor.project.repository.ProjectRespository;
import agilementor.sprint.repository.SprintRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectRespository projectRespository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private BacklogRepository backlogRepository;

    @Mock
    private StoryRepository storyRepository;

    @Mock
    private SprintRepository sprintRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    @DisplayName("프로젝트를 생성할 수 있다.")
    void createProject() {
        // given
        Long memberId = 1L;
        ProjectCreateRequest projectCreateRequest = new ProjectCreateRequest("title");
        Project project = projectCreateRequest.toEntity();

        given(memberRepository.findById(any())).willReturn(Optional.of(getMember()));
        given(projectRespository.save(any())).willReturn(projectCreateRequest.toEntity());
        given(projectMemberRepository.save(any())).willReturn(null);

        // when
        ProjectResponse actual = projectService.createProject(memberId, projectCreateRequest);

        // then
        assertThat(actual).isEqualTo(ProjectResponse.from(project));
        then(projectMemberRepository).should().save(any());
    }


    @Test
    @DisplayName("참가한 프로젝트 목록을 조회할 수 있다.")
    void getProjectList() {
        // given
        Member member = getMember();
        Project project1 = new Project("project1");
        Project project2 = new Project("project2");
        ProjectMember projectMember1 = new ProjectMember(project1, member, true);
        ProjectMember projectMember2 = new ProjectMember(project2, member, true);
        List<ProjectMember> projectMemberList = List.of(projectMember1, projectMember2);

        given(projectMemberRepository.findByMemberId(any())).willReturn(projectMemberList);

        // when
        List<ProjectResponse> projectList = projectService.getProjectList(1L);

        // then
        assertThat(projectList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("특정 프로젝트 정보를 조회할 수 있다.")
    void getProject() {
        // given
        String projectTitle = "title";
        Member member = getMember();
        Project project = new Project(projectTitle);
        ProjectMember projectMember = new ProjectMember(project, member, true);

        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.of(projectMember));

        // when
        ProjectResponse actual = projectService.getProject(1L, 1L);

        // then
        assertThat(actual.title()).isEqualTo(projectTitle);
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트 정보는 조회할 수 없다.")
    void getProjectFailIfNotExisting() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> projectService.getProject(1L, 1L))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트 정보는 조회할 수 없다.")
    void getProjectFailIfNotParticipating() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> projectService.getProject(1L, 1L))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("프로젝트 정보를 수정할 수 있다.")
    void updateProject() {
        // given
        String title = "title";
        String newTitle = "newTitle";
        Member member = getMember();
        Project project = new Project(title);
        ProjectMember projectMember = new ProjectMember(project, member, true);
        ProjectUpdateRequest projectUpdateRequest = new ProjectUpdateRequest(newTitle);

        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.of(projectMember));

        // when
        ProjectResponse actual = projectService.updateProject(1L, 1L, projectUpdateRequest);

        // then
        assertThat(actual.title()).isEqualTo(newTitle);
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트 정보를 수정할 수 없다.")
    void updateProjectFailIfNotExisting() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.empty());
        ProjectUpdateRequest projectUpdateRequest = new ProjectUpdateRequest("newTitle");

        // when
        // then
        assertThatThrownBy(() -> projectService.updateProject(1L, 1L, projectUpdateRequest))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트 정보를 수정할 수 없다.")
    void updateProjectFailIfNotParticipating() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.empty());
        ProjectUpdateRequest projectUpdateRequest = new ProjectUpdateRequest("newTitle");

        // when
        // then
        assertThatThrownBy(() -> projectService.updateProject(1L, 1L, projectUpdateRequest))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("관리자 권한이 없는 프로젝트 정보를 수정할 수 없다.")
    void updateProjectFailIfNotAdmin() {
        // given
        String title = "title";
        String newTitle = "newTitle";
        Member member = getMember();
        Project project = new Project(title);
        ProjectMember projectMember = new ProjectMember(project, member, false);
        ProjectUpdateRequest projectUpdateRequest = new ProjectUpdateRequest(newTitle);

        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.of(projectMember));

        // when
        // then
        assertThatThrownBy(() -> projectService.updateProject(1L, 1L, projectUpdateRequest))
            .isInstanceOf(NotProjectAdminException.class);
    }

    @Test
    @DisplayName("프로젝트를 삭제할 수 있다")
    void deleteProject() {
        // given
        Member member = getMember();
        Project project = new Project("title");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.of(projectMember));

        // when
        projectService.deleteProject(1L, 1L);

        // then
        then(projectMemberRepository).should().deleteAllByProject(project);
        then(backlogRepository).should().deleteByProject(project);
        then(storyRepository).should().deleteByProject(project);
        then(sprintRepository).should().deleteByProject(project);
        then(projectRespository).should().delete(project);
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트를 삭제할 수 없다")
    void deleteProjectFailIfNotExisting() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> projectService.deleteProject(1L, 1L))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트를 삭제할 수 없다")
    void deleteProjectFailIfNotParticipating() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> projectService.deleteProject(1L, 1L))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("관리자 권한이 없는 프로젝트를 삭제할 수 없다.")
    void deleteProjectFailIfNotAdmin() {
        // given
        Member member = getMember();
        Project project = new Project("title");
        ProjectMember projectMember = new ProjectMember(project, member, false);

        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.of(projectMember));

        // when
        // then
        assertThatThrownBy(() -> projectService.deleteProject(1L, 1L))
            .isInstanceOf(NotProjectAdminException.class);
    }

    @Test
    @DisplayName("프로젝트에서 나갈 수 있다.")
    void leaveProject() {
        // given
        Member member = getMember();
        Project project = new Project("title");
        ProjectMember projectMember = new ProjectMember(project, member, true);

        Backlog backlog1 = new Backlog("backlog1", "backlog1", Priority.MEDIUM, project, null, null,
            member);
        Backlog backlog2 = new Backlog("backlog2", "backlog2", Priority.MEDIUM, project, null, null,
            member);
        backlog2.update("backlog2", "backlog2", Status.IN_PROGRESS, Priority.MEDIUM, project, null,
            null, member);
        Backlog backlog3 = new Backlog("backlog3", "backlog3", Priority.MEDIUM, project, null, null,
            member);
        backlog3.update("backlog3", "backlog3", Status.DONE, Priority.MEDIUM, project, null, null,
            member);
        List<Backlog> backlogs = List.of(backlog1, backlog2, backlog3);

        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.of(projectMember));
        given(backlogRepository.findByAssigneeAndProject(member, project))
            .willReturn(backlogs);

        // when
        projectService.leaveProject(1L, 1L);

        // then
        then(projectMemberRepository).should().delete(projectMember);
        assertThat(backlog1.getStatus()).isEqualTo(Status.TODO);
        assertThat(backlog1.getAssignee()).isNull();
        assertThat(backlog2.getStatus()).isEqualTo(Status.TODO);
        assertThat(backlog2.getAssignee()).isNull();
        assertThat(backlog3.getStatus()).isEqualTo(Status.DONE);
        assertThat(backlog3.getAssignee()).isEqualTo(member);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트에서는 나갈 수 없다.")
    void leaveProjectFailIfNotParticipating() {
        // given
        given(projectMemberRepository.findByMemberIdAndProjectId(any(), any()))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> projectService.leaveProject(1L, 1L))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    private static Member getMember() {
        return new Member("email@email.com", "이름", "picture.jpg");
    }
}