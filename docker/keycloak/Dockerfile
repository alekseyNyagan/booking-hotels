FROM quay.io/keycloak/keycloak:26.0.5 AS builder

ENV KC_HEALTH_ENABLED=true \
    KC_METRICS_ENABLED=true \
    KC_DB=postgres

WORKDIR /opt/keycloak

RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:26.0.5

COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
