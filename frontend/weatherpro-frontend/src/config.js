export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api';

export const auth0Config = {
    domain: import.meta.env.VITE_AUTH0_DOMAIN ?? '',
    clientId: import.meta.env.VITE_AUTH0_CLIENT_ID ?? '',
    audience: import.meta.env.VITE_AUTH0_AUDIENCE ?? '',
};

export const isAuthConfigured = Boolean(
    auth0Config.domain && auth0Config.clientId && auth0Config.audience,
);