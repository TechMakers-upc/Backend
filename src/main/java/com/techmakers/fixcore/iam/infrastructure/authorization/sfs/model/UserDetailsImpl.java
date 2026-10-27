package com.techmakers.fixcore.iam.infrastructure.authorization.sfs.model;

import com.techmakers.fixcore.iam.domain.model.aggregates.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Long plantId;
    private final List<GrantedAuthority> authorities;

    private UserDetailsImpl(Long id, String username, String password, Long plantId, List<GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.plantId = plantId;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        var authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        return new UserDetailsImpl(user.getId(), user.getEmail().address(), user.getPasswordHash(),
                user.getPlantId(), List.of(authority));
    }

    public Long getId() { return id; }

    public Long getPlantId() { return plantId; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }
}
