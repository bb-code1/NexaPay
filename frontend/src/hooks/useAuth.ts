import { useState, useEffect } from 'react';
import { AuthPersona } from '../types/auth';
import { api, setAuthToken } from '../api/client';

export const PERSONAS: AuthPersona[] = [
  {
    name: 'Sarah Chen',
    role: 'ROLE_OPERATIONS_ANALYST',
    label: 'Operations Analyst',
    roleHint: 'OPERATIONS_ANALYST',
  },
  {
    name: 'Vikram Patel',
    role: 'ROLE_FRAUD_SPECIALIST',
    label: 'Fraud Specialist',
    roleHint: 'FRAUD_SPECIALIST',
  },
  {
    name: 'Elena Rostova',
    role: 'ROLE_SETTLEMENT_MANAGER',
    label: 'Settlement Manager',
    roleHint: 'SETTLEMENT_MANAGER',
  },
  {
    name: 'System Administrator',
    role: 'ROLE_ADMIN',
    label: 'Platform Admin',
    roleHint: 'ADMIN',
  },
];

export const useAuth = () => {
  const [currentPersona, setCurrentPersona] = useState<AuthPersona>(PERSONAS[0]);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  useEffect(() => {
    let isMounted = true;
    setIsLoading(true);

    api.login(currentPersona.name, currentPersona.roleHint)
      .then((res) => {
        if (isMounted) {
          setToken(res.token);
          setAuthToken(res.token);
        }
      })
      .catch((err) => {
        console.error('Failed to authenticate persona:', err);
      })
      .finally(() => {
        if (isMounted) setIsLoading(false);
      });

    return () => {
      isMounted = false;
    };
  }, [currentPersona]);

  return {
    currentPersona,
    setCurrentPersona,
    token,
    isLoading,
    personas: PERSONAS,
  };
};
