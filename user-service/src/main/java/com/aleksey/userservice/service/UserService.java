package com.aleksey.userservice.service;

import com.aleksey.userservice.api.request.UserRequestDTO;
import com.aleksey.userservice.api.response.UserResponse;
import com.aleksey.userservice.properties.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Keycloak keycloak;

    private final KeycloakProperties keycloakProperties;

    public UserResponse update(UserRequestDTO dto) {
        UserResource userResource = getUserResource(dto.username());
        UserRepresentation representation = userResource.toRepresentation();
        representation.setEmail(dto.email());
        representation.setCredentials(Collections.singletonList(createPasswordCredentials(dto.password())));
        representation.setFirstName(dto.firstName());
        representation.setLastName(dto.lastName());
        userResource.update(representation);
        return new UserResponse(representation.getId(), representation.getUsername(), representation.getEmail());
    }

    private UserResource getUserResource(String username) {
        RealmResource realmResource = getRealm();
        List<UserRepresentation> users = realmResource.users().search(username);
        return realmResource.users().get(users.getFirst().getId());
    }

    private RealmResource getRealm() {
         return keycloak.realm(keycloakProperties.getRealm());
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }
}