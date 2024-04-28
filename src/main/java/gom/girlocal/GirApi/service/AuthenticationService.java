package gom.girlocal.GirApi.service;

import gom.girlocal.GirApi.dto.authentication.AuthenticationRequest;
import gom.girlocal.GirApi.dto.authentication.AuthenticationResponse;
import gom.girlocal.GirApi.dto.authentication.RegistrationRequest;

public interface AuthenticationService {
    AuthenticationResponse signinUser(AuthenticationRequest authenticationRequest);
    AuthenticationResponse signupUser(RegistrationRequest registrationRequest);
}
