import React from 'react';
import { Layers, CheckCircle2, ShieldCheck, ArrowRightLeft } from 'lucide-react';

export const LedgerWorkspace: React.FC = () => {
  const samplePostings = [
    { account: 'Cardholder Clearing (Asset)', debit: '₹2,500.00', credit: '-', type: 'DEBIT' },
    { account: 'Merchant Settlement (Liability)', debit: '-', credit: '₹2,450.00', type: 'CREDIT' },
    { account: 'Interchange Processing Fee (Income)', debit: '-', credit: '₹50.00', type: 'CREDIT' },
  ];

  return (
    <div className="ledger-grid">
      <div className="balanced-equation-card">
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <ShieldCheck size={20} />
          <span>Double-Entry Mathematical Invariant: Total Debits (₹2,500.00) == Total Credits (₹2,500.00)</span>
        </div>
        <span className="status-pill success">Strictly Balanced</span>
      </div>

      <div className="bento-card">
        <div className="bento-card-header">
          <div>
            <div className="bento-card-title">
              <ArrowRightLeft size={16} />
              Journal Batch Visualizer (T-Account Breakdown)
            </div>
            <div className="bento-card-subtitle">
              Batch Ref: <span className="font-mono">JB-8F92A10B</span> • Transaction Ref: <span className="font-mono">TXN-74192</span>
            </div>
          </div>
        </div>

        <div className="t-account-container">
          <div>
            <div className="t-column-header t-column-debit">
              <span>Debit Side (Uses of Funds)</span>
              <span>Amount</span>
            </div>
            <div style={{ padding: '12px 0', display: 'flex', flexDirection: 'column', gap: 8 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13 }}>
                <span>Cardholder Clearing Account</span>
                <span className="font-mono" style={{ fontWeight: 600, color: 'var(--text-primary)' }}>₹2,500.00</span>
              </div>
            </div>
          </div>

          <div>
            <div className="t-column-header t-column-credit">
              <span>Credit Side (Sources of Funds)</span>
              <span>Amount</span>
            </div>
            <div style={{ padding: '12px 0', display: 'flex', flexDirection: 'column', gap: 8 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13 }}>
                <span>Merchant Settlement Account</span>
                <span className="font-mono" style={{ fontWeight: 600, color: 'var(--text-primary)' }}>₹2,450.00</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13 }}>
                <span>Interchange Processing Fee</span>
                <span className="font-mono" style={{ fontWeight: 600, color: 'var(--text-primary)' }}>₹50.00</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="bento-card">
        <div className="bento-card-header">
          <div className="bento-card-title">
            <Layers size={16} />
            Recent Settled Journal Entries
          </div>
        </div>

        <div className="data-table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Account Subsystem</th>
                <th>Entry Type</th>
                <th>Debit</th>
                <th>Credit</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {samplePostings.map((p, idx) => (
                <tr key={idx}>
                  <td style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{p.account}</td>
                  <td>
                    <span className={`status-pill ${p.type === 'DEBIT' ? 'danger' : 'success'}`}>
                      {p.type}
                    </span>
                  </td>
                  <td className="font-mono">{p.debit}</td>
                  <td className="font-mono">{p.credit}</td>
                  <td>
                    <span className="status-pill success">POSTED</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
