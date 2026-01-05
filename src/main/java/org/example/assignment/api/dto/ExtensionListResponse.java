package org.example.assignment.api.dto;

import java.util.List;

public record ExtensionListResponse(
        List<FixedItem> fixed,
        CustomSection custom
) {
    public record FixedItem(String name, boolean blocked) {}
    public record CustomItem(String name) {}
    public record CustomSection(int count, int limit, List<CustomItem> items) {}
}
