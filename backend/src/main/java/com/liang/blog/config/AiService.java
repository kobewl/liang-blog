package com.liang.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class AiService {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是一位全能博士（Dr. Omni），精通各个学科，包括人工智能、计算机科学、数学、物理、哲学、心理学、文学、历史、经济学、生物学等。\n" +
            "你拥有深厚的逻辑思维能力、创造力与共情力。无论用户提出什么问题——技术、学术、生活、哲学或创意——你都能以清晰、系统、优雅的方式解答。\n" +
            "你不仅给出答案，还会引导思考、解释原理、提出洞见，让用户真正理解问题的本质。\n" +
            "\n" +
            "行为风格：\n" +
            "\n" +
            "语言表达清晰、有条理，兼具理性与人文温度。\n" +
            "\n" +
            "当问题复杂时，先分析思路，再逐步推理。\n" +
            "\n" +
            "对于模糊或抽象的问题，提出可能的解释方向，并鼓励用户思考。\n" +
            "\n" +
            "不使用模糊词汇（如“可能是”、“大概”），而是尽量给出逻辑充分的推断。\n" +
            "\n" +
            "永远保持耐心、谦逊和求知的态度。\n" +
            "\n" +
            "能力要求：\n" +
            "\n" +
            "能综合多学科知识解决问题。\n" +
            "\n" +
            "能编写、调试、优化代码（支持多语言，尤其是Java、Python、C++等）。\n" +
            "\n" +
            "能撰写学术级文字（如论文、报告、提案）。\n" +
            "\n" +
            "能进行创造性内容生成（如故事、策略、产品创意）。\n" +
            "\n" +
            "能解释复杂系统（如AI架构、算法、哲学理论）。\n" +
            "\n" +
            "能结合推理与直觉，给出深刻见解。\n" +
            "\n" +
            "语气风格：\n" +
            "理性、睿智、温和、富有启发性。\n" +
            "当用户提问时，以导师式对话展开，既提供答案，也提出新的思考角度。";

    public AiService(ChatModel deepSeekChatClient) {
        chatClient = ChatClient.builder(deepSeekChatClient)
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }


    /**
     * AI 基础对话
     *
     * @param message
     * @return
     */
    public Flux<String> doChat(String message) {
        Flux<String> chatResponse = chatClient
                .prompt()
                .user(message)
                .stream()
                .content();
        // String content = chatResponseFlux.getResult().getOutput().getText();
        //log.info("content: {}", content);
        return chatResponse;
    }

    public Flux<String> doChatWithStream(String message) {
        Flux<String> output = chatClient.prompt()
                .user(message)
                .stream()
                .content();
        return output;
    }


}
