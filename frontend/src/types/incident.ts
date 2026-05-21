export type ConclusionType =
  | 'CONFIRMED_DECLINE'
  | 'SETTLEMENT_MISMATCH'
  | 'FRAUD_SUSPECTED'
  | 'RECOVERED_AFTER_RETRY'
  | 'INCONCLUSIVE';

export interface EvidenceItem {
  domain: string;
  keyMetric: string;
  observedValue: string;
  statusFlag: 'BREACH' | 'MATCH' | 'MISMATCH' | 'ANOMALY' | 'NORMAL' | 'WARNING' | string;
}

export interface PolicyReference {
  policyId: string;
  documentName: string;
  sectionClause: string;
}

export interface InvestigationReport {
  investigationId: string;
  entityRef: string;
  entityType: 'TRANSACTION' | 'PAYMENT' | 'CARD' | 'GENERAL' | string;
  conclusion: ConclusionType;
  primaryReason: string;
  confidenceScore: number;
  evidence: EvidenceItem[];
  citedPolicies: PolicyReference[];
  toolsInvoked: string[];
  recommendedAction: string;
  isFallback: boolean;
  latencyMs: number;
}

export interface ScenarioPreset {
  id: string;
  entityRef: string;
  title: string;
  category: 'CARD_LIMIT' | 'FRAUD_SIGNAL' | 'SETTLEMENT' | 'LIFECYCLE' | 'AUTHORIZATION';
  query: string;
  expectedConclusion: string;
}
