package agilementor.project.service;

import agilementor.backlog.entity.Backlog;
import agilementor.backlog.repository.BacklogRepository;
import agilementor.common.exception.AlreadyJoinedMemberException;
import agilementor.common.exception.KickOneselfException;
import agilementor.common.exception.MemberNotFoundException;
import agilementor.common.exception.NotProjectAdminException;
import agilementor.common.exception.ProjectNotFoundException;
import agilementor.member.entity.Member;
import agilementor.member.repository.MemberRepository;
import agilementor.project.dto.request.ProjectInviteRequest;
import agilementor.project.dto.response.ProejctMemberResponse;
import agilementor.project.entity.Invitation;
import agilementor.project.entity.Project;
import agilementor.project.entity.ProjectMember;
import agilementor.project.repository.InvitationRepository;
import agilementor.project.repository.ProjectMemberRepository;
import agilementor.project.repository.ProjectRespository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProjectMemberService {

    private final MemberRepository memberRepository;
    private final ProjectRespository projectRespository;
    private final ProjectMemberRepository projectMemberRepository;
    private final InvitationRepository invitationRepository;
    private final BacklogRepository backlogRepository;

    public ProjectMemberService(MemberRepository memberRepository,
        ProjectRespository projectRespository, ProjectMemberRepository projectMemberRepository,
        InvitationRepository invitationRepository, BacklogRepository backlogRepository) {
        this.memberRepository = memberRepository;
        this.projectRespository = projectRespository;
        this.projectMemberRepository = projectMemberRepository;
        this.invitationRepository = invitationRepository;
        this.backlogRepository = backlogRepository;
    }

    public List<ProejctMemberResponse> getProjectMemberList(Long memberId, Long projectId) {

        List<ProjectMember> projectMemberList = projectMemberRepository.findByProjectId(projectId);

        boolean isNotMemberOfProject = projectMemberList.stream()
            .noneMatch(projectMember -> projectMember.getMember().getMemberId().equals(memberId));

        if (isNotMemberOfProject) {
            throw new ProjectNotFoundException();
        }

        return projectMemberList.stream()
            .map(ProejctMemberResponse::from)
            .toList();
    }

    public void kickMember(Long loginMemberId, Long projectId, Long targetMemberId) {

        List<ProjectMember> projectMemberList = projectMemberRepository.findByProjectId(projectId);

        if (projectMemberList.isEmpty()) {
            throw new ProjectNotFoundException();
        }

        checkAdmin(loginMemberId, projectMemberList);

        if (loginMemberId.equals(targetMemberId)) {
            throw new KickOneselfException();
        }

        ProjectMember targetProjectMember = projectMemberList.stream()
            .filter(projectMember -> projectMember.getMember().getMemberId().equals(targetMemberId))
            .findFirst()
            .orElseThrow(MemberNotFoundException::new);

        Member member = targetProjectMember.getMember();
        Project project = targetProjectMember.getProject();

        List<Backlog> backlogList = backlogRepository.findByAssigneeAndProject(member, project);
        backlogList.stream()
            .filter(Backlog::isNotDone)
            .forEach(Backlog::deleteAssignee);

        projectMemberRepository.delete(targetProjectMember);
    }

    public void inviteMember(Long loginMemberId, Long projectId,
        ProjectInviteRequest projectInviteRequest) {
        String targetEmail = projectInviteRequest.email();

        List<ProjectMember> projectMemberList = projectMemberRepository.findByProjectId(projectId);

        if (projectMemberList.isEmpty()) {
            throw new ProjectNotFoundException();
        }

        checkAdmin(loginMemberId, projectMemberList);

        boolean isAlreadyMember = projectMemberList.stream()
            .anyMatch(projectMember -> projectMember.getMember().getEmail().equals(targetEmail));

        if (isAlreadyMember) {
            throw new AlreadyJoinedMemberException();
        }

        Optional<Member> target = memberRepository.findByEmail(targetEmail);
        if (target.isEmpty()) {
            // 해당 이메일을 가진 회원이 없다면 실제 초대 작업 안 함
            // 가입한 회원인지 알 수 없도록 성공 응답 함
            // 추후 초대 메일을 보내거나 가입한 회원이 아니라고 응답하는식으로 수정 가능
            // 초대 메일을 보낸다면 비회원 초대 내역도 저장하도록 수정 필요
            return;
        }

        Project project = projectRespository.findById(projectId)
            .orElseThrow(ProjectNotFoundException::new);

        boolean isAlreadyInvited = invitationRepository
            .existsByProjectAndInvitee(project, target.get());

        if (isAlreadyInvited) {
            // 이미 초대된 회원일 경우에도 아무 작업 안 하고 성공 응답.
            return;
        }

        Member invitor = memberRepository.findById(loginMemberId)
            .orElseThrow(MemberNotFoundException::new);

        Invitation invitation = new Invitation(project, target.get(), invitor);
        invitationRepository.save(invitation);
    }

    private static void checkAdmin(Long loginMemberId, List<ProjectMember> projectMemberList) {
        ProjectMember loginProjectMember = projectMemberList.stream()
            .filter(projectMember -> projectMember.getMember().getMemberId().equals(loginMemberId))
            .findFirst()
            .orElseThrow(MemberNotFoundException::new);

        if (loginProjectMember.isNotAdmin()) {
            throw new NotProjectAdminException();
        }
    }
}
