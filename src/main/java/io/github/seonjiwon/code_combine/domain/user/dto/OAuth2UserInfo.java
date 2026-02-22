package io.github.seonjiwon.code_combine.domain.user.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2UserInfo {

    private Integer id;
    private String username;
    private String avatarUrl;

    public static OAuth2UserInfo from(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                             .id((Integer)attributes.get("id"))
                             .username((String) attributes.get("login"))
                             .avatarUrl((String) attributes.get("avatar_url"))
                             .build();
    }
}
