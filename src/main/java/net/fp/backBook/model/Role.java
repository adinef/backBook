package net.fp.backBook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role implements Serializable, GrantedAuthority {
    @Transient
    private static final long serialVersionUID = 32994189510287583L;

    @Id
    private String id;

    private String name;

    @Override
    public String getAuthority() {
        return this.name;
    }
}
