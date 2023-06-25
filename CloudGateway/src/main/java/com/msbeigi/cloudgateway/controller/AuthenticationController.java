package com.msbeigi.cloudgateway.controller;

import com.msbeigi.cloudgateway.model.AuthenticationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController {

    @GetMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @AuthenticationPrincipal OidcUser oidcUser,
            Model model,
            @RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient client
    ) {
        AuthenticationResponse authenticationResponse =
                AuthenticationResponse.builder()
                        .userId(oidcUser.getEmail())
                        .accessToken(client.getAccessToken().getTokenValue())
                        .refreshToken(Objects.requireNonNull(client.getRefreshToken()).getTokenValue())
                        .expiresAt(Objects.requireNonNull(client.getAccessToken().getExpiresAt()).getEpochSecond())
                        .authorityList(oidcUser.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                        .build();
        return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }

}
