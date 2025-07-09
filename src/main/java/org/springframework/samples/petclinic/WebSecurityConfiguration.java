package org.springframework.samples.petclinic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

	private final ClientRegistrationRepository clientRegistrationRepository;

	public WebSecurityConfiguration(ClientRegistrationRepository clientRegistrationRepository) {
		this.clientRegistrationRepository = clientRegistrationRepository;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.oauth2Login(oauth2Login -> oauth2Login
			.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
				.userAuthoritiesMapper(userAuthoritiesMapper())));
		http.logout(logout -> logout
			.logoutSuccessHandler(oidcClientInitiatedLogoutSuccessHandler()));
		http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
			.anyRequest().authenticated());
		http.headers(Customizer.withDefaults());
		http.anonymous(Customizer.withDefaults());
		http.csrf(AbstractHttpConfigurer::disable);
		return http.build();
	}

	OidcClientInitiatedLogoutSuccessHandler oidcClientInitiatedLogoutSuccessHandler() {
		OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
		successHandler.setPostLogoutRedirectUri("http://localhost:8080/");
		return successHandler;
	}

	public GrantedAuthoritiesMapper userAuthoritiesMapper() {
		return authorities -> {
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
			authorities.forEach(authority -> {
//                TODO: Do not forget to enable "Add to userinfo" in Keycloak (Realm | Client scopes | roles | Mappers | client roles)
//                if (!(authority instanceof OidcUserAuthority oidcUserAuthority)) {
//                	return;
//                }
//
//                // noinspection unchecked
//                Optional.ofNullable(oidcUserAuthority.getAttributes().get("resource_access"))
//                	.map(ra -> ((Map<String, ?>) ra).get("sk"))
//                	.map(sbLegacy -> ((Map<String, ?>) sbLegacy).get("roles"))
//                	.ifPresent(roles -> ((List<String>) roles).stream()
//                		.map(r -> new SimpleGrantedAuthority("ROLE_" + r))
//                		.forEach(mappedAuthorities::add));
			});
			return mappedAuthorities;
		};
	}
}
