package agilementor.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import agilementor.backlog.entity.Backlog;
import agilementor.backlog.entity.Priority;
import agilementor.backlog.entity.Status;
import agilementor.backlog.repository.BacklogRepository;
import agilementor.common.exception.AlreadyJoinedMemberException;
import agilementor.common.exception.KickOneselfException;
import agilementor.common.exception.MemberNotFoundException;
import agilementor.common.exception.NotProjectAdminException;
import agilementor.common.exception.ProjectNotFoundException;
import agilementor.member.entity.Member;
import agilementor.member.repository.MemberRepository;
import agilementor.project.dto.request.ProjectInviteRequest;
import agilementor.project.entity.Project;
import agilementor.project.entity.ProjectMember;
import agilementor.project.repository.InvitationRepository;
import agilementor.project.repository.ProjectMemberRepository;
import agilementor.project.repository.ProjectRespository;
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
class ProjectMemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectRespository projectRespository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private BacklogRepository backlogRepository;

    @InjectMocks
    private ProjectMemberService projectMemberService;

    private static Long ADMIN_MEMBER_ID = 1L;
    private static Long MEMBER_ID_1 = 2L;
    private static Long MEMBER_ID_2 = 3L;
    private static Long OTHER_MEMBER_ID = 10L;
    private static Long PROJECT_ID = 1L;
    private static Long OTHER_PROJECT_ID = 2L;
    private static String OTHER_MEMBER_EMAIL = "otherMember@email.com";
    private static String MEMBER_EMAIL_1 = "member@email.com";

    @Test
    @DisplayName("프로젝트에 참가한 회원 목록을 조회할 수 있다.")
    void getProjectMemberList() {
        // given
        List<ProjectMember> projectMemberList = createProjectMemberList();

        given(projectMemberRepository.findByProjectId(PROJECT_ID)).willReturn(projectMemberList);

        // when
        var actual = projectMemberService.getProjectMemberList(ADMIN_MEMBER_ID, PROJECT_ID);

        // then
        assertThat(actual.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트의 회원 목록은 조회할 수 없다.")
    void getProjectMemberListFailIfNotExisting() {
        // given
        List<ProjectMember> projectMemberList = List.of();

        given(projectMemberRepository.findByProjectId(any())).willReturn(projectMemberList);

        // when
        // then
        assertThatThrownBy(
            () -> projectMemberService.getProjectMemberList(MEMBER_ID_1, OTHER_PROJECT_ID))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("참가하지 않은 프로젝트의 회원 목록은 조회할 수 없다.")
    void getProjectMemberListFailIfNotParticipating() {
        // given
        List<ProjectMember> projectMemberList = createProjectMemberList();

        given(projectMemberRepository.findByProjectId(PROJECT_ID)).willReturn(projectMemberList);

        // when
        // then
        assertThatThrownBy(
            () -> projectMemberService.getProjectMemberList(OTHER_MEMBER_ID, PROJECT_ID))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("프로젝트 관리자는 프로젝트의 회원을 추방할 수 있다.")
    void kickMember() {
        // given
        List<ProjectMember> projectMemberList = createProjectMemberList();

        Project project = projectMemberList.getFirst().getProject();
        Member target = projectMemberList.get(1).getMember();
        Long targetMemberId = target.getMemberId();

        Backlog backlog1 = new Backlog("backlog1", "backlog1", Priority.MEDIUM, project, null, null,
            target);
        Backlog backlog2 = new Backlog("backlog2", "backlog2", Priority.MEDIUM, project, null, null,
            target);
        backlog2.update("backlog2", "backlog2", Status.IN_PROGRESS, Priority.MEDIUM, project, null,
            null, target);
        Backlog backlog3 = new Backlog("backlog3", "backlog3", Priority.MEDIUM, project, null, null,
            target);
        backlog3.update("backlog3", "backlog3", Status.DONE, Priority.MEDIUM, project, null, null,
            target);
        List<Backlog> backlogs = List.of(backlog1, backlog2, backlog3);

        given(projectMemberRepository.findByProjectId(PROJECT_ID)).willReturn(projectMemberList);
        given(backlogRepository.findByAssigneeAndProject(target, project))
            .willReturn(backlogs);

        // when
        projectMemberService.kickMember(ADMIN_MEMBER_ID, PROJECT_ID, targetMemberId);

        // then
        then(projectMemberRepository).should().delete(any());
        assertThat(backlog1.getStatus()).isEqualTo(Status.TODO);
        assertThat(backlog1.getAssignee()).isNull();
        assertThat(backlog2.getStatus()).isEqualTo(Status.TODO);
        assertThat(backlog2.getAssignee()).isNull();
        assertThat(backlog3.getStatus()).isEqualTo(Status.DONE);
        assertThat(backlog3.getAssignee()).isEqualTo(target);
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트의 회원을 추방할 수 없다.")
    void kickMemberFailIfNotExisting() {
        // given
        List<ProjectMember> projectMemberList = List.of();

        given(projectMemberRepository.findByProjectId(PROJECT_ID)).willReturn(projectMemberList);

        // when
        // then
        assertThatThrownBy(
            () -> projectMemberService.kickMember(MEMBER_ID_1, PROJECT_ID, MEMBER_ID_2))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("프로젝트 관리자가 아니면 프로젝트의 회원을 추방할 수 없다.")
    void kickMemberFailIfNotAdmin() {
        // given
        List<ProjectMember> projectMemberList = createProjectMemberList();

        given(projectMemberRepository.findByProjectId(PROJECT_ID)).willReturn(projectMemberList);

        // when
        // then
        assertThatThrownBy(
            () -> projectMemberService.kickMember(MEMBER_ID_1, PROJECT_ID, MEMBER_ID_2))
            .isInstanceOf(NotProjectAdminException.class);
    }

    @Test
    @DisplayName("프로젝트 관리자가 스스로를 추방할 수 없다.")
    void kickOneSelfFail() {
        // given
        List<ProjectMember> projectMemberList = createProjectMemberList();

        given(projectMemberRepository.findByProjectId(PROJECT_ID)).willReturn(projectMemberList);

        // when
        // then
        assertThatThrownBy(
            () -> projectMemberService.kickMember(ADMIN_MEMBER_ID, PROJECT_ID, ADMIN_MEMBER_ID))
            .isInstanceOf(KickOneselfException.class);
    }

    @Test
    @DisplayName("프로젝트의 회원이 아닌 사람을 추방할 수 없다.")
    void kickMemberFailIfNotParticipating() {
        // given
        List<ProjectMember> projectMemberList = createProjectMemberList();

        given(projectMemberRepository.findByProjectId(PROJECT_ID)).willReturn(projectMemberList);

        // when
        // then
        assertThatThrownBy(
            () -> projectMemberService.kickMember(ADMIN_MEMBER_ID, PROJECT_ID, OTHER_MEMBER_ID))
            .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("프로젝트 관리자는 프로젝트에 새로운 회원을 초대할 수 있다.")
    void inviteMember() {
        // given
        ProjectInviteRequest inviteRequest = new ProjectInviteRequest(OTHER_MEMBER_EMAIL);

        List<ProjectMember> projectMemberList = createProjectMemberList();
        Member targetMember = new Member(OTHER_MEMBER_EMAIL, "새 회원", "pic.jpg");
        Member invitor = new Member("admin@email.com", "관리자", "pic.jpg");
        ReflectionTestUtils.setField(targetMember, "memberId", OTHER_MEMBER_ID);
        ReflectionTestUtils.setField(invitor, "memberId", ADMIN_MEMBER_ID);
        Project project = new Project("title");

        given(projectMemberRepository.findByProjectId(PROJECT_ID)).willReturn(projectMemberList);
        given(memberRepository.findByEmail(OTHER_MEMBER_EMAIL))
            .willReturn(Optional.of(targetMember));
        given(projectRespository.findById(PROJECT_ID))
            .willReturn(Optional.of(project));
        given(invitationRepository.existsByProjectAndInvitee(project, targetMember))
            .willReturn(false);
        given(memberRepository.findById(ADMIN_MEMBER_ID))
            .willReturn(Optional.of(invitor));

        // when
        projectMemberService.inviteMember(ADMIN_MEMBER_ID, PROJECT_ID, inviteRequest);

        // then
        then(invitationRepository).should().save(any());
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트에 새로운 회원을 초대할 수 없다.")
    void inviteMemberFailIfNotExisting() {
        // given
        ProjectInviteRequest inviteRequest = new ProjectInviteRequest(OTHER_MEMBER_EMAIL);

        given(projectMemberRepository.findByProjectId(PROJECT_ID)).willReturn(List.of());

        // when
        // then
        assertThatThrownBy(
            () -> projectMemberService.inviteMember(ADMIN_MEMBER_ID, PROJECT_ID, inviteRequest))
            .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    @DisplayName("프로젝트 관리자가 아니면 프로젝트에 새로운 회원을 초대할 수 없다.")
    void inviteMemberFailIfNotAdmin() {
        // given
        List<ProjectMember> projectMemberList = createProjectMemberList();

        ProjectInviteRequest inviteRequest = new ProjectInviteRequest(OTHER_MEMBER_EMAIL);

        given(projectMemberRepository.findByProjectId(PROJECT_ID)).willReturn(projectMemberList);

        // when
        // then
        assertThatThrownBy(
            () -> projectMemberService.inviteMember(MEMBER_ID_1, PROJECT_ID, inviteRequest))
            .isInstanceOf(NotProjectAdminException.class);
    }

    @Test
    @DisplayName("프로젝트에 이미 참가한 회원을 초대할 수 없다.")
    void inviteMemberFailIfAlreadyJoined() {
        // given
        List<ProjectMember> projectMemberList = createProjectMemberList();

        ProjectInviteRequest inviteRequest = new ProjectInviteRequest(MEMBER_EMAIL_1);

        given(projectMemberRepository.findByProjectId(PROJECT_ID)).willReturn(projectMemberList);

        // when
        // then
        assertThatThrownBy(
            () -> projectMemberService.inviteMember(ADMIN_MEMBER_ID, PROJECT_ID, inviteRequest))
            .isInstanceOf(AlreadyJoinedMemberException.class);
    }

    @Test
    @DisplayName("잘못된 이메일로 초대하면 아무 작업도 하지 않는다.")
    void inviteMemberFailIfUnknownEmail() {
        // given
        String unknownEmail = "unknown@email.com";

        List<ProjectMember> projectMemberList = createProjectMemberList();

        ProjectInviteRequest inviteRequest = new ProjectInviteRequest(unknownEmail);

        given(projectMemberRepository.findByProjectId(PROJECT_ID)).willReturn(projectMemberList);
        given(memberRepository.findByEmail(unknownEmail))
            .willReturn(Optional.empty());

        // when
        projectMemberService.inviteMember(ADMIN_MEMBER_ID, PROJECT_ID, inviteRequest);

        // then
        then(invitationRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("이미 초대된 회원을 다시 초대하면 아무 작업도 하지 않는다.")
    void inviteMemberFailIfAlreadyInvited() {
        // Given

        ProjectInviteRequest inviteRequest = new ProjectInviteRequest(OTHER_MEMBER_EMAIL);
        Member targetMember = new Member(OTHER_MEMBER_EMAIL, "새 회원", "pic.jpg");
        ReflectionTestUtils.setField(targetMember, "memberId", OTHER_MEMBER_ID);
        List<ProjectMember> projectMemberList = createProjectMemberList();
        Project project = new Project("title");

        given(projectMemberRepository.findByProjectId(PROJECT_ID)).willReturn(projectMemberList);
        given(memberRepository.findByEmail(OTHER_MEMBER_EMAIL))
            .willReturn(Optional.of(targetMember));
        given(projectRespository.findById(PROJECT_ID))
            .willReturn(Optional.of(project));
        given(invitationRepository.existsByProjectAndInvitee(project, targetMember))
            .willReturn(true);

        // When
        projectMemberService.inviteMember(ADMIN_MEMBER_ID, PROJECT_ID, inviteRequest);

        // Then
        then(invitationRepository).should(never()).save(any());
    }

    private List<ProjectMember> createProjectMemberList() {
        Member member1 = new Member("admin@email.com", "name", "pic.jpg");
        Member member2 = new Member(MEMBER_EMAIL_1, "name", "pic.jpg");
        Member member3 = new Member("email3@email.com", "name", "pic.jpg");
        Project project = new Project("project");
        ProjectMember projectMember1 = new ProjectMember(project, member1, true);
        ProjectMember projectMember2 = new ProjectMember(project, member2, false);
        ProjectMember projectMember3 = new ProjectMember(project, member3, false);

        ReflectionTestUtils.setField(member1, "memberId", ADMIN_MEMBER_ID);
        ReflectionTestUtils.setField(member2, "memberId", MEMBER_ID_1);
        ReflectionTestUtils.setField(member3, "memberId", MEMBER_ID_2);
        ReflectionTestUtils.setField(project, "projectId", PROJECT_ID);

        return List.of(projectMember1, projectMember2, projectMember3);
    }
}