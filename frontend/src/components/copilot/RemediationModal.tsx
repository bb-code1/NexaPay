import React, { useState } from 'react';
import { AlertCircle, CheckCircle2, ShieldAlert, X } from 'lucide-react';
import { api } from '../../api/client';

interface RemediationModalProps {
  isOpen: boolean;
  onClose: () => void;
  entityRef: string;
  recommendedAction: string;
  onSuccess: (message: string) => void;
}

export const RemediationModal: React.FC<RemediationModalProps> = ({
  isOpen,
  onClose,
  entityRef,
  recommendedAction,
  onSuccess,
}) => {
  const [isExecuting, setIsExecuting] = useState(false);

  if (!isOpen) return null;

  const handleConfirm = async () => {
    setIsExecuting(true);
    try {
      if (entityRef.startsWith('CARD-') || recommendedAction.toLowerCase().includes('block')) {
        await api.blockCard(entityRef, recommendedAction);
      } else {
        // Simulated remediation endpoint execution
        await new Promise((r) => setTimeout(r, 600));
      }
      onSuccess(`Successfully executed remediation: "${recommendedAction}" on ${entityRef}`);
      onClose();
    } catch (err: any) {
      alert(`Action execution failed: ${err.message}`);
    } finally {
      setIsExecuting(false);
    }
  };

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(0, 0, 0, 0.75)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 100,
        padding: 20,
      }}
    >
      <div
        className="bento-card"
        style={{
          maxWidth: 480,
          width: '100%',
          boxShadow: 'var(--shadow-elevated)',
          backgroundColor: 'var(--bg-surface-elevated)',
        }}
      >
        <div className="bento-card-header">
          <div className="bento-card-title">
            <ShieldAlert size={18} color="var(--accent-primary)" />
            Execute Remediation Action
          </div>
          <button className="btn btn-secondary" style={{ padding: 4 }} onClick={onClose}>
            <X size={16} />
          </button>
        </div>

        <div style={{ fontSize: 13, color: 'var(--text-secondary)', marginBottom: 16 }}>
          You are about to execute the following operational resolution for reference{' '}
          <strong className="font-mono" style={{ color: 'var(--text-primary)' }}>
            {entityRef}
          </strong>
          :
        </div>

        <div
          style={{
            padding: 14,
            backgroundColor: 'var(--bg-surface-active)',
            border: '1px solid var(--border-subtle)',
            borderRadius: 'var(--radius-md)',
            marginBottom: 20,
            fontSize: 14,
            fontWeight: 500,
            color: 'var(--text-primary)',
          }}
        >
          {recommendedAction}
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 10 }}>
          <button className="btn btn-secondary" onClick={onClose} disabled={isExecuting}>
            Cancel
          </button>
          <button className="btn btn-primary" onClick={handleConfirm} disabled={isExecuting}>
            {isExecuting ? 'Executing...' : 'Confirm & Apply'}
          </button>
        </div>
      </div>
    </div>
  );
};
