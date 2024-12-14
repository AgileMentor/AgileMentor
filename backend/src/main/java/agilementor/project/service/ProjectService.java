package agilementor.project.service;

import agilementor.backlog.entity.Backlog;
import agilementor.backlog.repository.BacklogRepository;
import agilementor.backlog.repository.StoryRepository;
import agilementor.common.exception.MemberNotFoundException;
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
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProjectService {

    private final MemberRepository memberRepository;
    private final ProjectRespository projectRespository;
    private final ProjectMemberRepository projectMemberRepository;
    private final BacklogRepository backlogRepository;
    private final StoryRepository storyRepository;
    private final SprintRepository sprintRepository;

    public ProjectService(MemberRepository memberRepository, ProjectRespository projectRespository,
        ProjectMemberRepository projectMemberRepository, BacklogRepository backlogRepository,
        StoryRepository storyRepository, SprintRepository sprintRepository) {
        this.memberRepository = memberRepository;
        this.projectRespository = projectRespository;
        this.projectMemberRepository = projectMemberRepository;
        this.backlogRepository = backlogRepository;
        this.storyRepository = storyRepository;
        this.sprintRepository = sprintRepository;
    }

    public ProjectResponse createProject(Long memberId, ProjectCreateRequest projectCreateRequest) {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);
        Project project = projectRespository.save(projectCreateRequest.toEntity());
        projectMemberRepository.save(new ProjectMember(project, member, true));

        return ProjectResponse.from(project);
    }

    public List<ProjectResponse> getProjectList(Long memberId) {

        List<ProjectMember> projectMemberList = projectMemberRepository.findByMemberId(memberId);
        return projectMemberList.stream()
            .map(projectMember -> ProjectResponse.from(projectMember.getProject()))
            .toList();
    }

    public ProjectResponse getProject(Long memberId, Long projectId) {

        ProjectMember projectMember = getProjectMember(memberId, projectId);

        return ProjectResponse.from(projectMember.getProject());
    }

    public ProjectResponse updateProject(Long memberId, Long projectId,
        ProjectUpdateRequest projectUpdateRequest) {

        ProjectMember projectMember = getProjectMember(memberId, projectId);

        if (projectMember.isNotAdmin()) {
            throw new NotProjectAdminException();
        }

        Project project = projectMember.getProject();
        project.update(projectUpdateRequest.title());
        return ProjectResponse.from(project);
    }

    public void deleteProject(Long memberId, Long projectId) {

        ProjectMember projectMember = getProjectMember(memberId, projectId);

        if (projectMember.isNotAdmin()) {
            throw new NotProjectAdminException();
        }

        Project project = projectMember.getProject();

        backlogRepository.deleteByProject(project);
        storyRepository.deleteByProject(project);
        sprintRepository.deleteByProject(project);

        projectMemberRepository.deleteAllByProject(project);
        projectRespository.delete(project);
    }

    public void leaveProject(Long memberId, Long projectId) {

        ProjectMember projectMember = getProjectMember(memberId, projectId);

        Member member = projectMember.getMember();
        Project project = projectMember.getProject();

        List<Backlog> backlogList = backlogRepository.findByAssigneeAndProject(member, project);
        backlogList.stream()
            .filter(Backlog::isNotDone)
            .forEach(Backlog::deleteAssignee);

        projectMemberRepository.delete(projectMember);
    }

    private ProjectMember getProjectMember(Long memberId, Long projectId) {
        return projectMemberRepository
            .findByMemberIdAndProjectId(memberId, projectId)
            .orElseThrow(ProjectNotFoundException::new);
    }
}
