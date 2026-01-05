package org.example.assignment.service;

import lombok.RequiredArgsConstructor;
import org.example.assignment.api.dto.ExtensionListResponse;
import org.example.assignment.api.dto.FileCheckResponse;
import org.example.assignment.domain.Extension;
import org.example.assignment.exception.AppException;
import org.example.assignment.exception.ErrorCode;
import org.example.assignment.repository.ExtensionRepository;
import org.example.assignment.util.FileNameUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExtensionService {

    private static final int CUSTOM_LIMIT = 200;

    private final ExtensionRepository extensionRepository;

    @Transactional(readOnly = true)
    public ExtensionListResponse getAll() {
        List<Extension> fixed = extensionRepository.findByFixedTrueOrderByNameAsc();
        List<Extension> custom = extensionRepository.findByFixedFalseOrderByCreatedAtDesc();

        return new ExtensionListResponse(
                fixed.stream()
                        .map(e -> new ExtensionListResponse.FixedItem(e.getName(), e.isBlocked()))
                        .toList(),
                new ExtensionListResponse.CustomSection(
                        custom.size(),
                        CUSTOM_LIMIT,
                        custom.stream()
                                .map(e -> new ExtensionListResponse.CustomItem(e.getName()))
                                .toList()
                )
        );
    }

    public void toggleFixedBlocked(String name) {
        String normalized = normalize(name);

        Extension ext = extensionRepository.findByName(normalized)
                .orElseThrow(() -> new AppException(
                        ErrorCode.EXTENSION_NOT_FOUND,
                        "extension not found: " + normalized
                ));

        if (!ext.isFixed()) {
            throw new AppException(ErrorCode.FIXED_EXTENSION_TOGGLE_NOT_ALLOWED);
        }

        // 낙관락 충돌(ObjectOptimisticLockingFailureException)은 글로벌에서 409로 변환
        ext.toggleBlocked();
    }

    public void addCustom(String name) {
        String normalized = normalize(name);

        validateExtensionName(normalized);

        if (extensionRepository.existsByName(normalized)) {
            throw new AppException(ErrorCode.DUPLICATE_EXTENSION);
        }

        long customCount = extensionRepository.countByFixedFalse();
        if (customCount >= CUSTOM_LIMIT) {
            throw new AppException(ErrorCode.CUSTOM_EXTENSION_LIMIT_EXCEEDED);
        }

        // 커스텀 확장자는 “차단 리스트 추가” 성격 → blocked=true로 시작
        extensionRepository.save(new Extension(normalized, false, true));
    }

    public void deleteCustom(String name) {
        String normalized = normalize(name);

        Extension ext = extensionRepository.findByName(normalized)
                .orElseThrow(() -> new AppException(
                        ErrorCode.EXTENSION_NOT_FOUND,
                        "extension not found: " + normalized
                ));

        if (ext.isFixed()) {
            throw new AppException(ErrorCode.FIXED_EXTENSION_DELETE_NOT_ALLOWED);
        }

        // 낙관락 충돌(ObjectOptimisticLockingFailureException)은 글로벌에서 409로 변환
        extensionRepository.delete(ext);
    }

    @Transactional(readOnly = true)
    public FileCheckResponse checkFile(String filename) {
        String derived = FileNameUtil.extractLastExtension(filename);

        if (derived.isBlank()) {
            return FileCheckResponse.deny("", "NO_EXTENSION");
        }

        return extensionRepository.findByName(derived)
                .filter(Extension::isBlocked)
                .map(e -> FileCheckResponse.deny(derived, "BLOCKED"))
                .orElseGet(() -> FileCheckResponse.ok(derived));
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private static void validateExtensionName(String normalized) {
        // 1~20, 공백/점 제외 (요구사항 맞춘 최소 검증)
        if (normalized.isBlank() || normalized.length() > 20) {
            throw new AppException(ErrorCode.INVALID_EXTENSION_NAME);
        }
        if (normalized.contains(" ") || normalized.contains(".")) {
            throw new AppException(ErrorCode.INVALID_EXTENSION_NAME);
        }
    }
}
