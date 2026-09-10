import { useAuth0 } from '@auth0/auth0-react';
import { Alert, Button, Container, Spinner } from 'react-bootstrap';

import { isAuthConfigured } from '../config';

export default function AuthGate({ children }) {
    const { isLoading, isAuthenticated, error, loginWithRedirect } = useAuth0();

    if (!isAuthConfigured) {
        return (
            <Centered>
                <Alert variant="warning" className="text-start">
                    <Alert.Heading>Auth0 is not configured</Alert.Heading>
                </Alert>
            </Centered>
        )
    }

    if (isLoading) {
        return (
            <Centered>
                <Spinner animation="border" role="status" />
                <p className="mt-3 wp-text-muted">Checking your session...</p>
            </Centered>
        );
    }

    if (error) {
        return (
            <Centered>
                <Alert variant="danger" className="text-start">
                    <Alert.Heading>Sign-in failed</Alert.Heading>
                    {error.message}
                </Alert>
                <Button variant="outline-primary" onClick={() => loginWithRedirect()}>
                    Try again
                </Button>

            </Centered>
        );
    }

    if (!isAuthenticated) {
        return (
            <Centered>
                <h1 className="fw-bold display-6 mb-2">
                    Weather<span className="text-primary">Pro</span>
                </h1>
                <p className="wp-text-muted mb-4">
                    Sighn in to view the Comfort Index dashboard.
                </p>
                <Button size="lg" onClick={() => loginWithRedirect()}>
                    Sign in
                </Button>
            </Centered>
        );
    }

    return children;
}

function Centered({ children }) {
    return (
        <Container
            className="d-flex flex-column align-items-center justify-content-center text-center"
            style={{ minHeight: '100vh', maxWidth: '32rem' }}
        >
            {children}
        </Container>
    );
}