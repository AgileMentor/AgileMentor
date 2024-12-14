package agilementor.chatgpt.service;

import agilementor.backlog.entity.Backlog;
import agilementor.backlog.entity.Story;
import agilementor.backlog.repository.BacklogRepository;
import agilementor.backlog.repository.StoryRepository;
import agilementor.chatgpt.dto.BacklogResponseDTO;
import agilementor.chatgpt.dto.GPTRequest;
import agilementor.chatgpt.dto.GPTResponse;
import agilementor.chatgpt.dto.GPTResponse.StoryDTO;
import agilementor.chatgpt.dto.ProjectResponseDTO;
import agilementor.chatgpt.dto.SprintResponseDTO;
import agilementor.chatgpt.dto.StoryResponseDTO;
import agilementor.common.exception.NotJsonResponseException;
import agilementor.common.exception.SprintNotFoundException;
import agilementor.common.exception.StoryNotFoundException;
import agilementor.project.entity.Project;
import agilementor.sprint.entity.Sprint;
import agilementor.sprint.repository.SprintRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

@Service
public class GPTService {

    private static final Logger logger = LoggerFactory.getLogger(GPTService.class);
    private final StoryRepository storyRepository;
    private final BacklogRepository backlogRepository;
    private final SprintRepository sprintRepository;
    private final WebClient webClient;

    @Value("${openai.api.key:@null}")
    private String apiKey;

    public GPTService(StoryRepository storyRepository, BacklogRepository backlogRepository,
        SprintRepository sprintRepository, Builder webClientBuilder) {
        this.storyRepository = storyRepository;
        this.backlogRepository = backlogRepository;
        this.sprintRepository = sprintRepository;
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/completions").build();
    }

