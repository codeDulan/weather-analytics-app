import { useAuth0 } from '@auth0/auth0-react';
import { Button } from 'react-bootstrap';

export default function UserMenu() {
    const { user, logout } = useAuth0();

    return(
        <div className="d-flex align-items-center gap-3">
            <span className="wp-text-muted small text-truncate" style={{ maxWidth: '12rem' }}>
                {user?.email ?? user?.name}
            </span>
            <Button variant="outline-secondary" size="sm" onClick={() => logout({ logoutParms: { returnTo: window.location.origin}})}>
                Log out
            </Button>
        </div>
    );
}