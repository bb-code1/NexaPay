import React from 'react';
import { CheckCircle2, Search, ShieldAlert, CreditCard, BookOpen } from 'lucide-react';

interface ToolExecutionTimelineProps {
  toolsInvoked: string[];
  latencyMs: number;
}

const HUMAN_TOOL_NAMES: Record<string, { label: string; icon: React.ReactNode }> = {
  getTransactionDetails: {
    label: 'Retrieved Transaction Details & Gateway Record',
    icon: <Search size={14} />,
  },
  getCardLedgerSummary: {
    label: 'Verified Card Balance, Daily Limits & Active Holds',
    icon: <CreditCard size={14} />,
  },
  getFraudRiskAssessment: {
    label: 'Calculated Multi-Factor Velocity & Fraud Risk Score',
    icon: <ShieldAlert size={14} />,
  },
  getPaymentLifecycleHistory: {
    label: 'Traced Gateway Settlement Attempts & Timeline',
    icon: <Search size={14} />,
  },
  getSettlementDiscrepancies: {
    label: 'Audited Clearing Batch & Reconciled Amount Variance',
    icon: <Search size={14} />,
  },
  getCustomerProfileData: {
    label: 'Verified Customer KYC Tier & Identity Profile',
    icon: <Search size={14} />,
  },
};

export const ToolExecutionTimeline: React.FC<ToolExecutionTimelineProps> = ({
  toolsInvoked,
  latencyMs,
}) => {
  if (!toolsInvoked || toolsInvoked.length === 0) return null;

  return (
    <div className="bento-card">
      <div className="bento-card-header">
        <div>
          <div className="bento-card-title">
            <CheckCircle2 size={16} color="var(--status-success)" />
            Autonomous Investigation Steps
          </div>
          <div className="bento-card-subtitle">
            {toolsInvoked.length} operational tools coordinated in {latencyMs}ms
          </div>
        </div>
      </div>

      <div className="tool-timeline">
        {toolsInvoked.map((toolName, idx) => {
          const info = HUMAN_TOOL_NAMES[toolName] || {
            label: toolName.replace(/([A-Z])/g, ' $1').trim(),
            icon: <Search size={14} />,
          };

          return (
            <div key={idx} className="tool-step">
              <div className="tool-step-left">
                <span className="tool-step-indicator"></span>
                <span>{info.label}</span>
              </div>
              <span className="tool-step-latency font-mono">Step {idx + 1}</span>
            </div>
          );
        })}
      </div>
    </div>
  );
};
