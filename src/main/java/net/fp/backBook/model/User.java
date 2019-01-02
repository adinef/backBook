package net.fp.backBook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @Transient
    private static final long serialVersionUID = -6822373555089395874L;

    @Id
    private String id;

    private String name;

    private String lastName;

    private String login;

    private String password;

    @DBRef
    private List<Role> roles;
}
