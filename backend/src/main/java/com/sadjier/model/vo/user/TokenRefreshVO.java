package com.sadjier.model.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "令牌刷新VO")
public class TokenRefreshVO {
    /// <summary>新的访问令牌</summary>
    @Schema(description = "新的访问令牌")
    private String accessToken;
    /// <summary>新的刷新令牌</summary>
    @Schema(description = "新的刷新令牌")
    private String refreshToken;
}
