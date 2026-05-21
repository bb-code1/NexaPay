export type UserRole =
  | 'ROLE_OPERATIONS_ANALYST'
  | 'ROLE_FRAUD_SPECIALIST'
  | 'ROLE_SETTLEMENT_MANAGER'
  | 'ROLE_ADMIN';

export interface AuthPersona {
  name: string;
  role: UserRole;
  label: string;
  roleHint: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  username: string;
  role: string;
  actorId: string;
}
