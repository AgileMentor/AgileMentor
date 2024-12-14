package agilementor.backlog.repository;

import agilementor.backlog.entity.Story;
import agilementor.project.entity.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Long> {

    List<Story> findByProject(Project project);

    Optional<Story> findByStoryIdAndProject(Long storyId, Project project);

    void deleteByProject(Project project);
}
