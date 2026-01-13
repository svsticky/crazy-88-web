import type {UserRole} from '~/model/UserRole';

export interface User {
    id: string;
    name: string;
    role: UserRole;
}
