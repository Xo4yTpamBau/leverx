package com.sprect.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprect.model.Role;
import com.sprect.model.StatusUser;
import com.sprect.utils.DefaultString;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static com.sprect.utils.DefaultString.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Schema(description = "the user ID is generated automatically",
            example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idUser;

    @Schema(description = "The user name is always in lowercase from 3 to 18 characters long",
            example = "user")
    @Column(nullable = false, unique = true)
    @Length(min = 3, max = 24, message = FAILED_VALIDATE_USERNAME)
    private String username;

    @Schema(description = "Email user",
            example = "user@mail.com")
    @Column(nullable = false, unique = true)
    @Pattern(regexp = PATTERN_EMAIL,
            message = FAILED_VALIDATE_EMAIL)
    private String email;

    @Schema(description = "First name of the user",
            example = "User")
    @Column(nullable = false)
    @Pattern(regexp = PATTERN_FIRST_NAME,
            message = FAILED_VALIDATE_FIRST_NAME)
    private String firstName;

    @Schema(description = "Last name of the user",
            example = "User")
    @Column(nullable = false)
    @Pattern(regexp = PATTERN_LAST_NAME,
            message = FAILED_VALIDATE_LAST_NAME)
    private String lastName;

    @Schema(description = DefaultString.FAILED_VALIDATE_PASSWORD,
            example = "1234Qwer")
    @Column(nullable = false)
    @Pattern(regexp = PATTERN_PASSWORD,
            message = FAILED_VALIDATE_PASSWORD)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Schema(description = "user registration date")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @CreationTimestamp
    private LocalDate registrationDate;

    @Schema(description = "Link to download the user's avatar (if available)",
            example = "link")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Transient
    private URL urlAvatar;

    @Schema(description = "The index of the presence of the user's avatar",
            example = "false")
    @Column(columnDefinition = "boolean default false")
    @JsonIgnore
    private boolean avatar;

    @Schema(description = "status user в системе(active, banned)")
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private StatusUser status;

    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private Role role;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(getRole().toString()));
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return getStatus() != StatusUser.BLOCKED;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return getStatus() != StatusUser.NOT_ACTIVE;
    }
}
