package org.example.assignment.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.assignment.api.dto.*;
import org.example.assignment.service.ExtensionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ExtensionController {

    private final ExtensionService extensionService;

    @GetMapping("/extensions")
    public ExtensionListResponse getAll() {
        return extensionService.getAll();
    }

    @PatchMapping("/extensions/fixed/{name}/toggle")
    public ResponseEntity<Void> toggleFixed(@PathVariable String name) {
        extensionService.toggleFixedBlocked(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/extensions/custom")
    public ResponseEntity<Void> addCustom(@Valid @RequestBody AddCustomExtensionRequest request) {
        extensionService.addCustom(request.name());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/extensions/custom/{name}")
    public ResponseEntity<Void> deleteCustom(@PathVariable String name) {
        extensionService.deleteCustom(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/files/check")
    public FileCheckResponse check(@Valid @RequestBody FileCheckRequest request) {
        return extensionService.checkFile(request.filename());
    }
}
