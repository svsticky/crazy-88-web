'use client';
import {useSearchParams} from 'react-router';
import {useEffect, useState} from 'react';
import {redirectToLogin} from '~/util/auth';

export default function OAuthReturn() {
    const [message, setMessage]: [string | null, Function] = useState(null);
    const [searchParams] = useSearchParams();
    const code = searchParams.get('code');

    useEffect(() => {
        (async function () {
            if (!code) {
                return redirectToLogin();
            }

            let formData = new FormData();
            formData.append('code', code || '');

            let resp = await fetch('/api/oauth/token', {
                method: 'POST',
                body: formData
            });
            if (!resp.ok) {
                setMessage(`Error exchanging code for token: ${await resp.text()}`);
                return;
            }
            let data = await resp.json();

            localStorage.setItem('auth_token', data.token);
            // Intentional hard-redirect to make sure all cached auth state is cleared
            window.location.href = '/';
        })();
    }, [code]);

    if (!message) {
        return <div>
            <p>Authenticating...</p>
        </div>;
    }

    return message;
}
