package com.codeaim.urlcheck.domain;

import java.time.Instant;

import org.apache.commons.lang3.Validate;

public final class UserDto
{
    private long id;
    private String username;
    private String email;
    private String resetToken;
    private String accessToken;
    private String password;
    private boolean emailVerified;
    private Instant created;
    private Instant modified;
    private long version;

    private UserDto(
            final long id,
            final String username,
            final String email,
            final String resetToken,
            final String accessToken,
            final String password,
            final boolean emailVerified,
            final Instant created,
            final Instant modified,
            final long version
    )
    {
        this.id = id;
        this.username = username;
        this.email = email;
        this.resetToken = resetToken;
        this.accessToken = accessToken;
        this.password = password;
        this.emailVerified = emailVerified;
        this.created = created;
        this.modified = modified;
        this.version = version;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static Builder buildFrom(UserDto userDto)
    {
        return builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .resetToken(userDto.getResetToken())
                .accessToken(userDto.getAccessToken())
                .password(userDto.getPassword())
                .emailVerified(userDto.isEmailVerified())
                .created(userDto.getCreated())
                .version(userDto.getVersion());
    }

    public long getId()
    {
        return id;
    }

    public String getUsername()
    {
        return username;
    }

    public String getEmail()
    {
        return email;
    }

    public String getResetToken()
    {
        return resetToken;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public String getPassword()
    {
        return password;
    }

    public boolean isEmailVerified()
    {
        return emailVerified;
    }

    public Instant getCreated()
    {
        return created;
    }

    public Instant getModified()
    {
        return modified;
    }

    public long getVersion()
    {
        return version;
    }

    public final static class Builder
    {
        private long id;
        private String username;
        private String email;
        private String resetToken;
        private String accessToken;
        private String password;
        private boolean emailVerified;
        private Instant created = Instant.now();
        private Instant modified = Instant.now();
        private long version;

        public Builder id(final long id)
        {
            Validate.notNull(id);

            this.id = id;
            return this;
        }

        public Builder username(final String username)
        {
            Validate.notNull(username);

            this.username = username;
            return this;
        }

        public Builder email(final String email)
        {
            Validate.notNull(email);

            this.email = email;
            return this;
        }

        public Builder resetToken(final String resetToken)
        {
            Validate.notNull(resetToken);

            this.resetToken = resetToken;
            return this;
        }

        public Builder accessToken(final String accessToken)
        {
            Validate.notNull(accessToken);

            this.accessToken = accessToken;
            return this;
        }

        public Builder password(final String password)
        {
            Validate.notNull(password);

            this.password = password;
            return this;
        }

        public Builder emailVerified(final boolean emailVerified)
        {
            Validate.notNull(emailVerified);

            this.emailVerified = emailVerified;
            return this;
        }

        public Builder created(final Instant created)
        {
            Validate.notNull(created);

            this.created = created;
            return this;
        }

        public Builder modified(final Instant modified)
        {
            Validate.notNull(modified);

            this.modified = modified;
            return this;
        }

        public Builder version(final long version)
        {
            Validate.notNull(version);

            this.version = version;
            return this;
        }

        public UserDto build()
        {
            Validate.notNull(username);
            Validate.notNull(email);
            Validate.notNull(resetToken);
            Validate.notNull(accessToken);
            Validate.notNull(password);

            return new UserDto(
                    this.id,
                    this.username,
                    this.email,
                    this.resetToken,
                    this.accessToken,
                    this.password,
                    this.emailVerified,
                    this.created,
                    this.modified,
                    this.version <= 0 ? 1 : this.version
            );
        }
    }
}