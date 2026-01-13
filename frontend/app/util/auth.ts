import type {UserRole} from '~/model/UserRole';
import type {User} from '~/model/User';

export async function redirectToLogin() {
    let resp = await fetch('/api/oauth/redirect');
    let data = await resp.json();

    window.location.href = data.url;
}

export function getMe(): User | null {
    let token = localStorage.getItem('auth_token');
    if (!token) {
        return null;
    }

    try {
        let payload = token.split('.')[1];
        let decoded = atob(payload);
        let parsed = JSON.parse(decoded);
        return {
            id: parsed.sub as string,
            name: parsed.name as string,
            role: parsed.role as UserRole
        };
    } catch (ignored) {
        return null;
    }
}
