package mingzuozhibi.common.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleAuthority implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    private String authority;

}
