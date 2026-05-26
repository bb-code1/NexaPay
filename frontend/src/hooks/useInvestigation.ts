import { useMutation } from '@tanstack/react-query';
import { api } from '../api/client';
import { InvestigationReport } from '../types/incident';

export const useInvestigation = () => {
  return useMutation<InvestigationReport, Error, string>({
    mutationFn: async (query: string) => {
      const controller = new AbortController();
      return api.investigate(query, controller.signal);
    },
  });
};
