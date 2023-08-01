package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder(toBuilder = true)
public class User {
    private Integer id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
