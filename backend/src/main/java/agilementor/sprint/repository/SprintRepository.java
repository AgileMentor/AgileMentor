package agilementor.sprint.repository;

import agilementor.project.entity.Project;
import agilementor.sprint.entity.Sprint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    // 특정 프로젝트와 특정 스프린트 ID 조회
    Optional<Sprint> findByProject_ProjectIdAndId(Long projectId, Long sprintId);

    // 프로젝트 ID로 스프린트 개수를 카운트
    long countByProject_ProjectId(Long projectId);

    // 활성 스프린트 조회
    Optional<Sprint> findByProjectAndIsActivateTrue(Project project);

    // 특정 프로젝트의 isdone이 false인 모든 스프린트 조회
    List<Sprint> findByProject_ProjectIdAndIsDoneFalse(Long projectId);

    List<Sprint> findByProject_ProjectIdAndIsDoneTrueOrderByEndDateAsc(Long projectId);

    void deleteByProject(Project project);
}
