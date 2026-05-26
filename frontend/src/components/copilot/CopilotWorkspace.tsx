import React, { useState, useEffect } from 'react';
import { Bot, Send, Sparkles, AlertCircle, CheckCircle2, ShieldAlert, ArrowRight, RefreshCw } from 'lucide-react';
import { IncidentFeed } from './IncidentFeed';
import { ToolExecutionTimeline } from './ToolExecutionTimeline';
import { EvidenceMatrix } from './EvidenceMatrix';
import { PolicyReferenceList } from './PolicyReferenceList';
import { RemediationModal } from './RemediationModal';
import { ScenarioPreset, InvestigationReport } from '../../types/incident';
import { SCENARIO_PRESETS } from '../../data/scenarios';
import { useInvestigation } from '../../hooks/useInvestigation';

interface CopilotWorkspaceProps {
  onShowToast: (message: string) => void;
}

export const CopilotWorkspace: React.FC<CopilotWorkspaceProps> = ({ onShowToast }) => {
  const [selectedScenario, setSelectedScenario] = useState<ScenarioPreset | null>(SCENARIO_PRESETS[0]);
  const [queryInput, setQueryInput] = useState<string>(SCENARIO_PRESETS[0].query);
  const [report, setReport] = useState<InvestigationReport | null>(null);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

  const { mutate: runInvestigation, isPending, error } = useInvestigation();

  useEffect(() => {
    if (selectedScenario) {
      setQueryInput(selectedScenario.query);
      handleExecuteQuery(selectedScenario.query);
    }
  }, [selectedScenario]);

  const handleExecuteQuery = (query: string) => {
    if (!query.trim()) return;
    runInvestigation(query, {
      onSuccess: (data) => {
        setReport(data);
      },
      onError: (err) => {
        console.error('Investigation error:', err);
      },
    });
  };

  const getConclusionBadge = (conclusion: string) => {
    switch (conclusion) {
      case 'CONFIRMED_DECLINE':
        return <span className="status-pill danger">Confirmed Decline</span>;
      case 'SETTLEMENT_MISMATCH':
        return <span className="status-pill warning">Settlement Mismatch</span>;
      case 'FRAUD_SUSPECTED':
        return <span className="status-pill danger">High Fraud Risk</span>;
      case 'RECOVERED_AFTER_RETRY':
        return <span className="status-pill success">Recovered on Retry</span>;
      default:
        return <span className="status-pill info">Inconclusive</span>;
    }
  };

  return (
    <div className="copilot-grid">
      <IncidentFeed
        selectedScenario={selectedScenario}
        onSelectScenario={(s) => setSelectedScenario(s)}
      />

      <main className="copilot-canvas">
        {/* Omnibar Card */}
        <div className="bento-card">
          <form
            onSubmit={(e) => {
              e.preventDefault();
              handleExecuteQuery(queryInput);
            }}
            style={{ display: 'flex', gap: 12 }}
          >
            <input
              type="text"
              className="input-search"
              placeholder="Ask the AI Copilot (e.g. 'Why was transaction TXN-84721 declined?')..."
              value={queryInput}
              onChange={(e) => setQueryInput(e.target.value)}
            />
            <button type="submit" className="btn btn-primary" disabled={isPending}>
              {isPending ? (
                <>
                  <RefreshCw size={15} className="spin" />
                  Investigating...
                </>
              ) : (
                <>
                  <Sparkles size={15} />
                  Diagnose
                </>
              )}
            </button>
          </form>
        </div>

        {/* Diagnosis Report Canvas */}
        {report && (
          <>
            <div className={`bento-card diagnosis-card ${report.conclusion.toLowerCase()}`}>
              <div className="bento-card-header">
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                    <span className="font-mono" style={{ fontSize: 18, fontWeight: 700, color: 'var(--text-primary)' }}>
                      {report.entityRef}
                    </span>
                    {getConclusionBadge(report.conclusion)}
                  </div>
                  <div className="bento-card-subtitle" style={{ marginTop: 6 }}>
                    Investigation Reference ID: <span className="font-mono">{report.investigationId}</span>
                  </div>
                </div>

                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: 22, fontWeight: 700, color: 'var(--accent-primary)', fontFamily: 'var(--font-mono)' }}>
                    {Math.round(report.confidenceScore * 100)}%
                  </div>
                  <div className="metric-label">Confidence Score</div>
                </div>
              </div>

              <div style={{ fontSize: 14, color: 'var(--text-primary)', lineHeight: 1.6, margin: '14px 0' }}>
                {report.primaryReason}
              </div>

              <div className="diagnosis-metrics-row">
                <div className="metric-badge">
                  <span className="metric-label">Target Subsystem</span>
                  <span className="metric-val">{report.entityType}</span>
                </div>
                <div className="metric-badge">
                  <span className="metric-label">Telemetry Ingested</span>
                  <span className="metric-val">{report.evidence?.length || 0} Data Points</span>
                </div>
                <div className="metric-badge">
                  <span className="metric-label">Analysis Latency</span>
                  <span className="metric-val">{report.latencyMs}ms</span>
                </div>
                <div className="metric-badge">
                  <span className="metric-label">Operating Rules Cited</span>
                  <span className="metric-val">{report.citedPolicies?.length || 0} Clauses</span>
                </div>
              </div>

              {report.recommendedAction && (
                <div
                  style={{
                    marginTop: 18,
                    padding: 14,
                    backgroundColor: 'var(--bg-surface-elevated)',
                    borderRadius: 'var(--radius-md)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    gap: 16,
                  }}
                >
                  <div>
                    <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase' }}>
                      Recommended Action
                    </div>
                    <div style={{ fontSize: 13, fontWeight: 500, color: 'var(--text-primary)', marginTop: 2 }}>
                      {report.recommendedAction}
                    </div>
                  </div>

                  <button
                    className="btn btn-primary"
                    onClick={() => setIsModalOpen(true)}
                  >
                    Execute Action
                    <ArrowRight size={14} />
                  </button>
                </div>
              )}
            </div>

            <ToolExecutionTimeline
              toolsInvoked={report.toolsInvoked}
              latencyMs={report.latencyMs}
            />

            <EvidenceMatrix evidence={report.evidence} />

            <PolicyReferenceList citedPolicies={report.citedPolicies} />
          </>
        )}
      </main>

      {report && (
        <RemediationModal
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          entityRef={report.entityRef}
          recommendedAction={report.recommendedAction}
          onSuccess={(msg) => onShowToast(msg)}
        />
      )}
    </div>
  );
};
