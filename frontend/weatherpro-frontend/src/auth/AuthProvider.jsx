import { Auth0Provider } from '@auth0/auth0-react';
import { auth0Config } from '../config';

export default function AuthProvider({ children }) {
    const onRedirectCallback = () => {
        window.history.replaceState({}, document.title, window.location.pathname);
    };

    return (
        <Auth0Provider
            domain={auth0Config.domain}
            clientId={auth0Config.clientId}
            authorizationParams={{
                redirect_uri: window.location.origin,
                audience: auth0Config.audience,
            }}
            useRefreshTokens
            cacheLocation="localstorage"
            onRedirectCallback={onRedirectCallback}
        >
            {children}
        </Auth0Provider>
    );
}