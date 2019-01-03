package net.fp.backBook.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private String id;

    private String name;

    private String lastName;

    private String login;

    private String password;

    private String email;
}
