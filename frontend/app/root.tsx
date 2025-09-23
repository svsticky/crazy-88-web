import {Links, Meta, Outlet, Scripts, ScrollRestoration,} from 'react-router';

import './app.css';
import React from 'react';

export function Layout({children}: { children: React.ReactNode }) {
    return (
        <html lang="en">
        <head>
            <meta charSet="utf-8"/>
            <title>Crazy88</title>
            <meta name="viewport" content="width=device-width, initial-scale=1"/>
            <Meta/>
            <Links/>
        </head>
        <body>
        {children}
        <ScrollRestoration/>
        <Scripts/>
        </body>
        </html>
    );
}

export default function App() {
    return <Outlet/>;
}
