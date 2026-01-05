package org.example.assignment.config;

import lombok.RequiredArgsConstructor;
import org.example.assignment.domain.Extension;
import org.example.assignment.repository.ExtensionRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "app.seed.extensions",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class ExtensionSeedRunner implements ApplicationRunner {

    private final ExtensionRepository extensionRepository;
    // 고정 확장자 목록
    private static final List<String> FIXED = List.of(
            "bat", "cmd", "com", "cpl", "exe", "scr", "js"
    );


    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (String name : FIXED) {
            // Extension 생성자에서 trim/lowercase 정규화
            String normalized = name.trim().toLowerCase();

            if (extensionRepository.existsByName(normalized)) {
                continue;
            }

            // 고정 확장자는 default unCheck로 시작
            extensionRepository.save(new Extension(normalized, true, false));
        }
    }
}
