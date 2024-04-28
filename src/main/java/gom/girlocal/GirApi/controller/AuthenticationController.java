package gom.girlocal.GirApi.controller;

import gom.girlocal.GirApi.dto.authentication.AuthenticationRequest;
import gom.girlocal.GirApi.dto.authentication.AuthenticationResponse;
import gom.girlocal.GirApi.dto.authentication.RegistrationRequest;
import gom.girlocal.GirApi.exception.ApiRequestException;
import gom.girlocal.GirApi.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.signinUser(authenticationRequest));
    }

    // Permite envair la solicitud sin ningun dato y esto da un error
    // La contraseña no se encripta
    // Añadir codigo de error

    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody @Valid RegistrationRequest registrationRequest) {
        return ResponseEntity.ok(authenticationService.signupUser(registrationRequest));
    }
}