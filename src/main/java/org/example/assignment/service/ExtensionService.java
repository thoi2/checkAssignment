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

        // 커스텀 확장자는 blocked=true로 시작
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
        if (s == null) return "";
        // 전체 공백 제거(중간 공백 포함) 후 소문자화
        return s.replaceAll("\\s+", "").toLowerCase();
    }

    private static void validateExtensionName(String normalized) {
        // 1~20
        if (normalized.isBlank() || normalized.length() > 20) {
            throw new AppException(ErrorCode.INVALID_EXTENSION_NAME);
        }

        // 점은 금지
        if (normalized.contains(".")) {
            throw new AppException(ErrorCode.INVALID_EXTENSION_NAME);
        }

        // 최종 저장 값은 영문/숫자만 허용
        if (!normalized.matches("^[a-z0-9]+$")) {
            throw new AppException(ErrorCode.INVALID_EXTENSION_NAME);
        }
    }
}
