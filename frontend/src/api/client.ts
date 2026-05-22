import { InvestigationReport } from '../types/incident';
import { AuthResponse } from '../types/auth';
import { BenchmarkSummary } from '../types/ledger';

let currentAuthToken: string | null = null;

export const setAuthToken = (token: string | null) => {
  currentAuthToken = token;
};

export const getAuthToken = () => currentAuthToken;

export const apiFetch = async <T>(endpoint: string, options: RequestInit = {}): Promise<T> => {
  const headers = new Headers(options.headers || {});
  
  if (!headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  if (currentAuthToken && !headers.has('Authorization')) {
    headers.set('Authorization', `Bearer ${currentAuthToken}`);
  }

  const correlationId = 'ui-' + Math.random().toString(36).substring(2, 9);
  headers.set('X-Correlation-ID', correlationId);

  const response = await fetch(endpoint, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`API Request Failed (${response.status}): ${errorText || response.statusText}`);
  }

  return response.json();
};

export const api = {
  login: (username: string, roleHint: string): Promise<AuthResponse> =>
    apiFetch('/api/v1/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, roleHint }),
    }),

  investigate: (query: string, signal?: AbortSignal): Promise<InvestigationReport> =>
    apiFetch('/api/v1/ai/investigate', {
      method: 'POST',
      body: JSON.stringify({ query }),
      signal,
    }),

  seedDatabase: (): Promise<{ message: string; customerCount: number; scenarioCount: number }> =>
    apiFetch('/api/v1/admin/seed', {
      method: 'POST',
    }),

  runBenchmark: (): Promise<BenchmarkSummary> =>
    apiFetch('/api/v1/admin/benchmark/run', {
      method: 'POST',
    }),

  blockCard: (cardId: string, reason: string): Promise<any> =>
    apiFetch(`/api/v1/cards/${cardId}/block`, {
      method: 'POST',
      body: JSON.stringify({ reason }),
    }),
};
