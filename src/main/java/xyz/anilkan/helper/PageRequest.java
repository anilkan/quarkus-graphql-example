package xyz.anilkan.helper;

import java.util.UUID;

public class PageRequest {
    private Integer first;
    private UUID after;

    public Integer getFirst() {
        return first;
    }

    public UUID getAfter() {
        return after;
    }

    public static PageRequest of(Integer first, UUID after) {
        final PageRequest pageRequest = new PageRequest();
        pageRequest.first = first;
        pageRequest.after = after;

        return pageRequest;
    }
}
