package com.pedidos360.pedidos.config;

import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@org.springframework.context.annotation.Profile("!integracion-local")
public class SecurityConfig {

    // =========================================================
    // MICROSOFT ENTRA ID - ISSUER
    // =========================================================

    private static final String ISSUER =
            "https://sts.windows.net/fd969d4c-5256-48f4-899c-b33c192dc051/";

    private static final String ISSUER_WITHOUT_SLASH =
            "https://sts.windows.net/fd969d4c-5256-48f4-899c-b33c192dc051";


    // =========================================================
    // MICROSOFT ENTRA ID - JWK
    // =========================================================

    private static final String JWK_SET_URI =
            "https://login.microsoftonline.com/fd969d4c-5256-48f4-899c-b33c192dc051/discovery/v2.0/keys";


    // =========================================================
    // PEDIDOS360 API - AUDIENCE
    // =========================================================

    private static final String API_AUDIENCE =
            "api://ef080cc3-9d30-4a1a-89b5-c5236f9bd00d";


    // =========================================================
    // JWT DECODER
    // =========================================================

    @Bean
    public JwtDecoder jwtDecoder() {

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder.withJwkSetUri(JWK_SET_URI).build();


        // -----------------------------------------------------
        // VALIDADOR DEL ISSUER
        // -----------------------------------------------------

        OAuth2TokenValidator<Jwt> issuerValidator = token -> {

            String issuer = token.getIssuer().toString();

            System.out.println("ISSUER RECIBIDO: " + issuer);

            if (ISSUER.equals(issuer)
                    || ISSUER_WITHOUT_SLASH.equals(issuer)) {

                return OAuth2TokenValidatorResult.success();
            }

            OAuth2Error error = new OAuth2Error(
                    "invalid_token",
                    "El issuer del token no es vÃƒÂ¡lido.",
                    null
            );

            return OAuth2TokenValidatorResult.failure(error);
        };


        // -----------------------------------------------------
        // VALIDADOR DE AUDIENCIA
        // -----------------------------------------------------

        OAuth2TokenValidator<Jwt> audienceValidator = token -> {

            Collection<String> audiences = token.getAudience();

            System.out.println(
                    "AUDIENCIA RECIBIDA: " + audiences
            );

            System.out.println(
                    "AUDIENCIA ESPERADA: " + API_AUDIENCE
            );

            if (audiences != null
                    && audiences.contains(API_AUDIENCE)) {

                return OAuth2TokenValidatorResult.success();
            }

            OAuth2Error error = new OAuth2Error(
                    "invalid_token",
                    "El token no contiene la audiencia esperada.",
                    null
            );

            return OAuth2TokenValidatorResult.failure(error);
        };


        // -----------------------------------------------------
        // VALIDADORES COMBINADOS
        // -----------------------------------------------------

        OAuth2TokenValidator<Jwt> validator =
                new DelegatingOAuth2TokenValidator<>(
                        new JwtTimestampValidator(),
                        issuerValidator,
                        audienceValidator
                );


        decoder.setJwtValidator(validator);

        return decoder;
    }


    // =========================================================
    // SPRING SECURITY
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // -------------------------------------------------
            // CSRF
            // -------------------------------------------------

            .csrf(csrf -> csrf.disable())


            // -------------------------------------------------
            // CORS
            // -------------------------------------------------

            .cors(cors -> {})


            // -------------------------------------------------
            // AUTORIZACIÃƒâ€œN
            // -------------------------------------------------

            .authorizeHttpRequests(auth -> auth

                // Permitir solicitudes OPTIONS
                .requestMatchers(
                        HttpMethod.OPTIONS,
                        "/**"
                ).permitAll()

                // GET requiere scope READ
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/pedidos/**"
                ).hasAuthority("SCOPE_read")

                // POST requiere scope WRITE
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/pedidos/**"
                ).hasAuthority("SCOPE_write")

                // PUT requiere scope WRITE
                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/pedidos/**"
                ).hasAuthority("SCOPE_write")

                // DELETE requiere scope WRITE
                .requestMatchers(
                        HttpMethod.DELETE,
                        "/api/pedidos/**"
                ).hasAuthority("SCOPE_write")

                // Cualquier otra peticiÃƒÂ³n requiere autenticaciÃƒÂ³n
                .anyRequest().authenticated()
            )


            // -------------------------------------------------
            // OAUTH2 RESOURCE SERVER
            // -------------------------------------------------

            .oauth2ResourceServer(
                oauth2 -> oauth2
                    .jwt(jwt -> {})
            );

        return http.build();
    }


    // =========================================================
    // CONFIGURACIÃƒâ€œN CORS
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        // Angular
        configuration.addAllowedOrigin(
                "http://localhost:4200"
        );


        // Headers
        configuration.addAllowedHeader("*");


        // MÃƒÂ©todos
        configuration.addAllowedMethod("*");


        // Credentials
        configuration.setAllowCredentials(true);


        // Registrar configuraciÃƒÂ³n
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}