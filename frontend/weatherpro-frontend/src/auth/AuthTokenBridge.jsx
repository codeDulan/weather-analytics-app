import { useEffect } from 'react';
import { useAuth0 } from '@auth0/auth0-react';

import { setAccessTokenGetter } from '../api/tokenProvider';

export default function AuthTokenBridge() {
    const { getAccessTokenSilently, isAuthenticated } = useAuth0();

    useEffect(() => {
        setAccessTokenGetter(isAuthenticated ? getAccessTokenSilently : null);
        return () => setAccessTokenGetter(null);
    }, [getAccessTokenSilently, isAuthenticated]);

    return null;
}