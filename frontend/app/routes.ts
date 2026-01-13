import {index, route, type RouteConfig} from '@react-router/dev/routes';

export default [
    index('routes/home.tsx'),
    route('/oauth/return', 'routes/oauth/return.tsx')
] satisfies RouteConfig;
