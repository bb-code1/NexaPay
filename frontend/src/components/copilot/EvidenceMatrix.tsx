import React from 'react';
import { Table, ShieldAlert, CheckCircle2, AlertTriangle, AlertCircle } from 'lucide-react';
import { EvidenceItem } from '../../types/incident';

interface EvidenceMatrixProps {
  evidence: EvidenceItem[];
}

export const EvidenceMatrix: React.FC<EvidenceMatrixProps> = ({ evidence }) => {
  if (!evidence || evidence.length === 0) return null;

  const renderStatusPill = (flag: string) => {
    switch (flag) {
      case 'BREACH':
      case 'DECLINED':
      case 'CIRCUIT_OPEN':
        return (
          <span className="status-pill danger">
            <AlertCircle size={12} />
            {flag}
          </span>
        );
      case 'MISMATCH':
      case 'WARNING':
        return (
          <span className="status-pill warning">
            <AlertTriangle size={12} />
            {flag}
          </span>
        );
      case 'MATCH':
      case 'NORMAL':
      case 'APPROVED':
        return (
          <span className="status-pill success">
            <CheckCircle2 size={12} />
            {flag}
          </span>
        );
      default:
        return (
          <span className="status-pill info">
            {flag}
          </span>
        );
    }
  };

  return (
    <div className="bento-card">
      <div className="bento-card-header">
        <div>
          <div className="bento-card-title">
            <Table size={16} />
            Corroborating Evidence Matrix
          </div>
          <div className="bento-card-subtitle">Verified operational telemetry across payment subsystems</div>
        </div>
      </div>

      <div className="data-table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Subsystem</th>
              <th>Metric / Rule</th>
              <th>Observed Telemetry</th>
              <th>Status Flag</th>
            </tr>
          </thead>
          <tbody>
            {evidence.map((item, idx) => (
              <tr key={idx}>
                <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                  {item.domain.replace('_', ' ')}
                </td>
                <td>{item.keyMetric}</td>
                <td className="font-mono" style={{ color: 'var(--text-primary)' }}>
                  {item.observedValue}
                </td>
                <td>{renderStatusPill(item.statusFlag)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
