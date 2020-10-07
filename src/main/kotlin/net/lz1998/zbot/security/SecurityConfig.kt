package net.lz1998.zbot.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfig : WebSecurityConfigurerAdapter() {
    @Autowired
    lateinit var myAuthenticationEntryPoint: MyAuthenticationEntryPoint

    @Autowired
    lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter
    override fun configure(http: HttpSecurity) {
        http.cors()
                .and().csrf().disable()
                .formLogin()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests() // 登录通过
                .antMatchers("/api/security/**")
                .permitAll() // /api开头都要登陆
                .antMatchers("/api/**")
                .authenticated() // 不以/api开头的都通过
                .antMatchers("/**")
                .permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(myAuthenticationEntryPoint)
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    @Bean
    override fun authenticationManager(): AuthenticationManager {
        return super.authenticationManager()
    }
}