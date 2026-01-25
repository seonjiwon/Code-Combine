package io.github.seonjiwon.code_combine.domain.user.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2UserInfo {
    private String username;
    private String email;
    private String avatarUrl;

    public static OAuth2UserInfo from(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                             .username((String) attributes.get("login"))
                             .email((String) attributes.get("email"))
                             .avatarUrl((String) attributes.get("avatar_url"))
                             .build();
    }
}
