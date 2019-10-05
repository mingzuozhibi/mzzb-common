package mingzuozhibi.common.security;

import com.google.gson.JsonObject;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SimpleUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    public static SimpleUserDetails ofJsonObject(JsonObject userObj) {
        SimpleUserDetails userDetails = new SimpleUserDetails();
        userDetails.setUsername(userObj.get("username").getAsString());
        userDetails.setEnabled(userObj.get("enabled").getAsBoolean());
        userObj.get("roles").getAsJsonArray().forEach(role -> {
            String authority = "ROLE_" + role.getAsString();
            userDetails.getAuthorities().add(new SimpleAuthority(authority));
        });
        return userDetails;
    }

    private String username;
    private boolean enabled;
    private Set<SimpleAuthority> authorities = new HashSet<>();

    @Ignore
    public String getPassword() {
        return null;
    }

    @Ignore
    public boolean isAccountNonExpired() {
        return false;
    }

    @Ignore
    public boolean isAccountNonLocked() {
        return false;
    }

    @Ignore
    public boolean isCredentialsNonExpired() {
        return false;
    }

}