    public GPTResponse fetchGPTResponse(GPTRequest gptRequest) throws JsonProcessingException {
        String userMessage = generatePrompt(gptRequest);

        // OpenAI API 요청 메시지
        Map<String, Object> requestPayload = Map.of(
            "model", "gpt-4o-mini",
            "messages", List.of(
                Map.of("role", "system", "content",
                    "You are a helpful assistant for agile project management."),
                Map.of("role", "user", "content", userMessage)
            ),
            "max_tokens", 2000,
            "temperature", 0.7
        );

        // 요청 로그
        logger.info("Sending GPT API Request: {}", requestPayload);

        // WebClient로 API 호출
        GPTResponse gptResponse = webClient.post()
            .uri("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .bodyValue(requestPayload)
            .retrieve()
            .bodyToMono(GPTResponse.class)
            .block();

        // 응답 로그
        logger.info("Received GPT API Response: {}", gptResponse);

        String content = gptResponse.getChoices()[0].getMessage().getContent();

        // ```json과 ```를 제거
        String sanitizedResponse = content.replaceAll("^```json|```$", "").trim();
        // 백틱(```) 제거
        String jsonResponse = sanitizedResponse.trim();
        if (jsonResponse.startsWith("```") && jsonResponse.endsWith("```")) {
            jsonResponse = jsonResponse.substring(3, jsonResponse.length() - 3).trim();
        }

        if (!(jsonResponse.startsWith("{") || jsonResponse.startsWith("["))) {
            throw new NotJsonResponseException();
        }

        // JSON 문자열을 DTO로 매핑
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(jsonResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

// stories와 sprints를 개별적으로 매핑
        List<GPTResponse.StoryDTO> stories = objectMapper.convertValue(
            rootNode.get("stories"),
            new TypeReference<List<StoryDTO>>() {
            }
        );

        List<GPTResponse.SprintDTO> sprints = objectMapper.convertValue(
            rootNode.get("sprints"),
            new TypeReference<List<GPTResponse.SprintDTO>>() {
            }
        );

        // DTO에 데이터 설정
        gptResponse.setStories(stories);
        gptResponse.setSprints(sprints);
        return gptResponse;
    }

    private String generatePrompt(GPTRequest gptRequest) {
        return """
            프로젝트 설명: %s
            유저 스토리 개수: %d
            스프린트 개수: %d
            
            다음 형식으로 유저 스토리와 작업(Task)과 스프린트를 생성한 후 json 데이터를 반환하세요.
            1. 유저 스토리: 제목과 설명을 포함하세요.
            2. 작업(Task): 각 유저 스토리에 대해 작업 목록을 적당한 개수로 생성하고, 각 작업에는 제목, 설명, 우선순위(Values: "HIGH", "MEDIUM", "LOW"), 스프린트를 포함하세요. 우선순위는 작업의 중요도에 따라 세 가지 순위 중에 설정하세요.
            3. 스프린트: 제목, 목표를 포함하세요.
            
            주의사항1:스프린트는 하나의 스토리에 국한되지 않습니다. 생성된 작업 목록을 모든 스프린트에 적절히 분배하여 최적의 스프린트 계획을 수립해주세요.
            주의사항2:각 스프린트에는 한 개 이상의 작업이 필수로 할당되어야 합니다.
            
            다음 형식의 순수 JSON 데이터로만 응답하세요:
            {
               "stories": [
                 {
                   "id": 1,
                   "title": "손쉬운 제품 검색",
                   "description": "사용자가 원하는 전자제품을 쉽게 검색할 수 있는 기능 제공",
                   "tasks": [
                     {
                       "title": "검색 엔진 설계",
                       "description": "검색 시 사용자가 입력한 키워드를 기반으로 제품을 표시하는 엔진 설계",
                       "priority": "HIGH",
                       "sprintId": 1
                     },
                     {
                       "title": "필터링 옵션 추가",
                       "description": "가격과 브랜드로 제품을 필터링할 수 있는 기능 개발",
                       "priority": "MEDIUM",
                       "sprintId": 1
                     }
                   ]
                 }
               ],
               "sprints": [
                 {
                   "id": 1,
                   "title": "기초 시스템 구현",
                   "goal": "검색 및 필터링 시스템 구축"
                 },
                 {
                   "id": 2,
                   "title": "UI 개선",
                   "goal": "사용자 경험 향상을 위한 인터페이스 업데이트"
                 }
               ]
             }
            
            
            """
            .formatted(gptRequest.getProjectDescription(), gptRequest.getStoryCount(),
                gptRequest.getSprintCount());
    }

    /**
     * Retrieves a Sprint by its ID.
     *
     * @param sprintId the ID of the Sprint.
     * @return the Sprint entity.
     * @throws IllegalArgumentException if no Sprint is found with the given ID.
     */
    Sprint getSprintById(Long sprintId) {
        return sprintRepository.findById(sprintId)
            .orElseThrow(SprintNotFoundException::new);
    }

    /**
     * Retrieves a Story by its ID.
     *
     * @param storyId the ID of the Story.
     * @return the Story entity.
     * @throws IllegalArgumentException if no Story is found with the given ID.
     */
    Story getStoryById(Long storyId) {
        return storyRepository.findById(storyId)
            .orElseThrow(StoryNotFoundException::new);
    }

    @Transactional
    public ProjectResponseDTO saveGPTResponse(GPTResponse gptResponse, Project project) {

        // Sprint 저장 및 매핑 생성
        Map<Long, Long> sprintIdMapping = saveSprintsAndMapIds(gptResponse, project);

        // Story 저장 및 매핑 생성
        Map<Long, Long> storyIdMapping = saveStoriesAndMapIds(gptResponse, project);

        List<SprintResponseDTO> sprintResponses = saveSprints(gptResponse, project,
            sprintIdMapping);
        List<StoryResponseDTO> storyResponses = saveStories(gptResponse, project, storyIdMapping);
        List<BacklogResponseDTO> backlogResponses = saveBacklogs(gptResponse, project,
            sprintIdMapping, storyIdMapping);

        return new ProjectResponseDTO(storyResponses, sprintResponses, backlogResponses);
    }

    Map<Long, Long> saveSprintsAndMapIds(GPTResponse gptResponse, Project project) {
        Map<Long, Long> sprintIdMapping = new HashMap<>();

        gptResponse.getSprints().forEach(sprintDTO -> {
            Sprint sprint = sprintRepository.save(
                new Sprint(project, sprintDTO.getTitle(), sprintDTO.getGoal(), null, null, false,
                    false));
            sprintIdMapping.put(sprintDTO.getId(), sprint.getId());
        });

        return sprintIdMapping;
    }

    Map<Long, Long> saveStoriesAndMapIds(GPTResponse gptResponse, Project project) {
        Map<Long, Long> storyIdMapping = new HashMap<>();

        gptResponse.getStories().forEach(storyDTO -> {
            Story story = storyRepository.save(
                new Story(project, storyDTO.getTitle(), storyDTO.getDescription()));
            storyIdMapping.put(storyDTO.getId(), story.getStoryId());
        });

        return storyIdMapping;
    }

    private List<SprintResponseDTO> saveSprints(GPTResponse gptResponse, Project project,
        Map<Long, Long> sprintIdMapping) {
        return gptResponse.getSprints().stream().map(sprintDTO -> {
            Long databaseSprintId = sprintIdMapping.get(sprintDTO.getId());

            if (databaseSprintId == null) {
                throw new SprintNotFoundException();
            }

            Sprint sprint = sprintRepository.findById(databaseSprintId)
                .orElseThrow(SprintNotFoundException::new);

            return SprintResponseDTO.from(sprint);
        }).toList();
    }

    private List<StoryResponseDTO> saveStories(GPTResponse gptResponse, Project project,
        Map<Long, Long> storyIdMapping) {
        return gptResponse.getStories().stream().map(storyDTO -> {
            Long databaseStoryId = storyIdMapping.get(storyDTO.getId());

            if (databaseStoryId == null) {
                throw new StoryNotFoundException();
            }

            Story story = storyRepository.findById(databaseStoryId)
                .orElseThrow(StoryNotFoundException::new);

            return StoryResponseDTO.from(story);
        }).toList();
    }

    List<BacklogResponseDTO> saveBacklogs(GPTResponse gptResponse, Project project,
        Map<Long, Long> sprintIdMapping, Map<Long, Long> storyIdMapping) {
        return gptResponse.getStories().stream()
            .flatMap(storyDTO -> storyDTO.getTasks().stream().map(taskDTO -> {
                Long databaseStoryId = storyIdMapping.get(storyDTO.getId());

                if (databaseStoryId == null) {
                    throw new StoryNotFoundException();
                }

                Story story = getStoryById(databaseStoryId);

                Long databaseSprintId = sprintIdMapping.get(taskDTO.getSprintId());

                if (databaseSprintId == null) {
                    throw new SprintNotFoundException();
                }

                Sprint sprint = getSprintById(databaseSprintId);

                Backlog backlog = backlogRepository.save(
                    new Backlog(taskDTO.getTitle(),
                        taskDTO.getDescription(), taskDTO.getPriority(), project, sprint, story,
                        null));

                return BacklogResponseDTO.from(backlog);
            }))
            .toList();
    }
}