package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
@Slf4j
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> putItem(ItemDto item, Long userId) {
        log.info("ItemClient putItem {}", item);
        return post("", userId, item);

    }

    public ResponseEntity<Object> updateItem(Long itemId,
                                             Long userId,
                                             ItemDto item) {
        log.info("itemClient updateItem {}", item);
        return patch("/" + itemId, userId, item);
    }


    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemOfUser(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        log.info("ItemClient getItemOfUser");
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getSearch(String text,
                                            Integer from,
                                            Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> putComment(Long itemId,
                                             Long userId,
                                             CommentDto comment) {
        log.info("ItemClient putComment {}", comment);
        return post("/" + itemId + "/comment", userId, comment);
    }
}
