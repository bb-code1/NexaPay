export interface BenchmarkSummary {
  totalScenarios: number;
  passedScenarios: number;
  accuracyScore: number;
  status: string;
}

export interface LedgerPostingItem {
  id: string;
  accountType: string;
  accountId: string;
  entryType: 'DEBIT' | 'CREDIT';
  amount: number;
  currency: string;
  description: string;
  createdAt: string;
}

export interface LedgerBatchItem {
  batchId: string;
  transactionRef: string;
  totalDebits: number;
  totalCredits: number;
  isBalanced: boolean;
  postings: LedgerPostingItem[];
  postedAt: string;
}
