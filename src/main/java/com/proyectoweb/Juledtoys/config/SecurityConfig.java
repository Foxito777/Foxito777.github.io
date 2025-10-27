package com.proyectoweb.Juledtoys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(auth -> auth
        // Páginas públicas (accesibles sin autenticación)
        
  .requestMatchers("/", "/login", "/register", "/post-register", "/api/promociones/**", "/acerca", "/productos", "/detalle/**", "/contacto", "/buscador", "/buscar/**", "/checkout", "/checkout/**").permitAll()
        // Recursos estáticos públicos
        .requestMatchers("/css/**", "/js/**", "/Imagenes/**", "/Video/**", "/h2-console/**").permitAll()
        // Páginas que requieren autenticación
        .requestMatchers("/carrito", "/carrito/agregar", "/carrito/actualizar", "/carrito/eliminar", "/carrito/limpiar", "/carrito/eliminar-sin-stock", "/perfil/**").authenticated()
        // API endpoints del carrito (permiten acceso para AJAX)
        .requestMatchers("/carrito/api/**").permitAll()
  // Backoffice solo para staff (ADMIN, VENDEDOR, CAJERO, MARKETING)
  .requestMatchers("/backoffice/**").hasAnyRole("ADMIN","VENDEDOR","CAJERO","MARKETING")
        // Cualquier otra URL requiere autenticación
        .anyRequest().authenticated()
      )
      .formLogin(login -> login
        .loginPage("/login")               // GET para tu login.html
        .loginProcessingUrl("/login")      // POST del form
        .defaultSuccessUrl("/", true)      // redirección tras login
        .failureUrl("/login?error")
        .permitAll()
      )
      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/")             // Redirigir al inicio después del logout
        .permitAll()
      )
      .csrf(csrf -> csrf
        // Deshabilitar CSRF para H2 console, endpoints AJAX del carrito y para el formulario que elimina productos sin stock
        .ignoringRequestMatchers("/h2-console/**", "/carrito/api/**", "/carrito/eliminar-sin-stock")
      )
      .headers(headers -> headers
        .frameOptions(frame -> frame.sameOrigin())  // Permitir frames para H2 console
      );
    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
