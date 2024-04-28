package gom.girlocal.GirApi.service.impl;

import gom.girlocal.GirApi.dto.authentication.AuthenticationRequest;
import gom.girlocal.GirApi.dto.authentication.AuthenticationResponse;
import gom.girlocal.GirApi.dto.authentication.RegistrationRequest;
import gom.girlocal.GirApi.entity.User;
import gom.girlocal.GirApi.enums.Role;
import gom.girlocal.GirApi.exception.ApiRequestException;
import gom.girlocal.GirApi.repository.UserRepository;
import gom.girlocal.GirApi.security.JwtTokenProvider;
import gom.girlocal.GirApi.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthenticationResponse signinUser(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
            User user = userRepository.findByEmail(authenticationRequest.getEmail())
                    .orElseThrow(() -> new ApiRequestException("Email not found", HttpStatus.NOT_FOUND));
            return new AuthenticationResponse(jwtTokenProvider.generateToken(user, jwtTokenProvider.generateExtraClaims(user)));

        } catch (AuthenticationException e) {
            throw new ApiRequestException("Invalid credentials", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public AuthenticationResponse signupUser(RegistrationRequest registrationRequest) {
        if (registrationRequest.getPassword() != null && !registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())) {
            throw new ApiRequestException("Passwords do not match", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new ApiRequestException("Email already exists", HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(registrationRequest.getPassword()));
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setPhoneNumber(registrationRequest.getPhoneNumber());
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);

        return new AuthenticationResponse(jwtTokenProvider.generateToken(user, jwtTokenProvider.generateExtraClaims(user)));
    }


}