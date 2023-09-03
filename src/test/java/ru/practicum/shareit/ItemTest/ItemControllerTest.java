package ru.practicum.shareit.ItemTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;
    @Autowired
    private MockMvc mvc;

    private UserDto userDto;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private final String requestHeader = "X-Sharer-User-Id";
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        userDto = UserDto.builder().id(1L).name("Fff").email("pf@mail.ru").build();
        commentDto = CommentDto.builder().text("blabla").id(1L).build();
        itemDto = ItemDto.builder().available(true).name("Name").description("description").owner(userDto).build();
    }

    @Test
    void testSaveNewItem() throws Exception {
        when(itemService.putItem(any(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(requestHeader, 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())));
    }

    @Test
    void testPostComment() throws Exception {
        when(itemService.putComment(anyLong(), anyLong(), any(), any()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(requestHeader, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }

    @Test
    void testGetAllItems() throws Exception {
        when(itemService.getItemOfUser(anyLong(), any(), any()))
                .thenReturn(List.of(itemDto));
        mvc.perform(get("/items").header(requestHeader, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())));

        when(itemService.getItemOfUser(anyLong(), any(), any()))
                .thenThrow(NotFoundException.class);
        mvc.perform(get("/items").header(requestHeader, 1))
                .andExpect(status().isNotFound());
        mvc.perform(get("/items?from=-1&size=10").header(requestHeader, 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(itemDto);
        mvc.perform(patch("/items/1").content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(requestHeader, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        when(itemService.updateItem(anyLong(), anyLong(), any())).thenThrow(NotFoundException.class);
        mvc.perform(patch("/items/1").content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(requestHeader, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);
        mvc.perform(get("/items/1").header(requestHeader, 1L))
                .andExpect(status().isOk());

        when(itemService.getItemById(anyLong(), anyLong())).thenThrow(NotFoundException.class);
        mvc.perform(get("/items/1").header(requestHeader, 1L))
                .andExpect(status().isNotFound());

    }

    @Test
    void testSearch() throws Exception {
        mvc.perform(get("/items/search?text=&from=-1&size=10"))
                .andExpect(status().isBadRequest());
    }
}
